package me.exrates.controller.exception;

/**
 * Created by OLEG on 18.04.2017.
 */
public class InvalidNumberParamException extends RuntimeException {
    public InvalidNumberParamException() {
    }

    public InvalidNumberParamException(String message) {
        super(message);
    }

    public InvalidNumberParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNumberParamException(Throwable cause) {
        super(cause);
    }
}
