package me.exrates.model.enums;

import java.util.stream.Stream;

public enum Market {

    USD, BTC, ETH, FIAT, USDT, ICO/*IEO*/, UNDEFINED;

    public static Market of(String type) {
        return Stream.of(Market.values())
                .filter(market -> market.name().equals(type.toUpperCase()))
                .findFirst()
                .orElse(Market.UNDEFINED);
    }
}