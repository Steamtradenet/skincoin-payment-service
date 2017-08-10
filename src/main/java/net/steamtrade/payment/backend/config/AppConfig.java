package net.steamtrade.payment.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sasha on 29.06.17.
 */
@Data
@Configuration
public class AppConfig {

    /**
     *  Web3 client
     */
    @Value("${web3j.client-address}")
    private String clientUrl;


    /**
     *  Admin
     */
    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    /**
     *  Ethereum service
     */
    @Value("${ethereumService.accountCreator.pool.size}")
    private int accountPoolSize;

    @Value("${ethereumService.enabledDebug}")
    private boolean enabledDebug;


    /**
     * JWT
     */
    @Value("${admin.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${admin.jwt.expires_in:86400}") // 24 hours
    private int jwtExpiresIn;

}
