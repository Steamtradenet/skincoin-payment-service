package net.steamtrade.payment.backend.ethereum.watch;

import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.ethereum.dao.EthAccountDao;
import net.steamtrade.payment.backend.ethereum.dao.EthPaymentDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthPaymentPK;
import net.steamtrade.payment.backend.ethereum.utils.NotifyHelper;
import net.steamtrade.payment.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.UUID;

/**
 * Created by sasha on 26.09.17.
 */
public class WatchInternalPayoutsService {


    private static final long PACKAGE_SIZE = 100;

    @Autowired
    private JpaTransactionManager txManager;
    @Autowired
    private EthPaymentDao paymentDao;
    @Autowired
    private EthAccountDao accountDao;
    @Autowired
    private NotifyHelper notifyHelper;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private LogServiceClient log;

    public void poll() {
        if (appConfig.isDisabled()) {
            log.info(this.getClass(), "WatchInternalPayoutsService is DISABLED");
            return;
        }

        while(processPayouts()) {
            if (appConfig.isDisabled()) {
                log.info(this.getClass(), "WatchInternalPayoutsService is DISABLED");
                break;
            }
        }
    }

    private boolean processPayouts() {
        List<EthPayment> payouts = paymentDao.getPayoutsToCheck(PACKAGE_SIZE);
        for (EthPayment payout: payouts) {
            EthAccount account = accountDao.getByAccountId(payout.getTo().toLowerCase());
            if (account != null && StringUtils.isEmpty(payout.getPaymentRequestId())) {

                TransactionStatus status = txManager.getTransaction(
                        new DefaultTransactionDefinition());
                try {
                    // Internal payout
                    EthPayment payment = new EthPayment();
                    payment.setId(new EthPaymentPK(account.getId().getAppId(), EthPayment.Type.PAYMENT,
                            UUID.randomUUID().toString()));
                    payment.setAmount(payout.getAmount());
                    payment.setFrom(payout.getFrom());
                    payment.setTo(payout.getTo());
                    payment.setCurrency(payout.getCurrency());
                    payment.setHash(payout.getHash());
                    payment.setStatus(EthPayment.Status.ACCEPTED);

                    paymentDao.save(payment);

                    payout.setPaymentRequestId(payment.getId().getRequestId());
                    payout.setStatus(EthPayment.Status.ACCEPTED);
                    paymentDao.save(payout);

                    txManager.commit(status);

                    notifyHelper.notifyStatusChanged(payment);
                    notifyHelper.notifyStatusChanged(payout);

                } catch (Exception ex) {
                    txManager.rollback(status);
                    log.error(this.getClass(), "Can't process payout(requestId:"+payout.getId().getRequestId()+")", ex);
                }
            } else {
                payout.setStatus(EthPayment.Status.ACCEPTED);
                paymentDao.save(payout);

                notifyHelper.notifyStatusChanged(payout);
            }
        }
        return PACKAGE_SIZE == payouts.size();
    }
}
