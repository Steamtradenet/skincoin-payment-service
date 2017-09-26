package net.steamtrade.payment.backend.ethereum.watch.logic;

import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.ethereum.dao.EthAccountDao;
import net.steamtrade.payment.backend.ethereum.dao.EthPaymentDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthPaymentPK;
import net.steamtrade.payment.backend.ethereum.utils.NotifyHelper;
import net.steamtrade.payment.backend.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import net.steamtrade.payment.backend.exceptions.Error;

import java.util.UUID;

/**
 * Created by sasha on 03.07.17.
 */
@Service
public class PaymentProcessor {

    @Autowired
    private LogServiceClient log;
    @Autowired
    private EthPaymentDao paymentDao;
    @Autowired
    private JpaTransactionManager txManager;
    @Autowired
    private NotifyHelper notifyHelper;
    @Autowired
    private EthAccountDao accountDao;

    public void processPayment(EthTransaction transaction) {
        TransactionStatus status = txManager.getTransaction(
                new DefaultTransactionDefinition());

        EthPayment payment;
        try {
            payment = paymentDao.getPayment(transaction.getHash(), true);
            if (payment == null) {
                Integer appId = getAppId(transaction.getTo());
                if (appId == null) {
                    throw new AppException(Error.UNEXPECTED_EXCEPTION, "Can't detect appId for transaction(hash:" +
                            transaction.getHash() + ",from:" + transaction.getFrom() + ",to:" + transaction.getTo() + ") not found");
                }
                payment = new EthPayment();
                payment.setId(new EthPaymentPK(appId, EthPayment.Type.PAYMENT, UUID.randomUUID().toString()));
                payment.setHash(transaction.getHash());

                payment.setFrom(transaction.getFrom());
                payment.setTo(transaction.getTo());
                payment.setAmount(transaction.getAmount());
                payment.setCurrency(transaction.getCurrency());
            }
            payment.setStatus(getPaymentStatus(transaction.getStatus(), payment.getId().getType()));
            payment.setStackTrace(transaction.getStackTrace());
            payment.setError(transaction.getError());
            paymentDao.save(payment);

            txManager.commit(status);

            if (!payment.getStatus().equals(EthPayment.Status.CHECK_PAYOUT)) {
                notifyHelper.notifyStatusChanged(payment);
            }
        } catch (AppException ex) {
            ex.printStackTrace();
            txManager.rollback(status);
            log.error(this.getClass(), "Payment not found. Transaction:" + transaction, ex);
        } catch (Exception ex) {
            txManager.rollback(status);
            log.error(this.getClass(), "Can't process transaction: " + transaction, ex);
            ex.printStackTrace();
        }
    }


    private Integer getAppId(String toAddress) {
        EthAccount account = accountDao.getByAccountId(toAddress.toLowerCase());
        return account != null ? account.getId().getAppId(): null;
    }


    private int getPaymentStatus(int transactionStatus, int paymentType) {
        switch (transactionStatus) {
            case EthTransaction.Status.CREATED:
            case EthTransaction.Status.PENDING:
                return EthPayment.Status.CREATED;
            case EthTransaction.Status.ACCEPTED:
                if (paymentType == EthPayment.Type.PAYOUT) {
                    return EthPayment.Status.CHECK_PAYOUT;
                } else {
                    return EthPayment.Status.ACCEPTED;
                }
            case EthTransaction.Status.ERROR:
                return EthPayment.Status.ERROR;
            default:
                throw new RuntimeException("Unsupported transaction status: " + transactionStatus);
        }
    }
}
