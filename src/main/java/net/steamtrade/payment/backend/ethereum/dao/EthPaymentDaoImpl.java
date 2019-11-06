package net.steamtrade.payment.backend.ethereum.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.ethereum.dao.model.QEthPayment;
import net.steamtrade.payment.backend.ethereum.dao.repository.EthPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by sasha on 29.06.17.
 */
@Repository
public class EthPaymentDaoImpl implements EthPaymentDao {

    @Autowired
    private EthPaymentRepository repository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public EthPayment save(EthPayment payment) {
        return repository.save(payment);
    }

    @Override
    public EthPayment getPayment(int appId, int type, String requestId) {
        QEthPayment payment = QEthPayment.ethPayment;
        return repository.findOne(payment.id.appId.eq(appId)
                .and(payment.id.type.eq(type))
                .and(payment.id.requestId.eq(requestId))).get();
    }

    @Override
    public EthPayment getPayment(String hash, boolean forUpdate) {
        QEthPayment payment = QEthPayment.ethPayment;
        JPAQuery<EthPayment> query = new JPAQuery<>(em);
        query.from(payment).where(payment.hash.eq(hash));
        if (forUpdate) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return query.fetchOne();
    }

    @Override
    public List<EthPayment> getPayoutsToCheck(long limit) {
        QEthPayment payment = QEthPayment.ethPayment;

        BooleanBuilder where = new BooleanBuilder();
        where.and(payment.id.type.eq(EthPayment.Type.PAYOUT))
                .and(payment.status.eq(EthPayment.Status.CHECK_PAYOUT));

        return new JPAQuery<EthPayment>(em).from(payment).where(where).limit(limit).fetch();
    }
}
