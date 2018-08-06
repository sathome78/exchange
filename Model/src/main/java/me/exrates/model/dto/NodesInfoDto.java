package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Data
public class NodesInfoDto {

    private String nodeName;
    private boolean isNodeWork;
    private boolean isNodeWorkCorrect;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastPollingTime;
}
