package net.steamtrade.payment.backend.ethereum.watch;

import net.steamtrade.payment.backend.ethereum.dao.EthTransactionDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;
import net.steamtrade.payment.backend.ethereum.watch.logic.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by sasha on 04.07.17.
 */
public class WatchPendingTransactionService {


    private static final int PACKAGE_SIZE = 200;

    @Autowired
    private EthTransactionDao transactionDao;
    @Autowired
    private JpaTransactionManager txManager;

    @Autowired
    private TransactionProcessor transactionProcessor;

    private final TaskExecutor taskExecutor;


    public WatchPendingTransactionService(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void initialize() {
        TransactionStatus status = txManager.getTransaction(
                new DefaultTransactionDefinition());
        try {
            transactionDao.unlockAll(); // Unlock all transactions
            txManager.commit(status);
        } catch (Exception ex) {
            txManager.rollback(status);
            throw new RuntimeException(ex);
        }
    }

    public void poll() {
        List<EthTransaction> pending = transactionDao.getPending(PACKAGE_SIZE);
        for (EthTransaction transaction: pending) {
            transaction.setLocked(true);
            transactionDao.save(transaction);
        }

        if (pending.size() > 0) {
            taskExecutor.execute(() -> transactionProcessor.process(pending));
        }
    }


}
