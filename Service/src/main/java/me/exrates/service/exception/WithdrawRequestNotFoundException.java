package me.exrates.service.exception;

public class WithdrawRequestNotFoundException extends RuntimeException{
    public WithdrawRequestNotFoundException(String message) {
        super(message);
    }
}
