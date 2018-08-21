package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class CandleDto {
    private long time;
    private double close;
    private double open;
    private double high;
    private double low;
    private double volume;

    public CandleDto() {
    }

    public CandleDto(CandleChartItemDto candleChartItemDto) {
        this.time = candleChartItemDto.getBeginDate().getTime();
        this.close = candleChartItemDto.getCloseRate().doubleValue();
        this.open = candleChartItemDto.getOpenRate().doubleValue();
        this.high = candleChartItemDto.getHighRate().doubleValue();
        this.low = candleChartItemDto.getLowRate().doubleValue();
        this.volume = candleChartItemDto.getBaseVolume().doubleValue();
    }
}
