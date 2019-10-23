package me.exrates.model.dto.tradingview;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.ExOrder;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class ExternalOrderDto {

    private String pairName;
    private BigDecimal exrate;
    private BigDecimal amountBase;
    private String operationType;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDate;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime acceptDate;

    public ExternalOrderDto(ExOrder order) {
        this.exrate = order.getExRate();
        this.amountBase = order.getAmountBase();
        this.operationType = Objects.nonNull(order.getOperationType())
                ? order.getOperationType().name()
                : null;
        this.createDate = order.getDateCreation();
        this.acceptDate = order.getDateAcception();
    }
}