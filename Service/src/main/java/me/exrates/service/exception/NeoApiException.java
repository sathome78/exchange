package me.exrates.service.exception;

public class NeoApiException extends RuntimeException {

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public NeoApiException(String message) {
        super(message);
    }

    public NeoApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public NeoApiException(Throwable cause) {
        super(cause);
    }
}
