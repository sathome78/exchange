package me.exrates.service.exception;

public class FreecoinsException extends RuntimeException {

    public FreecoinsException() {
        super();
    }

    public FreecoinsException(String message) {
        super(message);
    }

    public FreecoinsException(String message, Throwable cause) {
        super(message, cause);
    }
}