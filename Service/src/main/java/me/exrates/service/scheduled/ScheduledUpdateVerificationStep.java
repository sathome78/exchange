package me.exrates.service.scheduled;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.service.UserService;
import me.exrates.service.impl.ShuftiProKYCService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@EnableScheduling
@PropertySource(value = {"classpath:/scheduler.properties"})
@Component
public class ScheduledUpdateVerificationStep {

    private final UserService userService;
    private final ShuftiProKYCService kycService;

    @Autowired
    public ScheduledUpdateVerificationStep(UserService userService,
                                           ShuftiProKYCService kycService) {
        this.userService = userService;
        this.kycService = kycService;
    }

    @Scheduled(cron = "${scheduled.update.verification-step}")
    public void updateVerificationStepScheduler() {
        List<String> referenceIds = userService.getAllUsersReferenceIds();

        referenceIds.forEach(referenceId -> {
            Pair<String, EventStatus> statusPair = kycService.getVerificationStatus(referenceId);

            kycService.updateVerificationStep(statusPair.getLeft(), statusPair.getRight());
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException ex) {
                log.debug("Delay interrupted!", ex);
            }
        });
    }
}