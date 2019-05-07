package me.exrates.service.stomp;

import lombok.SneakyThrows;
import me.exrates.model.CurrencyPair;
import me.exrates.model.IEODetails;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.enums.OperationType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StompMessenger {

    void sendRefreshTradeOrdersMessage(CurrencyPair currencyPair, OperationType operationType);

    void sendRefreshTradeOrdersDetailMessage(String pairName, String message);

    void sendPersonalOpenOrdersAndDealsToUser(Integer userId, String pairName, String message);

    void sendMyTradesToUser(int userId, Integer currencyPair);

    void sendAllTrades(CurrencyPair currencyPair);

    List<ChartTimeFrame> getSubscribedTimeFramesForCurrencyPair(Integer pairId);

    void sendStatisticMessage(Set<Integer> currenciesIds);

    void sendCpInfoMessage(Map<String, String> currenciesStatisticMap);

    void sendEventMessage(String sessionId, String message);

    void sendAlerts(String message, String lang);

    @SneakyThrows
    void sendPersonalMessageToUser(String userEmail, UserNotificationMessage message);

    void sendPersonalDetailsIeo(String userEmail, String payload);

    void sendDetailsIeo(Integer detailId, String payload);

    void sendAllIeos(Collection<IEODetails> ieoDetails);
}
