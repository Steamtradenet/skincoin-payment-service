package net.steamtrade.payment.backend.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Created by sasha on 5/15/17.
 */
@Component
public class LogServiceClient {

    public void info(Class<?> clazz, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info(message);
    }

    public void info(Class<?> clazz, String appender, String message) {
        Logger logger = LoggerFactory.getLogger(appender);
        logger.info(message);
    }


    public void warn(Class<?> clazz, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.warn(message);
    }

    public void error(Class<?> clazz, String message, Throwable t) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(message, t);
    }

    public void error(Class<?> clazz, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(message);
    }

}
