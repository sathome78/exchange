package me.exrates.model.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum CommissionTypeParameterUpdateEnum {

    CURRENCY_NAME("merchant_secondary_commission_currency", (o, function) -> {
        if (StringUtils.isEmpty(o)) {
            return null;
        }
        return function.apply(o);
    }),

    COMMISSION_TYPE("withdraw_merchant_commission_type",
            (o, function) -> {
                MerchantCommissonTypeEnum commissonTypeEnum = MerchantCommissonTypeEnum.valueOf(o);
                return commissonTypeEnum.name();
            }
    );

    private String dbColumnName;
    private BiFunction<String, Function<String, Object>, Object> valueConverter;

    CommissionTypeParameterUpdateEnum(String dbColumnName, BiFunction<String, Function<String, Object>, Object> valueConverter) {
        this.dbColumnName = dbColumnName;
        this.valueConverter = valueConverter;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public Object getFinalValue(String value, Function<String, Object> addititonalFunction) {

        return valueConverter.apply(value, addititonalFunction);
    }

}
