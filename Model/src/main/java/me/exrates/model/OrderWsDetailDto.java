package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderEventEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Data
public class OrderWsDetailDto {

    private int id;
    private OperationType orderType;
    private String exrate;
    private String amountBase;
    private String amountConvert;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateAcception;
    private String orderEvent;
    private long timestamp;
    private Integer orderParentId;
    private String orderStatus;

    public OrderWsDetailDto(ExOrder exOrder, OrderEventEnum orderEventEnum) {
        this.id = exOrder.getId();
        this.orderType = exOrder.getOperationType();
        this.exrate = exOrder.getExRate().toPlainString();
        this.amountBase = exOrder.getAmountBase().toPlainString();
        this.amountConvert = exOrder.getAmountConvert().toPlainString();
        this.orderEvent = orderEventEnum.name();
        this.dateAcception = exOrder.getDateAcception();
        this.timestamp = exOrder.getEventTimestamp();
        this.orderParentId = exOrder.getSourceId();
        this.orderStatus = exOrder.getStatus() == null ? null : exOrder.getStatus().name();
    }

    public OrderWsDetailDto(OrderListDto dto) {
        this.id = dto.getId();
        this.orderType = dto.getOrderType();
        this.exrate = dto.getExrate();
        this.amountBase = dto.getAmountBase();
        this.amountConvert = dto.getAmountConvert();
        this.orderParentId = dto.getOrderSourceId();
        this.orderStatus = OrderStatus.OPENED.name();
    }
}
