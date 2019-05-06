package me.exrates.controller.exception;

public class NotCreateUserException extends RuntimeException {
    public NotCreateUserException(String message) {
        super(message);
    }
}
