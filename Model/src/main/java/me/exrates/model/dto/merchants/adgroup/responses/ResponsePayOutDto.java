package me.exrates.model.dto.merchants.adgroup.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
public class ResponsePayOutDto {

    @JsonProperty("original_amount")
    private BigDecimal originalAmount;

    private BigDecimal amount;

    private String status;

    @JsonProperty("_id")
    private String id;

    @JsonProperty("ref_id")
    private String refId;

    @JsonProperty("extra_id")
    private String extraId;


}
