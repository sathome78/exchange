package me.exrates.service.exception.invoice;

public class IllegalInvoiceAmountException extends Exception {

    public IllegalInvoiceAmountException(String message) {
        super(message);
    }

    public IllegalInvoiceAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
