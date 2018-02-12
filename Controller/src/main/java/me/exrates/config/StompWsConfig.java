package me.exrates.config;

import me.exrates.controller.interceptor.WsHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;

/**
 * Created by Maks on 24.08.2017.
 */
@PropertySource(value = "classpath:/websocket.properties")
@Configuration
@EnableWebSocketMessageBroker
public class StompWsConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Value("${ws.origin}")
    private String allowedOrigins;

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
        String[] origins = allowedOrigins.split(",");

        System.out.println("*************************** Orginis for WebSockets: " + Arrays.toString(origins) + " ****************");
        registry
                .addEndpoint("/public_socket")
                .setAllowedOrigins(origins)
                .withSockJS()
                .setClientLibraryUrl("//cdn.jsdelivr.net/sockjs/1/sockjs.min.js")
                .setInterceptors(wsHandshakeInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app", "/user");
        config.enableSimpleBroker("/queue", "/topic", "/app");
    }
}
