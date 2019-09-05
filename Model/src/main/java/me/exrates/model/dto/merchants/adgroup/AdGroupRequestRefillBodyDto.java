package me.exrates.model.dto.merchants.adgroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdGroupRequestRefillBodyDto {

    private BigDecimal amount;

    private Long tel;

    private String platform;

    private String currency;

    @JsonProperty("payment_method")
    private String paymentMethod;

}
