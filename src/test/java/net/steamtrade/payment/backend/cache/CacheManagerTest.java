package net.steamtrade.payment.backend.cache;

import net.steamtrade.payment.Application;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthAccountPK;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by sasha on 02.08.17.
 */
public class CacheManagerTest extends Application {

//    @Test
    public void warmUpCacheTest() {

        String key = "0xbfdd89fc7e1166d332a91c96ce7f69380d10b70e";

        cacheManager.clearCache(CacheName.ACCOUNT_CACHE);
        EthAccount fromCache = cacheManager.getValueFromCache(CacheName.ACCOUNT_CACHE, key);
        assertNull(fromCache);

        EthAccount account = new EthAccount();
        account.setId(new EthAccountPK(1, key.toLowerCase()));

        cacheManager.addValueToCache(CacheName.ACCOUNT_CACHE, key, account);
        fromCache = cacheManager.getValueFromCache(CacheName.ACCOUNT_CACHE, key);

        assertNotNull(fromCache);
        assertEquals(key.toLowerCase(), fromCache.getId().getAccountId());
    }


}
