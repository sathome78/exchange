package me.exrates.service.exception;

public class InvalidContactFormatException extends RuntimeException {

    public InvalidContactFormatException() {
    }

    public InvalidContactFormatException(String message) {
        super(message);
    }
}
