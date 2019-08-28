package me.exrates.service.exception;

/**
 * Created by Valk
 */
public class IllegalOperationTypeException extends RuntimeException {

    private final static String ERR_MSG = "error.illegal_opertion_type";

    public IllegalOperationTypeException(String message) {
        super(message);
    }

    public IllegalOperationTypeException() {
        super(ERR_MSG);
    }

    public IllegalOperationTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
