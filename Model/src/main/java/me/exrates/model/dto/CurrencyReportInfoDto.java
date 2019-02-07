package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
@NoArgsConstructor
public class CurrencyReportInfoDto {
    private String email;
    private String activeBalance;
    private String reservedBalance;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateUserRegistration;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateLastRefillByUser;
}

