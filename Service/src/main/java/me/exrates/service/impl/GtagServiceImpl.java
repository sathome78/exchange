package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.GtagService;
import me.exrates.service.api.ExchangeApi;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;

@Service("gtagService")
@PropertySource(value = "classpath:/analytics.properties")
@Log4j2
public class GtagServiceImpl implements GtagService {

    @Value("${google.analytics.host}")
    private String googleAnalyticsHost;

    @Value("${google.analytics.enable}")
    private boolean enable;

    @Autowired
    private Client client;
    @Autowired
    private ExchangeApi exchangeApi;

    public void sendGtagEvents(String coinsCount, String tiker, String userName) {
        if (!enable) return;
        try {
            Pair<BigDecimal, BigDecimal> pair = exchangeApi.getRates().get(tiker);
            String price = pair.getKey().multiply(new BigDecimal(coinsCount)).toString();
            String transactionId = sendTransactionHit(userName, price, tiker);
            log.info("Successfully send transaction hit to gtag");
            sendItemHit(userName, transactionId, tiker, coinsCount, pair.getKey().toString());
            log.info("Successfully send item hit to gtag");
            log.info("Send all analytics");
        } catch (Throwable exception) {
            log.warn("Unable to send statistic to gtag ", exception);
        }
    }

    private String sendTransactionHit(String userName, String price, String tiker) {
        String transactionId = UUID.randomUUID().toString();

        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

        formData.add("v", "1");
        formData.add("cid", userName);
        formData.add("tid", "UA-75711135-1");
        formData.add("t", "transaction");
        formData.add("in", tiker);
        formData.add("cu", "USD");
        formData.add("tr", price);
        formData.add("ti", transactionId);

        log.info("Form params " + formData);

        Response response = client.target(googleAnalyticsHost).request().post(Entity.form(formData));

        log.info("Response is " + response.readEntity(String.class));
        return transactionId;
    }

    private void sendItemHit(String userName, String transactionId, String tiker, String amount, String price) {

        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

        formData.add("v", "1");
        formData.add("cid", userName);
        formData.add("tid", "UA-75711135-1");
        formData.add("t", "item");
        formData.add("ti", transactionId);
        formData.add("in", tiker);
        formData.add("ip", price);
        formData.add("iq", amount);
        formData.add("ic", tiker);
        formData.add("cu", "USD");

        log.info("Form params " + formData);

        Response response = client.target(googleAnalyticsHost).request().post(Entity.form(formData));

        String jsonResponse = response.readEntity(String.class);

        log.info("Response is " + jsonResponse);
    }
}