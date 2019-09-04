package me.exrates.model.dto.merchants.coinpay;

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
public class CoinPayResponseDepositDto {
    private String status;
    private String qr;
    private String addr;
}
