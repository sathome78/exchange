package me.exrates.service.decred;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Log4j2(topic = "decred")
public class DecredWSConfig extends ClientEndpointConfig.Configurator {

    private ClientEndpointConfig clientConfig;

    private String username;
    private String password;

    private Properties prop = new Properties();



    public DecredWSConfig() {
        super();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("merchants/decred.properties"));
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        username = prop.getProperty("decred.rpcuser");
        password = prop.getProperty("decred.rpcpass");
    }


    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        headers.put("Authorization", Collections.singletonList("Basic " + DatatypeConverter.printBase64Binary(String.join("", username, ":", password).getBytes())));
        super.beforeRequest(headers);
    }

    @Override
    public void afterResponse(HandshakeResponse hr) {
        super.afterResponse(hr);
    }
}
