package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.service.CurrencyService;
import me.exrates.service.GapiCurrencyService;
import me.exrates.service.GapiService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Log4j2 (topic = "gapi_log")
public class GapiRecieveService {

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private GapiCurrencyService gapiCurrencyService;

    @Autowired
    private GapiService gapiService;

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
        currency = currencyService.findByName(GapiServiceImpl.MERCHANT_NAME);
        merchant = merchantService.findByName(GapiServiceImpl.MERCHANT_NAME);
    }

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 1000 * 30 * 1)
    void checkIncomePayment() {
        log.info("*** GAPI *** Scheduler start");
        List<String> listOfAddress = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());

        try {
            gapiCurrencyService.getAccountTransactions().stream()
                    .forEach(transaction -> gapiService.checkAddressForTransactionReceive(listOfAddress, transaction));
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
