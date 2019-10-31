package me.exrates.model.dto.freecoins;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class AdminGiveawayResultDto {

    private static final String SINGLE = "single";

    private int id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;
    private String currency;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    private String period;
    @JsonProperty("prize_amount")
    private BigDecimal prizeAmount;
    private int quantity;
    @JsonProperty("quantity_left")
    private int quantityLeft;
    @JsonProperty("unique_acceptors")
    private int uniqueAcceptors;
    private String creator;
    private String status;

    public AdminGiveawayResultDto(GiveawayResultDto claim) {
        this.id = claim.getId();
        this.date = claim.getCreatedAt();
        this.currency = claim.getCurrencyName();
        this.totalAmount = claim.getAmount();
        this.period = claim.isSingle() ? SINGLE : claim.getTimeRange().toString();
        this.prizeAmount = claim.getPartialAmount();
        this.quantity = (int) (claim.getAmount().doubleValue() / claim.getPartialAmount().doubleValue());
        this.quantityLeft = claim.getTotalQuantity();
        this.creator = claim.getCreatorEmail();
        this.status = claim.getStatus().name();
    }
}