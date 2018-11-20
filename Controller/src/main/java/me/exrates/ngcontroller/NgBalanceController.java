package me.exrates.ngcontroller;

import me.exrates.model.User;
import me.exrates.model.dto.StatisticForMarket;
import me.exrates.ngcontroller.mobel.UserBalancesDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.UserService;
import me.exrates.service.cache.MarketRatesHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/info/private/v2/balances/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgBalanceController {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private MarketRatesHolder marketRatesHolder;

    @GetMapping
    public ResponseEntity<PagedResult<UserBalancesDto>> getBalances(@RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
                                                                    @RequestParam(required = false, name = "limit", defaultValue = "14") Integer limit,
                                                                    @RequestParam(required = false, name = "sortByCreated", defaultValue = "DESC") String sortByCreated,
                                                                    @RequestParam(required = false, name = "tikerName", defaultValue = "") String tikerName) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        List<UserBalancesDto> userBalances = balanceService.getUserBalances(tikerName, sortByCreated, page, limit, user.getId());
        Map<Integer, StatisticForMarket> collect = marketRatesHolder.getAll().stream().collect(Collectors.toMap(StatisticForMarket::getCurrencyPairId, v -> v));
        userBalances.forEach(element->{
            StatisticForMarket statisticForMarket = collect.get(element.getCurrencyId());
            BigDecimal result = statisticForMarket.
                    getLastOrderRate().
                    multiply(new BigDecimal(100)).
                    divide(statisticForMarket.getPredLastOrderRate()).
                    subtract(new BigDecimal(100));
            element.setChartChanges(result);

        });
        return ResponseEntity.ok(new PagedResult<>(userBalances.size(), userBalances));
    }
}
