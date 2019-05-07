package me.exrates.service.exception.api;

public class UniqueEmailConstraintException extends RuntimeException {

    public UniqueEmailConstraintException(String message) {
        super(message);
    }

}
