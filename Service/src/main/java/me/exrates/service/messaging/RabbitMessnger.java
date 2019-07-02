package me.exrates.service.messaging;

import me.exrates.model.chartservicemodels.TradeDataDto;

public interface RabbitMessnger {
    void sendTradeInfoToChartService(TradeDataDto dataDto);
}
