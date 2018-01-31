package me.exrates.controller;

import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CoinmarketApiJsonDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.MerchantService;
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

/**
 * Created by Valk on 11.05.2016.
 */

@RestController
public class PublicController {
    private static final Logger LOGGER = LogManager.getLogger(PublicController.class);

    @Autowired
    OrderService orderService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/public/coinmarketcap/ticker", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, CoinmarketApiJsonDto> tiker(@RequestParam(required = false) String currencyPair, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        List<CoinmarketApiDto> list = null;
        String ip = "";
        try {
            if (currencyPair != null) {
                currencyPair = currencyPair.replace('_', '/');
            }
            ip = request.getHeader("X-FORWARDED-FOR");
            if (ip == null) {
                ip = request.getRemoteHost();
            }
            list = orderService.getCoinmarketDataForActivePairs(currencyPair, new BackDealInterval("24 HOUR"));
            Map<String, CoinmarketApiJsonDto> result = list.stream().collect(Collectors.toMap(dto -> dto.getCurrency_pair_name().replace('/','_'), CoinmarketApiJsonDto::new));
            long after = System.currentTimeMillis();
            LOGGER.debug(String.format("completed... from ip: %s ms: %s", ip, (after - before)));
            return result;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error(String.format("error... for pair: %s from ip: %s ms: %s : %s", currencyPair, ip, (after - before), e));
            throw e;
        }
    }

    @RequestMapping(value = "/public/if_email_exists", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> checkIfNewUserEmailUnique(@RequestParam("email") String email, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            List<String> errors = new ArrayList<>();
            if (!userService.ifEmailIsUnique(email)) {
                errors.add("Email exists");
            }
            long after = System.currentTimeMillis();
            LOGGER.debug(String.format("completed... : ms: %d", (after - before)));
            return errors;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error(String.format("error... for email: %s ms: %d : %s", email, (after - before), e.getMessage()));
            throw e;
        }
    }

    @RequestMapping(value = "/public/if_username_exists", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> checkIfNewUserUsernameUnique(@RequestParam("username") String username, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            List<String> errors = new ArrayList<>();
            if (!userService.ifNicknameIsUnique(username)) {
                errors.add("Username exists");
            }
            long after = System.currentTimeMillis();
            LOGGER.debug(String.format("completed...: ms: %s", (after - before)));
            return errors;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error(String.format("error... for username: %s ms: %s : %s", username, (after - before), e.getMessage()));
            throw e;
        }
    }
}
