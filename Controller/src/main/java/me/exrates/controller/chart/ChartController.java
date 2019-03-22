package me.exrates.controller.chart;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.enums.ChartTimeFramesEnum;
import me.exrates.security.annotation.OnlineMethod;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class ChartController {

    private OrderService orderService;

    private CurrencyService currencyService;
    private HashMap<String, Object> responseCache = new HashMap<>();

    @Autowired
    public ChartController(OrderService orderService, CurrencyService currencyService) {
        this.orderService = orderService;
        this.currencyService = currencyService;
    }

    @OnlineMethod
    @RequestMapping(value = "/dashboard/history", method = RequestMethod.GET)
    public ResponseEntity getCandleChartHistoryData(
            @QueryParam("symbol") String symbol,
            @QueryParam("to") Long to,
            @QueryParam("from") Long from,
            @QueryParam("resolution") String resolution) {

 /*       String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd ";
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(from, 0, ZoneOffset.UTC);
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(to, 0, ZoneOffset.UTC);
        ChartResolution resolution1 = ChartTimeFramesEnum.ofResolution(resolution).getTimeFrame().getResolution();
        ChartTimeFrame timeFrame = ChartTimeFramesEnum.ofResolution(resolution).getTimeFrame();
        LocalDateTime startFrameTime = endTime.minus(timeFrame.getTimeValue() + 1, timeFrame.getTimeUnit().getCorrespondingTimeUnit());

        LocalDateTime fromDate = Instant.ofEpochMilli(36000000L).atZone(ZoneId.systemDefault()).toLocalDateTime();
        String starDay = fromDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        String endDay = endTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));*/


        CurrencyPair currencyPair = currencyService.getCurrencyPairByName(symbol);
        List<CandleDto> result = new ArrayList<>();
        if (currencyPair == null) {
            HashMap<String, Object> errors = new HashMap<>();
            errors.putAll(filterDataPeriod(result, from, to, resolution));
            errors.put("s", "error");
            errors.put("errmsg", "can not find currencyPair");
            return new ResponseEntity(errors, HttpStatus.NOT_FOUND);
        }

        String rsolutionForChartTime = (resolution.equals("W") || resolution.equals("M")) ? "D" : resolution;
        result = orderService.getCachedDataForCandle(currencyPair,
                ChartTimeFramesEnum.ofResolution(rsolutionForChartTime).getTimeFrame())
                .stream().map(CandleDto::new).collect(Collectors.toList());
        return new ResponseEntity(filterDataPeriod(result, from, to, resolution), HttpStatus.OK);

    }


    @OnlineMethod
    @RequestMapping(value = "/dashboard/time", method = RequestMethod.GET)
    public ResponseEntity getChartTime() {
        return new ResponseEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), HttpStatus.OK);
    }

    @OnlineMethod
    @RequestMapping(value = "/dashboard/config", method = RequestMethod.GET)
    public ResponseEntity getChartConfig() {
        return new ResponseEntity(getConfig().toString(), HttpStatus.OK);
    }

    @OnlineMethod
    @RequestMapping(value = "/dashboard/symbols", method = RequestMethod.GET)
    public ResponseEntity getChartSymbol(@QueryParam("symbol") String symbol) {
        return new ResponseEntity(getSymbolInfo(symbol).toString(), HttpStatus.OK);
    }

 /*   @OnlineMethod
    @RequestMapping(value = "/ico_dashboard/history", method = RequestMethod.GET)
    public ResponseEntity getCandleChartHistoryData2(
            @QueryParam("symbol") String symbol,
            @QueryParam("to") Long to,
            @QueryParam("from") Long from,
            @QueryParam("resolution") String resolution) {

*//*        String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";

        LocalDateTime startTime = LocalDateTime.ofEpochSecond(from, 0, ZoneOffset.UTC);
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(to, 0, ZoneOffset.UTC);
        ChartResolution resolution1 = ChartTimeFramesEnum.ofResolution(resolution).getTimeFrame().getResolution();
        ChartTimeFrame timeFrame = ChartTimeFramesEnum.ofResolution(resolution).getTimeFrame();
        LocalDateTime startFrameTime = endTime.minus(timeFrame.getTimeValue() + 1, timeFrame.getTimeUnit().getCorrespondingTimeUnit());

        LocalDateTime fromDate = Instant.ofEpochMilli(36000000L).atZone(ZoneId.systemDefault()).toLocalDateTime();
        String starDay = fromDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        String endDay = endTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));*//*


        CurrencyPair currencyPair = currencyService.getCurrencyPairByName(symbol);
        List<CandleDto> result = new ArrayList<>();
        if (currencyPair == null) {
            HashMap<String, Object> errors = new HashMap<>();
            errors.putAll(filterDataPeriod(result, from, to, resolution));
            errors.put("s", "error");
            errors.put("errmsg", "can not find currencyPair");
            return new ResponseEntity(errors, HttpStatus.NOT_FOUND_ERROR);
        }
        String rsolutionForChartTime = (resolution.equals("W") || resolution.equals("M")) ? "D" : resolution;
        result = orderService.getCachedDataForCandle(currencyPair,
                ChartTimeFramesEnum.ofResolution(rsolutionForChartTime).getTimeFrame())
                .stream().map(CandleDto::new).collect(Collectors.toList());
        return new ResponseEntity(filterDataPeriod(result, from, to, resolution), HttpStatus.OK);

    }*/


