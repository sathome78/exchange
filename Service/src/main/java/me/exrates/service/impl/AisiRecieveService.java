package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.AisiCurrencyService;
import me.exrates.service.AisiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Set;
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

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 1000 * 30 * 1)
    void checkIncomePayment() {
        log.info("*** Aisi *** Scheduler start");
// address filter
// dublicate hash

        Set<AisiCurrencyServiceImpl.Transaction> set;
    try {
        aisiCurrencyService.getAccountTransactions().stream()
                .filter(trans -> settrans.getRecieverAddress() ).forEach(transaction ->
            aisiService.onTransactionReceive(transaction));
    } catch (Exception e){
        e.getStackTrace();
        log.error(e.getMessage());
    }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }


}
