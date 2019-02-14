package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.AisiCurrencyService;
import me.exrates.service.AisiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    private void checkIncomePayment() {
        log.info("*** Aisi *** Scheduler start");

        aisiCurrencyService.getAccountTransactions();

        qiwiExternalService.getLastTransactions().stream()
                .filter(trans -> trans.getTx_type().equals(TRANSACTION_TYPE)
                        && trans.getTx_status().equals(TRANSACTION_STATUS)).forEach(transaction -> {
            try {
                log.info("*** Qiwi *** Process transaction");
                qiwiService.onTransactionReceive(transaction, transaction.getAmount(), transaction.getCurrency(), transaction.getProvider());
                log.info("*** Qiwi *** After process transaction");
            }catch (Exception ex){
                ex.getStackTrace();
                log.error(ex.getMessage());
            }
        });
        log.info("*** Qiwi ** Get transactions for process");
    }

}
