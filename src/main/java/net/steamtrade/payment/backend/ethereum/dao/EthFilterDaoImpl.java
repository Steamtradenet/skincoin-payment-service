package net.steamtrade.payment.backend.ethereum.dao;

import net.steamtrade.payment.backend.ethereum.dao.model.EthFilter;
import net.steamtrade.payment.backend.ethereum.dao.repository.EthFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by sasha on 04.07.17.
 */
@Repository
public class EthFilterDaoImpl implements EthFilterDao {

    @Autowired
    private EthFilterRepository repository;

    @Override
    public EthFilter save(EthFilter filter) {
        return repository.save(filter);
    }

    @Override
    public EthFilter getByName(String name) {
        return repository.findOne(name);
    }
}
