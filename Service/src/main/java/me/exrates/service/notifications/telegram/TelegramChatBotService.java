package me.exrates.service.notifications.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ChatDao;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Queue;

@Service
@Log4j2(topic = "message_notify")
@PropertySource("classpath:/telegram_chat_bot.properties")
public class TelegramChatBotService extends TelegramLongPollingBot {

    private final static Logger logger = LogManager.getLogger(TelegramChatBotService.class);

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private final static ChatLang LANGUAGE = ChatLang.EN;

    private final SimpMessagingTemplate messagingTemplate;
    private final TelegramChatDao telegramChatDao;

    @Value("${telegram.chat_bot.key}")
    private String key;
    @Value("${telegram.chat_bot.username}")
    private String botName;
    @Value("${telegram.chat_bot.chat.id}")
    private String chatCommunityId;

    @Autowired
    public TelegramChatBotService(SimpMessagingTemplate messagingTemplate, TelegramChatDao telegramChatDao) {
        this.messagingTemplate = messagingTemplate;
        this.telegramChatDao = telegramChatDao;
    }

    static {ApiContextInitializer.init();}

    @PostConstruct
    private void initBot() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasEditedMessage() && update.getEditedMessage().hasText()){
            Message editedMessage = update.getEditedMessage();

            ChatHistoryDto editedChatMessage = new ChatHistoryDto();
                editedChatMessage.setMessageId(editedMessage.getMessageId());
                editedChatMessage.setChatId(editedMessage.getChatId());
                editedChatMessage.setBody(editedMessage.getText());

            telegramChatDao.updateChatMessage(LANGUAGE, editedChatMessage);

            logger.info("Update chat message from TELEGRAM. Chat id: "+editedMessage.getChatId()+ " | Message id:"+editedMessage.getMessageId()+
                    "| UserId in Telegram:"+editedMessage.getFrom().getId()+" | Message text" + editedMessage.getText());

        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            Long chatId = message.getChatId();
            String messageText = message.getText();

            String firstName = message.getFrom().getFirstName();
            String lastName = message.getFrom().getLastName();

            String nickNameForDb = firstName+" "+Optional.ofNullable(lastName).orElse("");

            ChatHistoryDto chatMessage = new ChatHistoryDto();
            chatMessage.setMessageId(message.getMessageId());
            chatMessage.setChatId(chatId);
            chatMessage.setTelegramUserId(message.getFrom().getId());
            chatMessage.setEmail(nickNameForDb);
            chatMessage.setBody(messageText);
            chatMessage.setMessageTime(LocalDateTime.now().format(FORMATTER));


            Optional.ofNullable(update.getMessage().getReplyToMessage()).ifPresent(messageReply -> {
                chatMessage.setTelegramUserReplyId(messageReply.getFrom().getId());
                chatMessage.setMessageReplyId(messageReply.getMessageId());
                chatMessage.setMessageReplyUsername(messageReply.getFrom().getFirstName()+" "
                        +Optional.ofNullable(messageReply.getFrom().getLastName()).orElse(""));
                chatMessage.setMessageReplyText(messageReply.getText());
            });

            if(String.valueOf(chatId).equals(chatCommunityId)){
                telegramChatDao.saveChatMessage(LANGUAGE, chatMessage);
                String destination = "/topic/chat/".concat(LANGUAGE.val.toLowerCase());
                messagingTemplate.convertAndSend(destination, toJson(chatMessage));
                logger.info("Send chat message from TELEGRAM. Chat id: "+chatId+" | From user (userId in Telegram) name:"+nickNameForDb+" | Message text"+messageText);
            } else {
                logger.info("Received TELEGRAM message {} but for chat id: {}",  messageText, chatId);
            }
        }
    }

    private String toJson(ChatHistoryDto message) {
        String send = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            send = mapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.info("Failed to convert to json {} at {}", message.getBody(), message.getMessageTime());
        }
        return send;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return key;
    }

}
