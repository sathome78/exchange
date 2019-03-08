package me.exrates.service.aidos;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.service.RefillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "adk_log")
@Service
@Conditional(MonolitConditional.class)
public class AdkTransactionsServiceImpl implements TransactionsCheckService {


    private final AidosNodeService aidosNodeService;
    private final AdkService adkService;
    private final MerchantSpecParamsDao specParamsDao;
    private final RefillService refillService;
    private final ObjectMapper objectMapper;


    private static final String LAST_BLOCK_PARAM = "LastBundle";
    private static final Integer CONFIRMATION_VALUE = 100000;
    private static final String RECEIVE_CATEGORY_VALUE = "receive";
    private static final String MERCHANT_NAME = "ADK";
    private static final Integer TX_SCAN_COUNT = 10;


    private ScheduledExecutorService txScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService unconfScheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public AdkTransactionsServiceImpl(AidosNodeService aidosNodeService, AdkService adkService, MerchantSpecParamsDao specParamsDao, RefillService refillService, ObjectMapper objectMapper) {
        this.aidosNodeService = aidosNodeService;
        this.adkService = adkService;
        this.specParamsDao = specParamsDao;
        this.refillService = refillService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init() {
        txScheduler.scheduleAtFixedRate(this::checkTransactions, 0, 10, TimeUnit.MINUTES);
        unconfScheduler.scheduleAtFixedRate(this::checkUnconfirmedJob, 10, 15, TimeUnit.MINUTES);
    }



    private void checkTransactions() {
        try {
            log.info("start check transactions");
            int offset = 0;
            String lastBundle = loadLastBundle();
            log.info("lastBundle {}", lastBundle);
            List<TxReceivedByAddressFlatDto> transactions;
            do {
                transactions = objectMapper.readValue(aidosNodeService.getAllTransactions(TX_SCAN_COUNT, offset).toString(), new TypeReference<List<TxReceivedByAddressFlatDto>>(){});
                if (!transactions.isEmpty() && offset == 0) {
                    saveLastBundle(transactions.get(0).getTxId());
                }
                transactions.forEach(p -> {
                    log.info("tx {}", p);
                    if (p.getTxId().equals(lastBundle)) {
                        throw new RuntimeException("No new transactions");
                    }
                    try {
                        BtcTransactionDto transactionDto = aidosNodeService.getTransaction(p.getTxId());
                        log.info("tx dto {}", transactionDto);
                        if (p.getCategory().equals(RECEIVE_CATEGORY_VALUE) && transactionDto.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
                            RefillRequestAcceptDto requestDto = adkService.createRequest(p.getAddress(), p.getTxId(), p.getAmount());
                            refillService.invalidateAddress(requestDto.getAddress(), adkService.getMerchant().getId(), adkService.getCurrency().getId());
                            if (p.getConfirmations().equals(CONFIRMATION_VALUE)) {
                                processTransaction(p.getAddress(), p.getTxId(), p.getAmount().toString());
                            } else {
                                adkService.putOnBchExam(requestDto);
                            }
                        }
                    } catch (RuntimeException e) {
                        log.error(e);
                    }
                });
                offset += TX_SCAN_COUNT;
            } while (transactions.size() >= TX_SCAN_COUNT);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void processTransaction(String address, String hash, String amount) {
        try {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("txId", hash);
            paramsMap.put("address", address);
            paramsMap.put("amount", amount);
            adkService.processPayment(paramsMap);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void checkUnconfirmedJob() {
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(adkService.getMerchant().getId(), adkService.getCurrency().getId());
        dtos.forEach(p->{
            try {
                if (isTransactionConfirmed(p.getMerchantTransactionId())) {
                    processTransaction(p.getAddress(), p.getMerchantTransactionId(), p.getAmount().toString());
                }
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private boolean isTransactionConfirmed(String txHash) {
        return aidosNodeService.getTransaction(txHash).getConfirmations().equals(CONFIRMATION_VALUE);
    }

    private void saveLastBundle(String bundle) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, bundle);
    }

    private String loadLastBundle() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto.getParamValue();
    }


}
