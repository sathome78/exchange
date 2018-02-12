package me.exrates.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@PropertySource(value = "classpath:/websocket.properties")
@EnableWebSocketMessageBroker
@Order(1)
public class AngularWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer{

	@Value("${ws.origin}")
	private String allowedOrigins;


	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		String [] origins = allowedOrigins.split(",");
		registry
				.addEndpoint("/ws")
				.setAllowedOrigins(origins)
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app").enableSimpleBroker("/chat");
	}
}
