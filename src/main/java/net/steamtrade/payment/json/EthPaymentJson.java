package net.steamtrade.payment.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * Created by sasha on 30.06.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EthPaymentJson {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("creating_time")
    private Long creatingTime;

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("amount")
    private BigInteger amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("status")
    private String status;

    @JsonProperty("error")
    private String error;
}
