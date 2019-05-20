package me.exrates.service.tron;


import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.model.dto.TronTransactionTypeEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "tron")
@Service
@Conditional(MonolitConditional.class)
public class TronReceiveServiceImpl {

    private final TronNodeService nodeService;
    private final TronServiceImpl tronService;
    private final MerchantSpecParamsDao specParamsDao;
    private final TronTransactionsService tronTransactionsService;
    private final TronTokenContext tronTokenContext;

    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";
    private static final String MERCHANT_NAME = "TRX";
    private static final String CURRENCY_NAME = "TRX";
    private static final int TRX_DECIMALS = 6;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public TronReceiveServiceImpl(TronNodeService nodeService, TronServiceImpl tronService, MerchantSpecParamsDao specParamsDao, TronTransactionsService tronTransactionsService, TronTokenContext tronTokenContext) {
        this.nodeService = nodeService;
        this.tronService = tronService;
        this.specParamsDao = specParamsDao;
        this.tronTransactionsService = tronTransactionsService;
        this.tronTokenContext = tronTokenContext;
    }


    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkBlocks, 5, 5, TimeUnit.MINUTES);
    }

    private void checkBlocks() {
        try {
            log.debug("tron start check blocks");
            long lastScannedBlock = loadLastBlock();
            long blockchainHeight = getLastBlockNum() - 10;
            log.debug("last scanned block {} height {}", lastScannedBlock, blockchainHeight);
            while (lastScannedBlock < blockchainHeight) {
                JSONObject object = nodeService.getTransactions(lastScannedBlock++);
                List<TronReceivedTransactionDto> transactionDtos = parseResponse(object);
                checkTransactionsAndProceed(transactionDtos);
                saveLastBlock(lastScannedBlock);
            }
        } catch (Exception e) {
            /*ignore*/
        }
    }

    private void checkTransactionsAndProceed(List<TronReceivedTransactionDto> transactionDtos) {
        transactionDtos.forEach(p->{
            if(tronService.getAddressesHEX().contains(p.getAddress())) {
                try {
                    switch (p.getTxType()) {
                        case TransferContract: {
                            p.setAmount(parseAmount(p.getRawAmount(), TRX_DECIMALS));
                            p.setMerchantId(tronService.getMerchantId());
                            p.setCurrencyId(tronService.getCurrencyId());
                            break;
                        }
                        case TransferAssetContract: {
                            TronTrc10Token token = tronTokenContext.getByNameTx(p.getAssetName());
                            p.setAmount(parseAmount(p.getRawAmount(), token.getDecimals()));
                            p.setMerchantId(token.getMerchantId());
                            p.setCurrencyId(token.getCurrencyId());
                            break;
                        }
                        default: throw new RuntimeException("unsupported tx type");
                    }
                    setAdditionalTxInfo(p);
                    RefillRequestAcceptDto dto = tronService.createRequest(p);
                    p.setId(dto.getRequestId());
                    if (p.isConfirmed()) {
                        tronTransactionsService.processTransaction(p);
                    } else {
                        tronService.putOnBchExam(dto);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
    }

    private List<TronReceivedTransactionDto> parseResponse(JSONObject rawResponse) {
        if(rawResponse.isNull("transactions")) {
            return new ArrayList<>();
        }
        JSONArray transactions = rawResponse.getJSONArray("transactions");
        return parseTransactions(transactions);
    }

    private void setAdditionalTxInfo(TronReceivedTransactionDto dto) throws Exception {
        JSONObject rawResponse = nodeService.getTransaction(dto.getHash());
        JSONObject contractData = rawResponse.getJSONObject("contractData");
        if (dto.getRawAmount() != contractData.getLong("amount")) {
            throw new Exception("incorrect amount " + dto.getHash());
        }
        if (dto.getTxType().getContractType() != rawResponse.getInt("contractType")) {
            throw new Exception("incorrect contractType " + dto.getTxType());
        }
        dto.setAddressBase58(rawResponse.getString("toAddress"));
        dto.setConfirmed(rawResponse.getBoolean("confirmed"));
    }

    private List<TronReceivedTransactionDto> parseTransactions(JSONArray transactions) {
        List<TronReceivedTransactionDto> list = new ArrayList<>();
        transactions.forEach(p->{
            try {
                list.add(fromJson((JSONObject) p));
            } catch (Exception e) {
                log.error(e);
            }
        });
        return list;
    }

    private TronReceivedTransactionDto fromJson(JSONObject transaction) {
        Preconditions.checkArgument(transaction.getJSONArray("ret").getJSONObject(0).getString("contractRet").equals("SUCCESS"), "contract result not success");
        JSONObject contractData = transaction.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0);
        String type = contractData.getString("type");
        TronTransactionTypeEnum txType = TronTransactionTypeEnum.valueOf(type);
        JSONObject parameters = contractData.getJSONObject("parameter").getJSONObject("value");
        TronReceivedTransactionDto dto = new TronReceivedTransactionDto(parameters.getLong("amount"), transaction.getString("txID"), parameters.getString("to_address"));
        dto.setRawAmount(parameters.getLong("amount"));
        dto.setTxType(txType);
        dto.setAssetName(parameters.optString("asset_name"));
        return dto;
    }

    private static String parseAmount(long amount, Integer decimals) {
        return new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, -decimals))).setScale(decimals, RoundingMode.HALF_DOWN).toPlainString();
    }

    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }

    private long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private long getLastBlockNum() {
        JSONObject jsonObject = nodeService.getLastBlock();
        return jsonObject.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
    }

}
