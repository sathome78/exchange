package me.exrates.service.achain;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * Created by Maks on 14.06.2018.
 */
@Log4j2(topic = "achain")
@Service
public class SDKHttpClient {


    private CloseableHttpClient httpclient;

    @Autowired
    public SDKHttpClient(CloseableHttpClient closeableHttpClient) {
        this.httpclient = closeableHttpClient;
    }

    /**
     * Dealing exclusively with a parameter is json's broadcast transaction
     */
    public String post(String url, String key, String method, String... params) {
        String temp = "{\"jsonrpc\":\"2.0\",\"params\":" + Arrays.toString(params) +
                ",\"id\":\"" + new Random().nextInt(1024) + "\",\"method\":\"" +
                method + "\"}";
        return basePost(url, temp, key);
    }

    public String post(String url, String key, String method, JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", method);
        jsonObject.put("id", ((int) ((Math.random() * 9 + 1) * 10)));
        jsonObject.put("jsonrpc", "2.0");
        jsonObject.put("params", jsonArray);
        return basePost(url, jsonObject.toString(), key);
    }

    private String basePost(String url, String entity, String key) {
        HttpPost httppost = null;
        String result = null;
        try {
            String rpcAuth = (int) ((Math.random() * 9 + 1) * 100000) + "" + Base64.getEncoder().encodeToString(key.getBytes());
            httppost = new HttpPost(url);
            httppost.setEntity(new StringEntity(entity, Charset.forName("UTF-8")));
            httppost.setHeader("Content-type", "application/json");
            httppost.setHeader("Authorization", rpcAuth);
            log.debug("【SDKHttpClient】｜POST开始：url=[{}]", url);
            CloseableHttpResponse response = httpclient.execute(httppost);
            if (null != response) {
                try {
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                    log.debug("【SDKHttpClient】｜POST end URL:[{}][jsonArray={}],Response results[response={}][result={}]!", url, entity,
                            response.getStatusLine().getStatusCode(), result);
                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        result = null;
                    }
                } finally {
                    response.close();
                }
            } else {
                log.debug("【SDKHttpClient】｜POST URL:[{}],The response is empty!", url);
            }
        } catch (Exception e) {
            log.error("【SDKHttpClient】｜POST URL:[{}] Abnormal[{}]!", url, e.getStackTrace());
        } finally {
            try {
                if (null != httppost) {
                    httppost.releaseConnection();
                }
            } catch (Exception e) {
                log.error("【SDKHttpClient】｜POST URL:[{}] shut down httpclient.close() abnormal[{}]!", url, e.getStackTrace());
            }
        }
        return result;
    }
}
