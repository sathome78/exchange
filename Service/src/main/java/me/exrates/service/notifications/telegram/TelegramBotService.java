package me.exrates.service.notifications.telegram;

public interface TelegramBotService {
    void sendMessage(Long chatId, String text);
}
