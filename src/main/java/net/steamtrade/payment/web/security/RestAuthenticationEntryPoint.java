package net.steamtrade.payment.web.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        if (!request.getMethod().equals("OPTIONS")) {
            ResponseJson responseJson = new ResponseJson();
            responseJson.setStatus(ResponseJson.STATUS_FAIL);
            responseJson.setReason("Unauthorized: Authentication token was either missing or invalid.");
            ObjectMapper objectMapper = new ObjectMapper();

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println(objectMapper.writeValueAsString(responseJson));
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }


    @JsonPropertyOrder({ ResponseJson.STATUS, ResponseJson.REASON })
    public class ResponseJson {

        public static final String STATUS = "status";
        public static final String REASON = "reason";

        public static final String STATUS_OK = "OK";
        public static final String STATUS_FAIL = "FAIL";

        public ResponseJson() {
            this.status = STATUS_OK;
        }

        private String status;
        private String reason;

        @JsonProperty(STATUS)
        public String getStatus() {
            return status;
        }

        @JsonProperty(STATUS)
        public void setStatus(String status) {
            this.status = status;
        }

        @JsonProperty(REASON)
        public String getReason() {
            return reason;
        }

        @JsonProperty(REASON)
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
