package me.exrates.service.exception;

public class NoRequestedBeansFoundException extends RuntimeException {

    public NoRequestedBeansFoundException(String message) {
        super(message);
    }

}
