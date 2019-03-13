package me.exrates.security.config;

import me.exrates.security.service.AuthChannelInterceptorAdapter;
import me.exrates.security.service.WebSocketAuthenticatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@ComponentScan(basePackages = "me.exrates.security")
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class WebSocketAuthSecurityConfig extends AbstractWebSocketMessageBrokerConfigurer {

    private final WebSocketAuthenticatorService webSocketAuthenticatorService;

    @Autowired
    public WebSocketAuthSecurityConfig(WebSocketAuthenticatorService webSocketAuthenticatorService) {
        this.webSocketAuthenticatorService = webSocketAuthenticatorService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoints are already registered on WebSocketConfig, no need to add more.
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new AuthChannelInterceptorAdapter(webSocketAuthenticatorService));
    }
}

// Solution from: https://stackoverflow.com/questions/45405332/websocket-authentication-and-authorization-in-spring
// https://stackoverflow.com/questions/30887788/json-web-token-jwt-with-spring-based-sockjs-stomp-web-socket/39456274#39456274
