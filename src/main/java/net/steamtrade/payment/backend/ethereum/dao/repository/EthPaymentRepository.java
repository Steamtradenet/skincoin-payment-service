package net.steamtrade.payment.backend.ethereum.dao.repository;

import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthPaymentPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by sasha on 29.06.17.
 */
public interface EthPaymentRepository extends JpaRepository<EthPayment, EthPaymentPK>, QuerydslPredicateExecutor<EthPayment> {

}
