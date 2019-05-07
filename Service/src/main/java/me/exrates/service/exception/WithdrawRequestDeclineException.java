package me.exrates.service.exception;

public class WithdrawRequestDeclineException extends RuntimeException{
    public WithdrawRequestDeclineException(String message) {
        super(message);
    }
}
