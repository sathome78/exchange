package me.exrates.service.exception.process;

public class CancelOrderException extends ProcessingException {

    public CancelOrderException() {
        super();
    }

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
