package net.steamtrade.payment.backend.ethereum.watch.logic;

import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.cache.CacheManager;
import net.steamtrade.payment.backend.cache.CacheName;
import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.ethereum.dao.EthAccountDao;
import net.steamtrade.payment.backend.ethereum.dao.EthTransactionDao;
import net.steamtrade.payment.backend.ethereum.dao.EthTransactionPendingDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransactionPending;
import net.steamtrade.payment.backend.utils.DateUtils;
import net.steamtrade.payment.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by sasha on 19.09.17.
 */
@Component
public class TransactionAnalyser {

    private static final int PACKAGE_SIZE = 500;

    @Autowired
    private Web3j web3j;
    @Autowired
    private LogServiceClient log;
    @Autowired
    private AppDao appDao;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private EthAccountDao accountDao;
    @Autowired
    private EthTransactionPendingDao transactionPendingDao;
    @Autowired
    private EthTransactionDao transactionDao;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private JpaTransactionManager txManager;

    @PostConstruct
    public void initialize() {
        warmUpCaches();
        log.info(this.getClass(), "TransactionAnalyser was initialized");
    }

    public void process(List<EthTransactionPending> pendingTransactions) {
        for (EthTransactionPending pendingTransaction: pendingTransactions) {
            boolean processed = false;
            try {
                processed = processPendingTransaction(pendingTransaction.getHash());
            } catch (Exception ex) {
                log.warn(this.getClass(), "Can't process transaction(hash:"+pendingTransaction.getHash()+")", ex);
            } finally {
                if (!processed) {
                    pendingTransaction.setLocked(false);
                    transactionPendingDao.save(pendingTransaction);
                }
            }
        }
    }

    private boolean processPendingTransaction(String transactionHash) throws IOException {
        Transaction trx = web3j.ethGetTransactionByHash(transactionHash).send().getResult();
        if (trx != null) {
            String to = trx.getTo();

            Currency currency = Currency.ETHER;
            BigInteger amount = trx.getValue();
            if (appConfig.getSkincoinAddress().equalsIgnoreCase(to)) {
                if (!StringUtils.isEmpty(trx.getInput()) && trx.getInput().startsWith("0xa9059cbb000000000000000000000000")) {
                    to = "0x"+trx.getInput().substring(34, 74);
                    amount = Numeric.decodeQuantity(Numeric.prependHexPrefix(trx.getInput().substring(trx.getInput().length() - 64)));
                }
                currency = Currency.SKIN;
            }

            if (!StringUtils.isEmpty(trx.getFrom()) && !StringUtils.isEmpty(to)) {
                if (addressExist(trx.getFrom(), to)) {
                    log.info(this.getClass(), "Got a transaction(hash:" + trx.getHash() + ", from:" + trx.getFrom() +
                            ", to: " + trx.getTo() + ", value: " + amount + ")");

                    EthTransaction ethTransaction = transactionDao.getByHash(trx.getHash());
                    if (ethTransaction == null) {
                        TransactionStatus status = txManager.getTransaction(
                                new DefaultTransactionDefinition());
                        try {
                            EthTransaction transaction = new EthTransaction();
                            transaction.setHash(trx.getHash());
                            transaction.setBlockHash(trx.getBlockHash());
                            transaction.setFrom(trx.getFrom());
                            transaction.setTo(to);
                            transaction.setAmount(amount);
                            transaction.setCurrency(currency);

                            transaction.setHoldTime(DateUtils.addMinutes(new Date(), 2));

                            if (trx.getGas() != null) {
                                transaction.setGas(trx.getGas());
                            }
                            if (trx.getGasPrice() != null) {
                                transaction.setGasPrice(trx.getGasPrice());
                            }
                            transaction.setNonce(trx.getNonce());
                            transactionDao.save(transaction);

                            transactionPendingDao.delete(transactionHash);
                            txManager.commit(status);

                            return true;
                        } catch (Exception ex) {
                            log.error(this.getClass(), "Can't save new transaction(hash:" + transactionHash + ")", ex);
                            txManager.rollback(status);
                            return false;
                        }
                    } else {
                        log.warn(this.getClass(), "Not a new transaction(hash:" + trx.getHash() + ", from:" + trx.getFrom() +
                                ", to: " + trx.getTo() + ", value: " + amount + ")");
                    }
                }
            }
        }

        transactionPendingDao.delete(transactionHash);
        return true;
    }

    private boolean addressExist(String... address) {
        for (String a: address) {
            Object obj = cacheManager.getValueFromCache(CacheName.APP_ADDRESS_CACHE, a.toLowerCase());
            if (obj != null) {
                return true;
            }
            EthAccount account = cacheManager.getValueFromCache(CacheName.ACCOUNT_CACHE, a.toLowerCase());
            if (account != null) {
                return true;
            }
        }
        return false;
    }

    private void warmUpCaches() {
        // Collect all applications addresses
        appDao.getAll().forEach(a -> {
            if (!StringUtils.isEmpty(a.getFromAddress())) {
                cacheManager.addValueToCache(CacheName.APP_ADDRESS_CACHE, a.getFromAddress().toLowerCase(), 1);
                log.info(this.getClass(), "Add to cache: " + a.getFromAddress().toLowerCase());
            }
        });

        // Warm-up ACCOUNT_CACHE
        int page = 1;
        List<EthAccount> accounts;
        do {
            accounts = accountDao.getAccountsPage(page, PACKAGE_SIZE);
            log.info(this.getClass(), "Add to cache: " + accounts.size() + " accounts");

            // Warm-up cache
            accounts.forEach(a -> cacheManager.addValueToCache(CacheName.ACCOUNT_CACHE,
                    a.getId().getAccountId(), a));
            page++;
        } while (accounts.size() == PACKAGE_SIZE);
    }
}
