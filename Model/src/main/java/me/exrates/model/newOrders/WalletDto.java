package me.exrates.model.newOrders;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WalletDto implements Serializable {

    private Integer id;
    private Integer currencyId;
    private Integer userId;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
}
