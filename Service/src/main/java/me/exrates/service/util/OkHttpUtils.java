package me.exrates.service.util;


import me.exrates.service.exception.RestRetrievalException;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class OkHttpUtils {

    private static final Logger logger = LogManager.getLogger(OkHttpUtils.class);

    private OkHttpUtils(){

    }


    public static String sendGetRequest(String url, Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new RestRetrievalException("Could not parse URL");
        }
        HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
        params.forEach(httpUrlBuilder::addEncodedQueryParameter);

        Request request = new Request.Builder()
                .url(httpUrlBuilder.build())
                .get()
                .build();
        try (Response response = client.newCall(request).execute();
             ResponseBody responseBody = response.body()) {
            String body = responseBody == null ? "" : responseBody.string();
            if (!response.isSuccessful()) {
                logger.error(body);
                throw new RestRetrievalException(body);
            }
            return body;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RestRetrievalException(e.getMessage());
        }
    }

    public static String sendGetRequest(String url) {
        return sendGetRequest(url, Collections.emptyMap());
    }


}