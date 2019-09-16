package me.exrates.security.service.scheduled;

import lombok.extern.log4j.Log4j2;
import me.exrates.security.service.AuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@EnableScheduling
@Service
public class ScheduledDeleteExpiredTokens {

    private final AuthTokenService authTokenService;

    @Autowired
    public ScheduledDeleteExpiredTokens(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 12 * 60 * 60 * 1000)
    public void delete() {
        try {
            authTokenService.deleteExpiredTokens();
        } catch (Exception ex) {
            log.error("--> In processing 'ScheduledDeleteExpiredTokens' occurred error", ex);
        }
    }
}