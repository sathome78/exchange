package me.exrates.controller.exception;

public class RegistrationClosedException extends RuntimeException {

    public RegistrationClosedException() {
    }

    public RegistrationClosedException(String message) {
        super(message);
    }

    public RegistrationClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
