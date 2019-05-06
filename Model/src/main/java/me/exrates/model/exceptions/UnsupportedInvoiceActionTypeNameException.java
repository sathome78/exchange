package me.exrates.model.exceptions;

public class UnsupportedInvoiceActionTypeNameException extends RuntimeException {
    public UnsupportedInvoiceActionTypeNameException(String message) {
        super(message);
    }
}
