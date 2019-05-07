package me.exrates.service.exception;

public class TelegramSubscriptionException extends RuntimeException {

    public TelegramSubscriptionException() {
    }

    public TelegramSubscriptionException(String message) {
        super(message);
    }
}
