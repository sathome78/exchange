package me.exrates.service.exception.api;

public class TokenAccessDeniedException extends RuntimeException {

    public TokenAccessDeniedException(String message) {
        super(message);
    }

}
