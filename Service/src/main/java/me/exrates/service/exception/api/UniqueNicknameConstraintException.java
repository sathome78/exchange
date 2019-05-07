package me.exrates.service.exception.api;

public class UniqueNicknameConstraintException extends RuntimeException {

    public UniqueNicknameConstraintException(String message) {
        super(message);
    }

}
