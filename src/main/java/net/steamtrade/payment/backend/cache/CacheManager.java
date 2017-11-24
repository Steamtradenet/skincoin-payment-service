package net.steamtrade.payment.backend.cache;

/**
 * Created by sasha on 04.12.15.
 */
public interface CacheManager {

    void clearCache(String cacheName);

    void addValueToCache(String cacheName, Object key, Object value);

    <T> T getValueFromCache(String cacheName, Object key);

}
