package me.exrates.service.exception;

/**
 * Throw this exception when USDX REST API get error or fail operation
 */
public class UsdxApiException extends RuntimeException {
    public UsdxApiException() {
    }

    public UsdxApiException(String message) {
        super(message);
    }

    public UsdxApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsdxApiException(Throwable cause) {
        super(cause);
    }
}
