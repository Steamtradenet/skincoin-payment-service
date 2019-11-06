package net.steamtrade.payment.backend.ethereum.dao;

import com.querydsl.jpa.impl.JPAQuery;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.QEthAccount;
import net.steamtrade.payment.backend.ethereum.dao.repository.EthAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by sasha on 29.06.17.
 */
@Repository
public class EthAccountDaoImpl implements EthAccountDao {

    @Autowired
    private EthAccountRepository repository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public EthAccount save(EthAccount account) {
        return repository.save(account);
    }

    @Override
    public EthAccount getByAccountId(String accountId) {
        return repository.findByAccountId(accountId);
    }

    @Override
    public EthAccount getByRequestId(int appId, String requestId) {
        QEthAccount account = QEthAccount.ethAccount;
        return repository.findOne(account.id.appId.eq(appId)
                .and(account.requestId.eq(requestId))).get();
    }

    @Override
    public EthAccount getAccountFromPool(int appId) {
        QEthAccount account = QEthAccount.ethAccount;
        JPAQuery<EthAccount> query = new JPAQuery<>(em);

        return query.from(account).where(account.id.appId.eq(appId)
                .and(account.status.eq(EthAccount.Status.NEW))).fetchFirst();
    }

    @Override
    public long getAccountCountInPool(int appId) {
        QEthAccount account = QEthAccount.ethAccount;
        JPAQuery<EthAccount> query = new JPAQuery<>(em);
        return query.from(account).where(account.id.appId.eq(appId)
                        .and(account.status.eq(EthAccount.Status.NEW))).fetchCount();
    }

    @Override
    public List<EthAccount> getAccountsPage(int page, int pageSize) {
        QEthAccount account = QEthAccount.ethAccount;
        JPAQuery<EthAccount> query = new JPAQuery<>(em);
        return query.from(account).offset((page-1)*pageSize).limit(pageSize).fetch();
    }
}
