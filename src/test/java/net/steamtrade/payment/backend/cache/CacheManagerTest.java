package net.steamtrade.payment.backend.cache;

import net.steamtrade.payment.Application;
import net.steamtrade.payment.backend.admin.dao.model.App;
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


//    @Test
    public void addAppTest() {


        cacheManager.clearCache(CacheName.APP_CACHE);

        App app = new App();
        app.setId(2);
        app.setToken("123");
        app.setCallbackUrl("http://localhost:8989");
        app.setEnableCallback(true);
        app.setDescription("test1");
        app.setFromAddress("asdasdas");
        app.setFromPassword("www");

        appDao.save(app);

        App fromCache = cacheManager.getValueFromCache(CacheName.APP_CACHE, "2");


        App app1 = appDao.getAppById(2);





        assertNotNull(fromCache);
//        assertEquals(key.toLowerCase(), fromCache.getId().getAccountId());
    }


}
