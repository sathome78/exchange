package me.exrates.model.enums;

/**
 * Created by Maks on 13.12.2017.
 * Updated by Vlad on 09.07.2018 (add type of alert - SYSTEM_MESSAGE_TO_USER)
 */
public enum AlertType {

    UPDATE("message.alertUpdate", true),
    TECHNICAL_WORKS("message.alertTechWorks", false),
    /**
     * The value for this field is taken from the database, from the table SERVICE_ALERTS_SYSTEM_MESSAGE.
     */
    SYSTEM_MESSAGE_TO_USERS("", false);

    private String messageTmpl;
    private boolean needDateTime;

    public String getMessageTmpl() {
        return messageTmpl;
    }

    public boolean isNeedDateTime() {
        return needDateTime;
    }

    AlertType(String messageTmpl, boolean needDateTime) {
        this.messageTmpl = messageTmpl;
        this.needDateTime = needDateTime;
    }
}
