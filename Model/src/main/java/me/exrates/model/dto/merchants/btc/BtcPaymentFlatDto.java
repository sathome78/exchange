package me.exrates.model.dto.merchants.btc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
@Builder
@ToString
public class BtcPaymentFlatDto {
  private BigDecimal amount;
  private Integer confirmations;
  private String txId;
  private String address;
  private String blockhash;
  private Integer merchantId;
  private Integer currencyId;
}
