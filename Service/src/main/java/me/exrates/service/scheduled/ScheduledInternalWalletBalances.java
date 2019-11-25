package me.exrates.service.scheduled;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@EnableScheduling
@PropertySource(value = {"classpath:/scheduler.properties"})
@Service
public class ScheduledInternalWalletBalances {

    private final WalletService walletService;

    @Autowired
    public ScheduledInternalWalletBalances(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(cron = "${scheduled.update.internal-balances}")
    public void update() {
        try {
            walletService.updateInternalWalletBalances();
        } catch (Exception ex) {
            log.error("--> In processing 'ScheduledInternalWalletBalances' occurred error", ex);
        }
    }
}
