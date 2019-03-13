package me.exrates.model.dto.onlineTableDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.CurrencyPairType;

/**
 * Created by Valk
 */
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ExOrderStatisticsShortByPairsDto extends OnlineTableDto {

    private Integer currencyPairId;
    private String currencyPairName;
    private Integer currencyPairPrecision;
    private String lastOrderRate;
    private String predLastOrderRate;
    private String percentChange;
    private String market;
    private String priceInUSD;
    private CurrencyPairType type;
    @JsonIgnore
    private Integer pairOrder;
    @JsonIgnore
    private Integer currency1Id;
    private String volume;
    private String currencyVolume;

    private String high24hr;
    private String low24hr;

    private String lastUpdateCache;

    public ExOrderStatisticsShortByPairsDto() {
        this.needRefresh = true;
    }

    public ExOrderStatisticsShortByPairsDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public ExOrderStatisticsShortByPairsDto(ExOrderStatisticsShortByPairsDto statistic) {
        this.needRefresh = statistic.needRefresh;
        this.page = statistic.page;
        this.currencyPairId = statistic.currencyPairId;
        this.currencyPairName = statistic.currencyPairName;
        this.currencyPairPrecision = statistic.currencyPairPrecision;
        this.lastOrderRate = statistic.lastOrderRate;
        this.predLastOrderRate = statistic.predLastOrderRate;
        this.percentChange = statistic.percentChange;
        this.market = statistic.market;
        this.priceInUSD = statistic.priceInUSD;
        this.type = statistic.type;
        this.pairOrder = statistic.pairOrder;
        this.currency1Id = statistic.currency1Id;
        this.volume = statistic.volume;
        this.currencyVolume = statistic.currencyVolume;
        this.high24hr = statistic.high24hr;
        this.low24hr = statistic.low24hr;
    }

    @Override
    public int hashCode() {
        int result = currencyPairName != null ? currencyPairName.hashCode() : 0;
        result = 31 * result + (lastOrderRate != null ? lastOrderRate.hashCode() : 0);
        result = 31 * result + (predLastOrderRate != null ? predLastOrderRate.hashCode() : 0);
        return result;
    }
}