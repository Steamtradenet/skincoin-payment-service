package net.steamtrade.payment.backend.ethereum.watch;

import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.cache.CacheManager;
import net.steamtrade.payment.backend.cache.CacheName;
import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.ethereum.config.EthereumConfig;
import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.ethereum.dao.EthAccountDao;
import net.steamtrade.payment.backend.ethereum.dao.EthFilterDao;
import net.steamtrade.payment.backend.ethereum.dao.EthTransactionDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.EthFilter;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;
import net.steamtrade.payment.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sasha on 04.07.17.
 */
public class WatchNewTransactionService {

    private static final String FILTER_NAME = "pendingTransactionFilter";

    private static final int PACKAGE_SIZE = 500;

    @Autowired
    private Web3j web3j;
    @Autowired
    private LogServiceClient log;
    @Autowired
    private EthTransactionDao transactionDao;
    @Autowired
    private EthFilterDao filterDao;
    @Autowired
    private AppDao appDao;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private EthAccountDao accountDao;
    @Autowired
    private EthereumConfig ethereumConfig;

    private BigInteger filterId = null;

    @PostConstruct
    public void initialize() {
        setUpTransactionFilter();
        warmUpCaches();
    }

    private void generateNewFilter() throws IOException {
        filterId = web3j.ethNewPendingTransactionFilter().send().getFilterId();
        filterDao.save(new EthFilter(FILTER_NAME, filterId));
    }

    public void poll() {
        try {
            EthLog changes = web3j.ethGetFilterChanges(filterId).send();
            if (changes.hasError()) {
                generateNewFilter();
                changes = web3j.ethGetFilterChanges(filterId).send();
            }
            for (EthLog.LogResult ethLog: changes.getResult()) {

                String transactionHash = (String) ethLog.get();

                web3j.ethGetTransactionByHash(transactionHash).observable().subscribe(t -> {
                    org.web3j.protocol.core.methods.response.Transaction trx = t.getResult();

                    if (trx == null) return;

                    String to = trx.getTo();

                    Currency currency = Currency.ETHER;
                    BigInteger amount = trx.getValue();
                    if (ethereumConfig.getSkincoinAddress().equalsIgnoreCase(to)) {
                        if (!StringUtils.isEmpty(trx.getInput()) && trx.getInput().startsWith("0xa9059cbb000000000000000000000000")) {
                            to = "0x"+trx.getInput().substring(34, 74);
                            amount = Numeric.decodeQuantity(Numeric.prependHexPrefix(trx.getInput().substring(trx.getInput().length() - 64)));
                        }
                        currency = Currency.SKIN;
                    }

                    if (!StringUtils.isEmpty(trx.getFrom()) && !StringUtils.isEmpty(to)) {
                        if (addressExist(trx.getFrom(), to)) {
                            log.info(this.getClass(), "Got a transaction(hash:"+trx.getHash()+", from:"+trx.getFrom()+
                                    ", to: "+trx.getTo()+", value: "+trx.getValue()+")");

                            EthTransaction transaction = new EthTransaction();
                            transaction.setHash(trx.getHash());
                            transaction.setBlockHash(trx.getBlockHash());
                            transaction.setFrom(trx.getFrom());
                            transaction.setTo(to);
                            transaction.setAmount(amount);
                            transaction.setCurrency(currency);

                            //transaction.setHoldTime(DateUtils.addMinutes(new Date(), 2));

                            if (trx.getGas() != null) {
                                transaction.setGas(trx.getGas());
                            }
                            if (trx.getGasPrice() != null) {
                                transaction.setGasPrice(trx.getGasPrice());
                            }
                            transaction.setNonce(trx.getNonce());

                            transactionDao.save(transaction);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
            }
        });

        // Warm-up ACCOUNT_CACHE
        int page = 1;
        List<EthAccount> accounts;
        do {
            accounts = accountDao.getAccountsPage(page, PACKAGE_SIZE);
            // Warm-up cache
            accounts.forEach(a -> cacheManager.addValueToCache(CacheName.ACCOUNT_CACHE,
                    a.getId().getAccountId(), a));
            page++;
        } while (accounts.size() == PACKAGE_SIZE);
    }

    private void setUpTransactionFilter() {
        EthFilter filter = filterDao.getByName(FILTER_NAME);
        if (filter == null) {
            try {
                generateNewFilter();
                log.info(this.getClass(), "PendingTransactionFilter(id: "+filterId +") was created.");

                debug("pendingTransactionFilter Id: " + filterId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            filterId = filter.getValue();
            debug("Use filter Id: " + filterId);
        }
    }

    private String trxToString(org.web3j.protocol.core.methods.response.Transaction trx) {
        StringBuilder sb = new StringBuilder();
        sb.append("hash:"); sb.append(trx.getHash());
        sb.append(",block_hash:"); sb.append(trx.getBlockHash());
        sb.append(",creates:"); sb.append(trx.getCreates());
        sb.append(",nonce:"); sb.append(trx.getNonce());
        sb.append(",index:"); sb.append(trx.getTransactionIndex());

        sb.append(",from:"); sb.append(trx.getFrom());
        sb.append(",to:"); sb.append(trx.getTo());
        sb.append(",value:"); sb.append(trx.getValue());

        sb.append(",gas:"); sb.append(trx.getGas());
        sb.append(",gas_price:"); sb.append(trx.getGasPrice());

        return sb.toString();
    }

    public void debug(String msg) {
        System.out.println(msg);
    }
}
