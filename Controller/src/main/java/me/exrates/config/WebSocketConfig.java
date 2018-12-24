package me.exrates.config;

import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.enums.ChatLang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.EnumMap;

import static me.exrates.model.enums.ChatLang.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Configuration
@PropertySource(value = "classpath:/websocket.properties")
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EnumMap<ChatLang, ChatWebSocketHandler> handlers;


    @Value("${ws.lib.url}")
    private String clientLibraryUrl;

    @Value("${ws.origin}")
    private String allowedOrigins;

    @Autowired
    public WebSocketConfig(final EnumMap<ChatLang, ChatWebSocketHandler> handlers) {
        this.handlers = handlers;
    }


    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        registry.addHandler(handlers.get(EN), "/chat-en").setAllowedOrigins(origins).withSockJS()
                .setClientLibraryUrl(clientLibraryUrl);
        registry.addHandler(handlers.get(RU), "/chat-ru").setAllowedOrigins(origins).withSockJS()
                .setClientLibraryUrl(clientLibraryUrl);
        registry.addHandler(handlers.get(CN), "/chat-cn").setAllowedOrigins(origins).withSockJS()
                .setClientLibraryUrl(clientLibraryUrl);
        registry.addHandler(handlers.get(AR), "/chat-ar").setAllowedOrigins(origins).withSockJS()
                .setClientLibraryUrl(clientLibraryUrl);
        registry.addHandler(handlers.get(IN), "/chat-in").setAllowedOrigins(origins).withSockJS()
                .setClientLibraryUrl(clientLibraryUrl);
        registry.addHandler(handlers.get(KO), "/chat-ko").setAllowedOrigins(origins).withSockJS()
                .setClientLibraryUrl(clientLibraryUrl);
    }

    @Bean(name = "chatEN")
    public ChatWebSocketHandler chatENWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatRU")
    public ChatWebSocketHandler chatRUWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatCN")
    public ChatWebSocketHandler chatCNWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatAR")
    public ChatWebSocketHandler chatARWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatIN")
    public ChatWebSocketHandler chatINWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatKO")
    public ChatWebSocketHandler chatKOWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

}
