package me.exrates.service.exception.api;

public class MissingBodyParamException extends RuntimeException {

    public MissingBodyParamException(String message) {
        super(message);
    }

}
