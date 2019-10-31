package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.model.enums.SyndexOrderStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder

public class SyndexOrderDto {

    private int id;
    @JsonIgnore
    private Long syndexId;
    @JsonIgnore
    private int userId;
    private BigDecimal amount;
    private SyndexOrderStatusEnum status;
    private BigDecimal commission;
    private String paymentSystemId;
    private String currency;
    private String countryId;
    private String paymentDetails;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime statusModifDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime paymentDetailsReceivedDate;
    private boolean isConfirmed;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime paymentEndTime;

    @Tolerate
    public SyndexOrderDto(RefillRequestCreateDto dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.amount = dto.getAmount();
        this.commission = dto.getCommission();
        this.paymentSystemId = dto.getSyndexOrderParams().getPaymentSystem();
        this.currency = dto.getSyndexOrderParams().getCurrency();
        this.countryId = dto.getSyndexOrderParams().getCountry();
    }
}
