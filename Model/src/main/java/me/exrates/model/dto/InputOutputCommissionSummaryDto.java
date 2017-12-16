package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
public class InputOutputCommissionSummaryDto {
    private Integer orderNum;
    private String currencyName;
    private BigDecimal input;
    private BigDecimal output;
    private BigDecimal inputCommission;
    private BigDecimal outputCommission;


    public static String getTitle() {
        return Stream.of("No.", "currency", "input", "output", "input_commission", "output_commission")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), currencyName,
                BigDecimalProcessing.formatNoneComma(input, false),
                BigDecimalProcessing.formatNoneComma(output, false),
                BigDecimalProcessing.formatNoneComma(inputCommission, false),
                BigDecimalProcessing.formatNoneComma(outputCommission, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }
}
