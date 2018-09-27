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

    @RequestMapping(value = "/info/public/if_email_exists", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> checkIfNewUserEmailUnique(@RequestParam("email") String email, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        String clientIpAddress = IpUtils.getClientIpAddress(request);
        List<String> errors = new ArrayList<>();
        // todo I hope it's temporal fix as every 4 times we catch while checkIp for known email it needs deeper search
        try {
            ipBlockingService.checkIp(clientIpAddress, IpTypesOfChecking.OPEN_API);
            try {
                if (!userService.ifEmailIsUnique(email)) {
                    ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
                    errors.add("Email exists");
                }
                long after = System.currentTimeMillis();
                if (errors.isEmpty()) ipBlockingService.successfulProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
                LOGGER.debug(String.format("completed... : ms: %d", (after - before)));
            } catch (Exception e) {
                long after = System.currentTimeMillis();
                LOGGER.error(String.format("error... for email: %s ms: %d : %s", email, (after - before), e.getMessage()));
                ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
                errors.add("Maximum tries achieved");
            }
        } catch (Exception e) {
            LOGGER.info("Failure while checking ip: " + e.getMessage());
            errors.add("Failure while checking ip");
        }
        return errors;
    }

    @RequestMapping(value = "/info/public/if_username_exists", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> checkIfNewUserUsernameUnique(@RequestParam("username") String username, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        String clientIpAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(clientIpAddress, IpTypesOfChecking.OPEN_API);
        try {
            List<String> errors = new ArrayList<>();
            if (!userService.ifNicknameIsUnique(username)) {
                ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
                errors.add("Username exists");
            }
            long after = System.currentTimeMillis();
            LOGGER.debug(String.format("completed...: ms: %s", (after - before)));
            if (errors.isEmpty()) ipBlockingService.successfulProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            return errors;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error(String.format("error... for username: %s ms: %s : %s", username, (after - before), e.getMessage()));
            ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            throw e;
        }
    }

    private Map<String, CoinmarketApiJsonDto> getData(String currencyPair) {
        List<CoinmarketApiDto> list = orderService.getDailyCoinmarketData(currencyPair);
        return list.stream().collect(Collectors.toMap(dto -> dto.getCurrency_pair_name().replace('/', '_'), CoinmarketApiJsonDto::new));
    }


}
