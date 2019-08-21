package me.exrates.service.impl;

import me.exrates.service.AdgroupServiceImpl;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.http.AdGroupHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdGroupServiceImplTest {

    @Mock
    AdGroupHttpClient adGroupHttpClient;

    @Mock
    private RefillService refillService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private CurrencyService currencyService;



    @Test
    public void testRefill() {
        when(merchantService.findById(anyInt())).thenReturn(anyObject());
    }
}
