package net.steamtrade.payment.backend.ethereum.dao;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;
import net.steamtrade.payment.backend.ethereum.dao.model.QEthTransaction;
import net.steamtrade.payment.backend.ethereum.dao.repository.EthTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by sasha on 04.07.17.
 */
@Repository
public class EthTransactionDaoImpl implements EthTransactionDao {

    @Autowired
    private EthTransactionRepository repository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public EthTransaction save(EthTransaction transaction) {
        return repository.save(transaction);
    }

    @Override
    public EthTransaction getByHash(String hash) {
        return repository.findOne(hash);
    }

    @Override
    public List<EthTransaction> getPending(int limit) {
        QEthTransaction trx = QEthTransaction.ethTransaction;
        JPAQuery<EthTransaction> query = new JPAQuery<>(em);
        return query.from(trx).where(
                trx.status.in(EthTransaction.Status.PENDING, EthTransaction.Status.CREATED)
                        .and(trx.holdTime.isNull().or(trx.holdTime.loe(new Date()))).and(trx.locked.eq(false))
        ).orderBy(trx.updateTime.asc()).limit(limit).fetch();
    }

    @Override
    public void unlockAll() {
        QEthTransaction trx = QEthTransaction.ethTransaction;
        new JPAUpdateClause(em, trx).where(trx.locked.eq(true)).set(trx.locked, false).execute();
    }
}
