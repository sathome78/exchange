package me.exrates.service.nem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.MosaicIdDto;
import me.exrates.model.dto.NemMosaicTransferDto;
import me.exrates.service.exception.NemTransactionException;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by maks on 21.07.2017.
 */
@Log4j2(topic = "nem_log")
@Service
@PropertySource("classpath:/merchants/nem.properties")
@Conditional(MonolitConditional.class)
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

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkTransactions, 3, 5, TimeUnit.MINUTES);
    }


    public synchronized void checkTransactions() {
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
            log.info("transaction {}", transactionData);
            String trHash = params.get("hash");
            if (trHash.equals(lastHash)) {
                return null;
            }
            if (i == 0 && pagingHash == null) {
                saveLastHash(trHash);
            }
            saveLastHash(trHash);
            log.debug("mosaics {}", params.get("mosaics"));
            Double amountD = Double.valueOf(params.get("amount"));
            log.debug("amountD {}", amountD);
            if (amountD.equals(0d)) {
                continue;
            }
            if (params.get("mosaics") != null) {
                if (!amountD.equals(1d)) {
                    continue;
                }
                try {
                    List<NemMosaicTransferDto> mosaics = getMosaicPayments(params);
                    checkOwnedMosaics(params.get("signer"));
                    nemService.processMosaicPayment(mosaics, params);
                } catch (Exception e) {
                    log.error("nem mosaic refill process error {} {}", e, transactionData.toString());
                }
            } else {
                try {
                    checkOwnedMosaics(params.get("signer"));
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

    private void checkOwnedMosaics(String publicKey) {
        try {
            String address = nodeService.getAddressByPk(publicKey);
            JSONArray array = nodeService.getOwnedMosaics(address);
            array.forEach(p -> {
                JSONObject object = ((JSONObject) p).getJSONObject("mosaicId");
                MosaicIdDto idDto = new MosaicIdDto(object.getString("namespaceId"), object.getString("name"));
                if (nemService.getDeniedMosaicList().contains(idDto)) {
                    log.warn("sender contains denied mosaic!!! {} ", idDto);
                    throw new NemTransactionException("sender contains denied mosaic " + idDto);
                }
            });
        } catch (NemTransactionException e) {
            throw e;
        } catch (Exception e) {
            log.warn("can't check owned mosaics");
        }
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
        paramsMap.put("signer", transaction.getString("signer"));
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
