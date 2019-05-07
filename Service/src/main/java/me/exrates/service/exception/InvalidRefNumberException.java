package me.exrates.service.exception;

public class InvalidRefNumberException extends RuntimeException {

    public InvalidRefNumberException() {
    }

    public InvalidRefNumberException(String message) {
        super(message);
    }
}
