package me.exrates.chart;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.CandleDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@NoArgsConstructor(access = AccessLevel.NONE)
public final class CandleDataConverter {

    public static Map<String, Object> convert(List<CandleDto> data) {
        List<Long> t = new ArrayList<>();
        List<BigDecimal> o = new ArrayList<>();
        List<BigDecimal> c = new ArrayList<>();
        List<BigDecimal> h = new ArrayList<>();
        List<BigDecimal> l = new ArrayList<>();
        List<BigDecimal> v = new ArrayList<>();

        LocalDateTime first = data.get(0).getTime().truncatedTo(ChronoUnit.DAYS);

        t.add(first.toEpochSecond(ZoneOffset.UTC));
        o.add(BigDecimal.ZERO);
        h.add(BigDecimal.ZERO);
        l.add(BigDecimal.ZERO);
        c.add(BigDecimal.ZERO);
        v.add(BigDecimal.ZERO);

        data.forEach(candle -> {
            t.add(candle.getTime().toEpochSecond(ZoneOffset.UTC));
            o.add(candle.getOpen());
            h.add(candle.getHigh());
            l.add(candle.getLow());
            c.add(candle.getClose());
            v.add(candle.getVolume());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("t", t);
        response.put("o", o);
        response.put("h", h);
        response.put("l", l);
        response.put("c", c);
        response.put("v", v);

        return response;
    }
}