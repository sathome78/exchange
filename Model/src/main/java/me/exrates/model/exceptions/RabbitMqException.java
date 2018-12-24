package me.exrates.model.exceptions;

public class RabbitMqException extends RuntimeException {

    public RabbitMqException(String message) {
        super(message);
    }
}
