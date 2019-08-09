package me.exrates.service.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class InvalidAmountException extends RuntimeException {

    private final static String ERR_MSG = "error.invalid_amount";

    public InvalidAmountException() {
        super(ERR_MSG);
    }

    public InvalidAmountException(String message) {
        super(message);
    }
}
