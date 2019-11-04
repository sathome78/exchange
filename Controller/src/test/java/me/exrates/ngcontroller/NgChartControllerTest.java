package me.exrates.ngcontroller;

import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CandleDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.chart.CandleDataProcessingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgChartControllerTest extends AngularApiCommonTest {

    private final static String BASE_URL = "/api/public/v2/graph";

    @Mock
    private CurrencyService currencyService;
    @Mock
    private CandleDataProcessingService candleDataProcessingService;

    @InjectMocks
    private NgChartController ngChartController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngChartController)
                .build();
    }

    @Test
    public void getCandleChartHistoryData_WhenOk() throws Exception {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(new CurrencyPair());
        when(candleDataProcessingService.getData(anyString(), any(Long.class), any(Long.class), anyString()))
                .thenReturn(Collections.singletonList(CandleDto.builder()
                        .open(BigDecimal.TEN)
                        .close(BigDecimal.TEN)
                        .high(BigDecimal.TEN)
                        .low(BigDecimal.TEN)
                        .volume(BigDecimal.TEN)
                        .time(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                        .build()));

        mockMvc.perform(get(BASE_URL + "/history")
                .param("symbol", "btc")
                .param("to", "1563840000")
                .param("from", "1563835000")
                .param("resolution", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.s", is("ok")))
                .andExpect(jsonPath("$.o.*", hasSize(1)))
                .andExpect(jsonPath("$.c.*", hasSize(1)))
                .andExpect(jsonPath("$.h.*", hasSize(1)))
                .andExpect(jsonPath("$.l.*", hasSize(1)))
                .andExpect(jsonPath("$.v.*", hasSize(1)))
                .andExpect(jsonPath("$.t.*", hasSize(1)))
                .andExpect(jsonPath("$.*", hasSize(7)));

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
        verify(candleDataProcessingService, times(1)).getData(anyString(), any(Long.class), any(Long.class), anyString());
    }

    @Test
    public void getCandleChartHistoryData_WhenNotFoundAndExistPreviousCandle() throws Exception {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(new CurrencyPair());
        when(candleDataProcessingService.getData(anyString(), any(Long.class), any(Long.class), anyString()))
                .thenReturn(Collections.emptyList());
        when(candleDataProcessingService.getLastCandleTimeBeforeDate(anyString(), any(Long.class), anyString()))
                .thenReturn(LocalDateTime.now());

        mockMvc.perform(get(BASE_URL + "/history")
                .param("symbol", "btc")
                .param("to", "1563840000")
                .param("from", "1563835000")
                .param("resolution", "30"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.s", is("no_data")))
                .andExpect(jsonPath("$.nextTime", notNullValue(LocalDateTime.class)))
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
        verify(candleDataProcessingService, times(1)).getData(anyString(), any(Long.class), any(Long.class), anyString());
        verify(candleDataProcessingService, times(1)).getLastCandleTimeBeforeDate(anyString(), any(Long.class), anyString());
    }

    @Test
    public void getCandleChartHistoryData_WhenNotFoundAndNotExistPreviousCandle() throws Exception {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(new CurrencyPair());
        when(candleDataProcessingService.getData(anyString(), any(Long.class), any(Long.class), anyString()))
                .thenReturn(Collections.emptyList());
        when(candleDataProcessingService.getLastCandleTimeBeforeDate(anyString(), any(Long.class), anyString()))
                .thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/history")
                .param("symbol", "btc")
                .param("to", "1563840000")
                .param("from", "1563835000")
                .param("resolution", "30"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.s", is("no_data")))
                .andExpect(jsonPath("$.*", hasSize(1)));

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
        verify(candleDataProcessingService, times(1)).getData(anyString(), any(Long.class), any(Long.class), anyString());
        verify(candleDataProcessingService, times(1)).getLastCandleTimeBeforeDate(anyString(), any(Long.class), anyString());
    }

    @Test
    public void getCandleChartHistoryData_WhenCurrencyNotFound() throws Exception {
        when(currencyService.getCurrencyPairByName(anyString())).thenThrow(CurrencyPairNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/history")
                .param("symbol", "btc")
                .param("to", "1563840000")
                .param("from", "1563835000")
                .param("resolution", "30"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.s", is("error")))
                .andExpect(jsonPath("$.errmsg", is("did not find currency pair")))
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
        verify(candleDataProcessingService, never()).getData(anyString(), any(Long.class), any(Long.class), anyString());
    }

    @Test
    public void getChartConfig() throws Exception {
        mockMvc.perform(get(BASE_URL + "/config"))
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$.supports_search", is(true)))
                .andExpect(jsonPath("$.supports_group_request", is(false)))
                .andExpect(jsonPath("$.supports_marks", is(false)))
                .andExpect(jsonPath("$.supports_time", is(true)))
                .andExpect(jsonPath("$.exchanges", hasSize(2)))
                .andExpect(jsonPath("$.exchanges[0].value", is("")))
                .andExpect(jsonPath("$.exchanges[0].name", is("All Exchanges")))
                .andExpect(jsonPath("$.exchanges[0].desc", is("")))
                .andExpect(jsonPath("$.exchanges[1].value", is("EXRATES")))
                .andExpect(jsonPath("$.exchanges[1].name", is("EXRATES")))
                .andExpect(jsonPath("$.exchanges[1].desc", is("EXRATES")))
                .andExpect(jsonPath("$.symbols_types", hasSize(1)))
                .andExpect(jsonPath("$.symbols_types[0].name", is("All types")))
                .andExpect(jsonPath("$.symbols_types[0].value", is("")))
//                .andExpect(jsonPath("$.supported_resolutions", hasSize(6)))
//                .andExpect(jsonPath("$.supported_resolutions.[0]", is("5")))
//                .andExpect(jsonPath("$.supported_resolutions.[5]", is("D")))
                .andExpect(status().isOk());
    }

    @Test
    public void getChartSymbol() throws Exception {
        mockMvc.perform(get(BASE_URL + "/symbols")
                .param("symbol", "symbols"))
                .andExpect(jsonPath("$.*", hasSize(22)))
                .andExpect(jsonPath("$.name", is("symbols")))
                .andExpect(jsonPath("$.base_name", hasSize(1)))
                .andExpect(jsonPath("$.base_name.[0]", is("symbols")))
                .andExpect(jsonPath("$.full_name", is("symbols")))
                .andExpect(jsonPath("$.minmov", is(1)))
                .andExpect(jsonPath("$.fractional", is(false)))
                .andExpect(jsonPath("$.type", is("bitcoin")))
                .andExpect(jsonPath("$.ticker", is("symbols")))
                .andExpect(jsonPath("$.supported_resolutions", hasSize(6)))
                .andExpect(jsonPath("$.supported_resolutions.[0]", is("5")))
                .andExpect(jsonPath("$.has_empty_bars", is(true)))
                .andExpect(jsonPath("$.volume_precision", is(2)))
                .andExpect(status().isOk());
    }

    @Test
    public void getChartTime() throws Exception {
        mockMvc.perform(get(BASE_URL + "/time"))
                .andExpect(status().isOk());
    }
}
