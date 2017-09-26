package net.steamtrade.payment;

import net.steamtrade.payment.backend.cache.CacheManager;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * Created by sasha on 02.08.17.
 */
//@RunWith(SpringRunner.class)
//@ActiveProfiles("default")
//@SpringBootTest(classes = PaymentApplication.class)
//@WebAppConfiguration
public class Application {

    @Autowired
    protected CacheManager cacheManager;

}
