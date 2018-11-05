package me.exrates.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter @ToString
@Data
public class WalletsForOrderCancelDto {
    int orderId;
    int orderStatusId;
    BigDecimal reservedAmount;
    int walletId;
    BigDecimal activeBalance;
    BigDecimal reservedBalance;
}
