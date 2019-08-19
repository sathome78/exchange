package me.exrates.service.exception;

public class RabbitMqException extends RuntimeException {

    public RabbitMqException() {
        super();
    }

    public RabbitMqException(String message) {
        super(message);
    }

    public RabbitMqException(String message, Throwable cause) {
        super(message, cause);
    }
}