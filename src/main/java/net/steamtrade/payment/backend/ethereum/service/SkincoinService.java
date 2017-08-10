package net.steamtrade.payment.backend.ethereum.service;

import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.exceptions.AppException;

import java.math.BigInteger;

/**
 * Created by sasha on 29.06.17.
 */
public interface SkincoinService {


    EthPayment createPayout(int appId, String requestId, String toAddress, BigInteger amount, Currency currency,
                            BigInteger gasLimit, BigInteger gasPrice) throws AppException;

    EthPayment getPayment(int appId, int type, String requestId) throws AppException;
}
