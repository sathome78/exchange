package me.exrates.service.nem;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nem.core.messages.PlainMessage;
import org.nem.core.model.Account;
import org.nem.core.model.Address;
import org.nem.core.model.TransferTransactionAttachment;
import org.nem.core.serialization.DeserializationContext;
import org.nem.core.serialization.JsonDeserializer;
import org.nem.core.serialization.SimpleAccountLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maks on 21.07.2017.
 */
@Log4j2(topic = "nem_log")
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemRecieveTransactionsService {

    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private NemNodeService nodeService;
    @Autowired
    private NemTransactionsService transactionsService;
    @Autowired
    private NemService nemService;

    private static final String LAST_HASH_PARAM = "LastRecievedTrHash";
    private static final String MERCHANT_NAME = "NEM";

    DeserializationContext deserializationContext = new DeserializationContext(new SimpleAccountLookup() {
        @Override
        public Account findByAddress(Address address) {
            return nemService.getAccount();
        }
    });

    private @Value("${nem.address}")String address;


    /*@Scheduled(initialDelay = 1000, fixedRate = 1000 * 60 * 4)*/
    public void checkTransactions() {
        log.debug("starting check nem income payments");
        String lastHash = loadLastHash();
        String pagingHash = null;
        do {
            JSONArray transactions = nodeService.getIncomeTransactions(address, pagingHash);
            pagingHash = processTransactions(transactions, lastHash, pagingHash);
        } while (!StringUtils.isEmpty(pagingHash));
    }

    private String processTransactions(JSONArray transactions, String lastHash, String pagingHash) {
        for (int i = 0; transactions.opt(i) != null; i++) {
            JSONObject transactionData = transactions.getJSONObject(i);
            Map<String, String> params = extractParams(transactionData);
            /*check for mosaic transaction and*/
            if (params.get("mosaics") != null) {
                continue;
            }
            String trHash = params.get("hash");
            if (trHash.equals(lastHash)) {
                return null;
            }
            if (i == 0 && pagingHash == null) {
                saveLastHash(trHash);
            }
            try {
                nemService.processPayment(params);
            } catch (RefillRequestAppropriateNotFoundException e) {
                log.error("nem refill address not found {}", transactionData.toString());
            } catch (Exception e) {
                log.error("nem refill process error {} {}", e,transactionData.toString());
            }
            if (i == 24) {
                return trHash;
            }
        }
        return null;
    }

    private Map<String, String> extractParams(JSONObject transactionMetaPair) {
        JSONObject meta = transactionMetaPair.getJSONObject("meta");
        JSONObject transaction = transactionMetaPair.getJSONObject("transaction");
        String message = null;
        try {
            net.minidev.json.JSONObject object = new net.minidev.json.JSONObject();
            object.put("payload", transaction.getJSONObject("message").getString("payload"));
            PlainMessage plainMessage = new PlainMessage(new JsonDeserializer(object, deserializationContext));
            message = new String(plainMessage.getEncodedPayload());
            log.debug(message);
        } catch (Exception e) {
            log.error("unsupported encoding {}", e);
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", meta.getJSONObject("hash").getString("data"));
        paramsMap.put("address", message);
        paramsMap.put("amount", transactionsService.transformToString(transaction.getLong("amount")));
        paramsMap.put("transaction", transactionMetaPair.toString());
        paramsMap.put("mosaics", transaction.has("mosaics") ? "" : null);
        return paramsMap;
    }

    private void saveLastHash(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_HASH_PARAM, hash);
    }

    private String loadLastHash() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantIdAndParamName(MERCHANT_NAME, LAST_HASH_PARAM);
        return specParamsDto == null ? null : specParamsDto.getParamValue();
    }
}
