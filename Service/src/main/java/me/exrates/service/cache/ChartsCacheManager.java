package me.exrates.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.service.OrderService;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2(topic = "cache")
@Component
public class ChartsCacheManager {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private StompMessenger stompMessenger;
    @Autowired
    private ObjectMapper objectMapper;



    private Map<Integer, Map<String, ChartCacheUnit>> cacheMap = new ConcurrentHashMap<>();

    @Async
    public void onUpdateEvent(int pairId) {
        List<ChartTimeFrame> allIntervals = orderService.getChartTimeFrames();
        allIntervals.forEach(p -> setNeedUpdate(pairId, p));
    }


    private void setNeedUpdate(Integer pairId, ChartTimeFrame timeFrame) {
        ChartsCacheInterface cacheUnit = getRequiredCache(pairId, timeFrame);
        cacheUnit.setNeedToUpdate();
    }

    public List<CandleChartItemDto> getData(Integer pairId, ChartTimeFrame timeFrame) {

        return getData(pairId, timeFrame, false);
    }

    public List<CandleChartItemDto> getData(Integer pairId, ChartTimeFrame timeFrame, boolean lastOnly) {
        ChartsCacheInterface cacheUnit = getRequiredCache(pairId, timeFrame);
        return lastOnly ? cacheUnit.getLastData() : cacheUnit.getData();
    }

    private ChartsCacheInterface getRequiredCache(Integer pairId, ChartTimeFrame timeFrame) {
        return cacheMap.computeIfAbsent(pairId, p -> {
            Map<String, ChartCacheUnit> map = new ConcurrentHashMap<>();
            orderService.getChartTimeFrames().forEach(i->{
                map.put(i.getResolution().toString(), new ChartCacheUnit(pairId,
                        i,
                        orderService,
                        eventPublisher)
                );
            });
            return map;
        }).get(timeFrame.getResolution().toString());
    }
}
