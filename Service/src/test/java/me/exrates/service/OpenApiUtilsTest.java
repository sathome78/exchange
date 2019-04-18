package me.exrates.service;

import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.service.util.OpenApiUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        pair = OpenApiUtils.transformCurrencyPair(EDC_BTC_LOWER);

        assertEquals(EDC_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(EDC_BTC_HIGHER);

        assertEquals(EDC_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(DIM_USD_BTC_LOWER);

        assertEquals(DIM_USD_BTC, pair);

        pair = OpenApiUtils.transformCurrencyPair(DIM_USD_BTC_HIGHER);

        assertEquals(DIM_USD_BTC, pair);
    }

    @Test
    public void transformCurrencyPair_fail() {
        try {
            OpenApiUtils.transformCurrencyPair(BAD_CURRENCY_PAIR);
            fail();
        } catch (OpenApiException e) {
            assertTrue(e.getMessage().startsWith("Failed to parse currency pair name"));
            assertEquals(ErrorApiTitles.API_WRONG_CURRENCY_PAIR_PATTERN, e.getTitle());
        }
    }
}
