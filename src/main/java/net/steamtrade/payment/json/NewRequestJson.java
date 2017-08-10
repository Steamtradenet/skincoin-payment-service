package net.steamtrade.payment.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.backend.utils.StringUtils;
import net.steamtrade.payment.backend.exceptions.Error;

import java.math.BigInteger;


/**
 * Created by sasha on 30.06.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewRequestJson {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("to")
    private String to;

    @JsonProperty("amount")
    private BigInteger amount;

    @JsonProperty("currency")
    private String currency; // ETHER and SKIN are possible

    public void validate(boolean checkAmount) throws AppException {
        if (StringUtils.isAnyEmpty(requestId, to, currency)) {
            throw new AppException(Error.INCORRECT_FORMAT_JSON, "All parameters must be specified");
        }
        if (checkAmount && (amount == null || amount.compareTo(BigInteger.ONE) == -1)) {
            throw new AppException(Error.INCORRECT_FORMAT_JSON, "Incorrect amount was specified. Must be >=1");
        }
    }
}
