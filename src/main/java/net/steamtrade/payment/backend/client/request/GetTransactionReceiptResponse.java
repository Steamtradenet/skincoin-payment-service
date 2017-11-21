package net.steamtrade.payment.backend.client.request;

import lombok.Data;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

@Data
public class GetTransactionReceiptResponse {

    private String transactionHash;
    private String transactionIndex;
    private String blockHash;
    private String blockNumber;
    private String cumulativeGasUsed;
    private String gasUsed;
    private String contractAddress;
    private String root;
    private String from;
    private String to;
    private String logsBloom;
    private List<Log> logs;
    private String status;

    public BigInteger getTransactionIndex() {
        return Numeric.decodeQuantity(this.transactionIndex);
    }

    public BigInteger getBlockNumber() {
        return Numeric.decodeQuantity(this.blockNumber);
    }

    public BigInteger getCumulativeGasUsed() {
        return Numeric.decodeQuantity(this.cumulativeGasUsed);
    }

    public BigInteger getGasUsed() {
        return Numeric.decodeQuantity(this.gasUsed);
    }

    public BigInteger getStatus() {
        if (status != null) {
            return Numeric.decodeQuantity(this.status);
        }
        return null;
    }
}
