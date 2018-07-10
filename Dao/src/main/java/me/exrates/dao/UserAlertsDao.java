package me.exrates.dao;

import me.exrates.model.dto.AlertDto;
import me.exrates.model.enums.AlertType;

import java.util.List;

/**
 * Created by Maks on 13.12.2017.
 * Updated by Vlad on 09.07.2018 (add type of alert - SYSTEM_MESSAGE_TO_USER)
 */
public interface UserAlertsDao {
    List<AlertDto> getAlerts(boolean getOnlyEnabled);

    boolean updateAlert(AlertDto alertDto);

    boolean setEnable(String alertType, boolean enable);

    AlertDto getAlert(String name);

    /**
     * Method for type of alert "SYSTEM_MESSAGE_TO_USER".
     * Get alert system message for a user with a specific locale.
     * @param language
     * @return AlertDto
     */
    AlertDto getAlertSystemMessageToUser(String language);

    /**
     * Method for type of alert "SYSTEM_MESSAGE_TO_USER".
     * Set alert system message for a user with a specific locale.
     * @param alertDto
     */
    void setAlertSystemMessageToUser(AlertDto alertDto);
}
