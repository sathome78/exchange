package me.exrates.model.enums;

import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.exceptions.UnsupportedInvoiceOperationDirectionException;

import java.util.stream.Stream;

/**
 * Created by Maks on 29.09.2017.
 */
public enum NotificationMessageEventEnum {

    LOGIN(1, "message.pincode.forlogin", "message.subj.login.pin"),
    WITHDRAW(2, "message.pincode.forWithdraw", "message.subj.withdraw.pin"),
    TRANSFER(3, "message.pincode.forTransfer", "message.subj.transfer.pin");

    private int code;

    private String messageCode;

    private String sbjCode;

    public int getCode() {
        return code;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getSbjCode() {
        return sbjCode;
    }

    NotificationMessageEventEnum(int code, String messageCode, String sbjCode) {
        this.code = code;
        this.messageCode = messageCode;
        this.sbjCode = sbjCode;
    }

    public static NotificationMessageEventEnum convert(int id) {
        return Stream.of(NotificationMessageEventEnum.values()).filter(item -> item.code == id).findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("id: %s", id)));
    }

    public static NotificationMessageEventEnum convert(String value) {
        switch (value) {
            case "TRANSFER" :   return TRANSFER;
            case "WITHDRAW" :   return WITHDRAW;
            case "LOGIN" :      return LOGIN;
            default: return null;
        }
    }
}
