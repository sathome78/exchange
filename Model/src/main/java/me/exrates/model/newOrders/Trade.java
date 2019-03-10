package me.exrates.model.newOrders;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Trade {

    private Integer id;
    private Integer orderId;
    private Integer userMakerId;
    private Integer userTakerId;
    private Integer currencyPairId;
    private Integer orderTypeId;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private BigDecimal exrate;
    private BigDecimal commissionMakerAmount;
    private Integer commissionMakerId;
    private Integer commissionTakerAmount;
    private Integer commissionTakerId;
    private LocalDateTime dateOfTrade;

}
