package me.exrates.controller.exception;

/**
 * Created by OLEG on 10.05.2017.
 */
public class NoRequestedBeansFoundException extends RuntimeException {
    public NoRequestedBeansFoundException() {
    }

    public NoRequestedBeansFoundException(String message) {
        super(message);
    }

    public NoRequestedBeansFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRequestedBeansFoundException(Throwable cause) {
        super(cause);
    }
}
