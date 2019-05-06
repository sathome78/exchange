package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class BtcWalletInfoDto {

  private String balance;
  private String confirmedNonSpendableBalance;
  private String unconfirmedBalance;
  private Integer transactionCount;

}
