package me.exrates.service.exception;

import me.exrates.service.exception.invoice.MerchantException;

public class NotConfirmedFinPasswordException extends MerchantException {

    private final String REASON_CODE = "admin.notconfirmedfinpassword";

    @Override
    public String getReason() {
        return REASON_CODE;
    }

    public NotConfirmedFinPasswordException(String message) {
        super(message);
    }
}
