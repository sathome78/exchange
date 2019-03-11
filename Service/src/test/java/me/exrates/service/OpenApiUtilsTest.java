package me.exrates.service;

import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.service.util.OpenApiUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OpenApiUtilsTest {

    private String Q_BTC = "Q/BTC";
    private String Q_BTC_LOWER = "q_btc";
    private String Q_BTC_HIGHER = "Q_BTC";

    private String HT_BTC = "HT/BTC";
    private String HT_BTC_LOWER = "ht_btc";
    private String HT_BTC_HIGHER = "HT_BTC";

    private String EDR_BTC = "EDR/BTC";
    private String EDR_BTC_LOWER = "edr_btc";
    private String EDR_BTC_HIGHER = "EDR_BTC";

    private String DIM_USD_BTC = "DIM.USD/BTC";
    private String DIM_USD_BTC_LOWER = "dim.usd_btc";
    private String DIM_USD_BTC_HIGHER = "DIM.USD_BTC";

    private String BAD_CURRENCY_PAIR = "QQQQQQQQQ/BTC";


    @Test
    public void transformCurrencyPair_success() {
        String pair = OpenApiUtils.transformCurrencyPair(Q_BTC_LOWER);

        assertEquals(Q_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(Q_BTC_HIGHER);

        assertEquals(Q_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(HT_BTC_LOWER);

        assertEquals(HT_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(HT_BTC_HIGHER);

        assertEquals(HT_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(EDR_BTC_LOWER);

        assertEquals(EDR_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(EDR_BTC_HIGHER);

        assertEquals(EDR_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(DIM_USD_BTC_LOWER);

        assertEquals(DIM_USD_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(DIM_USD_BTC_HIGHER);

        assertEquals(DIM_USD_BTC, pair);
    }

    @Test(expected = InvalidCurrencyPairFormatException.class)
    public void transformCurrencyPair_fail() {
        OpenApiUtils.transformCurrencyPair(BAD_CURRENCY_PAIR);
    }
}
