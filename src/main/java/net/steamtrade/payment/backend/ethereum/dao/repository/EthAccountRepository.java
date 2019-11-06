package net.steamtrade.payment.backend.ethereum.dao.repository;

import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthAccountPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by sasha on 29.06.17.
 */
public interface EthAccountRepository extends JpaRepository<EthAccount, EthAccountPK>, QuerydslPredicateExecutor<EthAccount> {

    @Query("select a from EthAccount a where a.id.accountId = ?1")
    EthAccount findByAccountId(String accountId);

}
