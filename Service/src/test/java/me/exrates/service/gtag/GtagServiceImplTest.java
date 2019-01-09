package me.exrates.service.gtag;

import me.exrates.service.impl.GtagServiceImpl;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static org.junit.Assert.*;

public class GtagServiceImplTest {

    @Test
    public void testSendTransaction(){
        Client client = ClientBuilder.newClient();
        client.register(new LoggingFilter());

        GtagServiceImpl gtagService = new GtagServiceImpl();
//        gtagService.setClient(client);
//        gtagService.setGoogleAnalyticsHost("https://www.google-analytics.com/collect");
//        gtagService.sendTransactionHit("username","10","100","BTC");
//        gtagService.sendItemHit("username","10","BTC","10","10");
    }

}