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
public class ScheduledExternalMainWalletBalances {

    private final WalletService walletService;

    @Autowired
    public ScheduledExternalMainWalletBalances(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(cron = "${scheduled.update.external-balances}")
    public void update() {
        try {
            walletService.updateExternalMainWalletBalances();
        } catch (Exception ex) {
            log.error("--> In processing 'ScheduledExternalMainWalletBalances' occurred error", ex);
        }
    }
}
