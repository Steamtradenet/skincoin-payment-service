package net.steamtrade.payment.backend.exceptions;

import net.steamtrade.payment.backend.utils.StringUtils;
import java.util.Map;

/**
 * Created by sasha on 17.02.16.
 */
public class AppException extends Exception {

    private Error error;
    private String request;

    public AppException(Error error) {
        this.error = error;
    }

    public AppException(Error error, Object request) {
        this.error = error;
        this.request = toJsonSilently(request);
    }

    public AppException(Error error, String message) {
        this(error, message, null);
    }

    public AppException(Error error, String message, Object request) {
        super(message);
        this.error = error;
        this.request = toJsonSilently(request);
    }

    public AppException(Error error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
    }

    public AppException(Error error, Map<Error, Object> info) {
        this.error = error;
    }

    public AppException(Error error, Throwable cause) {
        this(error, cause, null);
    }

    public AppException(Error error, Throwable cause, Object request) {
        super(cause);
        this.error = error;
        this.request = toJsonSilently(request);
    }


    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(error).append("]: ");

        if (!StringUtils.isEmpty(super.getMessage())) {
            sb.append(super.getMessage());
        } else {
            sb.append(error.getDescription());
        }
        if (!StringUtils.isEmpty(request)) {
            sb.append(", request: ").append(request);
        }
        return sb.toString();
    }

    public Error getError() {
        return error;
    }

    private String toJsonSilently(Object object) {
        if (object == null) return null;
        try {
            return StringUtils.toJson(object);
        } catch (Exception e) {
            // Do nothing
        }
        return null;
    }
}
