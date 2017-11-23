package net.steamtrade.payment.backend.ethereum.watch.logic;

import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.client.Web3Client;
import net.steamtrade.payment.backend.client.request.GetTransactionReceiptResponse;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.ethereum.dao.EthTransactionDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthTransaction;
import net.steamtrade.payment.backend.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by sasha on 04.07.17.
 */
@Component
public class TransactionProcessor {

    private static final int SUCCESS_RECEIPT_STATUS = 1;

    @Autowired
    private Web3j web3j;
    @Autowired
    private EthTransactionDao transactionDao;
    @Autowired
    private LogServiceClient log;
    @Autowired
    private PaymentProcessor paymentProcessor;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private Web3Client web3Client;

    public void process(List<EthTransaction> transactions) {
        try {
            BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            debug("Current block number:"+blockNumber);

            for (EthTransaction transaction: transactions) {
                try {
                    log.info(this.getClass(), "Start processing transaction(hash:"+transaction.getHash()+")...");

                    if (needReceipt(transaction)) {
                        GetTransactionReceiptResponse receipt = web3Client.ethGetTransactionByHash(transaction.getHash());
                        if (receipt != null) {
                            if (isSuccess(receipt, transaction.getGas())) {
                                log.info(this.getClass(), "Transaction(hash:"+transaction.getHash()+") was created.");
                                transaction.setBlockHash(receipt.getBlockHash());
                                transaction.setBlockNumber(receipt.getBlockNumber());
                                transaction.setGasUsed(receipt.getGasUsed());
                                transaction.setCumulativeGasUsed(receipt.getCumulativeGasUsed());
                                transaction.setStatus(EthTransaction.Status.CREATED);

                                paymentProcessor.processPayment(transaction);

                            } else {
                                failTransaction(receipt, transaction);
                            }
                        } else {
                            log.info(this.getClass(), "Transaction(hash:"+transaction.getHash()+") was delayed on 30 sec.");
                            transaction.setHoldTime(DateUtils.addSeconds(new Date(), 30));
                        }
                    }

                    if (transaction.getBlockNumber() != null && !transaction.getStatus().equals(EthTransaction.Status.ERROR)) {
                        BigInteger confirmations = blockNumber.subtract(transaction.getBlockNumber());
                        if (confirmations.compareTo(appConfig.getConfirmationCount()) >= 0) {

                            // Check receipt one more time
                            GetTransactionReceiptResponse receipt = web3Client.ethGetTransactionByHash(transaction.getHash());
                            if (isSuccess(receipt, transaction.getGas())) {
                                log.info(this.getClass(), "Transaction(hash:"+transaction.getHash()+") was accepted.");

                                transaction.setStatus(EthTransaction.Status.ACCEPTED);
                                paymentProcessor.processPayment(transaction);
                            } else {
                                failTransaction(receipt, transaction);
                            }
                        } else {
                            int delay = calculateConfirmationDelay(confirmations);
                            log.info(this.getClass(), "Transaction(hash:"+transaction.getHash()+") was delayed on "+
                                    delay+" sec. Confirmations: " + confirmations + " from " + appConfig.getConfirmationCount());

                            transaction.setHoldTime(DateUtils.addSeconds(new Date(), delay));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error(this.getClass(), "Can't process transaction(hash:'" + transaction.getHash()+"')", ex);

                } finally {
                    transaction.setUpdateTime(new Date());
                    transaction.setLocked(false);
                    transactionDao.save(transaction);
                }
            }

        } catch (Exception ex) {
            transactions.forEach(transaction -> {
                transaction.setUpdateTime(new Date());
                transaction.setLocked(false);
                transactionDao.save(transaction);

            });
            ex.printStackTrace();
            log.error(this.getClass(), "Can't request current block number. ", ex);
        }
    }

    private void failTransaction(GetTransactionReceiptResponse receipt, EthTransaction transaction) {
        String errorMessage = "Unknown error";
        if (isOutOfGas(transaction.getGas(), receipt.getGasUsed())) {
            errorMessage = "Out of gas";
        }
        log.warn(this.getClass(), "Transaction(hash:"+transaction.getHash()+") was failed. Error: " + errorMessage);

        transaction.setStatus(EthTransaction.Status.ERROR);
        transaction.setError(errorMessage);
        paymentProcessor.processPayment(transaction);
    }

    private boolean isSuccess(GetTransactionReceiptResponse response, BigInteger gas) {
        BigInteger status = response.getStatus();

        if (status != null) {
            if (status.intValue() != SUCCESS_RECEIPT_STATUS) {
                return false;
            }
        } else {
            if (isOutOfGas(gas,response.getGasUsed())) {
                return false;
            }
        }

        return true;
    }


    private boolean isOutOfGas(BigInteger gas, BigInteger gasUsed) {
        if (gas != null && gasUsed != null) {
            return gas.compareTo(gasUsed) == 0;
        }
        return false;
    }


    private int calculateConfirmationDelay(BigInteger confirmations) {
        int confirmationCount = appConfig.getConfirmationCount().intValue();

        if (confirmations.compareTo(BigInteger.valueOf(confirmationCount / 3)) <= 0) {
            return 90;
        } else if (confirmations.compareTo(BigInteger.valueOf(confirmationCount / 2)) <= 0) {
            return 60;
        } else {
            return 10;
        }
    }


    private boolean needReceipt(EthTransaction transaction) {
        return transaction.getGasUsed() == null;
    }

    public void debug(String msg) {
        System.out.println(msg);
    }

}
