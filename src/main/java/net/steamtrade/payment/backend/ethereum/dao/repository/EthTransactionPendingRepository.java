package net.steamtrade.payment.backend.ethereum.dao.repository;

import net.steamtrade.payment.backend.ethereum.dao.model.EthTransactionPending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by sasha on 04.07.17.
 */
public interface EthTransactionPendingRepository extends JpaRepository<EthTransactionPending, String>,
        QuerydslPredicateExecutor<EthTransactionPending> {
}
