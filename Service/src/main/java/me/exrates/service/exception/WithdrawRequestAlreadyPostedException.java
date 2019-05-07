package me.exrates.service.exception;

public class WithdrawRequestAlreadyPostedException extends RuntimeException{
    public WithdrawRequestAlreadyPostedException(String message) {
        super(message);
    }
}
