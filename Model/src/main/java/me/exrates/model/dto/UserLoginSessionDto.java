package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginSessionDto {

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
