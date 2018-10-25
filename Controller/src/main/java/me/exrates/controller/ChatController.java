package me.exrates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.ChatMessage;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.dto.RemovedMessageDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.enums.UserRole;
import me.exrates.service.ChatService;
import me.exrates.service.UserService;
import me.exrates.service.annotation.ThreadSafe;
import me.exrates.service.exception.IllegalChatMessageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ThreadSafe
@RestController
public class ChatController {

    private final ChatService chatService;
    private final MessageSource messageSource;
    private final EnumMap<ChatLang, ChatWebSocketHandler> handlers;
    //   private final Gson gson = new Gson();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger LOG = LogManager.getLogger(ChatController.class);

    @Autowired
    private UserService userService;

    public ChatController(final ChatService chatService,
                          final MessageSource messageSource,
                          final EnumMap<ChatLang, ChatWebSocketHandler> handlers) {
        this.chatService = chatService;
        this.messageSource = messageSource;
        this.handlers = handlers;
    }

    @RequestMapping(value = "/chat/new-message", method = POST)
    public ResponseEntity<Map<String, String>> newMessage(final @RequestParam("body") String body,
                                                          final @RequestParam("lang") String lang,
                                                          final Principal principal,
                                                          final Locale locale) {
        if(!userService.hasNickname(principal.getName())){
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.put("Content-type", singletonList("application/json; charset=utf-8"));
            return new ResponseEntity<>(singletonMap("errorInfoSendChatMessageWithoutNickname",
                    messageSource.getMessage("chat.error.userWithoutNickname", null, locale)), headers, BAD_REQUEST);
        }
        final ChatLang chatLang = ChatLang.toInstance(lang);
        final ChatMessage message;
        try {
            message = chatService.persistMessage(body, principal.getName(), chatLang);
        } catch (IllegalChatMessageException e) {
            LOG.error(e);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.put("Content-type", singletonList("application/json; charset=utf-8"));
            return new ResponseEntity<>(singletonMap("errorInfo",
                    messageSource.getMessage("chat.invalidSymbols", null, locale)), headers, BAD_REQUEST);
        }
        handlers.get(chatLang).getSessions().forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
            } catch (IOException e) {
                LOG.error(e);
            }
        });
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(value = "/chat/history", method = GET)
    public Set<ChatMessage> chatMessages(final @RequestParam("lang") String lang) {
        return chatService.getLastMessages(ChatLang.toInstance(lang));
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/chat/allHistory", method = GET, produces = "text/plain;charset=UTF-8")
    public String downloadChatMessages(@RequestParam("lang") String lang) {
        chatService.flushCache();
        List<ChatHistoryDto> result = chatService.getChatHistory(ChatLang.toInstance(lang));
        return result.stream().map(ChatHistoryDto::toString)
                .collect(Collectors.joining("", ChatHistoryDto.getTitle(), ""));
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/chat/deleteMessage", method = POST)
    public @ResponseBody
    String deleteMessage(HttpServletRequest request) {

        Map<String, String[]> params = request.getParameterMap();
        ChatMessage message = new ChatMessage();
        message.setId(Long.parseLong(params.get("id")[0]));
        message.setUserId(Integer.parseInt(params.get("userId")[0]));
        message.setBody(params.get("body")[0]);
        message.setNickname(params.get("nickname")[0]);
        ChatLang lang = ChatLang.toInstance(params.get("lang")[0]);

        chatService.deleteMessage(message, lang);
        handlers.get(lang).getSessions().forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(new RemovedMessageDto(message.getId()))));
            } catch (IOException e) {
                LOG.error(e);
            }
        });

        return "successful";

    }


}
