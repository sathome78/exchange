package me.exrates.service.exception.api;

public class CancelOrderException extends RuntimeException {


    public CancelOrderException(String message) {
        super(message);
    }

    public CancelOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelOrderException(Throwable cause) {
        super(cause);
    }
}
