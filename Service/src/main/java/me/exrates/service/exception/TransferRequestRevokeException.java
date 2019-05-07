package me.exrates.service.exception;

public class TransferRequestRevokeException extends RuntimeException {
    public TransferRequestRevokeException(String message) {
        super(message);
    }

    public TransferRequestRevokeException() {
    }
}
