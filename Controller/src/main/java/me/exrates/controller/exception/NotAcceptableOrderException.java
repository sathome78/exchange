package me.exrates.controller.exception;

public class NotAcceptableOrderException extends RuntimeException {
    public NotAcceptableOrderException(String message) {
        super(message);
    }
}
