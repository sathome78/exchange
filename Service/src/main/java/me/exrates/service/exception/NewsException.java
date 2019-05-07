package me.exrates.service.exception;

public abstract class NewsException extends RuntimeException {
    public NewsException(String message) {
        super(message);
    }
}
