package net.steamtrade.payment.backend.ethereum.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * Created by sasha on 20.07.17.
 */
@Data
@Component
public class EthereumConfig {

    @Value("${ethereumService.disabled:false}")
    private boolean disabled;

    @Value("${ethereumService.gasLimit:90000}") // 90000 - Default value from mist and geth
    private int gasLimit;

    @Value("${ethereumService.gasPriceLimit:21000000000}") // 21 gwei
    private BigInteger gasPriceLimit;

    @Value("${skincoin.deployed-address:0xeff2ec01e96f340b47bff8d0a5ec6fc77fcb8a42}")
    private String skincoinAddress;
}
