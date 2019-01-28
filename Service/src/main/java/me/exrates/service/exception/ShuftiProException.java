package me.exrates.service.exception;

public class ShuftiProException extends RuntimeException {

    public ShuftiProException(String message) {
        super(message);
    }

    public ShuftiProException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShuftiProException(Throwable cause) {
        super(cause);
    }
}