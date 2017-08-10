package net.steamtrade.payment.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by sasha on 20.07.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EthereumInfoJson {

    @JsonProperty("network")
    private String network;

    @JsonProperty("skincoin_address")
    private String skincoinAddress;

    @JsonProperty("client_version")
    private String clientVersion;
}
