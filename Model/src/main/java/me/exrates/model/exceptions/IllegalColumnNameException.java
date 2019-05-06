package me.exrates.model.exceptions;

public class IllegalColumnNameException extends RuntimeException {

    public IllegalColumnNameException(String message) {
        super(message);
    }
}
