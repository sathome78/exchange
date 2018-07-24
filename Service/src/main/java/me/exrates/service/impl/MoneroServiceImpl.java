package me.exrates.service.impl;

import jota.IotaAPI;
import jota.dto.response.GetNodeInfoResponse;
import jota.model.Bundle;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.utils.Checksum;
import jota.utils.IotaUnitConverter;
import jota.utils.IotaUnits;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.*;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wallet.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by ajet
 */
@Log4j2(topic = "monero_log")
public class MoneroServiceImpl implements MoneroService {

    private MoneroWallet wallet;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RefillService refillService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    private String HOST;
    private String PORT;
    private String LOGIN;
    private String PASSWORD;
    private String MODE;

    private List<String> ADDRESSES = new ArrayList<>();

    private Merchant merchant;

    private Currency currency;

    private String merchantName;

    private String currencyName;

    private Integer minConfirmations;

    private Integer decimals;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final int INTEGRATED_ADDRESS_DIGITS = 16;

    public MoneroServiceImpl(String propertySource, String merchantName, String currencyName, Integer minConfirmations, Integer decimals) {

        Properties props = new Properties();

        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.HOST = props.getProperty("monero.host");
            this.PORT = props.getProperty("monero.port");
            this.LOGIN = props.getProperty("monero.login");
            this.PASSWORD = props.getProperty("monero.password");
            this.MODE = props.getProperty("monero.mode");
            this.merchantName = merchantName;
            this.currencyName = currencyName;
            this.minConfirmations = minConfirmations;
            this.decimals = decimals;
        } catch (IOException e) {
            log.error(e);
        }

    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        return new HashMap<>();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

        BigDecimal amount = new BigDecimal(params.get("amount"));

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(params.get("address"))
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(params.get("hash"))
                .build();

        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        refillService.autoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    @Transactional
    public Map<String, String> refill(RefillRequestCreateDto request) {

        Map<String, String> mapAddress = new HashMap<>();
        String address = "";
        String pubKey = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
            Date date = new Date();
            String dateString = dateFormat.format(date) + String.valueOf(request.getUserId());
            for (int i=dateString.length(); i<INTEGRATED_ADDRESS_DIGITS; i++){
                dateString += "0";
            }
            pubKey = dateString;
            address = String.valueOf(wallet.getIntegratedAddress(dateString));
            ADDRESSES.add(address);

        } catch (Exception e) {
            log.error(e);
        }

        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());

        mapAddress.put("message", message);
        mapAddress.put("address", address);
        mapAddress.put("qr", mapAddress.get("address"));
        mapAddress.put("pubKey", pubKey);

        return mapAddress;
    }

    @PostConstruct
    public void init(){

        currency = currencyService.findByName(currencyName);
        merchant = merchantService.findByName(merchantName);

        ADDRESSES = refillService.findAllAddresses(merchant.getId(), currency.getId());

        if (MODE.equals("main")){
            log.info(merchantName + " starting...");
            try {
                wallet = new MoneroWalletRpc(HOST, Integer.parseInt(PORT), LOGIN, PASSWORD);
                log.info(merchantName + " started");
                scheduler.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        checkIncomingTransactions();
                    }
                }, 0, 60, TimeUnit.MINUTES);
            }catch (Exception e){
                log.error(e);
            }
        }else {
            log.info(merchantName + " test mode...");
        }
    }

    private void checkIncomingTransactions(){
        try {
            log.info(merchantName + ": Checking transactions...");
            log.info(new java.util.Date());
            HashMap<String,String> mapAddresses = new HashMap<>();
            Set<String> payments = new HashSet<>();

            log.info(ADDRESSES.toString());
            for (String address : ADDRESSES){
                log.info(address.toString());
                String paymentId = wallet.splitIntegratedAddress(address).getPaymentId();
                mapAddresses.put(paymentId, address);
            }

            List<MoneroTransaction> transactions = wallet.getTransactions(true, false, false, false, false, mapAddresses.keySet(), (Integer)null, (Integer)null);
            for (MoneroTransaction transaction : transactions){
                    try {
                        log.info(transaction.toString());
                        String integratedAddress = mapAddresses.get(transaction.getPaymentId());
                        log.info("integratedAddress: " + integratedAddress);
                        log.info(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(integratedAddress,merchant.getId(),currency.getId(),transaction.getHash()));
                        if ((transaction.getType().equals("INCOMING")) || !transaction.getUnlockTime().equals(0)
                                || refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(integratedAddress,merchant.getId(),currency.getId()
                                ,transaction.getHash()).isPresent() || (!ADDRESSES.contains(integratedAddress))){
                            continue;
                        }
                        int confirmations = wallet.getHeight() - transaction.getHeight();
                        log.info("confirmations:" + confirmations);
                        if (confirmations < minConfirmations){
                            continue;
                        }

                        Double amount = transaction.getPayments().get(0).getAmount().doubleValue()/ Math.pow(10.0D, (double)decimals);

                        Map<String, String> mapPayment = new HashMap<>();
                        mapPayment.put("address", integratedAddress);
                        mapPayment.put("hash", transaction.getHash());
                        mapPayment.put("amount", String.valueOf(amount));

                        processPayment(mapPayment);
                    }catch (Exception e){
                        log.error(e);
                    }
            }

            log.info(new java.util.Date());

        } catch (Exception e) {
            log.error(e);
        }
    }

    @PreDestroy
    private void shutdown() {
        log.debug("Destroying " + merchantName);
        scheduler.shutdown();
        log.debug("Destroyed " + merchantName);
    }
}
