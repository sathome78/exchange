package me.exrates.service.exception;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
public class UserOperationAccessException extends RuntimeException {

    private final static String ERR_MSG = "merchant.operationNotAvailable";

    public UserOperationAccessException(String message) {
        super(message);
    }

    public UserOperationAccessException() {
        super(ERR_MSG);
    }
}
