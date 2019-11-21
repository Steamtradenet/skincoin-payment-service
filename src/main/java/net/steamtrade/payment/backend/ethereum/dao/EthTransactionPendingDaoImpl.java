package net.steamtrade.payment.backend.ethereum.dao;

import com.querydsl.jpa.impl.JPAQuery;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransactionPending;
import net.steamtrade.payment.backend.ethereum.dao.model.QEthTransactionPending;
import net.steamtrade.payment.backend.ethereum.dao.repository.EthTransactionPendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by sasha on 19.09.17.
 */
@Repository
public class EthTransactionPendingDaoImpl implements EthTransactionPendingDao {

    @Autowired
    private EthTransactionPendingRepository repository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public EthTransactionPending save(EthTransactionPending ethTransactionPending) {
        return repository.save(ethTransactionPending);
    }

    @Override
    public List<EthTransactionPending> getTransactions(long limit) {
        QEthTransactionPending transactionPending = QEthTransactionPending.ethTransactionPending;
        return new JPAQuery<EthTransactionPending>(em).from(transactionPending).limit(limit).fetch();
    }

    @Override
    public void delete(String hash) {
        repository.deleteById(hash);
    }
}
