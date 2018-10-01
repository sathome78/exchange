package me.exrates.controller.exception;

public class PasswordCreationException extends RuntimeException {
    public PasswordCreationException(String message) {
        super(message);
    }
}
