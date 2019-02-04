package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.OrderStatus;

import java.time.LocalDate;
import java.util.Map;

@Getter
@ToString(exclude = {"currencyPair", "sortedColumns"})
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterDataDto {

    private Integer userId;
    private OrderStatus status;
    private CurrencyPair currencyPair;
    private String currencyName;
    private Integer offset;
    private Integer limit;
    private Map<String, String> sortedColumns;
    private String scope;
    private Boolean hideCanceled;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
