package me.exrates.model.dto.merchants.adgroup.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class InvoiceDto {
    private String message;
    @JsonProperty("_id")
    private String id;
    private String walletAddr;
    private String comment;
    private String paymentLink;
}
