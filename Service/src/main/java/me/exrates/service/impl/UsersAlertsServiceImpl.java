package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserAlertsDao;
import me.exrates.model.dto.AlertDto;
import me.exrates.model.enums.AlertType;
import me.exrates.service.UsersAlertsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maks on 13.12.2017.
 * Updated by Vlad on 09.07.2018 (add type of alert - SYSTEM_MESSAGE_TO_USER)
 */
@Log4j2
@Service
public class UsersAlertsServiceImpl implements UsersAlertsService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserAlertsDao userAlertsDao;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostConstruct
    private void init() {
        AlertType alertType = AlertType.UPDATE;
        AlertDto alertDto = getAlert(AlertType.UPDATE);
        if (alertDto.isEnabled() && alertDto.getEventStart().isBefore(LocalDateTime.now())) {
            disableAlert(alertType);
        }
    }

    @Override
    public List<AlertDto> getActiveAlerts(Locale locale) {
       List<AlertDto> list = userAlertsDao.getAlerts(true);
       completeDtos(list, locale);
       return list;
    }

    @Override
    public List<AlertDto> getAllAlerts(Locale locale) {
        List<AlertDto> list = userAlertsDao.getAlerts(false);
        completeDtos(list, locale);
        return list;
    }

    private void completeDtos(List<AlertDto> alertDtos, Locale locale) {
        alertDtos.forEach(p->{
            if(p.isEnabled()) {
                AlertType alertType = AlertType.valueOf(p.getAlertType());
                if (alertType.isNeedDateTime() && p.isEnabled()) {
                    if (LocalDateTime.now().isBefore(p.getEventStart())) {
                        Duration duration = Duration.between(LocalDateTime.now(), p.getEventStart());
                        log.debug("now {}, launch {}, duration {}, seconds {}", LocalDateTime.now(), p.getEventStart(), duration, duration.getSeconds());
                        p.setTimeRemainSeconds(duration.getSeconds());
                    } else {
                        p.setTimeRemainSeconds(0L);
                    }
                    p.setText(messageSource.getMessage(alertType.getMessageTmpl(),
                            new String[]{p.getLenghtOfWorks().toString()}, locale));
                } else if(alertType.equals(AlertType.SYSTEM_MESSAGE_TO_USERS)){
                    p.setTitle(userAlertsDao.getAlertSystemMessageToUser(locale.toString()).getTitle());
                    p.setText(userAlertsDao.getAlertSystemMessageToUser(locale.toString()).getText());
                } else {
                    p.setText(messageSource.getMessage(alertType.getMessageTmpl(), null, locale));
                }
            }
        });
    }

    @Override
    @Transactional
    public AlertDto getAlert(AlertType alertType) {
        return userAlertsDao.getAlert(alertType.name());
    }

    @Transactional
    @Override
    public void updateAction(AlertDto alertDto) {
        eventPublisher.publishEvent(alertDto);
        AlertType alertType = AlertType.valueOf(alertDto.getAlertType());
        if (alertDto.isEnabled()) {
            enableAlert(alertType, alertDto);
        } else {
            disableAlert(alertType);
        }
    }

    private void enableAlert(AlertType alertType, AlertDto alertDto) {
        LocalDateTime eventStart = null;
        if (alertType.isNeedDateTime()) {
            eventStart = LocalDateTime.now().plusMinutes(alertDto.getMinutes());
        }
        userAlertsDao.updateAlert(AlertDto
                .builder()
                .alertType(alertType.name())
                .launchDateTime(LocalDateTime.now())
                .enabled(true)
                .eventStart(eventStart)
                .lenghtOfWorks(alertDto.getLenghtOfWorks())
                .build());
    }

    private void disableAlert(AlertType alertType) {
        userAlertsDao.updateAlert(AlertDto
                .builder()
                .alertType(alertType.name())
                .lenghtOfWorks(null)
                .eventStart(null)
                .launchDateTime(null)
                .enabled(false)
                .build());
    }
}
