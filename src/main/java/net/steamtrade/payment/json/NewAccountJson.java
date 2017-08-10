package net.steamtrade.payment.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.backend.utils.StringUtils;
import net.steamtrade.payment.backend.exceptions.Error;

/**
 * Created by sasha on 30.06.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewAccountJson {

    @JsonProperty("request_id")
    private String requestId;


    public void validate() throws AppException {
        if (StringUtils.isEmpty(requestId)) {
            throw new AppException(Error.INCORRECT_FORMAT_JSON, "RequestId must be specified");
        }
    }
}
