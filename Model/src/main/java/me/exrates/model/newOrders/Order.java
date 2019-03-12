package me.exrates.model.newOrders;

import lombok.Data;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order implements Serializable {

    private Integer id;
    private Integer userId;
    private Integer currencyPairId;
    private Integer orderTypeId;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private BigDecimal amountAccepted;
    private BigDecimal amountAvailable;
    private BigDecimal exrate;
    private BigDecimal commissionMakerFixedAmount;
    private Integer commissionId;
    private Integer orderStatusId;
    private LocalDateTime dateOfCreation;
    private LocalDateTime dateOfLastUpdate;
    private OrderBaseType baseType;

    public Order(OrderCreateDto orderCreateDto) {
        this.id = orderCreateDto.getOrderId();
        this.userId = orderCreateDto.getUserId();
        this.currencyPairId = orderCreateDto.getCurrencyPair().getId();
        this.orderTypeId = OrderType.convert(orderCreateDto.getOperationType().name()).getType();
        this.exrate = orderCreateDto.getExchangeRate();
        this.amountBase = orderCreateDto.getAmount();
        this.amountConvert = orderCreateDto.getTotal();
        this.amountAvailable = orderCreateDto.getAmount();
        this.commissionId = orderCreateDto.getComissionId();
        this.commissionMakerFixedAmount = orderCreateDto.getComission();
        this.orderStatusId = orderCreateDto.getStatus().getStatus();
        this.currencyPairId = orderCreateDto.getCurrencyPair().getId();
        this.baseType = orderCreateDto.getOrderBaseType();
    }
}
