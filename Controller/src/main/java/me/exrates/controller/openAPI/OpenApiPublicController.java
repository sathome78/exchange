package me.exrates.controller.openAPI;

import me.exrates.controller.model.BaseResponse;
import me.exrates.model.CurrencyPair;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.dto.openAPI.TickerJsonDto;
import me.exrates.model.dto.openAPI.TradeHistoryDto;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.chart.CandleDataProcessingService;
import me.exrates.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/openapi/v1/public")
public class OpenApiPublicController {

    private final OrderService orderService;
    private final CurrencyService currencyService;
    private final ExchangeApi exchangeApi;
    private final CandleDataProcessingService candleDataProcessingService;

    @Autowired
    public OpenApiPublicController(OrderService orderService,
                                   CurrencyService currencyService,
                                   ExchangeApi exchangeApi,
                                   CandleDataProcessingService candleDataProcessingService) {
        this.orderService = orderService;
        this.currencyService = currencyService;
        this.exchangeApi = exchangeApi;
        this.candleDataProcessingService = candleDataProcessingService;
    }

    @GetMapping("/ticker")
    public List<TickerJsonDto> getDailyTicker(@RequestParam(value = "currency_pair", required = false) String currencyPair) {
        String currencyPairName = null;
        if (Objects.nonNull(currencyPair)) {
            currencyPairName = transformCurrencyPair(currencyPair);
            validateCurrencyPair(currencyPairName);
        }
        return formatCoinmarketData(orderService.getDailyCoinmarketData(currencyPairName));
    }

    @GetMapping("/rates")
    public ResponseEntity<Map<String, RateDto>> getCurrencyRates() {
        return ResponseEntity.ok(exchangeApi.getRates());
    }

    @RequestMapping("/orderbook/{currency_pair}")
    public Map<OrderType, List<OrderBookItem>> getOrderBook(@PathVariable(value = "currency_pair") String currencyPair,
                                                            @RequestParam(value = "order_type", required = false) OrderType orderType) {
        String currencyPairName = transformCurrencyPair(currencyPair);
        return orderService.getOrderBook(currencyPairName, orderType);
    }

    @GetMapping(value = "/history/{currency_pair}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TradeHistoryDto>>> getTradeHistory(@PathVariable(value = "currency_pair") String currencyPair,
                                                                               @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                               @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                               @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                               @RequestParam(required = false, defaultValue = "ASC") String direction) {
        if (fromDate.isAfter(toDate)) {
            throw new OpenApiException(ErrorApiTitles.API_REQUEST_ERROR_DATES, "From date is after to date");
        }
        if (nonNull(limit) && limit <= 0) {
            throw new OpenApiException(ErrorApiTitles.API_REQUEST_ERROR_LIMIT, "Limit value equals or less than zero");
        }

        final String transformedCurrencyPair = transformCurrencyPair(currencyPair);

        return ResponseEntity.ok(BaseResponse.success(orderService.getTradeHistory(transformedCurrencyPair, fromDate, toDate, limit, direction)));
    }

    @RequestMapping("/currency_pairs")
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return currencyService.findActiveCurrencyPairs();
    }


    @GetMapping(value = "/{currency_pair}/candle_chart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<CandleDto>>> getCandleChartData(@PathVariable(value = "currency_pair") String pairName,
                                                                            @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                            @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                            @RequestParam(value = "interval_type") IntervalType intervalType,
                                                                            @RequestParam(value = "interval_value") Integer intervalValue) {
        final CurrencyPair currencyPair = currencyService.getCurrencyPairByName(transformCurrencyPair(pairName));
        final BackDealInterval interval = new BackDealInterval(intervalValue, intervalType);

        List<CandleDto> dataForCandleChart = candleDataProcessingService.getData(
                currencyPair.getName(),
                fromDate.atTime(LocalTime.MIN),
                toDate.minusDays(1).atTime(LocalTime.MAX),
                interval);

        return ResponseEntity.ok(BaseResponse.success(dataForCandleChart));
    }

    private void validateCurrencyPair(String currencyPairName) {
        currencyService.findCurrencyPairIdByName(currencyPairName);
    }

    private List<TickerJsonDto> formatCoinmarketData(List<CoinmarketApiDto> data) {
        return CollectionUtil.isNotEmpty(data)
                ? data
                .stream()
                .map(TickerJsonDto::new)
                .collect(toList())
                : Collections.emptyList();
    }
}