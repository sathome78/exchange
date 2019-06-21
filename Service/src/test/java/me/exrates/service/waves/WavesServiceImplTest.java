package me.exrates.service.waves;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.MerchantCurrencyBasicInfoDto;
import me.exrates.model.dto.merchants.waves.WavesTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.SendMailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WavesServiceImplTest {

    @Mock
    private WavesRestClient restClient;

    @Mock
    private RefillService refillService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private SendMailService sendMailService;

    @InjectMocks
    private WavesServiceImpl wavesService = new WavesServiceImpl("WAVES", "Waves", "");

    private WavesTransaction wavesTransactionInputWaves;
    private WavesTransaction wavesTransactionInpuToken;
    private WavesTransaction wavesTransactionTransferFee;

    private final String TX_ID_WAVES = "81rSa32JBQCD2uRXD8haqT4ZNvZD3K9XeThWJ2x3EkZm";
    private final String TX_ID_TOKEN = "EhkwfmHKWnGFVgkeNi2szFJpwuh27UrVHQi32seXAPYH";
    private final String TX_ID_FEE_TRANSFER = "FLdzVkwEnBD9cjcJZYy4K1uXQLJKjhN5dwgUL5HwBbDj";

    private final String TOKEN_ID = "3wUfc4mWYtLTDgYwHiEYBJBbU75aqH8wjUnsQu8gsHin";
    private final String USER_EXCHANGE_ADDRESS = "3N4dPo5hfcgdjTHB75DwjxNva6oLBtzC9As";
    private final String FEE_ADDRESS = "3MxE9izMhKiD3ANksRpvKfzPkjQ6F7ER1R7";
    private final String MAIN_ADDRESS = "3N8DRLQKAc9arr6q5G9AagYUExXLhDJeGH3";
    private final String USER_OWN_ADDRESS = "3MwCNDMhb28QpHcYobftxJU4DMATQGF6Vs4";

    @Before
    public void setUp() {

        wavesTransactionInputWaves = new WavesTransaction();
        wavesTransactionInputWaves.setId(TX_ID_WAVES);
        wavesTransactionInputWaves.setAmount(200000000L);
        wavesTransactionInputWaves.setFee(100000L);
        wavesTransactionInputWaves.setHeight(899_990);
        wavesTransactionInputWaves.setSender(USER_OWN_ADDRESS);
        wavesTransactionInputWaves.setRecipient(USER_EXCHANGE_ADDRESS);

        wavesTransactionInpuToken = new WavesTransaction();
        wavesTransactionInpuToken.setId(TX_ID_TOKEN);
        wavesTransactionInpuToken.setAssetId(TOKEN_ID);
        wavesTransactionInpuToken.setAmount(200000L);
        wavesTransactionInpuToken.setFee(100000L);
        wavesTransactionInpuToken.setHeight(899_990);
        wavesTransactionInpuToken.setSender(USER_OWN_ADDRESS);
        wavesTransactionInpuToken.setRecipient(USER_EXCHANGE_ADDRESS);

        wavesTransactionTransferFee = new WavesTransaction();
        wavesTransactionTransferFee.setId(TX_ID_WAVES);
        wavesTransactionTransferFee.setAmount(100000L);
        wavesTransactionTransferFee.setFee(100000L);
        wavesTransactionTransferFee.setHeight(899_990);
        wavesTransactionTransferFee.setSender(FEE_ADDRESS);
        wavesTransactionTransferFee.setAttachment("inner");
        wavesTransactionTransferFee.setRecipient(USER_EXCHANGE_ADDRESS);

        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn(" ");
        when(restClient.generateNewAddress()).thenReturn("1234");
        when(restClient.getCurrentBlockHeight()).thenReturn(900_000);
        when(restClient.transferCosts(any())).thenReturn("4321");
        when(restClient.getTransactionById(TX_ID_WAVES)).thenReturn(Optional.of(wavesTransactionInputWaves));
        when(restClient.getTransactionById(TX_ID_TOKEN)).thenReturn(Optional.of(wavesTransactionInpuToken));
        when(restClient.getTransactionById(TX_ID_FEE_TRANSFER)).thenReturn(Optional.of(wavesTransactionTransferFee));
        when(restClient.getTransactionsForAddress(USER_EXCHANGE_ADDRESS)).
                thenReturn(Arrays.asList(wavesTransactionInputWaves, wavesTransactionInpuToken, wavesTransactionTransferFee));
        Merchant wavesMerchant = new Merchant();
        wavesMerchant.setName("Waves");
        wavesMerchant.setId(55);
        Currency wavesCurrency = new Currency();
        wavesCurrency.setName("WAVES");
        wavesCurrency.setId(47);
        MerchantCurrencyBasicInfoDto tokenDto = new MerchantCurrencyBasicInfoDto();
        tokenDto.setCurrencyId(60);
        tokenDto.setMerchantId(70);
        tokenDto.setCurrencyName("GX");
        tokenDto.setMerchantName("GameX");
        tokenDto.setRefillScale(2);
        tokenDto.setWithdrawScale(2);
        tokenDto.setTransferScale(2);



        when(merchantService.findByName("Waves")).thenReturn(wavesMerchant);
        when(currencyService.findByName("WAVES")).thenReturn(wavesCurrency);
        when(merchantService.findTokenMerchantsByParentId(55)).thenReturn(Collections.singletonList(tokenDto));

        when(refillService.findAllAddresses(anyInt(), anyInt())).thenReturn(Collections.singletonList(USER_EXCHANGE_ADDRESS));
        when(refillService.findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(eq(USER_EXCHANGE_ADDRESS), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());
        when(refillService.createRefillRequestByFact(any())).thenReturn(0);

        when(refillService.findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(eq(USER_EXCHANGE_ADDRESS), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());
        when(refillService.findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(eq(USER_EXCHANGE_ADDRESS), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());
        when(refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());

        wavesService.setFeeAccount(FEE_ADDRESS);
        wavesService.setMainAccount(MAIN_ADDRESS);
        wavesService.setMinConfirmations(5);
        wavesService.setNotifyEmail("");
        Properties properties = new Properties();
        properties.put("waves.token.GameX.id", TOKEN_ID);
        wavesService.initAssets(properties);

    }

    @Test
    public void testProcessWavesTransactionsForKnownAddresses() {
        wavesService.processWavesTransactionsForKnownAddresses();
        verify(refillService, never()).
                findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(eq(USER_EXCHANGE_ADDRESS), anyInt(), anyInt(), eq(TX_ID_FEE_TRANSFER));
        verify(refillService).
                findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(eq(USER_EXCHANGE_ADDRESS), anyInt(), anyInt(), eq(TX_ID_WAVES));
        verify(refillService).
                findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(eq(USER_EXCHANGE_ADDRESS), anyInt(), anyInt(), eq(TX_ID_TOKEN));
    }

}
