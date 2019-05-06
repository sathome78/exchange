package me.exrates.model.exceptions;

public class AuthorisedUserIsHolderParamNeededForThisActionException extends RuntimeException {
    public AuthorisedUserIsHolderParamNeededForThisActionException(String message) {
        super(message);
    }
}
