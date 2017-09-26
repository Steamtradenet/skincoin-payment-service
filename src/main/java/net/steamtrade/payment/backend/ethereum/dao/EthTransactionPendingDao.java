package net.steamtrade.payment.backend.ethereum.dao;

import net.steamtrade.payment.backend.ethereum.dao.model.EthTransactionPending;

import java.util.List;

/**
 * Created by sasha on 19.09.17.
 */
public interface EthTransactionPendingDao {

    EthTransactionPending save(EthTransactionPending ethTransactionPending);

    List<EthTransactionPending> getTransactions(long limit);

    void delete(String hash);
}
