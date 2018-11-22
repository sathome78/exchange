package me.exrates.ngcontroller;

import me.exrates.model.User;
import me.exrates.model.dto.StatisticForMarket;
import me.exrates.model.dto.TableParams;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.PagingDirection;
import me.exrates.model.vo.CacheData;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.model.UserBalancesDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.security.annotation.OnlineMethod;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.cache.MarketRatesHolder;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
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

    @Autowired
    private RefillPendingRequestService refillPendingRequestService;

    @Autowired
    InputOutputService inputOutputService;

    @Autowired
    LocaleResolver localeResolver;

    @GetMapping
    public ResponseEntity<PagedResult<UserBalancesDto>> getBalances(@RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
                                                                    @RequestParam(required = false, name = "limit", defaultValue = "14") Integer limit,
                                                                    @RequestParam(required = false, name = "sortByCreated", defaultValue = "DESC") String sortByCreated,
                                                                    @RequestParam(required = false, name = "tickerName", defaultValue = "") String tickerName) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<UserBalancesDto> userBalances = balanceService.getUserBalances(tickerName, sortByCreated, page, limit, userService.getIdByEmail(email));
        Map<Integer, StatisticForMarket> collect = marketRatesHolder.getAll().stream().collect(Collectors.toMap(StatisticForMarket::getCurrencyPairId, v -> v));

        userBalances.forEach(element-> {
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

    @GetMapping("/getPendingRequests")
    public List<RefillPendingRequestDto> getPendingRequests(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return refillPendingRequestService.getPendingRefillRequests(userService.getIdByEmail(email));
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's input/output orders to show in pages "History input/output"
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param tableId          determines table on pages "History" to show data
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @param principal
     * @param request
     * @return list the data of user's orders
     */
    @OnlineMethod
    @RequestMapping(value = "/getInputOutputData/{tableId}", method = RequestMethod.GET)
    public List<MyInputOutputHistoryDto> getMyInputoutputData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myInputoutputData" + tableId + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyInputOutputHistoryDto> result = inputOutputService.getMyInputOutputHistory(cacheData, email, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

}