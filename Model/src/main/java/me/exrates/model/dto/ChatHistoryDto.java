package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
public class ChatHistoryDto  {

    @JsonIgnore
    private Integer messageId;

    private Long chatId;

    @JsonIgnore
    private Integer telegramUserId;

    private String  email;
    private String  body;
    private String messageTime;

    @JsonIgnore
    private Integer telegramUserReplyId;
    @JsonIgnore
    private Integer messageReplyId;

    private String messageReplyUsername;
    private String messageReplyText;

    @JsonIgnore
    private LocalDateTime when;

    public static String getTitle() {
        return Stream.of("email", "body", "message_time", "messageReplyUsername", "messageReplyText")
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    @Override
    public String toString() {
        return Stream.of(
                email,
                body,
                messageTime,
                messageReplyUsername,
                messageReplyText
        ).collect(Collectors.joining(";", "", "\r\n"));
    }
}
