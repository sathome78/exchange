package me.exrates.service;

import me.exrates.model.dto.AlertDto;
import me.exrates.model.enums.AlertType;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maks on 13.12.2017.
 * Updated by Vlad on 09.07.2018 (add type of alert - SYSTEM_MESSAGE_TO_USER)
 */
public interface UsersAlertsService {

    List<AlertDto> getActiveAlerts(Locale locale);

    List<AlertDto> getAllAlerts(Locale locale);

    @Transactional
    AlertDto getAlert(AlertType alertType);

    @Transactional
    void updateAction(AlertDto alertDto);

    /**
     * Method for type of alert "SYSTEM_MESSAGE_TO_USER".
     * Set alert system message for a user with a specific locale.
     * @param alertDto
     */
    void setAlertSystemMessageToUser(AlertDto alertDto);

    /**
     * Method for type of alert "SYSTEM_MESSAGE_TO_USER".
     * Get alert system message for a user with a specific locale.
     * @param language
     * @return AlertDto
     */
    AlertDto getAlertSystemMessageToUser(String language);
}
