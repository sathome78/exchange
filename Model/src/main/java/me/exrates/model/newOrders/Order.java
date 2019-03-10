package me.exrates.model.newOrders;

import lombok.Data;
import me.exrates.model.enums.OrderBaseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {

    private Integer id;
    private Integer userId;
    private Integer currencyPairId;
    private Integer orderTypeId;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private BigDecimal amountAccepted;
    private BigDecimal exrate;
    private BigDecimal commissionMakerFixedAmount;
    private Integer commissionId;
    private Integer orderStatusId;
    private LocalDateTime dateOfCreation;
    private LocalDateTime dateOfLastUpdate;
    private OrderBaseType baseType;

}
