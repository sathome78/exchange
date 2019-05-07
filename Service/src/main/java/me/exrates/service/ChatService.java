package me.exrates.service;

import me.exrates.model.ChatMessage;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.service.exception.IllegalChatMessageException;

import java.util.List;
import java.util.SortedSet;

public interface ChatService {

    ChatMessage persistMessage(String body, String email, ChatLang lang) throws IllegalChatMessageException;

    SortedSet<ChatMessage> getLastMessages(ChatLang lang);

    void deleteMessage(ChatMessage message, ChatLang lang);

    void flushCache();

    List<ChatHistoryDto> getChatHistory(ChatLang chatLang);

    ChatMessage persistPublicMessage(String body, String email, ChatLang lang) throws IllegalChatMessageException;
}
