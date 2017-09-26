package net.steamtrade.payment.backend.ethereum.watch;

import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.ethereum.dao.EthTransactionPendingDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransactionPending;
import net.steamtrade.payment.backend.ethereum.watch.logic.TransactionAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by sasha on 19.09.17.
 */
public class NewTransactionAnalyser {

    private static final int PACKAGE_SIZE = 100;

    @Autowired
    private LogServiceClient log;
    @Autowired
    private EthTransactionPendingDao transactionPendingDao;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private TransactionAnalyser transactionAnalyser;

    private final TaskExecutor taskExecutor;

    public NewTransactionAnalyser(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void initialize() {
        log.info(this.getClass(), "PendingTransactionAnalyser was initialized");
    }

    public void poll() {
        if (appConfig.isDisabled()) {
            log.info(this.getClass(), "PendingTransactionAnalyser is DISABLED");
            return;
        }
        List<EthTransactionPending> pendingTransactions = transactionPendingDao.getTransactions(PACKAGE_SIZE);
        for (EthTransactionPending transaction: pendingTransactions) {
            transaction.setLocked(true);
            transactionPendingDao.save(transaction);
        }

        if (pendingTransactions.size() > 0) {
            taskExecutor.execute(() -> transactionAnalyser.process(pendingTransactions));
        }
    }
}
