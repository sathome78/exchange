package me.exrates.model.dto.ngDto;

import lombok.Data;
import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.form.NotificationOptionsForm;

import java.util.List;
import java.util.Map;

/**
 * Created by Maks on 07.02.2018.
 */
@Data
public class UserSettingsDto {

    private User user;
    private List<UserFile> userFiles;
    private NotificationOptionsForm notificationOptionsForm;
    private SessionParams sessionParams;
    private List<SessionLifeTimeType> sessionLifeTimeTypes;
    private Map<String, Object> user2faOptions;
    private String telegramBotName;
    private String telegramBotUrl;
}
