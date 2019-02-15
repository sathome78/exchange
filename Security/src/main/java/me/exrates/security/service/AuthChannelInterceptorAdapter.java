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
    private static final String USERNAME_HEADER = "login";
    private static final String PASSWORD_HEADER = "password";
    private final WebSocketAuthenticatorService webSocketAuthenticatorService;

    @Autowired
    public AuthChannelInterceptorAdapter(WebSocketAuthenticatorService webSocketAuthenticatorService) {
        this.webSocketAuthenticatorService = webSocketAuthenticatorService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            final String ip = accessor.getFirstNativeHeader(IP_HEADER);
            if (!StringUtils.isEmpty(accessor.getFirstNativeHeader(TOKEN_HEADER))) {
                final String token = accessor.getFirstNativeHeader(TOKEN_HEADER);
                final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFailByJwt(token, ip);
                accessor.setUser(user);
            } else if (!StringUtils.isEmpty(accessor.getFirstNativeHeader(USERNAME_HEADER)) && !StringUtils.isEmpty(accessor.getFirstNativeHeader(PASSWORD_HEADER))) {
                final String email = accessor.getLogin();
                String password = accessor.getFirstNativeHeader(PASSWORD_HEADER);
                System.out.println("login: " + email + "  password: " + password);
                final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFailByUsernamePassword(email, password);
                accessor.setUser(user);
            }
        }
        return message;
    }

}