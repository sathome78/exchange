package me.exrates.model.ngExceptions;

import static me.exrates.model.constants.ErrorApiTitles.MARKET_ORDER_ACCEPTING_FAILED;

public class MarketOrderAcceptionException extends NgResponseException {

    public MarketOrderAcceptionException(String message) {
        super(MARKET_ORDER_ACCEPTING_FAILED, message);
    }
}
