package me.exrates.service.ieo;

import me.exrates.model.IEOClaim;
import org.springframework.scheduling.annotation.Scheduled;

public interface IEOQueueService {

    @Scheduled(fixedDelay = 1000)
    void processClaims();

    boolean add(IEOClaim claim);
}
