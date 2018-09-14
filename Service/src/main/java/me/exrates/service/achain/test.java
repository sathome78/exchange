package me.exrates.service.achain;

import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.CodingErrorAction;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maks on 19.06.2018.
 */
public class test {

    private final static String nodeUrl = "http://127.0.0.1:8078/rpc";
    private final static String rpcUser = "admin:123456";

    public static void main(String[] args) {
        SDKHttpClient sdkHttpClient = new SDKHttpClient(getClient());
       /* String result =
                sdkHttpClient.post(nodeUrl, rpcUser, "blockchain_get_transaction",
                        "d6ef0fc8628ec1e63837d7c327fb1619ddff7768");*/
     /*   String result =
                sdkHttpClient.post(nodeUrl, rpcUser, "wallet_get_transaction", "b67b2fd708295f446daa0d093e6b0e5c6be01e43");*/
       /* String result =
                sdkHttpClient.post(nodeUrl, rpcUser, "wallet_account_transaction_history",
                        "ACT54nD2uNstDg3AYfGJJ9g8ZxiC4RPs7EAG", "ACT", "1000", "0", "2850075");*/
       /*String result1 =
                sdkHttpClient.post(
                        nodeUrl,
                        rpcUser,
                        "blockchain_get_account",
                        "ACT5yMzxAE6TmGa2gT8UCvKXiFa1XwydgK8C2dvuYYrVEqkhKspWs");
        System.out.println(result1);
       String result = sdkHttpClient.post(nodeUrl,
               rpcUser,"blockchain_get_block_count", new JSONArray());*/

        /*System.out.println(result);*/
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("5b1c942e4a9771cb9be596df6a2fe1678e9b156c");
        System.out.println("request");
        String resultSignee =
                sdkHttpClient
                        .post(nodeUrl, rpcUser, "blockchain_get_pretty_contract_transaction", jsonArray);
        System.out.println(resultSignee);
        JSONObject resultJson2 = new JSONObject(resultSignee).getJSONObject("result");
        System.out.println(resultJson2.getString("orig_trx_id"));
    }



    private static CloseableHttpClient getClient() {
        int timeout5MinsInMillis = 5 * 60 * 1000;
        RequestConfig requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                .setSocketTimeout(timeout5MinsInMillis)
                .setConnectionRequestTimeout(timeout5MinsInMillis)
                .setConnectTimeout(timeout5MinsInMillis)
                .build();
        ConnectionConfig connectionConfig = ConnectionConfig.copy(ConnectionConfig.DEFAULT)
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
                new PoolingHttpClientConnectionManager();
        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setConnectionManagerShared(false)
                .evictIdleConnections(60, TimeUnit.SECONDS)
                .evictExpiredConnections()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultConnectionConfig(connectionConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .useSystemProperties()
                .build();
    }
}
