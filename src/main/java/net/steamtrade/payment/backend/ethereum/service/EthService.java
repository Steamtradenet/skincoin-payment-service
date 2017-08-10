package net.steamtrade.payment.backend.ethereum.service;

import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.json.EthNetworkJson;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigInteger;

/**
 * Created by sasha on 29.06.17.
 */
public interface EthService {

    String getClientVersion() throws AppException;

    EthNetworkJson getEthNetwork() throws AppException;

    BigInteger getBalance(String accountId, Currency currency) throws AppException;

    BigInteger getGasPrice() throws AppException;

    BigInteger estimateGasLimit(Transaction transaction) throws AppException;

    BigInteger getNonce(String address) throws AppException;

    String createSkinTransaction(String fromAddress, String fromPassword, String toAddress,
                                 BigInteger amount, BigInteger gasLimit, BigInteger gasPrice) throws AppException;

    String createEtherTransaction(String from, String fromPassword, String toAddress,
                                  BigInteger amount, BigInteger gasLimit, BigInteger gasPrice) throws AppException;
}
