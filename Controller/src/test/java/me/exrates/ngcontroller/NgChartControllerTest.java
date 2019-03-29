package me.exrates.ngcontroller;

import me.exrates.model.CurrencyPair;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.enums.ChartTimeFramesEnum;
import me.exrates.ngService.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyString;
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
    private NgOrderService ngOrderService;
    @Mock
    private OrderService orderService;

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
        String resolution = "30";
        String rsolutionForChartTime = (resolution.equals("W") || resolution.equals("M")) ? "D" : resolution;
        CurrencyPair currencyPair = new CurrencyPair();

        List<CandleDto> result = orderService.getCachedDataForCandle(currencyPair,
                ChartTimeFramesEnum.ofResolution(rsolutionForChartTime).getTimeFrame())
                .stream()
                .map(CandleDto::new)
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("symbol", "symbol");
        map.put("from", 5);

        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(currencyPair);
        when(orderService.getCachedDataForCandle(any(CurrencyPair.class), any(ChartTimeFrame.class))
                .stream()
                .map(CandleDto::new)
                .collect(Collectors.toList())).thenReturn(result);
        when(ngOrderService.filterDataPeriod(anyList(), anyLong(), anyLong(), anyString())).thenReturn(map);

        mockMvc.perform(get(BASE_URL + "/history")
                .param("symbol", "btc")
                .param("to", "2")
                .param("from", "3")
                .param("resolution", "30")
                .param("countback", "countback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol", is("symbol")))
                .andExpect(jsonPath("$.from", is(5)))
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
        verify(orderService, times(2)).getCachedDataForCandle(any(CurrencyPair.class), any(ChartTimeFrame.class));
        verify(ngOrderService, times(1)).filterDataPeriod(anyList(), anyLong(), anyLong(), anyString());
    }

    @Test
    public void getCandleChartHistoryData_WhenNotFound() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", "symbol");
        map.put("from", 5);

        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(null);
        when(ngOrderService.filterDataPeriod(anyList(), anyLong(), anyLong(), anyString())).thenReturn(map);

        mockMvc.perform(get(BASE_URL + "/history")
                .param("symbol", "freg")
                .param("to", "2")
                .param("from", "3")
                .param("resolution", "deq")
                .param("countback", "eqded"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.s", is("error")))
                .andExpect(jsonPath("$.errmsg", is("can not find currencyPair")))
                .andExpect(jsonPath("$.*", hasSize(4)));

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
        verify(ngOrderService, times(1)).filterDataPeriod(anyList(), anyLong(), anyLong(), anyString());
    }

    @Test
    public void getCandleTimeScaleMarks_WhenNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/timescale_marks")
                .param("symbol", "symbol")
                .param("to", "2")
                .param("from", "3")
                .param("resolution", "W")
                .param("countback", "eqded"))
                .andExpect(jsonPath("$.s", is("error")))
                .andExpect(jsonPath("$.errmsg", is("can not find currencyPair")))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCandleTimeScaleMarks_WhenOk() throws Exception {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(new CurrencyPair());

        mockMvc.perform(get(BASE_URL + "/timescale_marks")
                .param("symbol", "symbol")
                .param("to", "2")
                .param("from", "3")
                .param("resolution", "W")
                .param("countback", "eqded"))
                .andExpect(status().isOk());

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
    }

    @Test
    public void getChartConfig() throws Exception {
        mockMvc.perform(get(BASE_URL + "/config"))
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$.supports_search", is(true)))
                .andExpect(jsonPath("$.supports_group_request", is(false)))
                .andExpect(jsonPath("$.supports_marks", is(false)))
                .andExpect(jsonPath("$.supports_timescale_marks", is(true)))
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
                .andExpect(jsonPath("$.supported_resolutions", hasSize(10)))
                .andExpect(jsonPath("$.supported_resolutions.[0]", is("30")))
                .andExpect(jsonPath("$.supported_resolutions.[9]", is("M")))
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
                .andExpect(jsonPath("$.supported_resolutions", hasSize(10)))
                .andExpect(jsonPath("$.supported_resolutions.[0]", is("30")))
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