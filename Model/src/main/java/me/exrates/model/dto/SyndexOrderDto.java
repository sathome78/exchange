package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.model.enums.SyndexOrderStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder

public class SyndexOrderDto {

    private int id;
    @JsonIgnore
    private long syndexId;
    @JsonIgnore
    private int userId;
    private BigDecimal amount;
    private SyndexOrderStatusEnum status;
    private BigDecimal commission;
    private String paymentSystemId;
    private String currency;
    private String countryId;
    private String paymentDetails;
    private LocalDateTime lastModifDate;
    private boolean isConfirmed;

    @Tolerate
    public SyndexOrderDto(RefillRequestCreateDto dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.amount = dto.getAmount();
        this.paymentSystemId = dto.getSyndexOrderParams().getPaymentSystem();
        this.currency = dto.getSyndexOrderParams().getCurrency();
        this.countryId = dto.getSyndexOrderParams().getCountry();
    }
}
