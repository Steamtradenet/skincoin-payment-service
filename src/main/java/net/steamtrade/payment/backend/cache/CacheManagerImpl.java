package net.steamtrade.payment.backend.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

/**
 * Created by sasha on 04.12.15.
 */
@Service
public class CacheManagerImpl implements CacheManager {

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;

    @Override
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.clear();
    }

    @Override
    public void addValueToCache(String cacheName, String key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.put(key, value);
    }

    @Override
    public <T> T  getValueFromCache(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper valueWrapper = cache.get(key);
        return valueWrapper != null ? (T)valueWrapper.get(): null;
    }
}
