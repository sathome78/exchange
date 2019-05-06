package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class MerchantCurrencyLifetimeDto {
  private Integer merchantId;
  private Integer currencyId;
  private Integer refillLifetimeHours;
  private Integer withdrawLifetimeHours;
}
