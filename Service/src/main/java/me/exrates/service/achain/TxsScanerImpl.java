package me.exrates.service.achain;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.achain.enums.AchainTransactionType;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maks on 14.06.2018.
 */
@Log4j2(topic = "achain")
@Service
@Conditional(MonolitConditional.class)
public class TxsScanerImpl implements BlocksScaner {

    private final NodeService nodeService;
    private final AchainTokenContext tokenContext;
    private final AchainService achainService;
    private final MerchantSpecParamsDao specParamsDao;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final String PARAM_NAME = "LastScannedBlock";
    private static final String MERCHANT_NAME = "ACHAIN";
    private static final String CURRENCY_NAME = "ACT";

    @Autowired
    public TxsScanerImpl(NodeService nodeService, AchainTokenContext tokenContext, AchainService achainService, MerchantSpecParamsDao specParamsDao) {
        this.nodeService = nodeService;
        this.tokenContext = tokenContext;
        this.achainService = achainService;
        this.specParamsDao = specParamsDao;
    }

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::scan, 3, 5, TimeUnit.MINUTES);
    }

    @Override
    public void scan() {
        log.info("achain scan tx's");
        /*check is node synced*/
        if (!nodeService.getSyncState()) {
            log.debug("achain not synced");
            return;
        }
        Long lastProcessedBlock = loadLastBlock();
        /*scan to the pred-last block*/
        Long endBlock = nodeService.getBlockCount() - 1;
        log.info("achain end block {}, last block {}", endBlock, lastProcessedBlock);
        while (lastProcessedBlock < endBlock) {
            lastProcessedBlock++;
            JSONArray transactions =  nodeService.getBlockTransactions(lastProcessedBlock);
            checkTransactionsArray(transactions);
            saveLastBlock(lastProcessedBlock);
        }
    }

    private void checkTransactionsArray(JSONArray transactions) {
        for (Object p : transactions) {
            try {
                JSONArray tx = (JSONArray)p;
                log.debug(tx);
                JSONObject trx = tx.getJSONObject(1).getJSONObject("trx");
                String result = trx.getString("result_trx_type");
                String recieveAccount = trx.getString("alp_account");
                if (!StringUtils.isEmpty(recieveAccount)
                        && recieveAccount.startsWith(nodeService.getMainAccountAddress())
                        && result.equals("origin_transaction")) {
                    processAct(tx, trx, recieveAccount);
                } else {
                    JSONArray operations = trx.getJSONArray("operations");
                    if ("transaction_op_type".equals(operations.getJSONObject(0).getString("type")) &&
                            result.equals("complete_result_transaction")) {
                        log.info(operations);
                        JSONObject innerTrx = operations.getJSONObject(0).getJSONObject("data")
                                .getJSONObject("trx").getJSONArray("operations").getJSONObject(0).getJSONObject("data");
                        log.info(innerTrx);
                        String[] args = innerTrx.getString("args").split("\\|");
                        String fullAddress = args[0];
                        if (!(fullAddress.startsWith(nodeService.getMainAccountAddress()) &&
                                innerTrx.getString("method").equals("transfer_to"))) {
                            continue;
                        }
                        String contractId = innerTrx.getString("contract");
                        AchainContract contract =
                                Preconditions.checkNotNull(tokenContext.getByContractId(contractId));
                        recieveAccount = fullAddress.replace(nodeService.getMainAccountAddress(), "");
                        String txInnerHash = tx.getString(0);
                        String txHash = getContractTxId(txInnerHash);
                        acceptPayment(recieveAccount, txHash, args[1], contract.getMerchantName(), contract.getCurencyName());
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }


    private void processAct(JSONArray tx, JSONObject trx, String recieveAccount) {
        log.info("income tx {}", trx);
        //determine the transaction type
        JSONArray operations = trx.getJSONArray("operations");
        AchainTransactionType transactionType = determinteTxType(operations);
        JSONObject amountData = trx.getJSONObject("alp_inport_asset");
        String finalAmount = parseAmount(amountData.getDouble("amount"));
        String txHash = tx.getString(0);
        if (transactionType.equals(AchainTransactionType.SIMPLE_TRANSFER)) {
            acceptPayment(recieveAccount, txHash, finalAmount, MERCHANT_NAME, CURRENCY_NAME);
        }
    }

    private AchainTransactionType determinteTxType(JSONArray operations) {
        String operation = "";
        if (operations.length() == 1) {
            operation = operations.getJSONObject(0).getString("type");
            if (operation.equals(AchainTransactionType.CONTRACT_CALL.name())
                    && !operations.getJSONObject(0).getString("method").equals("transfer_to")) {
                throw new RuntimeException("not supported method");
            }
        } else if (operations.length() > 1) {
            for (Object op : operations) {
                JSONObject opJson = (JSONObject)op;
                if (opJson.get("type").equals("deposit_op_type")) {
                    operation = "deposit_op_type";
                }
            }
        }
        return AchainTransactionType.convert(operation);
    }

    private String getContractTxId(String innerHash) {
        try {
            JSONObject res = nodeService.getPrettyContractTransaction(innerHash);
            JSONObject resultJson2 = res.getJSONObject("result");
            return resultJson2.getString("orig_trx_id");
        } catch (Exception e) {
            return innerHash;
        }
    }

    private String parseAmount(Double amount) {
        Double res = amount/100000d;
        return res.toString();
    }

    private void acceptPayment(String address, String txHash, String amount, String merchantName, String currencyName) {
        Map<String, String> paymentParamsMap = new HashMap<>();
        paymentParamsMap.put("currency", currencyName);
        paymentParamsMap.put("merchant", merchantName);
        paymentParamsMap.put("address", address.replace(nodeService.getMainAccountAddress(), ""));
        paymentParamsMap.put("hash", txHash);
        paymentParamsMap.put("amount", amount);
        try {
            achainService.processPayment(paymentParamsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    private void saveLastBlock(Long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, PARAM_NAME, blockNum.toString());
    }

    private Long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, PARAM_NAME);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }
}