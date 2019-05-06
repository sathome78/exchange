package me.exrates.model.exceptions;

public class UnsupportedInvoiceStatusForActionException extends RuntimeException {
  public UnsupportedInvoiceStatusForActionException(String message) {
    super(message);
  }
}
