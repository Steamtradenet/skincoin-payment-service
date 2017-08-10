package net.steamtrade.payment.backend.admin.dao;

import net.steamtrade.payment.backend.cache.CacheName;
import net.steamtrade.payment.backend.admin.dao.model.App;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

/**
 * Created by sasha on 29.06.17.
 */
public interface AppDao {

    @Caching(put = {
            @CachePut(value = CacheName.APP_CACHE, key = "#app.id"),
            @CachePut(value = CacheName.APP_CACHE, key = "'token:'+#app.token")
    }, evict = {
            @CacheEvict(value = CacheName.APP_CACHE, key = "'all'")
    })
    App save(App app);

    @Cacheable(value = CacheName.APP_CACHE, key = "#id")
    App getAppById(int id);

    @Cacheable(value = CacheName.APP_CACHE, key = "'token:'+#token")
    App getAppByToken(String token);

    @Cacheable(value = CacheName.APP_CACHE, key = "'all'")
    List<App> getAll();
}
