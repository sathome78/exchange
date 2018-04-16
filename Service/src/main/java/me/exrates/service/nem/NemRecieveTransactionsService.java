package me.exrates.service.nem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.NemMosaicTransferDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nem.core.messages.PlainMessage;
import org.nem.core.model.Account;
import org.nem.core.model.Address;
import org.nem.core.serialization.DeserializationContext;
import org.nem.core.serialization.JsonDeserializer;
import org.nem.core.serialization.SimpleAccountLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

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
    @Autowired
    private NemMosaicStrategy mosaicStrategy;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String LAST_HASH_PARAM = "LastRecievedTrHash";
    private static final String MERCHANT_NAME = "NEM";

    DeserializationContext deserializationContext = new DeserializationContext(new SimpleAccountLookup() {
        @Override
        public Account findByAddress(Address address) {
            return nemService.getAccount();
        }
    });

    private @Value("${nem.address}")String address;


    @Scheduled(initialDelay = 1000, fixedRate = 1000 * 60 * 4)
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

            String trHash = params.get("hash");
            if (trHash.equals(lastHash)) {
                return null;
            }
            if (i == 0 && pagingHash == null) {
                saveLastHash(trHash);
            }
            log.debug("mosaics {}", params.get("mosaics"));
            double amountD = Double.valueOf(params.get("amount"));
            log.debug("amountD {}", amountD);
            if (amountD == 0) {
                continue;
            }
            if (params.get("mosaics") != null && amountD != 1) {
                try {
                    List<NemMosaicTransferDto> mosaics = getMosaicPayments(params);
                    nemService.processMosaicPayment(mosaics, params);
                } catch (Exception e) {
                    log.error("nem mosaic refill process error {} {}", e, transactionData.toString());
                }
            } else {
                try {
                    nemService.processPayment(params);
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error("nem refill address not found {}", transactionData.toString());
                } catch (Exception e) {
                    log.error("nem refill process error {} {}", e, transactionData.toString());
                }
            }
            if (i == 24) {
                return trHash;
            }
        }
        return null;
    }

    private List<NemMosaicTransferDto> getMosaicPayments(Map<String, String> params) throws IOException {
        List<NemMosaicTransferDto> dtos;
            dtos = Lists.newArrayList(
                    objectMapper.readValue(params.get("mosaics"), NemMosaicTransferDto[].class));
            log.debug("dtos. before size {}", dtos.size());
            dtos.removeIf(p -> mosaicStrategy.getByIdDto(p.getMosaicIdDto()) == null);
            log.debug("dtos. size {}", dtos.size());
            dtos.forEach(p->{
                XemMosaicService service = mosaicStrategy.getByIdDto(p.getMosaicIdDto());
                if(service == null) {
                    dtos.remove(p);
                } else {
                    p.setService(service);
                }
            });
            return dtos;
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
        paramsMap.put("mosaics", transaction.optString("mosaics", null));
        return paramsMap;
    }

    private void saveLastHash(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_HASH_PARAM, hash);
    }

    private String loadLastHash() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_HASH_PARAM);
        return specParamsDto == null ? null : specParamsDto.getParamValue();
    }
}
