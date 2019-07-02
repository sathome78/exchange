package me.exrates.model.chartservicemodels;

import lombok.Data;
import me.exrates.model.vo.BackDealInterval;

import java.util.List;

@Data
public class CandlesDataDto {

    private List<CandleModel> candleModels;
    private String pairName;
    private BackDealInterval interval;
}