/*    @OnlineMethod
    @RequestMapping(value = "/ico_dashboard/time", method = RequestMethod.GET)
    public ResponseEntity getChartTime2() {
        return new ResponseEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), HttpStatus.OK);
    }*/

/*
    @OnlineMethod
    @RequestMapping(value = "/ico_dashboard/config", method = RequestMethod.GET)
    public ResponseEntity getChartConfig2() {
        return new ResponseEntity(getConfig().toString(), HttpStatus.OK);
    }
*/

/*    @OnlineMethod
    @RequestMapping(value = "/ico_dashboard/symbols", method = RequestMethod.GET)
    public ResponseEntity getChartSymbol2(@QueryParam("symbol") String symbol) {
        return new ResponseEntity(getSymbolInfo(symbol).toString(), HttpStatus.OK);
    }*/

    private JsonObject getSymbolInfo(@QueryParam("symbol") String symbol) {

        return Json.createObjectBuilder()
                .add("name", symbol)
                .add("base_name", Json.createArrayBuilder().add(symbol))
                .add("description", "description")
                .add("full_name", symbol)
                .add("has_seconds", false)
                .add("has_intraday", true)
                .add("has_no_volume", false)
                .add("listed_exchange", "EXRATES")
                .add("exchange", "EXRATES")
                .add("minmov", 1)
                .add("fractional", false)
                .add("pricescale", 1000000000)
                .add("type", "bitcoin")
                .add("session", "24x7")
                .add("ticker", symbol)
                .add("timezone", "UTC")
                .add("supported_resolutions", Json.createArrayBuilder()
                         .add("30") .add("60") .add("240").add("720").add("D").add("2D").add("3D").add("W").add("3W").add("M"))
                .add("force_session_rebuild", false)
                .add("has_daily", true)
                .add("has_weekly_and_monthly", true)
                .add("has_empty_bars", true)
                .add("volume_precision", 2)
                .build();
    }

    /**
     * Returns configurations for chart graphic
     *
     * @return
     */
    private JsonObject getConfig() {

        return Json.createObjectBuilder()
                .add("supports_search", true)
                .add("supports_group_request", false)
                .add("supports_marks", false)
                .add("supports_timescale_marks", true)
                .add("supports_time", true)
                .add("exchanges", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("value", "")
                                .add("name", "All Exchanges")
                                .add("desc", ""))
                        .add(Json.createObjectBuilder()
                                .add("value", "EXRATES")
                                .add("name", "EXRATES")
                                .add("desc", "EXRATES")))
                .add("symbols_types", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("name", "All types")
                                .add("value", "")))
                .add("supported_resolutions", Json.createArrayBuilder()
                       .add("30") .add("60") .add("240").add("720").add("D").add("2D").add("3D").add("W").add("3W").add("M"))
                .build();
    }

    private Map<String, Object> filterDataPeriod(List<CandleDto> data, long fromSeconds, long toSeconds, String resolution) {
        List<CandleDto> filteredData = new ArrayList<>(data);
        HashMap<String, Object> filterDataResponse = new HashMap<>();
        if (filteredData.isEmpty()) {
            filterDataResponse.put("s", "ok");
            getData(filterDataResponse, filteredData, resolution);
            return filterDataResponse;
        }

        if ((filteredData.get(data.size() - 1).getTime() / 1000) < fromSeconds) {
            filterDataResponse.put("s", "no_data");
            filterDataResponse.put("nextTime", filteredData.get(data.size() - 1).getTime() / 1000);
            return filterDataResponse;

        }

        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < filteredData.size(); i++) {
            long time = filteredData.get(i).getTime() / 1000;
            if (fromIndex == -1 && time >= fromSeconds) {
                fromIndex = i;
            }
            if (toIndex == -1 && time >= toSeconds) {
                toIndex = time > toSeconds ? i - 1 : i;
            }
            if (fromIndex != -1 && toIndex != -1) {
                break;
            }
        }

        fromIndex = fromIndex > 0 ? fromIndex : 0;
        toIndex = toIndex > 0 ? toIndex + 1 : filteredData.size();


        toIndex = Math.min(fromIndex + 1000, toIndex); // do not send more than 1000 bars for server capacity reasons

        String s = "ok";

        if (toSeconds < filteredData.get(0).getTime() / 1000) {
            s = "no_data";
        }
        filterDataResponse.put("s", s);

        toIndex = Math.min(fromIndex + 1000, toIndex);

        if (fromIndex > toIndex) {
            filterDataResponse.put("s", "no_data");
            filterDataResponse.put("nextTime", filteredData.get(data.size() - 1).getTime() / 1000);
            return filterDataResponse;
        }

        filteredData = filteredData.subList(fromIndex, toIndex);
        getData(filterDataResponse, filteredData, resolution);
        return filterDataResponse;

    }

    private void getData(HashMap<String, Object> response, List<CandleDto> result, String resolution) {
        List<Long> t = new ArrayList<>();
        List<BigDecimal> o = new ArrayList<>();
        List<BigDecimal> h = new ArrayList<>();
        List<BigDecimal> l = new ArrayList<>();
        List<BigDecimal> c = new ArrayList<>();
        List<BigDecimal> v = new ArrayList<>();

        LocalDateTime first = LocalDateTime.ofEpochSecond((result.get(0).getTime()/1000), 0, ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.DAYS);
        t.add(first.toEpochSecond(ZoneOffset.UTC));
        o.add(BigDecimal.ZERO);
        h.add(BigDecimal.ZERO);
        l.add(BigDecimal.ZERO);
        c.add(BigDecimal.ZERO);
        v.add(BigDecimal.ZERO);


        for (CandleDto r : result) {
            LocalDateTime now =  LocalDateTime.ofEpochSecond((r.getTime()/1000), 0, ZoneOffset.UTC)
                                                                         .truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime actualDateTime;
            long currentMinutesOfHour = now.getLong(ChronoField.MINUTE_OF_HOUR);
            long currentHourOfDay = now.getLong(ChronoField.HOUR_OF_DAY);

            switch (resolution){
                case "30":
                    long minutes =  Math.abs(currentMinutesOfHour - 30);
                    actualDateTime = now.minusMinutes(currentMinutesOfHour <= 30 ? currentMinutesOfHour : minutes);
                    break;
                case "60":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour);
                    break;
                case "240":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour).minusHours(currentHourOfDay % 4);
                    break;
                case "720":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour).minusHours(currentHourOfDay % 12);
                    break;
                case "M":
                    actualDateTime = now.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
                    break;
                default:
                    actualDateTime = now.minusMinutes(currentMinutesOfHour);
                
            }

            t.add(actualDateTime.toEpochSecond(ZoneOffset.UTC));
            o.add(r.getOpen());
            h.add(r.getHigh());
            l.add(r.getLow());
            c.add(r.getClose());
            v.add(r.getVolume());
        }
        response.put("t", t);
        response.put("o", o);
        response.put("h", h);
        response.put("l", l);
        response.put("c", c);
        response.put("v", v);
    }
}
