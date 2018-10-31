package me.exrates.service;

import me.exrates.model.ChatMessage;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.service.exception.IllegalChatMessageException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ChatService {

    ChatMessage persistMessage(String body, String email, ChatLang lang) throws IllegalChatMessageException;

    ChatMessage persistPublicMessage(String body, String email, ChatLang lang) throws IllegalChatMessageException;

    SortedSet<ChatMessage> getLastMessages(ChatLang lang);

    void deleteMessage(ChatMessage message, ChatLang lang);

    List<ChatHistoryDto> getPublicChatHistory(ChatLang chatLang);

    Map<LocalDate, List<ChatHistoryDto>> getPublicChatHistoryByDate(ChatLang chatLang);

    void flushCache();

    List<ChatHistoryDto> getChatHistory(ChatLang chatLang);
}
