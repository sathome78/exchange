package me.exrates.service.achain;

import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Maks on 14.06.2018.
 */

@PropertySource("classpath:/merchants/achain.properties")
@Log4j2(topic = "achain")
@Component
@Conditional(MonolitConditional.class)
public class NodeServiceImpl implements NodeService {

    private final SDKHttpClient httpClient;

    private @Value("${achain.node.url}")String nodeUrl;
    private @Value("${achain.node.rpcUser}")String rpcUser;
    private @Value("${achain.mainAddress}")String mainAccountAddress;
    private @Value("${achain.accountName}")String accountName;

    @Autowired
    public NodeServiceImpl(SDKHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String getMainAccountAddress() {
        return mainAccountAddress;
    }



    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public long getBlockCount() {
        log.debug("NodeServiceImpl|getBlockCount");
        String result =
                httpClient.post(nodeUrl, rpcUser, "blockchain_get_block_count", new JSONArray());
        JSONObject createTaskJson = new JSONObject(result);
        return createTaskJson.getLong("result");
    }

    @Override
    public JSONArray getBlock(long blockNum) {
        log.debug("NodeServiceImpl|getBlock [{}]", blockNum);
        String result =
                httpClient.post(nodeUrl, rpcUser, "blockchain_get_block", String.valueOf(blockNum));
        JSONObject createTaskJson = new JSONObject(result);
        return createTaskJson.getJSONObject("result").getJSONArray("user_transaction_ids");
    }

    @Override
    public boolean getSyncState() {
        log.debug("NodeServiceImpl|getSyncState [{}]");
        String result =
                httpClient.post(nodeUrl, rpcUser, "blockchain_is_synced", new JSONArray());
        JSONObject createTaskJson = new JSONObject(result);
        return createTaskJson.getBoolean("result");
    }

    @Override
    public JSONArray getBlockTransactions(long blockNum) {
        log.debug("NodeServiceImpl|getBlockTransactions [{}]", blockNum);
        String result =
                httpClient.post(nodeUrl, rpcUser, "blockchain_get_block_transactions", String.valueOf(blockNum));
        JSONObject transactions = new JSONObject(result);
        return transactions.getJSONArray("result");
    }

    @Override
    public JSONObject getPrettyContractTransaction(String innerHash) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(innerHash);
        log.info("getPretty|[result_trx_id={}]",
                innerHash);
        String result =
                httpClient
                        .post(nodeUrl, rpcUser, "blockchain_get_pretty_contract_transaction", jsonArray);
        return new JSONObject(result);
    }

   /* @Override
    public List<TransactionDTO> getTransactionsList(String account, String asset, Integer limit, String startBlock, String endBlock) {
        log.info("NodeServiceImpl|getTransactionsList [{}, {}, {}, {}]", account, asset, limit, startBlock, endBlock);
        String result =
                httpClient.post(nodeUrl, rpcUser, "wallet_account_transaction_history",
                        account, asset, limit.toString(), startBlock, endBlock);
        JSONArray transactions = new JSONObject(result).getJSONArray("result");
        List<TransactionDTO> txsList = new ArrayList<>();
        transactions.forEach(p-> {
            try {
                JSONObject jso = (JSONObject) p;
                long blockNum = jso.getLong("block_num");
                String txId = jso.getString("trx_id");
                boolean isConfirmed = jso.getBoolean("is_confirmed");
                TransactionDTO dto = getTransaction(blockNum, txId);
                dto.setConfirmed(isConfirmed);
                txsList.add(dto);
            } catch (Exception e) {
                log.error(e);
            }
        });
        return null;
    }*/


    /*@Override
    public TransactionDTO getTransaction(long blockNum, String trxId) {
        try {
            log.info("NodeServiceImpl|getBlock [{}]", trxId);
            String result = httpClient.post(nodeUrl, rpcUser, "blockchain_get_transaction", trxId);
            JSONObject createTaskJson = new JSONObject(result);
            JSONArray resultJsonArray = createTaskJson.getJSONArray("result");
            JSONObject trx = resultJsonArray.getJSONObject(1).getJSONObject("trx");
            JSONObject operationJson = trx
                    .getJSONArray("operations")
                    .getJSONObject(0);
            //determine the transaction type
            String operationType = operationJson.getString("type");
            //Not ignored on contract invocation
            AchainTransactionType transactionType = AchainTransactionType.convert(operationType);
            String recieveAccount = trx.getString("alp_account");
            if (StringUtils.isEmpty(recieveAccount)) {
                throw new RuntimeException("no reciever defined");
            }
            JSONObject amountData = trx.getJSONObject("alp_inport_asset");
            String finalAmount = parseAmount(amountData.getLong("amount"));

            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setTrxId(trxId);
            transactionDTO.setToAddr(recieveAccount);
            transactionDTO.setBlockNum(blockNum);
            transactionDTO.setAmount(finalAmount);
            transactionDTO.setContractId("");




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
                            .post(nodeUrl, rpcUser, "blockchain_get_pretty_contract_transaction", jsonArray);
            JSONObject resultJson2 = new JSONObject(resultSignee).getJSONObject("result");

            String origTrxId = resultJson2.getString("orig_trx_id");
            Integer trxType = Integer.parseInt(resultJson2.getString("trx_type"));

            Date trxTime = dealTime(resultJson2.getString("timestamp"));
            JSONArray reserved = resultJson2.getJSONArray("reserved");
            JSONObject temp = resultJson2.getJSONObject("to_contract_ledger_entry");
            String contractId = temp.getString("to_account");
            *//*todo: check contracts*//*
            *//*if (!config.contractId.equals(contractId)) {
                return null;
            }*//*
            *//*TrxType type = TrxType.getTrxType(trxType);
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
                String data = httpClient.post(nodeUrl, rpcUser, "blockchain_get_events", jsonArray);
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
            }*//*
            return transactionDTO;
        } catch (Exception e) {
            log.error("NodeServiceImpl", e);
        }
        return null;
    }*/
}
