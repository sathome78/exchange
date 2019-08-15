package me.exrates.model.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleDetailedDto {

    private String pairName;
    private String backDealInterval;
    private CandleDto candleDto;
}
