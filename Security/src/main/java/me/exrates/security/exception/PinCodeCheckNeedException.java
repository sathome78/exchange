package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by maks on 03.07.2017.
 */
public class PinCodeCheckNeedException extends AuthenticationException {

    private String uuid;

    public PinCodeCheckNeedException(String msg, Throwable t) {
        super(msg, t);
    }

    public PinCodeCheckNeedException(String msg) {
        super(msg);
    }

    public PinCodeCheckNeedException(String msg, String uuid) {
        super(msg);
        this.uuid = uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
