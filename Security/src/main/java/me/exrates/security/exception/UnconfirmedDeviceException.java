package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;


public class UnconfirmedDeviceException extends AuthenticationException {


    public UnconfirmedDeviceException(String msg, Throwable t) {
        super(msg, t);
    }

    public UnconfirmedDeviceException(String msg) {
        super(msg);
    }
}
