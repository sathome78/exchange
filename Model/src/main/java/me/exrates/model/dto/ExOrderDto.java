package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExOrderDto {

    private int id;
    private int currencyPairId;
    @JsonProperty("order_type")
    private OperationType operationType;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal amountConvert;
    private BigDecimal commission;
    private int userAcceptorId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime accepted;
    private OrderStatus status;
    private OrderBaseType baseType;
    private BigDecimal stop;
    private BigDecimal partiallyAcceptedAmount;

    public static ExOrderDto valueOf(ExOrder exOrder) {
        ExOrderDto exOrderDto = new ExOrderDto();
        exOrderDto.setId(exOrder.getId());
        exOrderDto.setCurrencyPairId(exOrder.getCurrencyPairId());
        exOrderDto.setOperationType(exOrder.getOperationType());
        exOrderDto.setPrice(exOrder.getExRate());
        exOrderDto.setAmount(exOrder.getAmountBase());
        exOrderDto.setAmountConvert(exOrder.getAmountConvert());
        exOrderDto.setCommission(exOrder.getCommissionFixedAmount());
        exOrderDto.setUserAcceptorId(exOrder.getUserAcceptorId());
        exOrderDto.setCreated(exOrder.getDateCreation());
        exOrderDto.setAccepted(exOrder.getDateAcception());
        exOrderDto.setStatus(exOrder.getStatus());
        exOrderDto.setStop(exOrder.getStop());
        exOrderDto.setPartiallyAcceptedAmount(exOrder.getPartiallyAcceptedAmount());
        exOrderDto.setBaseType(exOrder.getOrderBaseType());
        return exOrderDto;
    }

}
