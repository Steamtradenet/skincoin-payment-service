package net.steamtrade.payment.backend.client;


import net.steamtrade.payment.backend.client.request.GetTransactionReceiptRequest;
import net.steamtrade.payment.backend.client.request.GetTransactionReceiptResponse;
import net.steamtrade.payment.backend.client.request.JsonRpcResponse;
import net.steamtrade.payment.backend.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Web3Client  {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppConfig appConfig;

    public GetTransactionReceiptResponse ethGetTransactionByHash(String trxHash) {
        String url = String.format(appConfig.getClientUrl()+ "/");

        HttpEntity<GetTransactionReceiptRequest> body = new HttpEntity<>(new GetTransactionReceiptRequest(trxHash));

        ResponseEntity<JsonRpcResponse<GetTransactionReceiptResponse>> response =
                restTemplate.exchange(url,
                        HttpMethod.POST, body,
                        new ParameterizedTypeReference<JsonRpcResponse<GetTransactionReceiptResponse>>() {});
        return response.getBody().getResult();
    }
}
