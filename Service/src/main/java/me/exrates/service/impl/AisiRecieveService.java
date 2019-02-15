package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.AisiCurrencyService;
import me.exrates.service.impl.AisiCurrencyServiceImpl.Transaction;
import me.exrates.service.AisiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Log4j2(topic = "Aisi")
public class AisiRecieveService {

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private AisiCurrencyService aisiCurrencyService;

    @Autowired
    private AisiService aisiService;

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 1000 * 60 * 2)
    void checkIncomePayment() {
        log.info("*** Aisi *** Scheduler start");

        aisiCurrencyService.getAccountTransactions().stream().forEach(transaction -> {
            aisiService.onTransactionReceive(transaction);
        });
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
