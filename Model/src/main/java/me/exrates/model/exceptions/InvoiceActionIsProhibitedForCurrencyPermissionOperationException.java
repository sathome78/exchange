package me.exrates.model.exceptions;

public class InvoiceActionIsProhibitedForCurrencyPermissionOperationException extends RuntimeException {
    public InvoiceActionIsProhibitedForCurrencyPermissionOperationException(String message) {
        super(message);
    }
}
