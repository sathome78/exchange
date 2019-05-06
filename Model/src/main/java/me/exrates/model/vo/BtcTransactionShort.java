package me.exrates.model.vo;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcTransactionShort {
  private Integer invoiceId;
  private String btcTransactionIdHash;
  private BigDecimal amount;
  private Integer confirmations;
}
