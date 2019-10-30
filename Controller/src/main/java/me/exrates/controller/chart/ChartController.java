package me.exrates.controller.chart;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.dto.CandleDto;
import me.exrates.properties.chart.ChartProperty;
import me.exrates.properties.chart.SymbolInfoProperty;
import me.exrates.security.annotation.OnlineMethod;
import me.exrates.service.CurrencyService;
import me.exrates.service.chart.CandleDataConverter;
import me.exrates.service.chart.CandleDataProcessingService;
import me.exrates.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Log4j2
@RestController
@RequestMapping("/dashboard")
public class ChartController {

    private CurrencyService currencyService;
    private final CandleDataProcessingService processingService;

    @Autowired
    public ChartController(CurrencyService currencyService,
                           CandleDataProcessingService processingService) {
        this.currencyService = currencyService;
        this.processingService = processingService;
    }

    @OnlineMethod
    @GetMapping("/history")
    public ResponseEntity getCandleChartHistoryData(@QueryParam("symbol") String symbol,
                                                    @QueryParam("from") Long from,
                                                    @QueryParam("to") Long to,
                                                    @QueryParam("resolution") String resolution) {
        Map<String, Object> response = new HashMap<>();

        try {
            currencyService.getCurrencyPairByName(symbol);
        } catch (CurrencyPairNotFoundException ex) {
            response.put("s", "error");
            response.put("errmsg", "did not find currency pair");

            return ResponseEntity.badRequest().body(response);
        }

        List<CandleDto> result = processingService.getData(symbol, from, to, resolution);

        if (CollectionUtil.isEmpty(result)) {
            LocalDateTime nextTime = processingService.getLastCandleTimeBeforeDate(symbol, from, resolution);

            response.put("s", "no_data");

            if (nonNull(nextTime)) {
                response.put("nextTime", nextTime);
            }

            return new ResponseEntity(response, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(CandleDataConverter.convert(result));
    }

    @OnlineMethod
    @GetMapping("/config")
    public ResponseEntity<String> getChartConfig() {
        return ResponseEntity.ok(ChartProperty.get());
    }

    @OnlineMethod
    @GetMapping("/symbols")
    public ResponseEntity<String> getChartSymbol(@QueryParam("symbol") String symbol) {
        return ResponseEntity.ok(SymbolInfoProperty.get(symbol));
    }

    @OnlineMethod
    @GetMapping("/time")
    public ResponseEntity<Long> getChartTime() {
        return ResponseEntity.ok(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }
}