package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.RefillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by ValkSam
 */
@Service
@Log4j2
public class RefillRequestJob {

  @Autowired
  RefillService refillService;

  @Scheduled(initialDelay = 180000, fixedDelay = 1000 * 60 * 5)
  private void refillExpiredClean() throws Exception {
    log.debug("\nstart expired refill cleaning ... ");
    Integer expireCount = refillService.clearExpiredInvoices();
    log.debug("\n... end expired refill cleaning. Mark as expired: " + expireCount);
  }

}



