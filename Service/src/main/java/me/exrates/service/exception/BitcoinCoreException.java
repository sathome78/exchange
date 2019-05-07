package me.exrates.service.exception;

public class BitcoinCoreException extends RuntimeException {

  public BitcoinCoreException(String message) {
    super(message);
  }
  
  public BitcoinCoreException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public BitcoinCoreException(Throwable cause) {
    super(cause);
  }
}
