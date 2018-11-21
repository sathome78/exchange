package me.exrates.ngcontroller.service;

import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;
import me.exrates.ngcontroller.mobel.OrderBookWrapperDto;
import me.exrates.ngcontroller.mobel.ResponseInfoCurrencyPairDto;
import me.exrates.ngcontroller.mobel.ResponseUserBalances;

import java.util.List;
import java.util.Map;

public interface NgOrderService {
    OrderCreateDto prepareOrder(InputCreateOrderDto inputOrder);

    boolean processUpdateOrder(User user, InputCreateOrderDto inputOrder);

    boolean processUpdateStopOrder(User user, InputCreateOrderDto inputOrder);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommision(String email, OperationType operationType,
                                                                   int activeCurrencyPair);

    ResponseInfoCurrencyPairDto getCurrencyPairInfo(int currencyPairId);

    ResponseUserBalances getBalanceByCurrencyPairId(int currencyPairId, User user);

    String createOrder(InputCreateOrderDto inputOrder);

    Map<String, Object> filterDataPeriod(List<CandleDto> data, long fromSeconds, long toSeconds, String resolution);

    OrderBookWrapperDto findAllOrderBookItems(OrderType orderType, Integer currencyId, int precision);

    List<CurrencyPair> getAllPairsByFirstPartName(String pathName);

    List<CurrencyPair> getAllPairsBySecondPartName(String pathName);
}
