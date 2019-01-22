package me.exrates.service.apollo;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "apollo")
@PropertySource("classpath:/merchants/apollo.properties")
@Component
public class ApolloReceiveServiceImpl {


    private @Value("${apollo.main_address}")String MAIN_ADDRESS;
    private static final String PARAM_NAME = "LastBlockTime";
    private static final String MERCHANT_NAME = "APL";
    private static final long GENESIS_TIME = 1515931200;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private ApolloNodeService apolloNodeService;
    @Autowired
    private ApolloService apolloService;
    @Autowired
    private RefillService refillService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService unconfirmedScheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkTransactions, 3, 5, TimeUnit.MINUTES);
        unconfirmedScheduler.scheduleAtFixedRate(this::checkUnconfirmed, 5, 8, TimeUnit.MINUTES);
    }

    private void checkTransactions() {
        try {
            log.debug("start check apl transactions");
            long lastBLockTime = loadLastBlockTime();
            JSONArray transactions = new JSONObject(apolloNodeService.getTransactions(MAIN_ADDRESS, lastBLockTime)).getJSONArray("transactions");
            log.debug("txs {}", transactions);
            if (transactions.length() > 0) {
                long lastTxBlockTimestamp = transactions.getJSONObject(0).getLong("blockTimestamp");
                saveLastBlockTime(lastTxBlockTimestamp + 1L);
            }
            transactions.forEach(p -> {
                try {
                    JSONObject tx = (JSONObject) p;
                    String sender = tx.getString("senderRS");
                    String recipient = tx.getString("recipientRS");
                    JSONObject attachment = tx.getJSONObject("attachment");
                    if (!tx.getBoolean("phased")
                            && attachment.has("message")
                            && attachment.getBoolean("messageIsText")
                            && !sender.equalsIgnoreCase(MAIN_ADDRESS)
                            && recipient.equalsIgnoreCase(MAIN_ADDRESS)) {
                        String hash = tx.getString("fullHash");
                        BigDecimal amount = parseAmount(tx.getString("amountATM"));
                        String address = attachment.getString("message");
                        RefillRequestAcceptDto requestAcceptDto = apolloService.createRequest(address, amount, hash);
                        if (needConfirmations(tx)) {
                            apolloService.putOnBchExam(requestAcceptDto);
                        } else {
                            try {
                                apolloService.processPayment(new HashMap<String, String>() {{
                                    put("address", address);
                                    put("hash", hash);
                                    put("amount", amount.toPlainString());
                                    put("id", String.valueOf(requestAcceptDto.getRequestId()));
                                }});
                            } catch (RefillRequestAppropriateNotFoundException e) {
                                log.error(e);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            });
        } catch (JSONException e) {
            log.error(e);
        }
    }


    private void checkUnconfirmed() {
        log.debug("check unconfirmed apl ");
        try {
            List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(apolloService.getMerchant().getId(), apolloService.getCurrency().getId());
            dtos.forEach(p->{
                log.debug("unconfirmed {}", p);
                try {
                    JSONObject tx = getTransaction(p.getMerchantTransactionId());
                    if (!needConfirmations(tx)) {
                        apolloService.processPayment(new HashMap<String, String>() {{
                            put("address", p.getAddress());
                            put("hash", p.getMerchantTransactionId());
                            put("amount", p.getAmount().toPlainString());
                            put("id", String.valueOf(p.getId()));
                        }});
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            });
        } catch (Exception e) {
            log.error(e);
        }
    }


    private JSONObject getTransaction(String txHash) {
        return new JSONObject(apolloNodeService.getTransaction(txHash));
    }

    private BigDecimal parseAmount(String amount) {
        return new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, -8))).setScale(8, RoundingMode.HALF_DOWN);
    }

    private boolean needConfirmations(JSONObject tx) {
        long confirmations = tx.getLong("confirmations");
        long txTimestamp = tx.getLong("timestamp");
        long blockTimestamp = tx.getLong("blockTimestamp");
        long deadLine = tx.getLong("deadline");
        if ((txTimestamp + (deadLine * 60)) > (blockTimestamp + 82800L)) {
            return confirmations <= 10;
        } else {
            return confirmations < 720;
        }
    }

    private void saveLastBlockTime(long lastblockTime) {
        specParamsDao.updateParam(MERCHANT_NAME, PARAM_NAME, String.valueOf(lastblockTime));
    }

    private long loadLastBlockTime() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, PARAM_NAME);
        return specParamsDto.getParamValue() == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }


}
