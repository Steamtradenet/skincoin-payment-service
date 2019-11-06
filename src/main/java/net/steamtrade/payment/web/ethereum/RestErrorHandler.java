package net.steamtrade.payment.web.ethereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.backend.exceptions.Error;
import net.steamtrade.payment.backend.utils.StringUtils;
import org.hibernate.TypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.BindException;

@ControllerAdvice
public class RestErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RestErrorHandler.class);

    @Autowired
    private AppConfig appConfig;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public FailJson handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        FailJson result;
        HttpStatus httpStatus = getHttpStatusException(exception);
        if (httpStatus != null) {
            log.error("Request '" + request.getRequestURI() + "' has failed. ", exception);
            response.setStatus(httpStatus.value());
            result = new FailJson(httpStatus.name(), httpStatus.getReasonPhrase());
            if (appConfig.isEnabledDebug()) {
                result.setTrace(StringUtils.stackTraceToString(exception));
            }
            return result;
        } else {
            Error error = getApplicationError(exception);
            if (error != null) {
                log.error("Request '" + request.getRequestURI() + "' has failed. ", exception);

                result = new FailJson(error);
                if (appConfig.isEnabledDebug()) {
                    result.setTrace(StringUtils.stackTraceToString(exception));
                }
                return result;
            } else {
                log.error("Request '" + request.getRequestURI() + "' has failed. ", exception);
            }
        }
        result = new FailJson(HttpStatus.INTERNAL_SERVER_ERROR.name(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        if (appConfig.isEnabledDebug()) {
            result.setTrace(StringUtils.stackTraceToString(exception));
        }
        return result;
    }

    private Error getApplicationError(Exception ex) {
        Error error = null;
        if (ex instanceof AppException) {
            error = ((AppException)ex).getError();
        }
        return error;
    }

    private HttpStatus getHttpStatusException(Exception ex) {
        HttpStatus status = null;
        if(ex instanceof HttpRequestMethodNotSupportedException) {
            status = HttpStatus.METHOD_NOT_ALLOWED;
        } else if(ex instanceof HttpMediaTypeNotSupportedException) {
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        } else if(ex instanceof HttpMediaTypeNotAcceptableException) {
            status = HttpStatus.NOT_ACCEPTABLE;
        } else if(ex instanceof MissingServletRequestParameterException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof ServletRequestBindingException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof ConversionNotSupportedException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if(ex instanceof TypeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof HttpMessageNotReadableException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof HttpMessageNotWritableException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if(ex instanceof MethodArgumentNotValidException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof MissingServletRequestPartException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof BindException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof NoHandlerFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return status;
    }


    @Data
    public static class FailJson {

        @JsonProperty("status")
        private String status = "FAIL";
        @JsonProperty("error_code")
        private String errorCode;
        @JsonProperty("error")
        private String error;
        @JsonProperty("info")
        private String info;
        @JsonProperty("trace")
        private String trace;

        public FailJson(String errorCode, String error) {
            this.errorCode = errorCode;
            this.error = error;
        }

        public FailJson(Error error) {
            this.errorCode = error.name();
            this.error = error.getDescription();
        }
    }

}
