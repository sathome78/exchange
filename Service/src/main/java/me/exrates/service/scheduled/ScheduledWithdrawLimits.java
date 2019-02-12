package me.exrates.service.scheduled;

import me.exrates.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@PropertySource(value = {"classpath:/scheduler.properties"})
@Service
public class ScheduledWithdrawLimits {

    private final CurrencyService currencyService;

    @Autowired
    public ScheduledWithdrawLimits(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(cron = "${scheduled.update.withdraw-limits}")
    public void update() {
        currencyService.updateWithdrawLimits();
    }
}