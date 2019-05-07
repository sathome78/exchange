package me.exrates.security.exception;

public class MissingCredentialException extends RuntimeException {

    public MissingCredentialException(String message) {
        super(message);
    }

}
