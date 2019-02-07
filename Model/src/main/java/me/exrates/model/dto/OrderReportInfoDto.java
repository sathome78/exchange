package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@ToString
@NoArgsConstructor
public class OrderReportInfoDto {
    private int id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateAcception;
    private String currencyPairName;
    private String orderTypeName;
    private String exrate;
    private String amountBase;
    private String orderCreatorEmail;
    private String creatorRole;
    private String orderAcceptorEmail;
    private String acceptorRole;
    private String orderStatusName;
}

