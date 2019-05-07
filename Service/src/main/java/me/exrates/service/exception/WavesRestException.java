package me.exrates.service.exception;

public class WavesRestException extends RuntimeException {

    private int code;

    public int getCode() {
        return code;
    }

    public WavesRestException(String message) {
        super(message);
    }

    public WavesRestException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public WavesRestException(Throwable cause) {
        super(cause);
    }
}
