package net.steamtrade.payment.web.ethereum.handler;

import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.ethereum.service.EthService;
import net.steamtrade.payment.backend.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by sasha on 30.06.17.
 */
@Component
public class EthControllerHandler {

    @Autowired
    private EthService ethService;


    public BigInteger getBalance(Map<String, String[]> filter) throws AppException {
        Currency currency = Currency.ETHER;
        String address = null;
        if (filter.get("currency") != null) {
            currency = Currency.fromName(filter.get("currency")[0]);
        }

        if (filter.get("address") != null) {
            address = filter.get("address")[0];
        }
        return ethService.getBalance(address, currency);
    }
}
