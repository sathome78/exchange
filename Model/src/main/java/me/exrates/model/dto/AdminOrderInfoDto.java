package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminOrderInfoDto {

    private boolean isAcceptable;
    private String notification;
    private OrderInfoDto orderInfo;

    public AdminOrderInfoDto(OrderInfoDto orderInfo) {
        this.orderInfo = orderInfo;
    }
}
