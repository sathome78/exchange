package me.exrates.service.exception;

public class WithdrawRequestCreationException extends RuntimeException{
    public WithdrawRequestCreationException(String message) {
        super(message);
    }
}
