package me.exrates.model.exceptions;

public class UnsupportedWithdrawRequestStatusNameException extends RuntimeException {
    public UnsupportedWithdrawRequestStatusNameException(String message) {
        super(message);
    }
}
