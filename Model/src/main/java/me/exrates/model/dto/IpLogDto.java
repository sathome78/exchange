package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Builder
@Data
public class IpLogDto {

    private int id;
    private String ip;
    private String email;
    private String event;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateTime;
    private String url;
}
