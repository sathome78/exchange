package me.exrates.controller.exception;

/**
 * Throw this exception when withdraw request has amount bigger than has in property/db/other config
 */
public class WithdrawAmountLargeException extends RuntimeException {
    public WithdrawAmountLargeException(String message) {
        super(message);
    }
}
