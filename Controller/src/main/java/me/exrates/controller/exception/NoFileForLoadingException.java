package me.exrates.controller.exception;

public class NoFileForLoadingException extends RuntimeException {
    public NoFileForLoadingException(String message) {
        super(message);
    }
}
