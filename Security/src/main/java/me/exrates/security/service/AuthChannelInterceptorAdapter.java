package me.exrates.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;

public class AuthChannelInterceptorAdapter extends ChannelInterceptorAdapter {
	private static final String TOKEN_HEADER = "Exrates-Rest-Token";
	private static final String IP_HEADER = "X-Forwarded-For";
	private final WebSocketAuthenticatorService webSocketAuthenticatorService;

	@Autowired
	public AuthChannelInterceptorAdapter(WebSocketAuthenticatorService webSocketAuthenticatorService) {
		this.webSocketAuthenticatorService = webSocketAuthenticatorService;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())
				&& !StringUtils.isEmpty(accessor.getFirstNativeHeader(TOKEN_HEADER))) {
			final String token = accessor.getFirstNativeHeader(TOKEN_HEADER);
			final String ip = accessor.getFirstNativeHeader(IP_HEADER);

			final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFail(token, ip);

			accessor.setUser(user);
		}

        return message;
    }
}
