package net.steamtrade.payment.backend.ethereum.dao.repository;

import net.steamtrade.payment.backend.ethereum.dao.model.EthFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by sasha on 04.07.17.
 */
public interface EthFilterRepository extends JpaRepository<EthFilter, String>, QuerydslPredicateExecutor<EthFilter> {
}
