package net.steamtrade.payment.backend.ethereum.service;

import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.ethereum.config.EthereumConfig;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.json.EthNetworkJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;
import net.steamtrade.payment.backend.exceptions.Error;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sasha on 29.06.17.
 */
@Service
public class EthServiceImpl implements EthService {

    private static final String MAIN_NET = "0xd4e56740f876aef8c010b86a40d5f56745a118d0906a34e69aec8c0db1cb8fa3";
    private static final String TEST_NET = "0x41941023680923e0fe4d74a34bdac8141f2540e3ae90623718e47d66d1ca4a2d";

    @Autowired
    private Web3j web3j;
    @Autowired
    private EthereumConfig ethereumConfig;
    @Autowired
    private AppConfig appConfig;

    @Override
    public BigInteger getBalance(String address, Currency currency) throws AppException {
        switch (currency) {
            case ETHER:
                return getEthBalance(address);
            case SKIN:
                return getSkinBalance(address);
            default:
                throw new RuntimeException("Unsupported currency " + currency.getName());
        }
    }

    @Override
    public BigInteger getNonce(String address) throws AppException {
        try {
            EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(address,
                    DefaultBlockParameterName.LATEST).send();
            return transactionCount.getTransactionCount();
        } catch (Exception ex) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Ethereum net is unavailable", ex);
        }
    }

    @Override
    public BigInteger getGasPrice() throws AppException {
        try {
            EthGasPrice gasPrice = web3j.ethGasPrice().send();
            return gasPrice.getGasPrice();
        } catch (Exception ex) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Ethereum net is unavailable", ex);
        }
    }

    @Override
    public BigInteger estimateGasLimit(org.web3j.protocol.core.methods.request.
                                                Transaction transaction) throws AppException {
        try {
            EthEstimateGas estimateGas = web3j.ethEstimateGas(transaction).send();
            return estimateGas.getAmountUsed();
        } catch (Exception ex) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Ethereum net is unavailable", ex);
        }
    }

    @Override
    public String createSkinTransaction(String fromAddress, String fromPassword, String toAddress,
                                         BigInteger amount, BigInteger gasLimit, BigInteger gasPrice) throws AppException {

        gasLimit = gasLimit == null ? BigInteger.valueOf(ethereumConfig.getGasLimit()): gasLimit;

        Function function = new Function("transfer", Arrays.<Type>asList(new Address(toAddress), new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        // Prepare transaction
        Transaction transaction = Transaction.
                createFunctionCallTransaction(fromAddress, null, gasPrice, gasLimit,
                        ethereumConfig.getSkincoinAddress(), encodedFunction);

        // Estimate and check gasLimit
        BigInteger estimatedGasLimit = estimateGasLimit(transaction);

        if (estimatedGasLimit.compareTo(gasLimit) == 1) {
            throw new AppException(Error.TOO_LOW_GAS_SPECIFIED, "Too much gas("+gasLimit+") required for transaction. Limit " +
                    gasLimit);
        }

        Parity parity = Parity.build(new HttpService(appConfig.getClientUrl()));
        try {
            // Sent ether to new Account
            EthSendTransaction ethSendTransaction = parity.personalSignAndSendTransaction(transaction, fromPassword).send();
            if (ethSendTransaction.hasError()) {
                throw new AppException(Error.SEND_TRANSACTION_FAILED, ethSendTransaction.getError());
            }
            return ethSendTransaction.getTransactionHash();

        } catch (Exception ex) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Ethereum net is unavailable", ex);
        }
    }

    @Override
    public String createEtherTransaction(String fromAddress, String fromPassword, String toAddress,
                                          BigInteger amount, BigInteger gasLimit, BigInteger gasPrice) throws AppException {

        gasLimit = gasLimit == null ? BigInteger.valueOf(ethereumConfig.getGasLimit()): gasLimit;

        // Prepare transaction
        Transaction transaction = Transaction.
                createEtherTransaction(fromAddress, null, gasPrice, gasLimit, toAddress, amount);

        // Estimate and check gasLimit
        BigInteger estimatedGasLimit = estimateGasLimit(transaction);

        if (estimatedGasLimit.compareTo(gasLimit) == 1) {
            throw new AppException(Error.TOO_LOW_GAS_SPECIFIED, "Too low gas("+gasLimit+") specified for transaction. Required: " + estimatedGasLimit);
        }

        Parity parity = Parity.build(new HttpService(appConfig.getClientUrl()));
        try {
            // Sent ether to new Account
            EthSendTransaction ethSendTransaction = parity.personalSignAndSendTransaction(transaction, fromPassword).send();
            if (ethSendTransaction.hasError()) {
                throw new AppException(Error.SEND_TRANSACTION_FAILED, ethSendTransaction.getError());
            }
            return ethSendTransaction.getTransactionHash();

        } catch (Exception ex) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Ethereum net is unavailable", ex);
        }
    }

    private BigInteger getEthBalance(String accountId) throws AppException {
        try {
            EthGetBalance ethGetBalance = web3j.ethGetBalance(accountId, DefaultBlockParameterName.LATEST).send();
            return ethGetBalance.getBalance();

        } catch (Exception ex) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Ethereum net is unavailable", ex);
        }
    }

    private BigInteger getSkinBalance(String accountId) throws AppException {
        try {
            Function function = new Function("balanceOf",
                    Arrays.<Type>asList(new Address(accountId)),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall ethCall = this.web3j.ethCall(Transaction.createEthCallTransaction(
                    accountId, ethereumConfig.getSkincoinAddress(), encodedFunction), DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> result = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            return !result.isEmpty()? ((Uint256)result.get(0)).getValue(): null;
        } catch (Exception ex) {
            throw new AppException(Error.UNEXPECTED_EXCEPTION, "Can't get SKIN Balance for account(account:"+accountId+")");
        }
    }

    @Override
    public String getClientVersion() throws AppException {
        try {
            return web3j.web3ClientVersion().send().getWeb3ClientVersion();
        } catch (Exception ex) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Can't request a client version", ex);
        }
    }

    public EthNetworkJson getEthNetwork() throws AppException {
        try {
            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.ZERO), false).send();
            if (ethBlock.hasError()) {
                throw new AppException(Error.UNEXPECTED_EXCEPTION, ethBlock.getError().getMessage());
            }
            String genesisBlockHash = ethBlock.getBlock().getHash();

            switch (genesisBlockHash) {
                case MAIN_NET:
                    return EthNetworkJson.MAIN_NET;
                case TEST_NET:
                    return EthNetworkJson.TEST_NET;
                default:
                    return EthNetworkJson.PRIVATE_NET;
            }
        } catch (IOException e) {
            throw new AppException(Error.ETHEREUM_UNAVAILABLE, "Can't detect a network name", e);
        }
    }

}
