package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Conditional(MonolitConditional.class)
@Log4j2 (topic = "aisi_log")
public class AisiRecieveService {

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private AisiCurrencyService aisiCurrencyService;

    @Autowired
    private AisiService aisiService;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private RefillService refillService;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(AisiServiceImpl.MERCHANT_NAME);
        merchant = merchantService.findByName(AisiServiceImpl.MERCHANT_NAME);
    }

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 1000 * 60 * 5)
    void checkIncomePayment() {
        log.info("*** Aisi *** Scheduler start");
        List<String> listOfAddress = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());

    try {
        aisiCurrencyService.getAccountTransactions().stream()
                .forEach(transaction ->
            aisiService.checkAddressForTransactionReceive(listOfAddress, transaction));
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
