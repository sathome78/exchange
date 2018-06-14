package me.exrates.service.achain;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.achain.ActBlock;
import me.exrates.model.dto.achain.ActTransaction;
import me.exrates.model.dto.achain.TransactionDTO;
import me.exrates.model.dto.achain.enums.TrxType;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Maks on 14.06.2018.
 */
@Log4j2
@Component
public class NodeServiceImpl implements NodeService {

    @Autowired
    private SDKHttpClient httpClient;

    private String walletUrl;
    private String rpcUser;
    private String mainAccountAddress;

    @Override
    public String getMainAccountAddress() {
        return mainAccountAddress;
    }

    @Override
    public String getNewAddress() {
        String result =
                httpClient.post(walletUrl, rpcUser, "sub_address", new JSONArray());
        JSONObject createTaskJson = new JSONObject(result);
        return createTaskJson.getString("result");
    }


    @Override
    public long getBlockCount() {
        log.info("BlockchainServiceImpl|getBlockCount");
        String result =
                httpClient.post(walletUrl, rpcUser, "blockchain_get_block_count", new JSONArray());
        JSONObject createTaskJson = new JSONObject(result);
        return createTaskJson.getLong("result");
    }

    @Override
    public JSONArray getBlock(long blockNum) {
        log.info("BlockchainServiceImpl|getBlock [{}]", blockNum);
        String result =
                httpClient.post(walletUrl, rpcUser, "blockchain_get_block", String.valueOf(blockNum));
        JSONObject createTaskJson = new JSONObject(result);
        return createTaskJson.getJSONObject("result").getJSONArray("user_transaction_ids");
    }

    /**
     * Need to determine the type of transaction, the contract id, the method used to call the contract, and the address to which the transfer was made.
     *
     * @param trxId Transaction id
     */
    @Override
    public TransactionDTO getTransaction(long blockNum, String trxId) {
        try {
            log.info("BlockchainServiceImpl|getBlock [{}]", trxId);
            String result = httpClient.post(walletUrl, rpcUser, "blockchain_get_transaction", trxId);
            JSONObject createTaskJson = new JSONObject(result);
            JSONArray resultJsonArray = createTaskJson.getJSONArray("result");
            JSONObject operationJson = resultJsonArray.getJSONObject(1)
                    .getJSONObject("trx")
                    .getJSONArray("operations")
                    .getJSONObject(0);
            //determine the transaction type
            String operationType = operationJson.getString("type");
            //Not ignored on contract invocation
            if (!"transaction_op_type".equals(operationType)) {
                return null;
            }

            JSONObject operationData = operationJson.getJSONObject("data");
            log.info("BlockchainServiceImpl|operationData={}", operationData);

            String resultTrxId =
                    resultJsonArray.getJSONObject(1).getJSONObject("trx").getString("result_trx_id");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(StringUtils.isEmpty(resultTrxId) ? trxId : resultTrxId);
            log.info("getTransaction|transaction_op_type|[blockId={}][trxId={}][result_trx_id={}]", blockNum, trxId,
                    resultTrxId);
            String resultSignee =
                    httpClient
                            .post(walletUrl, rpcUser, "blockchain_get_pretty_contract_transaction", jsonArray);
            JSONObject resultJson2 = new JSONObject(resultSignee).getJSONObject("result");

            String origTrxId = resultJson2.getString("orig_trx_id");
            Integer trxType = Integer.parseInt(resultJson2.getString("trx_type"));

            Date trxTime = dealTime(resultJson2.getString("timestamp"));
            JSONArray reserved = resultJson2.getJSONArray("reserved");
            JSONObject temp = resultJson2.getJSONObject("to_contract_ledger_entry");
            String contractId = temp.getString("to_account");
            /*todo: check contracts*/
            /*if (!config.contractId.equals(contractId)) {
                return null;
            }*/
            TrxType type = TrxType.getTrxType(trxType);
            if (TrxType.TRX_TYPE_DEPOSIT_CONTRACT == type) {
                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setTrxId(origTrxId);
                transactionDTO.setBlockNum(blockNum);
                transactionDTO.setTrxTime(trxTime);
                transactionDTO.setContractId(contractId);
                //transactionDTO.setCallAbi(ContractGameMethod.RECHARGE.getValue());
                return transactionDTO;
            } else if (TrxType.TRX_TYPE_CALL_CONTRACT == type) {
                String fromAddr = temp.getString("from_account");
                Long amount = temp.getJSONObject("amount").getLong("amount");
                String callAbi = reserved.length() >= 1 ? reserved.getString(0) : null;
                String apiParams = reserved.length() > 1 ? reserved.getString(1) : null;

                if (StringUtils.isEmpty(callAbi)) {
                    return null;
                }
                jsonArray = new JSONArray();
                jsonArray.put(blockNum);
                jsonArray.put(trxId);
                String data = httpClient.post(walletUrl, rpcUser, "blockchain_get_events", jsonArray);
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray1 = jsonObject.getJSONArray("result");
                JSONObject resultJson = new JSONObject();
                parseEventData(resultJson, jsonArray1);
                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setContractId(contractId);
                transactionDTO.setTrxId(origTrxId);
                transactionDTO.setEventParam(resultJson.getString("event_param"));
                transactionDTO.setEventType(resultJson.getString("event_type"));
                transactionDTO.setBlockNum(blockNum);
                transactionDTO.setTrxTime(trxTime);
                transactionDTO.setCallAbi(callAbi);
                transactionDTO.setFromAddr(fromAddr);
                transactionDTO.setAmount(amount);
                transactionDTO.setApiParams(apiParams);
                return transactionDTO;
            }
        } catch (Exception e) {
            log.error("BlockchainServiceImpl", e);
        }
        return null;
    }



    private void parseEventData(JSONObject result, JSONArray jsonArray1) {
        if (Objects.nonNull(jsonArray1) && jsonArray1.length() > 0) {
            StringBuffer eventType = new StringBuffer();
            StringBuffer eventParam = new StringBuffer();
            jsonArray1.forEach(json -> {
                JSONObject jso = (JSONObject) json;
                eventType.append(eventType.length() > 0 ? "|" : "").append(jso.getString("event_type"));
                eventParam.append(eventParam.length() > 0 ? "|" : "").append(jso.getString("event_param"));
            });
            result.put("event_type", eventType);
            result.put("event_param", eventParam);
        }
    }


    private Date dealTime(String timestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return format.parse(timestamp);
        } catch (ParseException e) {
            log.error("dealTime|error|", e);
            return null;
        }
    }


}
