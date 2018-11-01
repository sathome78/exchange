package me.exrates.service.notifications.telegram;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ChatDao;
import me.exrates.model.ChatMessage;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.dto.TelegramSubscription;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.enums.NotificatorSubscriptionStateEnum;
import me.exrates.service.UserService;
import me.exrates.service.exception.MessageUndeliweredException;
import me.exrates.service.exception.TelegramSubscriptionException;
import me.exrates.service.notifications.Subscribable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2(topic = "message_notify")
@Service
public class TelegramChatBotService extends TelegramLongPollingBot {

    private final static String CHAT_ID_EXRATES_OFFICIAL = "-1001195048692";
    private final static String USER_EMAIL_TEST = "promo@exrates.me";
    private final static Integer NUMBER_FOR_SET_UNIQ_MESSAGE_ID = 1800000000;

    private final static Logger LOG = LogManager.getLogger(TelegramChatBotService.class);

    private final static String KEY = "698963124:AAENi1yq5gnqY8S2Fzlfd9smAYCGBmCSFY4";
    private final static String BOT_NAME = "exrates_official_test";

    public static List<ChatHistoryDto> chatHistoryDtoListFromTelegram = new ArrayList<>();
    public final static Integer COUNT_OF_MESSAGE_FOR_VIEW = 30;

    private final UserService userService;
    private final ChatDao chatDao;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TelegramChatBotService(ChatDao chatDao, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatDao = chatDao;
        this.userService = userService;
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
            Integer messageId = update.getMessage().getMessageId();

            Integer userId = update.getMessage().getFrom().getId();
            String userName = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();

            String nickNameForDb;
            if(userName!=null){
                nickNameForDb = userName;
            } else {
                nickNameForDb = firstName + " " + lastName;
            }

            int userIdTEST = userService.getIdByEmail(USER_EMAIL_TEST);
            Integer messageIdForBd = messageId+NUMBER_FOR_SET_UNIQ_MESSAGE_ID;
            ChatLang language = ChatLang.EN;

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(messageIdForBd);
            chatMessage.setUserId(userIdTEST);
            chatMessage.setNickname(nickNameForDb);
            chatMessage.setBody(messageText);
            chatMessage.setTime(LocalDateTime.now());

//            Set<ChatMessage> setCollectionChatMessage = new HashSet<>();
//            setCollectionChatMessage.add(chatMessage);

            if(String.valueOf(chatId).equals(CHAT_ID_EXRATES_OFFICIAL)){
                chatHistoryDtoListFromTelegram.add(fromChatMessage(chatMessage));
//                chatDao.persist(language, setCollectionChatMessage);
                String destination = "/topic/chat/".concat(language.val.toLowerCase());
                messagingTemplate.convertAndSend(destination, fromChatMessage(chatMessage));
                LOG.info("Send chat message from TELEGRAM. Message id in DB:"+messageIdForBd+" | Chat id: "+chatId+" | From user (userId in Telegram):"+userId+" | User name:"+nickNameForDb+" | Message text"+messageText);
            }
        }
    }

    private ChatHistoryDto fromChatMessage(ChatMessage message) {
        ChatHistoryDto dto = new ChatHistoryDto();
        dto.setMessageTime(message.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setEmail(message.getNickname());
        dto.setBody(message.getBody());
        return dto;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return KEY;
    }


}
