package me.exrates.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
public class RefillRequestAcceptParamDto {
  private Integer requestId;
  private BigDecimal amount;
  private String remark;
}
