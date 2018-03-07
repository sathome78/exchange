package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.Wallet;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by OLEG on 23.03.2017.
 */
@Getter @Setter
@ToString
public class WalletFormattedDto {
  private Integer id;
  private String name;
  private String description;
  private BigDecimal totalInput;
  private BigDecimal totalOutput;
  private BigDecimal totalSell;
  private BigDecimal totalBuy;
  private BigDecimal reserveOrders;
  private BigDecimal reserveWithdraw;
  private BigDecimal activeBalance;
  private BigDecimal reservedBalance;

  public WalletFormattedDto() {
  }

  public WalletFormattedDto(Wallet wallet) {
    this.id = wallet.getId();
    this.name = wallet.getName();
    this.activeBalance = wallet.getActiveBalance();
    this.reservedBalance = wallet.getReservedBalance();
  }
}
