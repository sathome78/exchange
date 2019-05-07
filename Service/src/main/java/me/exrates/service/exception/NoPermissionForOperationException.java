package me.exrates.service.exception;

public class NoPermissionForOperationException extends RuntimeException{
    public NoPermissionForOperationException() {
    }

    public NoPermissionForOperationException(String message) {
        super(message);
    }
}
