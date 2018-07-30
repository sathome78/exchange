package me.exrates.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NodesInfoDto {

    private String nodeName;
    private boolean isNodeWork;
    private boolean isNodeWorkCorrect;
    private LocalDateTime lastPollingTime;
}
