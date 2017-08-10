package net.steamtrade.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by sasha on 29.06.17.
 */
@EnableCaching
@SpringBootApplication
@ImportResource({"classpath:spring/scheduling-beans.xml"})
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}
