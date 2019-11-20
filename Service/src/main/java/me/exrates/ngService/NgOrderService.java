package me.exrates.ngService;

import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;

import java.util.List;
import java.util.Map;

public interface NgOrderService {

    OrderCreateDto prepareOrder(InputCreateOrderDto inputOrder);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommision(String email, OperationType operationType,
                                                                   int activeCurrencyPair);

    ResponseInfoCurrencyPairDto getCurrencyPairInfo(int currencyPairId);

    Map<String, Map<String, String>> getBalanceByCurrencyPairId(int currencyPairId, User user)
            throws CurrencyPairNotFoundException;

    String createOrder(InputCreateOrderDto inputOrder);

    List<CurrencyPair> getAllPairsByFirstPartName(String pathName);

    List<CurrencyPair> getAllPairsBySecondPartName(String pathName);
}
