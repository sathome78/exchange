package me.exrates.service.tron;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.model.dto.TronTransactionTypeEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "tron")
@Service
@Conditional(MonolitConditional.class)
public class TronReceiveServiceImpl {

    private final TronNodeService nodeService;
    private final TronService tronService;
    private final MerchantSpecParamsDao specParamsDao;
    private final TronTransactionsService tronTransactionsService;
    private final TronTokenContext tronTokenContext;

    private MerchantService merchantService;
    private CurrencyService currencyService;

    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";
    private static final String MERCHANT_NAME = "TRX";
    private static final String CURRENCY_NAME = "TRX";
    private static final int TRX_DECIMALS = 6;

    private static Map<String, Trc20TokenService> tokenTrc20Map = new HashMap<String, Trc20TokenService>(){{
        put("USDT", new Trc20TokenServiceImpl("USDT(TRX)", "USDT(TRX)"));
        put("WIN", new Trc20TokenServiceImpl("WIN", "WIN"));
    }};

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public TronReceiveServiceImpl(TronNodeService nodeService, TronService tronService, MerchantSpecParamsDao specParamsDao, TronTransactionsService tronTransactionsService,
                                  TronTokenContext tronTokenContext, MerchantService merchantService, CurrencyService currencyService) {
        this.nodeService = nodeService;
        this.tronService = tronService;
        this.specParamsDao = specParamsDao;
        this.tronTransactionsService = tronTransactionsService;
        this.tronTokenContext = tronTokenContext;
        this.merchantService = merchantService;
        this.currencyService = currencyService;
    }

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkBlocks, 0, 5, TimeUnit.MINUTES);
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
        transactionDtos.forEach(transaction->{
            if(tronService.getAddressesHEX().contains(transaction.getAddress())) {// TODO
                try {
                    switch (transaction.getTxType()) {
                        case TransferContract: {
                            transaction.setAmount(parseAmount(transaction.getRawAmount(), TRX_DECIMALS));
                            transaction.setMerchantId(tronService.getMerchantId());
                            transaction.setCurrencyId(tronService.getCurrencyId());
                            setAdditionalTxInfo(transaction);
                            break;
                        }
                        case TransferAssetContract: {
                            TronTrc10Token token = tronTokenContext.getByNameTx(transaction.getAssetName());
                            transaction.setAmount(parseAmount(transaction.getRawAmount(), token.getDecimals()));
                            transaction.setMerchantId(token.getMerchantId());
                            transaction.setCurrencyId(token.getCurrencyId());
                            setAdditionalTxInfo(transaction);
                            break;
                        }
                        case TriggerSmartContract: {
                            if (tokenTrc20Map.containsKey(transaction.getAssetName())){
                                Trc20TokenService trc20TokenService = tokenTrc20Map.get(transaction.getAssetName());
                                transaction.setMerchantId(merchantService.findByName(trc20TokenService.getMerchantName()).getId());
                                transaction.setCurrencyId(currencyService.findByName(trc20TokenService.getMerchantName()).getId());
                                transaction.setAmount(parseAmount(transaction.getRawAmount(), TRX_DECIMALS));
                            }
                            break;
                        }
                        default: throw new RuntimeException("unsupported tx type");
                    }
                    RefillRequestAcceptDto dto = tronService.createRequest(transaction);
                    transaction.setId(dto.getRequestId());
                    if (transaction.isConfirmed()) {
                        tronTransactionsService.processTransaction(transaction);
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
        TronReceivedTransactionDto dto;

        if (txType == TronTransactionTypeEnum.TriggerSmartContract){
            JSONObject transactionSmartContract = nodeService.getTransaction(transaction.getString("txID"));
            JSONObject tokenTransferInfo = transactionSmartContract.getJSONObject("tokenTransferInfo");
            dto = new TronReceivedTransactionDto(tokenTransferInfo.getLong("amount_str"), transaction.getString("txID"), TronNodeServiceImpl.base58checkToHexString(tokenTransferInfo.getString("to_address")));
            dto.setTxType(txType);
            dto.setAddressBase58(tokenTransferInfo.getString("to_address"));
            dto.setAssetName(tokenTransferInfo.getString("symbol"));
            dto.setConfirmed(transactionSmartContract.getBoolean("confirmed"));

        } else {
            JSONObject parameters = contractData.getJSONObject("parameter").getJSONObject("value");
            dto = new TronReceivedTransactionDto(parameters.getLong("amount"), transaction.getString("txID"), parameters.getString("to_address"));
            dto.setRawAmount(parameters.getLong("amount"));
            dto.setTxType(txType);
            dto.setAssetName(parameters.optString("asset_name"));
        }
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
