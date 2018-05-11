package me.exrates.service.stomp;

import me.exrates.model.enums.OperationType;

import java.util.List;

/**
 * Created by Maks on 24.08.2017.
 */
public interface StompMessenger {

    void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType);

    void sendMyTradesToUser(int userId, Integer currencyPair);

    void sendAllTrades(Integer currencyPair);

    void sendCurrencyBalance(int walletId, int currencyId);

    void sendChartData(Integer currencyPairId);

    void sendStatisticMessage(List<Integer> currenciesIds);

    void sendEventMessage(String sessionId, String message);

    void sendAlerts(String message, String lang);
}
