package me.exrates.model.ngModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ResponseInfoCurrencyPairDto {

    private String currencyRate;
    private String percentChange;
    private String changedValue;
    private String lastCurrencyRate;
    private String volume24h;
    private String rateHigh;
    private String rateLow;
    @JsonIgnore
    private String pairName;

    public ResponseInfoCurrencyPairDto() {
    }

    public ResponseInfoCurrencyPairDto(ExOrderStatisticsShortByPairsDto dto) {
        this.currencyRate = dto.getLastOrderRate();
        this.percentChange = dto.getPercentChange();
        this.lastCurrencyRate = dto.getLastOrderRate();
        this.changedValue = dto.getValueChange();
        this.volume24h = dto.getVolume();
        this.rateHigh = dto.getHigh24hr();
        this.rateLow = dto.getLow24hr();
        this.setPairName(dto.getCurrencyPairName());
    }
}
