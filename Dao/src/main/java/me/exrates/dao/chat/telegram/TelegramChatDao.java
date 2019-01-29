package me.exrates.dao.chat.telegram;

import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;

import java.util.List;

public interface TelegramChatDao {

    boolean saveChatMessage(ChatLang lang, ChatHistoryDto message);

    boolean updateChatMessage(ChatLang lang, ChatHistoryDto message);

    List<ChatHistoryDto> getChatHistoryQuick(ChatLang chatLang);
}
