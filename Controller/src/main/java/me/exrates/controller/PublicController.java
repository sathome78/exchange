package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CoinmarketApiJsonDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.MerchantService;
import me.exrates.service.OrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by Valk on 11.05.2016.
 */
@RestController
public class PublicController {
    private static final Logger LOGGER = LogManager.getLogger(PublicController.class);

    @Autowired
    private
    OrderService orderService;

    private ConcurrentMap<String, CoinmarketApiJsonDto> cachedData = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(()-> {
                Map<String, CoinmarketApiJsonDto> newData = getData(null);
                cachedData = new ConcurrentHashMap<>(newData);
        }, 0, 30, TimeUnit.MINUTES);
    }

    @RequestMapping(value = "/public/coinmarketcap/ticker", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, CoinmarketApiJsonDto> tiker(@RequestParam(required = false) String currencyPair, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        String ip = "";
        try {
            if (currencyPair != null) {
                currencyPair = currencyPair.replace('_', '/');
            }
            ip = request.getHeader("X-FORWARDED-FOR");
            if (ip == null) {
                ip = request.getRemoteHost();
            }
            Map<String, CoinmarketApiJsonDto> result;
            if (StringUtils.isEmpty(currencyPair) && cachedData != null && !cachedData.isEmpty()) {
                    result = cachedData;
            } else {
                result = getData(currencyPair);
            }
            long after = System.currentTimeMillis();
            LOGGER.debug(String.format("completed... from ip: %s ms: %s", ip, (after - before)));
            return result;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error(String.format("error... for pair: %s from ip: %s ms: %s : %s", currencyPair, ip, (after - before), e));
            throw e;
        }
    }

    private Map<String, CoinmarketApiJsonDto> getData(String currencyPair) {
        List<CoinmarketApiDto> list = orderService.getCoinmarketDataForActivePairs(currencyPair, new BackDealInterval("24 HOUR"));
        return list.stream().collect(Collectors.toMap(dto -> dto.getCurrency_pair_name().replace('/', '_'), CoinmarketApiJsonDto::new));
    }


}
