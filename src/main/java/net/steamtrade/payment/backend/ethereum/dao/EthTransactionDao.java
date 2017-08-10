package net.steamtrade.payment.backend.ethereum.dao;

import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;

import java.util.List;

/**
 * Created by sasha on 04.07.17.
 */
public interface EthTransactionDao {

    EthTransaction save(EthTransaction transaction);

    EthTransaction getByHash(String hash);

    List<EthTransaction> getPending(int limit);

    void unlockAll();

}