package me.exrates.service.exception.api;

public class UnconfirmedUserException extends RuntimeException {
    public UnconfirmedUserException() {
    }

    public UnconfirmedUserException(String message) {
        super(message);
    }

    public UnconfirmedUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnconfirmedUserException(Throwable cause) {
        super(cause);
    }
}
