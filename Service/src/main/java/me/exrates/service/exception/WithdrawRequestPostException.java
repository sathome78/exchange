package me.exrates.service.exception;

public class WithdrawRequestPostException extends RuntimeException{
    public WithdrawRequestPostException(String message) {
        super(message);
    }
}
