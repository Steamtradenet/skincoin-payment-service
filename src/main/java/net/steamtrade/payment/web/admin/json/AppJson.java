package net.steamtrade.payment.web.admin.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.steamtrade.payment.backend.exceptions.*;
import net.steamtrade.payment.backend.exceptions.Error;
import net.steamtrade.payment.backend.utils.StringUtils;

/**
 * Created by sasha on 07.08.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppJson {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("token")
    private String token;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("from_password")
    private String fromPassword;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("enable_callback")
    private Boolean enableCallback;

    @JsonProperty("auth_secret")
    private String authSecret;

    public void validate() throws AppException {
        if (StringUtils.isAnyEmpty(name, token)) {
            throw new AppException(Error.INCORRECT_FORMAT_JSON, "Name and Token can't be NULL");
        }
        if (StringUtils.isAnyEmpty(fromAddress, fromPassword)) {
            throw new AppException(Error.INCORRECT_FORMAT_JSON, "Payout credentials can't be NULL");
        }
        if (StringUtils.isEmpty(authSecret)) {
            throw new AppException(Error.INCORRECT_FORMAT_JSON, "Auth secret can't be NULL");
        }
    }

}
