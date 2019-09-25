package me.exrates.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLoginSessionDto {

    private Integer userId;
    private String device;
    private String userAgent;
    private String os;
    private String ip;
    private String country;
    private String region;
    private String city;
    private String token;
    private LocalDateTime started;
    private LocalDateTime modified;
}
