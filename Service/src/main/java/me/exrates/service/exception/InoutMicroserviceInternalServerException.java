package me.exrates.service.exception;

public class InoutMicroserviceInternalServerException extends RuntimeException {
    public InoutMicroserviceInternalServerException() {
    }

    public InoutMicroserviceInternalServerException(String message) {
        super(message);
    }
}
