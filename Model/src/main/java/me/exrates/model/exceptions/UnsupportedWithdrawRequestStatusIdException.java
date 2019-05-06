package me.exrates.model.exceptions;

public class UnsupportedWithdrawRequestStatusIdException extends RuntimeException {
    public UnsupportedWithdrawRequestStatusIdException(String message) {
        super(message);
    }
}
