package me.exrates.security.service;

import me.exrates.model.exceptions.InvalidCredentialsException;
import me.exrates.service.UserService;
import me.exrates.service.bitshares.memo.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Optional;


public class AuthChannelInterceptorAdapter extends ChannelInterceptorAdapter {
    private static final String TOKEN_HEADER = "Exrates-Rest-Token";
    private static final String IP_HEADER = "X-Forwarded-For";
    private static final String USERNAME_HEADER = "login";
    private static final String PASSWORD_HEADER = "password";
    private static final String HEADER_PUBLIC_KEY = "API-KEY";
    private static final String HEADER_TIMESTAMP = "API-TIME";
    private static final String HEADER_SIGNATURE = "API-SIGN";
    private static final String PRIVATE_PATH = "private";
    private final WebSocketAuthenticatorService webSocketAuthenticatorService;
    private final UserService userService;

    public AuthChannelInterceptorAdapter(WebSocketAuthenticatorService webSocketAuthenticatorService, UserService userService) {
        this.webSocketAuthenticatorService = webSocketAuthenticatorService;
        this.userService = userService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            final String ip = accessor.getFirstNativeHeader(IP_HEADER);
            if (!StringUtils.isEmpty(accessor.getFirstNativeHeader(HEADER_SIGNATURE))) {
                final String signature = accessor.getFirstNativeHeader(HEADER_SIGNATURE);
                final String pubKey = accessor.getFirstNativeHeader(HEADER_PUBLIC_KEY);
                final String timestamp = accessor.getFirstNativeHeader(HEADER_TIMESTAMP);
                final String destination = Optional.ofNullable(accessor.getDestination()).orElse(""); /*destination: for subscribe /user/queue/my_orders/1* for connect "" */
                final String method = accessor.getCommand().name(); /*CONNECT or SUBSCRIBE*/
                final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFailByHMAC(method, destination, Long.parseLong(timestamp), pubKey, signature);
                accessor.setUser(user);
            } else if (!StringUtils.isEmpty(accessor.getFirstNativeHeader(TOKEN_HEADER))) {
                final String token = accessor.getFirstNativeHeader(TOKEN_HEADER);
                final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFailByJwt(token, ip);
                accessor.setUser(user);
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor.getDestination().contains(PRIVATE_PATH)) {
                    checkUserAuth(accessor);
                }
            } /*else if (!StringUtils.isEmpty(accessor.getFirstNativeHeader(USERNAME_HEADER)) && !StringUtils.isEmpty(accessor.getFirstNativeHeader(PASSWORD_HEADER))) {
                final String email = accessor.getLogin();
                String password = accessor.getFirstNativeHeader(PASSWORD_HEADER);
                System.out.println("login: " + email + "  password: " + password);
                final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFailByUsernamePassword(email, password);
                accessor.setUser(user);
            }*/

        }
        return message;
    }

    private void checkUserAuth(StompHeaderAccessor accessor) {
        final Principal principal = Preconditions.checkNotNull(accessor.getUser());
        final String destination = accessor.getDestination();
        int index = org.apache.commons.lang.StringUtils.indexOf(destination, PRIVATE_PATH);
        final String pubId = destination.substring(index).split("/")[1];
        if (!principal.getName().equals(userService.getEmailByPubId(pubId))) {
            throw new InvalidCredentialsException();
        }
    }

}