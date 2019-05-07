package me.exrates.service.notifications.sms;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.LookupResponseDto;
import me.exrates.service.exception.InsuficcienceServiceBalanceException;
import me.exrates.service.exception.InvalidContactFormatException;
import me.exrates.service.exception.InvalidRefNumberException;
import me.exrates.service.exception.MessageUndeliweredException;
import me.exrates.service.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Random;

import static rx.internal.operators.NotificationLite.isError;

@PropertySource("classpath:1s2u_sms.properties")
@Log4j2(topic = "message_notify")
@Component
public class Sms1s2uService {

    @Autowired
    private RestTemplate restTemplate;


    private @Value("${1s2usms.key}") String key;
    private @Value("${1s2usms.hlr.url}") String hlrUrl;
    private @Value("${1s2usms.sms.url}") String smsUrl;
    private @Value("${1s2usms.username}") String userName;
    private @Value("${1s2usms.password}") String password;
    private final static String SENDER = "Exrates";


    @Transactional
    public void sendMessage(long contact, String message) {
        URI uri = UriComponentsBuilder
                .fromUriString(smsUrl)
                .queryParam("username", userName)
                .queryParam("password", password)
                .queryParam("mno", contact)
                .queryParam("msg", convertToUnicode(message))
                .queryParam("Sid", SENDER)
                .queryParam("fl", 0)
                .queryParam("mt", 1)
                .queryParam("ipcl", "127.0.0.1")
                .build().toUri();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
        if (isError(response)) {
            throw new MessageUndeliweredException();
        }
        BigDecimal res = new BigDecimal(response.getBody());
        if (res.compareTo(new BigDecimal(20)) == 0 ) {
            throw new InsuficcienceServiceBalanceException();
        }
        if (res.compareTo(new BigDecimal(9999)) < 0) {
            throw new MessageUndeliweredException();
        }
    }

    private String convertToUnicode(String message) {
        StringBuilder b = new StringBuilder();
        for (char c : message.toCharArray()) {
            b.append(String.format("%04X", (int) c));
        }
        return b.toString();
    }

}
