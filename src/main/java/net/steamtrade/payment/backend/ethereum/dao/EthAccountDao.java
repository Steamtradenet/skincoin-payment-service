package net.steamtrade.payment.backend.ethereum.dao;

import net.steamtrade.payment.backend.cache.CacheName;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

/**
 * Created by sasha on 29.06.17.
 */
public interface EthAccountDao {

    @Caching(put = {
            @CachePut(value = CacheName.ACCOUNT_CACHE, key = "#account.id.accountId"),
            @CachePut(value = CacheName.ACCOUNT_CACHE, key = "'appId:'+#account.id.appId+',requestId:'+#requestId")
    })
    EthAccount save(EthAccount account);

    @Cacheable(value = CacheName.ACCOUNT_CACHE, key = "#accountId")
    EthAccount getByAccountId(String accountId);

    EthAccount getByRequestId(int appId, String requestId);

    EthAccount getAccountFromPool(int appId);

    long getAccountCountInPool(int appId);

    List<EthAccount> getAccountsPage(int page, int pageSize);
}