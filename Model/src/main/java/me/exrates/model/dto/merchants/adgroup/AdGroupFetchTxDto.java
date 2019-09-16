package me.exrates.model.dto.merchants.adgroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AdGroupFetchTxDto {
    private Integer start;
    private Integer limit;

    @JsonProperty("tx_status")
    private String[] txStatus;

    @JsonProperty("order_id")
    private String[] orderId;
}
