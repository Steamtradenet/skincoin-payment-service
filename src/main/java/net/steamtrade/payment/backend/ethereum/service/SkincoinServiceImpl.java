package net.steamtrade.payment.backend.ethereum.service;

import com.hazelcast.core.HazelcastInstance;
import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.ethereum.config.EthereumConfig;
import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.ethereum.dao.EthPaymentDao;
import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthPaymentPK;
import net.steamtrade.payment.backend.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  net.steamtrade.payment.backend.exceptions.Error;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;

import static net.steamtrade.payment.backend.ethereum.dao.model.EthPayment.Type.*;


/**
 * Created by sasha on 05.07.17.
 */
@Service
public class SkincoinServiceImpl implements SkincoinService {

    private static final String CREATE_TRANSACTION_LOCK_PREFIX = "SkincoinServiceImpl.createTxn_";

    @Autowired
    private EthereumConfig ethereumConfig;
    @Autowired
    private EthPaymentDao paymentDao;
    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Autowired
    private EthService ethService;
    @Autowired
    private AppDao appDao;

    @Override
    public EthPayment createPayout(int appId, String requestId, String toAddress, BigInteger amount, Currency currency,
                                   BigInteger gasLimit, BigInteger gasPrice) throws AppException {

        // If transaction exists just get it back
        EthPayment payment = paymentDao.getPayment(appId, PAYOUT, requestId);
        if (payment != null) {
            return payment;
        }

        if (gasPrice == null) {
            gasPrice = ethService.getGasPrice(); // Calculate current gasPrice in Ethereum
            if (gasPrice.compareTo(ethereumConfig.getGasPriceLimit()) == 1) {
                throw new AppException(Error.GAS_PRICE_OVERLIMIT, "Estimated gas price("+gasPrice+") is too expensive. Limit is " + ethereumConfig.getGasPriceLimit());
            }
        }

        App app = appDao.getAppById(appId);

        // Lock address
        Lock lock = hazelcastInstance.getLock(CREATE_TRANSACTION_LOCK_PREFIX + app.getFromAddress());
        lock.lock();
        try {
            // Check money in balance
            BigInteger appBalance = ethService.getBalance(app.getFromAddress(), currency);
            if (appBalance.compareTo(amount) == -1) {
                throw new AppException(Error.INSUFFICIENT_AMOUNT, "Insufficient amount(appId:"+app+",from:"+app.getFromAddress()+",balance:"+appBalance+"). Need " + amount);
            }

            payment = new EthPayment();
            payment.setId(new EthPaymentPK(appId, PAYOUT, requestId));

            String hash;
            switch (currency) {
                case ETHER:
                    hash = ethService.createEtherTransaction(app.getFromAddress(), app.getFromPassword(), toAddress, amount, gasLimit, gasPrice);
                    break;
                case SKIN:
                    hash = ethService.createSkinTransaction(app.getFromAddress(), app.getFromPassword(), toAddress, amount, gasLimit, gasPrice);
                    break;
                default:
                    throw new RuntimeException("Unsupported currency " + currency.getName());
            }

            payment.setHash(hash);
            payment.setStatus(EthPayment.Status.CREATED);

            payment.setFrom(app.getFromAddress());
            payment.setTo(toAddress);

            payment.setAmount(amount);
            payment.setCurrency(currency);

            return paymentDao.save(payment);

        } finally {
            lock.unlock();
        }
    }


    @Override
    public EthPayment getPayment(int appId, int type, String requestId) throws AppException {
        EthPayment payment = paymentDao.getPayment(appId, type, requestId);
        if (payment == null) {
            throw new AppException(Error.INCORRECT_PAYMENT, "Payment(appId:"+appId+",requestId:"+requestId+") doesn't exist");
        }
        return payment;
    }
}
