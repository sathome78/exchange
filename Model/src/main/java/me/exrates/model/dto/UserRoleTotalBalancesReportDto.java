package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.RealCheckableRole;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
public class UserRoleTotalBalancesReportDto<T extends Enum & RealCheckableRole> {

    private String currency;
    private Map<String, BigDecimal> balances;
    private Class<T> enumClass;

    private String totalReal = BigDecimalProcessing.formatNoneComma(BigDecimal.ZERO, false);

    public UserRoleTotalBalancesReportDto(String currency, Map<String, BigDecimal> balances, Class<T> enumClass) {
        this.currency = currency;
        this.balances = balances;
        this.enumClass = enumClass;
        if (RealCheckableRole.class.isAssignableFrom(enumClass)) {
            BigDecimal totalRealValue = Arrays.stream(enumClass.getEnumConstants()).map(dto -> (RealCheckableRole) dto).filter(RealCheckableRole::isReal)
                    .map(dto -> balances.getOrDefault(dto.getName(), BigDecimal.ZERO)).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

            totalReal = BigDecimalProcessing.formatNoneComma(totalRealValue, false);
        }
    }

    public static <T extends Enum> String getTitle(Class<T> enumClass) {
        return Stream.concat(Stream.of("currency", "totalReal"), Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).sorted())
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.concat(Stream.of(currency, totalReal), Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).sorted()
                .map(item -> BigDecimalProcessing.formatNoneComma(balances.getOrDefault(item, BigDecimal.ZERO), false)))
                .collect(Collectors.joining(";", "", "\r\n"));
    }


}
