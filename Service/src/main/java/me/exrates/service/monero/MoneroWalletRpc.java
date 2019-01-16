package me.exrates.service.monero;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import types.HttpException;
import types.Pair;
import utils.JsonUtils;
import utils.StreamUtils;
import me.exrates.service.monero.utils.MoneroUtils;
import wallet.*;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class MoneroWalletRpc {
    private static final Logger LOGGER = Logger.getLogger(wallet.MoneroWalletRpc.class);
    public static ObjectMapper MAPPER = new ObjectMapper();
    private String rpcHost;
    private int rpcPort;
    private URI rpcUri;
    private HttpClient client;

    public MoneroWalletRpc(String endpoint) {
        this(parseUri(endpoint));
    }

    public MoneroWalletRpc(URI rpcUri) {
        this.rpcUri = rpcUri;
        this.rpcHost = rpcUri.getHost();
        this.rpcPort = rpcUri.getPort();
        this.client = HttpClients.createDefault();
    }

    public MoneroWalletRpc(String rpcHost, int rpcPort) throws URISyntaxException {
        this.rpcHost = rpcHost;
        this.rpcPort = rpcPort;
        this.rpcUri = new URI("http", (String)null, rpcHost, rpcPort, "/json_rpc", (String)null, (String)null);
        this.client = HttpClients.createDefault();
    }

    public MoneroWalletRpc(String rpcHost, int rpcPort, String username, String password) throws URISyntaxException {
        this.rpcHost = rpcHost;
        this.rpcPort = rpcPort;
        this.rpcUri = new URI("http", (String)null, rpcHost, rpcPort, "/json_rpc", (String)null, (String)null);
        CredentialsProvider creds = new BasicCredentialsProvider();
        creds.setCredentials(new AuthScope(this.rpcUri.getHost(), this.rpcUri.getPort()), new UsernamePasswordCredentials(username, password));
        this.client = HttpClients.custom().setDefaultCredentialsProvider(creds).build();
    }

    public String getRpcHost() {
        return this.rpcHost;
    }

    public int getRpcPort() {
        return this.rpcPort;
    }

    public URI getRpcUri() {
        return this.rpcUri;
    }

    public BigInteger getBalance() {
        return (BigInteger)this.getBalances().getFirst();
    }

    public BigInteger getUnlockedBalance() {
        return (BigInteger)this.getBalances().getSecond();
    }

    public int getHeight() {
        Map<String, Object> respMap = this.sendRpcRequest("getheight", (Map)null);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        return ((BigInteger)resultMap.get("height")).intValue();
    }

    public MoneroAddress getStandardAddress() {
        Map<String, Object> respMap = this.sendRpcRequest("getaddress", (Map)null);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        String standardAddress = (String)resultMap.get("address");
        MoneroAddress address = new MoneroAddress(standardAddress);
        MoneroUtils.validateAddress(address);
        return address;
    }

    
    public MoneroIntegratedAddress getIntegratedAddress(String paymentId) {
        Map<String, Object> paramMap = new HashMap();
        if (paymentId != null) {
            paramMap.put("payment_id", paymentId);
        }
        Map<String, Object> respMap = this.sendRpcRequest("make_integrated_address", paramMap);

        Map<String, Object> resultMap = (Map)respMap.get("result");
        paymentId = (String)resultMap.get("payment_id");
        String integratedAddress = (String)resultMap.get("integrated_address");
        MoneroIntegratedAddress address = new MoneroIntegratedAddress(this.getStandardAddress().getStandardAddress(), paymentId, integratedAddress);
        MoneroUtils.validateAddress(address);
        return address;
    }

    public MoneroIntegratedAddress splitIntegratedAddress(String integratedAddress) {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("integrated_address", integratedAddress);
        Map<String, Object> respMap = this.sendRpcRequest("split_integrated_address", paramMap);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        MoneroIntegratedAddress address = new MoneroIntegratedAddress((String)resultMap.get("standard_address"), (String)resultMap.get("payment_id"), integratedAddress);
        MoneroUtils.validateAddress(address);
        return address;
    }

    public String getMnemonicSeed() {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("key_type", "mnemonic");
        Map<String, Object> respMap = this.sendRpcRequest("query_key", paramMap);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        return (String)resultMap.get("key");
    }

    public String getViewKey() {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("key_type", "view_key");
        Map<String, Object> respMap = this.sendRpcRequest("query_key", paramMap);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        return (String)resultMap.get("key");
    }

    public URI toUri(MoneroUri moneroUri) {
        if (moneroUri == null) {
            throw new MoneroException("Given Monero URI is null");
        } else {
            Map<String, Object> paramMap = new HashMap();
            paramMap.put("address", moneroUri.getAddress());
            paramMap.put("amount", moneroUri.getAmount() == null ? null : moneroUri.getAmount());
            paramMap.put("payment_id", moneroUri.getPaymentId());
            paramMap.put("recipient_name", moneroUri.getRecipientName());
            paramMap.put("tx_description", moneroUri.getTxDescription());
            Map<String, Object> respMap = this.sendRpcRequest("make_uri", paramMap);
            Map<String, Object> resultMap = (Map)respMap.get("result");
            return parseUri((String)resultMap.get("uri"));
        }
    }

    public MoneroUri toMoneroUri(URI uri) {
        if (uri == null) {
            throw new MoneroException("Given URI is null");
        } else {
            Map<String, Object> paramMap = new HashMap();
            paramMap.put("uri", uri.toString());
            Map<String, Object> respMap = this.sendRpcRequest("parse_uri", paramMap);
            Map<String, Object> resultMap = (Map)((Map)respMap.get("result")).get("uri");
            MoneroUri mUri = new MoneroUri();
            mUri.setAddress((String)resultMap.get("address"));
            if ("".equals(mUri.getAddress())) {
                mUri.setAddress((String)null);
            }

            mUri.setAmount((BigInteger)resultMap.get("amount"));
            mUri.setPaymentId((String)resultMap.get("payment_id"));
            if ("".equals(mUri.getPaymentId())) {
                mUri.setPaymentId((String)null);
            }

            mUri.setRecipientName((String)resultMap.get("recipient_name"));
            if ("".equals(mUri.getRecipientName())) {
                mUri.setRecipientName((String)null);
            }

            mUri.setTxDescription((String)resultMap.get("tx_description"));
            if ("".equals(mUri.getTxDescription())) {
                mUri.setTxDescription((String)null);
            }

            return mUri;
        }
    }

    public void saveBlockchain() {
        this.sendRpcRequest("store", (Map)null);
    }

    public void stopWallet() {
        this.sendRpcRequest("stop_wallet", (Map)null);
    }

    public MoneroTransaction send(String address, BigInteger amount, String paymentId, int mixin, int unlockTime) {
        return this.send(new MoneroPayment((MoneroTransaction)null, address, amount), paymentId, mixin, unlockTime);
    }

    public MoneroTransaction send(MoneroAddress address, BigInteger amount, String paymentId, int mixin, int unlockTime) {
        return this.send(address.toString(), amount, paymentId, mixin, unlockTime);
    }

    public MoneroTransaction send(MoneroPayment payment, String paymentId, int mixin, int unlockTime) {
        List<MoneroPayment> payments = new ArrayList();
        payments.add(payment);
        return this.send((List)payments, paymentId, mixin, unlockTime);
    }

    public MoneroTransaction send(List<MoneroPayment> payments, String paymentId, int mixin, int unlockTime) {
        Map<String, Object> paramMap = new HashMap();
        List<Map<String, Object>> destinations = new ArrayList();
        paramMap.put("destinations", destinations);
        Iterator var7 = payments.iterator();

        while(var7.hasNext()) {
            MoneroPayment payment = (MoneroPayment)var7.next();
            Map<String, Object> destination = new HashMap();
            destination.put("address", payment.getAddress().toString());
            destination.put("amount", payment.getAmount());
            destinations.add(destination);
        }

        paramMap.put("payment_id", paymentId);
        paramMap.put("mixin", mixin);
        paramMap.put("unlockTime", unlockTime);
        paramMap.put("get_tx_key", true);
        Map<String, Object> respMap = this.sendRpcRequest("transfer", paramMap);
        Map<String, Object> txMap = (Map)respMap.get("result");
        MoneroTransaction tx = interpretTransaction(txMap);
        tx.setPayments(payments);
        tx.setMixin(mixin);
        tx.setUnlockTime(unlockTime);
        return tx;
    }

    public List<MoneroTransaction> sendSplit(List<MoneroPayment> payments, String paymentId, int mixin, int unlockTime, Boolean newAlgorithm) {
        Map<String, Object> paramMap = new HashMap();
        List<Map<String, Object>> destinations = new ArrayList();
        paramMap.put("destinations", destinations);
        Iterator var8 = payments.iterator();

        while(var8.hasNext()) {
            MoneroPayment payment = (MoneroPayment)var8.next();
            Map<String, Object> destination = new HashMap();
            destination.put("address", payment.getAddress().toString());
            destination.put("amount", payment.getAmount());
            destinations.add(destination);
        }

        paramMap.put("payment_id", paymentId);
        paramMap.put("mixin", mixin);
        paramMap.put("unlockTime", unlockTime);
        paramMap.put("new_algorithm", newAlgorithm);
        Map<String, Object> respMap = this.sendRpcRequest("transfer_split", paramMap);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        List<BigInteger> fees = (List)resultMap.get("fee_list");
        List<String> txHashes = (List)resultMap.get("tx_hash_list");
        List<MoneroTransaction> transactions = new ArrayList();

        for(int i = 0; i < fees.size(); ++i) {
            MoneroTransaction tx = new MoneroTransaction();
            tx.setFee((BigInteger)fees.get(i));
            tx.setMixin(mixin);
            tx.setHash((String)txHashes.get(0));
            transactions.add(tx);
            tx.setUnlockTime(unlockTime);
        }

        return transactions;
    }

    public List<MoneroTransaction> sweepDust() {
        Map<String, Object> respMap = this.sendRpcRequest("sweep_dust", (Map)null);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        List<String> txHashes = (List)resultMap.get("tx_hash_list");
        List<MoneroTransaction> txs = new ArrayList();
        if (txHashes == null) {
            return txs;
        } else {
            Iterator var5 = txHashes.iterator();

            while(var5.hasNext()) {
                String txHash = (String)var5.next();
                MoneroTransaction tx = new MoneroTransaction();
                tx.setHash(txHash);
                txs.add(tx);
            }

            return txs;
        }
    }

    public List<MoneroTransaction> getAllTransactions() {
        return this.getTransactions(true, true, true, true, true, (Collection)null, (Integer)null, (Integer)null);
    }

    public List<MoneroTransaction> getTransactions(boolean getIncoming, boolean getOutgoing, boolean getPending, boolean getFailed, boolean getMemPool, Collection<String> paymentIds, Integer minHeight, Integer maxHeight) {
        Map<MoneroTransaction.MoneroTransactionType, Map<String, MoneroTransaction>> txTypeMap = new HashMap();
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("in", getIncoming);
        paramMap.put("out", getOutgoing);
        paramMap.put("pending", getPending);
        paramMap.put("failed", getFailed);
        paramMap.put("pool", getMemPool);
        paramMap.put("filter_by_height", false);
        Map<String, Object> respMap = this.sendRpcRequest("get_transfers", paramMap);
        Map<String, Object> result = (Map)respMap.get("result");
        Iterator var13 = result.keySet().iterator();

        Iterator var15;
        Map paymentMap;
        MoneroTransaction tx;
        while(var13.hasNext()) {
            String key = (String)var13.next();
            var15 = ((List)result.get(key)).iterator();

            while(var15.hasNext()) {
                paymentMap = (Map)var15.next();
                tx = interpretTransaction(paymentMap);
                MoneroPayment payment = new MoneroPayment(tx, (String)null, (BigInteger)paymentMap.get("amount"));
                List<MoneroPayment> payments = new ArrayList();
                payments.add(payment);
                tx.setPayments(payments);
                addTransaction(txTypeMap, tx);
            }
        }

        Iterator var22;
        if (getIncoming) {
            paramMap = new HashMap();
            paramMap.put("transfer_type", "all");
            respMap = this.sendRpcRequest("incoming_transfers", paramMap);
            result = (Map)respMap.get("result");
            List<Map<String, Object>> outputMaps = (List)result.get("transfers");
            if (outputMaps == null) {
                return new ArrayList();
            }

            var22 = outputMaps.iterator();

            while(var22.hasNext()) {
                Map<String, Object> outputMap = (Map)var22.next();
                MoneroOutput output = new MoneroOutput();
                output.setAmount((BigInteger)outputMap.get("amount"));
                output.setIsSpent((Boolean)outputMap.get("spent"));
                tx = interpretTransaction(outputMap);
                tx.setType(MoneroTransaction.MoneroTransactionType.INCOMING);
                output.setTransaction(tx);
                List<MoneroOutput> outputs = new ArrayList();
                outputs.add(output);
                tx.setOutputs(outputs);
                addTransaction(txTypeMap, tx);
            }

            if (paymentIds != null && !paymentIds.isEmpty()) {
                paramMap = new HashMap();
                paramMap.put("payment_ids", paymentIds);
                respMap = this.sendRpcRequest("get_bulk_payments", paramMap);
                result = (Map)respMap.get("result");
                List<Map<String, Object>> paymentMaps = (List)result.get("payments");
                var15 = paymentMaps.iterator();

                while(var15.hasNext()) {
                    paymentMap = (Map)var15.next();
                    tx = interpretTransaction(paymentMap);
                    tx.setType(MoneroTransaction.MoneroTransactionType.INCOMING);
                    addTransaction(txTypeMap, tx);
                }
            }
        }

        List<MoneroTransaction> txs = new ArrayList();
        var22 = txTypeMap.entrySet().iterator();

        while(var22.hasNext()) {
            Map.Entry<MoneroTransaction.MoneroTransactionType, Map<String, MoneroTransaction>> entry = (Map.Entry)var22.next();
            txs.addAll(((Map)entry.getValue()).values());
        }

        Collection<MoneroTransaction> toRemoves = new HashSet();
        var15 = txs.iterator();

        while(true) {
            while(var15.hasNext()) {
                MoneroTransaction var16 = (MoneroTransaction)var15.next();
                if (paymentIds != null && !paymentIds.contains(var16.getPaymentId())) {
                    toRemoves.add(var16);
                } else if (minHeight == null || var16.getHeight() != null && var16.getHeight() >= minHeight) {
                    if (maxHeight != null && (var16.getHeight() == null || var16.getHeight() > maxHeight)) {
                        toRemoves.add(var16);
                    }
                } else {
                    toRemoves.add(var16);
                }
            }

            txs.removeAll(toRemoves);
            return txs;
        }
    }

    private static URI parseUri(String endpoint) {
        try {
            return new URI(endpoint);
        } catch (Exception var2) {
            throw new MoneroException(var2);
        }
    }

    private Pair<BigInteger, BigInteger> getBalances() {
        Map<String, Object> respMap = this.sendRpcRequest("getbalance", (Map)null);
        Map<String, Object> resultMap = (Map)respMap.get("result");
        return new Pair((BigInteger)resultMap.get("balance"), (BigInteger)resultMap.get("unlocked_balance"));
    }

    private static MoneroTransaction interpretTransaction(Map<String, Object> txMap) {
        MoneroTransaction tx = new MoneroTransaction();
        Iterator var2 = txMap.keySet().iterator();

        while(true) {
            while(true) {
                String key;
                Object val;
                do {
                    do {
                        if (!var2.hasNext()) {
                            return tx;
                        }

                        key = (String)var2.next();
                        val = txMap.get(key);
                    } while(key.equals("amount"));
                } while(key.equals("spent"));

                if (key.equalsIgnoreCase("fee")) {
                    tx.setFee((BigInteger)val);
                } else if (key.equalsIgnoreCase("height")) {
                    tx.setHeight(((BigInteger)val).intValue());
                } else if (key.equalsIgnoreCase("block_height")) {
                    tx.setHeight(((BigInteger)val).intValue());
                } else if (key.equalsIgnoreCase("note")) {
                    tx.setNote((String)val);
                } else if (key.equalsIgnoreCase("payment_id")) {
                    tx.setPaymentId((String)val);
                } else if (key.equalsIgnoreCase("timestamp")) {
                    tx.setTimestamp(((BigInteger)val).longValue());
                } else if (key.equalsIgnoreCase("tx_hash")) {
                    tx.setHash((String)val);
                } else if (key.equalsIgnoreCase("tx_key")) {
                    tx.setKey((String)val);
                } else if (key.equalsIgnoreCase("txid")) {
                    tx.setHash((String)val);
                } else if (key.equalsIgnoreCase("type")) {
                    tx.setType(getTransactionType((String)val));
                } else if (key.equalsIgnoreCase("tx_size")) {
                    tx.setSize(((BigInteger)val).intValue());
                } else if (key.equalsIgnoreCase("unlock_time")) {
                    tx.setUnlockTime(((BigInteger)val).intValue());
                } else if (!key.equalsIgnoreCase("global_index")) {
                    if (!key.equalsIgnoreCase("destinations")) {
                        LOGGER.warn("Ignoring unexpected transaction field: '" + key + "'");
                    } else {
                        List<MoneroPayment> payments = new ArrayList();
                        tx.setPayments(payments);
                        Iterator var6 = ((List)val).iterator();

                        while(var6.hasNext()) {
                            Map<String, Object> paymentMap = (Map)var6.next();
                            MoneroPayment payment = new MoneroPayment();
                            Iterator var9 = paymentMap.keySet().iterator();

                            while(var9.hasNext()) {
                                String paymentKey = (String)var9.next();
                                if (paymentKey.equals("address")) {
                                    payment.setAddress((String)paymentMap.get(paymentKey));
                                } else {
                                    if (!paymentKey.equals("amount")) {
                                        throw new MoneroException("Unrecognized transaction destination field: " + paymentKey);
                                    }

                                    payment.setAmount((BigInteger)paymentMap.get(paymentKey));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addTransaction(Map<MoneroTransaction.MoneroTransactionType, Map<String, MoneroTransaction>> txTypeMap, MoneroTransaction tx) {
        if (tx.getType() == null) {
            throw new MoneroException("Transaction type cannot be null: \n" + tx.toString());
        } else if (tx.getHash() == null) {
            throw new MoneroException("Transaction hash cannot be null: \n" + tx.getHash());
        } else {
            Map<String, MoneroTransaction> txHashMap = (Map)txTypeMap.get(tx.getType());
            if (txHashMap == null) {
                txHashMap = new HashMap();
                txTypeMap.put(tx.getType(), txHashMap);
            }

            MoneroTransaction targetTx = (MoneroTransaction)((Map)txHashMap).get(tx.getHash());
            if (targetTx == null) {
                ((Map)txHashMap).put(tx.getHash(), tx);
            } else {
                targetTx.merge(tx);
            }

        }
    }

    private static MoneroTransaction.MoneroTransactionType getTransactionType(String type) {
        if (type == null) {
            throw new MoneroException("Transaction type is null");
        } else if (type.equalsIgnoreCase("in")) {
            return MoneroTransaction.MoneroTransactionType.INCOMING;
        } else if (type.equalsIgnoreCase("out")) {
            return MoneroTransaction.MoneroTransactionType.OUTGOING;
        } else if (type.equalsIgnoreCase("pending")) {
            return MoneroTransaction.MoneroTransactionType.PENDING;
        } else if (type.equalsIgnoreCase("failed")) {
            return MoneroTransaction.MoneroTransactionType.FAILED;
        } else if (type.equalsIgnoreCase("pool")) {
            return MoneroTransaction.MoneroTransactionType.MEMPOOL;
        } else {
            throw new MoneroException("Unrecognized transaction type: " + type);
        }
    }

    private Map<String, Object> sendRpcRequest(String method, Map<String, Object> params) {
        try {
            Map<String, Object> body = new HashMap();
            body.put("jsonrpc", "2.0");
            body.put("id", "0");
            body.put("method", method);
            if (params != null) {
                body.put("params", params);
            }

            LOGGER.debug("Sending method '" + method + "' with body: " + JsonUtils.serialize(body));
            HttpPost post = new HttpPost(this.rpcUri);
            HttpEntity entity = new StringEntity(JsonUtils.serialize(body));
            post.setEntity(entity);
            HttpResponse resp = this.client.execute(post);
            validateHttpResponse(resp);
            Map<String, Object> respMap = JsonUtils.toMap(MAPPER, StreamUtils.streamToString(resp.getEntity().getContent()));
            LOGGER.debug("Received response to method '" + method + "': " + JsonUtils.serialize(respMap));
            EntityUtils.consume(resp.getEntity());
            validateRpcResponse(respMap, body);
            return respMap;
        } catch (HttpException var8) {
            throw var8;
        } catch (MoneroRpcException var9) {
            throw var9;
        } catch (Exception var10) {
            throw new MoneroException(var10);
        }
    }

    private static void validateHttpResponse(HttpResponse resp) {
        int code = resp.getStatusLine().getStatusCode();
        if (code < 200 || code > 299) {
            String content = null;

            try {
                content = StreamUtils.streamToString(resp.getEntity().getContent());
            } catch (Exception var4) {
            }

            throw new HttpException(code, resp.getStatusLine().getReasonPhrase() + (content != null ? ": " + content : ""));
        }
    }

    private static void validateRpcResponse(Map<String, Object> respMap, Map<String, Object> requestBody) {
        Map<String, Object> error = (Map)respMap.get("error");
        if (error != null) {
            int code = ((BigInteger)error.get("code")).intValue();
            String message = (String)error.get("message");
            throw new MoneroRpcException(code, message, requestBody);
        }
    }

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        MAPPER.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
    }
}