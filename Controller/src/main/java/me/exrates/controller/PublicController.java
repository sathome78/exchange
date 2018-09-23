package me.exrates.controller;

import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CoinmarketApiJsonDto;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PublicController {
    private static final Logger LOGGER = LogManager.getLogger(PublicController.class);

    @Autowired
    private
    OrderService orderService;
    @Autowired
    private UserService userService;

    @Autowired
    private IpBlockingService ipBlockingService;

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
            Map<String, CoinmarketApiJsonDto> result = getData(currencyPair);
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
        List<CoinmarketApiDto> list = orderService.getDailyCoinmarketData(currencyPair);
        return list.stream().collect(Collectors.toMap(dto -> dto.getCurrency_pair_name().replace('/', '_'), CoinmarketApiJsonDto::new));
    }


}
