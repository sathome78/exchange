package me.exrates.dao;

import me.exrates.model.ChatMessage;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;

import java.util.List;
import java.util.Set;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ChatDao {

    List<ChatMessage> findLastMessages(ChatLang lang, int messageCount);

    void persist(ChatLang lang, Set<ChatMessage> message);

    void delete(ChatLang lang, ChatMessage message);

    List<ChatHistoryDto> getChatHistory(ChatLang chatLang);
}
