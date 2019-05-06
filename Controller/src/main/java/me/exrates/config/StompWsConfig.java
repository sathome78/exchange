package me.exrates.config;

import me.exrates.controller.interceptor.WsHandshakeInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@PropertySource(value = "classpath:/websocket.properties")
@Configuration
@EnableWebSocketMessageBroker
public class StompWsConfig extends AbstractWebSocketMessageBrokerConfigurer {

    private static final Logger logger = LogManager.getLogger(StompWsConfig.class);

    @Value("${ws.origin}")
    private String[] allowedOrigins;

    @Value("${ws.lib.url}")
    private String sockJSLibUrl;

    @Bean
    public HandshakeInterceptor wsHandshakeInterceptor() {
        return new WsHandshakeInterceptor();
    }

    @Bean
    public DefaultSimpUserRegistry defaultSimpUserRegistry() {
        return new DefaultSimpUserRegistry();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/public_socket")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS()
                .setClientLibraryUrl(sockJSLibUrl)
                .setInterceptors(wsHandshakeInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app", "/user");
        config.enableSimpleBroker("/queue", "/topic", "/app");
    }
}
