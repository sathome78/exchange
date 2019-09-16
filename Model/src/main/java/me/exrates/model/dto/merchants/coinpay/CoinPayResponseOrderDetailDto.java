package me.exrates.model.dto.merchants.coinpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoinPayResponseOrderDetailDto {
    private String status;
    @JsonProperty("order_id")
    private String orderId;
}
