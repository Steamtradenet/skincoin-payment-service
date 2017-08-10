package net.steamtrade.payment.json;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by flash on 25.03.15.
 */
public class SimpleSuccessJson {

    @JsonProperty("success")
    public Boolean getSuccess() {
        return true;
    }
}
