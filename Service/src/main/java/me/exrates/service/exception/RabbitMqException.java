package me.exrates.service.exception;

public class RabbitMqException extends RuntimeException {

    public RabbitMqException(String message) {
        super(message);
    }
}