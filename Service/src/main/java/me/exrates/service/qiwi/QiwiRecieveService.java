package me.exrates.service.qiwi;

import lombok.extern.log4j.Log4j;
import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log4j
@PropertySource("classpath:/merchants/qiwi.properties")
public class QiwiRecieveService {

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static final String TRANSACTION_TYPE = "EXTERNAL-MERCHANT";
    private static final String TRANSACTION_STATUS = "APPROVED";

//    @Value("stellar.horizon.url")
    private String as;

    @Autowired
    private QiwiExternalService qiwiExternalService;

    @Autowired
    private QiwiService qiwiService;

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 1000 * 60)
    private void checkIncomePayment() {
        log.info("*** Qiwi *** Scheduler start");

        qiwiExternalService.getLastTransactions().stream()
                .filter(trans -> trans.getTx_type().equals(TRANSACTION_TYPE)
                        && trans.getTx_status().equals(TRANSACTION_STATUS)).forEach(transaction -> {
            try {
                log.info("*** Qiwi *** Process transaction");
                qiwiService.onTransactionReceive(transaction, transaction.getAmount(), transaction.getCurrency(), transaction.getProvider());
                // Record the paging token so we can start from here next time.
                log.info("*** Qiwi *** transaction - currency:"+transaction.getProvider()+" | hash:"+ transaction.get_id()+" Saved");
            }catch (Exception ex){
                ex.getStackTrace();
                log.error(ex.getMessage());
            }
        });
        log.info("*** Qiwi ** Get transactions for process");
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

}
