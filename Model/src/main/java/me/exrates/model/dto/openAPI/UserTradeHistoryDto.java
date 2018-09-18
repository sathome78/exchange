package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PRIVATE)
public class UserTradeHistoryDto extends TradeHistoryDto {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("maker")
    private Boolean isMaker;

    @Builder
    public UserTradeHistoryDto(Integer userId, Boolean isMaker, Integer orderId, String currencyPair, LocalDateTime dateAcceptance,
                               LocalDateTime dateCreation, BigDecimal amount, BigDecimal price, BigDecimal total, OrderType orderType) {
        super(orderId, currencyPair, dateAcceptance, dateCreation, amount, price, total, orderType);
        this.userId = userId;
        this.isMaker = isMaker;
    }
}
