package me.exrates.model.chart;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.vo.BackDealInterval;

@Getter
@Setter
public class CandleDetailedDto {

    private String pairName;
    private BackDealInterval backDealInterval;
    private CandleDto candleDto;

    public CandleDetailedDto(String pairName, BackDealInterval backDealInterval, CandleDto candleDto) {
        this.pairName = pairName;
        this.backDealInterval = backDealInterval;
        this.candleDto = candleDto;
    }
}
