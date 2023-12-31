package me.exrates.model.enums;

import org.apache.commons.lang3.StringUtils;

import static me.exrates.model.enums.OrderBaseType.LIMIT;

public enum CurrencyPairType {

    MAIN(LIMIT), ICO(me.exrates.model.enums.OrderBaseType.ICO), ALL(null);

    private OrderBaseType orderBaseType;

    public OrderBaseType getOrderBaseType() {
        return orderBaseType;
    }

    CurrencyPairType(OrderBaseType orderBaseType) {
        this.orderBaseType = orderBaseType;
    }

    public static CurrencyPairType getType(String value) {
        if (!StringUtils.isBlank(value)) {
            if (value.equalsIgnoreCase(ICO.toString())) {
                return ICO;
            }
            return MAIN;
        }
        return MAIN;
    }
}
