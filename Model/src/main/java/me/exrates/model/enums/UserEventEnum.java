package me.exrates.model.enums;

public enum UserEventEnum {

    REGISTER(true),
    LOGIN_SUCCESS(true),
    RESET_PASSWORD(false),
    CHANGE_PASSWORD(false),
    WITHDRAW(true),
    TRADE(false),
    REFILL_ADDRESS(false),
    REFILL_REQUEST(false),
    TRANSFER_SEND(true),
    TRANSFER_CODE_ACCEPT(true);

    private boolean isIpLogged;

    UserEventEnum(boolean isIpLogged) {
        this.isIpLogged = isIpLogged;
    }

    public boolean isIpLogged() {
        return isIpLogged;
    }
}
