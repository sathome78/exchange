package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
public class RefillRequestDataForCalculateCommissionDto {
  private Integer userId;
  private Integer currencyId;
  private Integer merchantId;
  private Integer commissionId;
  private BigDecimal amount;
}
