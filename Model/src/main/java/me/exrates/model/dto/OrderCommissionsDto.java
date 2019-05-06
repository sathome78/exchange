package me.exrates.model.dto;

import java.math.BigDecimal;

public class OrderCommissionsDto {
    private BigDecimal sellCommission;
    private BigDecimal buyCommission;

    public BigDecimal getSellCommission() {
        return sellCommission;
    }

    public void setSellCommission(BigDecimal sellCommission) {
        this.sellCommission = sellCommission;
    }

    public BigDecimal getBuyCommission() {
        return buyCommission;
    }

    public void setBuyCommission(BigDecimal buyCommission) {
        this.buyCommission = buyCommission;
    }

    public static OrderCommissionsDto zeroComissions() {
        OrderCommissionsDto orderCommissionsDto = new OrderCommissionsDto();
        orderCommissionsDto.setSellCommission(BigDecimal.ZERO);
        orderCommissionsDto.setBuyCommission(BigDecimal.ZERO);
        return orderCommissionsDto;
    }
}
