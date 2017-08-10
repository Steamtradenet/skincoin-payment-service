package net.steamtrade.payment.backend.ethereum.dao;

import net.steamtrade.payment.backend.ethereum.dao.model.EthFilter;

/**
 * Created by sasha on 04.07.17.
 */
public interface EthFilterDao {

    EthFilter save(EthFilter filter);

    EthFilter getByName(String name);

}