package net.steamtrade.payment.backend.ethereum.watch;

import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.ethereum.dao.EthFilterDao;
import net.steamtrade.payment.backend.ethereum.dao.EthTransactionPendingDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthFilter;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransactionPending;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthLog;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by sasha on 04.07.17.
 */
public class WatchNewTransactionService {

    private static final String FILTER_NAME = "pendingTransactionFilter";

    @Autowired
    private Web3j web3j;
    @Autowired
    private LogServiceClient log;
    @Autowired
    private EthFilterDao filterDao;
    @Autowired
    private EthTransactionPendingDao transactionPendingDao;
    @Autowired
    private AppConfig appConfig;

    private BigInteger filterId = null;

    @PostConstruct
    public void initialize() {
        setUpTransactionFilter();
        log.info(this.getClass(), "WatchNewTransactionService was initialized");
    }

    private void generateNewFilter() throws IOException {
        filterId = web3j.ethNewPendingTransactionFilter().send().getFilterId();
        filterDao.save(new EthFilter(FILTER_NAME, filterId));
        log.info(this.getClass(), "New pending transaction filter(id:"+filterId+") was created");
    }

    public void poll() {
        if (appConfig.isDisabled()) {
            log.info(this.getClass(), "WatchNewTransactionService is DISABLED");
            return;
        }
        try {
            EthLog changes = web3j.ethGetFilterChanges(filterId).send();

            if (changes.hasError()) {
                log.warn(this.getClass(), "Can't get changes: " + changes.getError().getMessage()); ;
                generateNewFilter();
                changes = web3j.ethGetFilterChanges(filterId).send();
            }
            for (EthLog.LogResult ethLog: changes.getResult()) {

                String transactionHash = (String) ethLog.get();
                transactionPendingDao.save(new EthTransactionPending(transactionHash));
            }
        } catch (Exception ex) {
            log.error(this.getClass(), "Can't poll pending transactions. ", ex);
        }
    }

    private void setUpTransactionFilter() {
        EthFilter filter = filterDao.getByName(FILTER_NAME);
        if (filter == null) {
            try {
                generateNewFilter();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            filterId = filter.getValue();
        }
    }
}
