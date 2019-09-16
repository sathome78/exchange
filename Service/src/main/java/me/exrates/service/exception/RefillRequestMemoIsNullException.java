package me.exrates.service.exception;

/**
 * Throw this exception when refill request must have memo but memo is null (uses for cryptocurrency with memo)
 */
public class RefillRequestMemoIsNullException extends RuntimeException{
    public RefillRequestMemoIsNullException(String message) {
        super(message);
    }
}
