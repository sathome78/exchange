package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.serializer.LocalDateTimeSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Getter @Setter
public class ChatHistoryDto  {

    private String  email;
    private String  body;
    private String messageTime;

    public static String getTitle() {
        return Stream.of("email", "body", "message_time")
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    @Override
    public String toString() {
        return Stream.of(
                email,
                body,
                messageTime
        ).collect(Collectors.joining(";", "", "\r\n"));
    }
}
