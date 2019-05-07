package me.exrates.service;

public class RequestLimitExceededException extends RuntimeException {

    public RequestLimitExceededException(String message) {
        super(message);
    }

}
