package me.exrates.service;

import me.exrates.model.BotLaunchSettings;
import me.exrates.model.BotTrader;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.BotTradingSettingsShortDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public interface BotService {

    void acceptAfterDelay(ExOrder exOrder);

    Optional<BotTrader> retrieveBotFromDB();

    void createBot(String nickname, String email, String password);

    void updateBot(BotTrader botTrader, Locale locale);

    void runOrderCreation(Integer currencyPairId, OrderType orderType);

    void prepareAndSaveOrder(CurrencyPair currencyPair, OperationType operationType, String userEmail, BigDecimal amount, BigDecimal rate);

    void enableBotForCurrencyPair(Integer currencyPairId, Locale locale);

    void disableBotForCurrencyPair(Integer currencyPairId);

    Map<String, BotTradingSettingsShortDto> retrieveTradingSettingsShort(int botLaunchSettingsId);

    List<BotLaunchSettings> retrieveLaunchSettings(int botId);

    void toggleBotStatusForCurrencyPair(Integer currencyPairId, boolean status, Locale locale);

    void updateLaunchSettings(BotLaunchSettings launchSettings);

    void updateTradingSettings(BotTradingSettingsShortDto tradingSettings);

    void setConsiderUserOrders(int launchSettingsId, boolean considerUserOrders);
}
