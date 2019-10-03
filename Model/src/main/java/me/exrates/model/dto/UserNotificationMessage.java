package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class UserNotificationMessage implements Serializable {

    private String messageId;
    private WsSourceTypeEnum sourceTypeEnum;
    private UserNotificationType notificationType;
    private String text;
    private boolean viewed;

    public UserNotificationMessage(WsSourceTypeEnum sourceTypeEnum, UserNotificationType notificationType, String text) {
        this.sourceTypeEnum = sourceTypeEnum;
        this.notificationType = notificationType;
        this.text = text;
    }
}
