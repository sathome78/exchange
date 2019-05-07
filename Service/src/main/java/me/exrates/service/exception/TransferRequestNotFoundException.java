package me.exrates.service.exception;

public class TransferRequestNotFoundException extends RuntimeException{
    public TransferRequestNotFoundException(String message) {
        super(message);
    }
}
