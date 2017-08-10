package net.steamtrade.payment.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by sasha on 7/25/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationJson {

    @JsonProperty("type")
    private String type;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("data")
    private String data;

}
