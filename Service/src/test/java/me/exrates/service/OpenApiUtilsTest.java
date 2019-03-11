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

    private String EDC_BTC = "EDC/BTC";
    private String EDC_BTC_LOWER = "edc_btc";
    private String EDC_BTC_HIGHER = "EDC_BTC";

    private String BAD_CURRENCY_PAIR = "QQQQQQQQQ/BTC";


    @Test
    public void transformCurrencyPair_success() {
        String pair = OpenApiUtils.transformCurrencyPair(Q_BTC_LOWER);

        assertEquals(Q_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(Q_BTC_HIGHER);

        assertEquals(Q_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(HT_BTC_LOWER);

        assertEquals(HT_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(HT_BTC_LOWER);

        assertEquals(HT_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(EDC_BTC_LOWER);

        assertEquals(EDC_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(EDC_BTC_LOWER);

        assertEquals(EDC_BTC, pair);
    }

    @Test(expected = InvalidCurrencyPairFormatException.class)
    public void transformCurrencyPair_fail() {
        OpenApiUtils.transformCurrencyPair(BAD_CURRENCY_PAIR);
    }
}
