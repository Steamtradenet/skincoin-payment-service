package net.steamtrade.payment.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

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
    @Value("${ethereumService.disabled:false}")
    private boolean disabled;

    @Value("${ethereumService.enabledDebug:true}")
    private boolean enabledDebug;

    @Value("${ethereumService.gasLimit:90000}") // 90000 - Default value from mist and geth
    private int gasLimit;

    @Value("${ethereumService.gasPriceLimit:46000000000}") // 46 gwei
    private BigInteger gasPriceLimit;

    @Value("${ethereumService.confirmationCount: 30}")
    private BigInteger confirmationCount;

    @Value("${ethereumService.accountCreator.pool.size}")
    private int accountPoolSize;

    @Value("${ethereumService.skincoin.deployed-address}")
    private String skincoinAddress;

    /**
     * JWT
     */
    @Value("${admin.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${admin.jwt.expires_in:86400}") // 24 hours
    private int jwtExpiresIn;

}
