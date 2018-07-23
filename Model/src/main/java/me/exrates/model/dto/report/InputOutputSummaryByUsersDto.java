package me.exrates.model.dto.report;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vlad Dziubak
 * Date: 20.07.2018
 */
@Getter @Setter
public class InputOutputSummaryByUsersDto {
    private int userId;
    private String userEmail;
    private int currencyId;
    private String currencyName;
    private BigDecimal balanceInHand;
    private BigDecimal rateToUSD;

    public static String getTitle() {
        return Stream.of("user_id", "email", "currency_id", "currency", "balanceInHand", "rateToUSD")
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    @Override
    public String toString() {
        return Stream.of(String.valueOf(userId), userEmail, String.valueOf(currencyId), currencyName,
                BigDecimalProcessing.formatNoneComma(balanceInHand, false),
                BigDecimalProcessing.formatNoneComma(rateToUSD, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }
}
