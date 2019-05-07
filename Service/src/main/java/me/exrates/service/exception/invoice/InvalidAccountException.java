package me.exrates.service.exception.invoice;

public class InvalidAccountException extends MerchantException {
  private final String REASON_CODE = "withdraw.reject.reason.invalidAccount";
  
  public InvalidAccountException() {
  }
  
  public InvalidAccountException(String message) {
    super(message);
  }
  
  public InvalidAccountException(Throwable cause) {
    super(cause);
  }
  
  @Override
  public String getReason() {
    return REASON_CODE;
  }
}
