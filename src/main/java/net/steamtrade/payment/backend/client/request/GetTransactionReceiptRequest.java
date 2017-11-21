package net.steamtrade.payment.backend.client.request;


import lombok.Data;

@Data
public class GetTransactionReceiptRequest extends JsonRpcRequest {


    public GetTransactionReceiptRequest(String trxHash) {
        method = "eth_getTransactionReceipt";
        params.add(trxHash);
    }
}




