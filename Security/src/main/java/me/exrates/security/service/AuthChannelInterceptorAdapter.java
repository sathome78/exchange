package me.exrates.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AuthChannelInterceptorAdapter extends ChannelInterceptorAdapter {
	private static final String TOKEN_HEADER = "Exrates-Rest-Token";
	private final WebSocketAuthenticatorService webSocketAuthenticatorService;

	@Autowired
	public AuthChannelInterceptorAdapter(WebSocketAuthenticatorService webSocketAuthenticatorService) {
		this.webSocketAuthenticatorService = webSocketAuthenticatorService;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if(StompCommand.SEND == accessor.getCommand()){
			final String token = accessor.getFirstNativeHeader(TOKEN_HEADER);
			/*todo method in ipUtils to get ip from header*/
			final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFail(token, null);

			accessor.setUser(user);
		}

		return message;
	}
}
