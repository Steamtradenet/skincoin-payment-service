package net.steamtrade.payment.backend.ethereum.dao;

import net.steamtrade.payment.backend.cache.CacheName;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

/**
 * Created by sasha on 29.06.17.
 */
public interface EthPaymentDao {

    @Caching(put = {
            @CachePut(value = CacheName.PAYMENT_CACHE, key = "#payment.id.appId"),
            @CachePut(value = CacheName.PAYMENT_CACHE, key = "'app_id:'+#payment.id.appId+',type:'+#payment.id.type+',request_id:'+#payment.id.requestId")
    })
    EthPayment save(EthPayment payment);

    @Cacheable(value = CacheName.PAYMENT_CACHE, key = "'app_id:'+#appId+',type:'+#type+',request_id:'+#requestId")
    EthPayment getPayment(int appId, int type, String requestId);

    EthPayment getPayment(String hash, boolean forUpdate);
}