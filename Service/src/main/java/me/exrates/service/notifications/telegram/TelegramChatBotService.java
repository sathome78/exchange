package me.exrates.service.notifications.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ChatDao;
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
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;

@Service
@Log4j2(topic = "message_notify")
@PropertySource("classpath:telegram_chat_bot.properties")
public class TelegramChatBotService extends TelegramLongPollingBot {

    private final static Logger logger = LogManager.getLogger(TelegramChatBotService.class);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public final static Integer COUNT_OF_MESSAGE_FOR_VIEW = 30;
    private final Queue<ChatHistoryDto> messages = EvictingQueue.create(COUNT_OF_MESSAGE_FOR_VIEW);


    private final SimpMessagingTemplate messagingTemplate;

    @Value("${telegram.chat_bot.key}")
    private String key;
    @Value("${telegram.chat_bot.username}")
    private String botName;
    @Value("${telegram.chat_bot.chat.id}")
    private String chatCommunityId;

    @Autowired
    public TelegramChatBotService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            String userName = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();

            String nickNameForDb = userName != null
                    ?  userName
                    : firstName + " " + lastName;

            ChatLang language = ChatLang.EN;

            ChatHistoryDto chatMessage = new ChatHistoryDto();
            chatMessage.setBody(messageText);
            chatMessage.setEmail(nickNameForDb);
            chatMessage.setMessageTime(LocalDateTime.now().format(FORMATTER));

            if(String.valueOf(chatId).equals(chatCommunityId)){
                messages.add(chatMessage);
                String destination = "/topic/chat/".concat(language.val.toLowerCase());
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

    public Queue<ChatHistoryDto> getMessages() {
        return messages;
    }
}
