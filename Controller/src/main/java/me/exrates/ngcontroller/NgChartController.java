package me.exrates.ngcontroller;

import me.exrates.chart.CandleDataConverter;
import me.exrates.chart.CandleDataProcessingService;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.enums.ChartResolution;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.properties.chart.ChartProperty;
import me.exrates.properties.chart.SymbolInfoProperty;
import me.exrates.service.CurrencyService;
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

@RestController
@RequestMapping("/api/public/v2/graph")
public class NgChartController {

    private final CurrencyService currencyService;
    private final CandleDataProcessingService processingService;

    @Autowired
    public NgChartController(CurrencyService currencyService,
                             CandleDataProcessingService processingService) {
        this.currencyService = currencyService;
        this.processingService = processingService;
    }

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

        final LocalDateTime fromDate = LocalDateTime.ofEpochSecond(from, 0, ZoneOffset.UTC);
        final LocalDateTime toDate = LocalDateTime.ofEpochSecond(to, 0, ZoneOffset.UTC);
        final BackDealInterval interval = ChartResolution.ofResolution(resolution);

        List<CandleDto> result = processingService.getData(symbol, fromDate, toDate, interval);

        if (CollectionUtil.isEmpty(result)) {
            response.put("s", "no_data");
            response.put("nextTime", toDate.minusMinutes(ChartResolution.getMinutes(resolution)));

            return new ResponseEntity(response, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(CandleDataConverter.convert(result));
    }

    @GetMapping("/timescale_marks")
    public ResponseEntity getCandleTimeScaleMarks(@QueryParam("symbol") String symbol,
                                                  @QueryParam("to") Long to,
                                                  @QueryParam("from") Long from,
                                                  @QueryParam("resolution") String resolution) {
        //todo: rewrite (piece of shit)
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/config")
    public ResponseEntity<String> getChartConfig() {
        return ResponseEntity.ok(ChartProperty.get());
    }

    @GetMapping("/symbols")
    public ResponseEntity<String> getChartSymbol(@QueryParam("symbol") String symbol) {
        return ResponseEntity.ok(SymbolInfoProperty.get(symbol));
    }

    @GetMapping("/time")
    public ResponseEntity<Long> getChartTime() {
        return ResponseEntity.ok(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }
}