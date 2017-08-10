package net.steamtrade.payment.backend.ethereum.dao.repository;

import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by sasha on 04.07.17.
 */
public interface EthTransactionRepository extends JpaRepository<EthTransaction, String>, QueryDslPredicateExecutor<EthTransaction> {
}
