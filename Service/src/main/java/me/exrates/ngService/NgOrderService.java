package me.exrates.ngService;

import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;

import java.util.List;
import java.util.Map;

public interface NgOrderService {
    OrderCreateDto prepareOrder(InputCreateOrderDto inputOrder);

    boolean processUpdateOrder(User user, InputCreateOrderDto inputOrder);

    boolean processUpdateStopOrder(User user, InputCreateOrderDto inputOrder);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommision(String email, OperationType operationType,
                                                                   int activeCurrencyPair);

    ResponseInfoCurrencyPairDto getCurrencyPairInfo(int currencyPairId);

    Map<String, Map<String, String>> getBalanceByCurrencyPairId(int currencyPairId, User user)
            throws CurrencyPairNotFoundException;

    String createOrder(InputCreateOrderDto inputOrder);

    Map<String, Object> filterDataPeriod(List<CandleDto> data, long fromSeconds, long toSeconds, String resolution);

    List<CurrencyPair> getAllPairsByFirstPartName(String pathName);

    List<CurrencyPair> getAllPairsBySecondPartName(String pathName);
}
