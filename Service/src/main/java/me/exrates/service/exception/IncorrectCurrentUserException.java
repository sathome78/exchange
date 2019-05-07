package me.exrates.service.exception;

public class IncorrectCurrentUserException extends RuntimeException {

    public IncorrectCurrentUserException(String message) {
        super(message);
    }

}
