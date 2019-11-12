package me.exrates.controller;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.ReferralInfoDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.NewsService;
import me.exrates.service.NotificationService;
import me.exrates.service.OrderService;
import me.exrates.service.ReferralService;
import me.exrates.service.RefillService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.RefillRequestMerchantException;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * The controller contains online methods. "Online method" is the handler of online requests,
 * which updates data on browser page in online mode.
 * The online request is the automatic request and is not sign of user activity and should not update
 * session param "sessionEndTime", which stores the time of end the current session.
 * Another (not online) methods, excluding resources request, reset session param "sessionEndTime" and session life starts from begin
 * Updating session param "sessionEndTime" happens in class XssRequestFilter.
 * <p>
 * IMPORTANT!
 * The OnlineRestController can contain not online methods. But all online methods must be placed in the OnlineRestController
 * Online methods must be annotated with @OnlineMethod
 *
 * @author ValkSam
 */
@Log4j2
@PropertySource(value = {"classpath:session.properties"})
@RestController
public class OnlineRestController {
    private static final Logger LOGGER = LogManager.getLogger(OnlineRestController.class);
    /* if SESSION_LIFETIME_HARD set, session will be killed after time expired, regardless of activity the session
    set SESSION_LIFETIME_HARD = 0 to ignore it*/
    /* public static final long SESSION_LIFETIME_HARD = Math.round(90 * 60); //SECONDS*/
    /* if SESSION_LIFETIME_INACTIVE set, session will be killed if it is inactive during the time
     * set SESSION_LIFETIME_INACTIVE = 0 to ignore it and session lifetime will be set to default value (30 mins)
     * The time of end the current session is stored in session param "sessionEndTime", which calculated in millisec as
     * new Date().getTime() + SESSION_LIFETIME_HARD * 1000*/
    /*public static final int SESSION_LIFETIME_INACTIVE = 0; //SECONDS*/
    /*depth the accepted order history*/
    final public static BackDealInterval ORDER_HISTORY_INTERVAL = new BackDealInterval("24 HOUR");
    /*limit the data fetching of order history (additional to ORDER_HISTORY_INTERVAL). (-1) means no limit*/
    final public static Integer ORDER_HISTORY_LIMIT = 100;
    /*default limit the data fetching for all tables. (-1) means no limit*/
    final public static Integer TABLES_LIMIT_DEFAULT = -1;
    /*it's need to install only one: SESSION_LIFETIME_HARD or SESSION_LIFETIME_INACTIVE*/

    private @Value("${session.timeParamName}")
    String sessionTimeMinutes;
    private @Value("${session.lastRequestParamName}")
    String sessionLastRequestParamName;

    @Autowired
    CommissionService commissionService;

    @Autowired
    OrderService orderService;

    @Autowired
    WalletService walletService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    NewsService newsService;

    @Autowired
    ReferralService referralService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    WithdrawService withdrawService;

    @Autowired
    InputOutputService inputOutputService;

    @Autowired
    StopOrderService stopOrderService;

    @Autowired
    private ExchangeRatesHolder exchangeRatesHolder;
    @Autowired
    private RefillService refillService;

    private final String HEADER_SECURITY = "username";

    @PostMapping(value = "/afgssr/call/refill", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> generateRefill(@RequestBody RefillRequestCreateDto requestDto, HttpServletRequest servletRequest) {
        try {
            Preconditions.checkNotNull(requestDto.getServiceBeanName(), "wrong params");
            String usernameHeader = servletRequest.getHeader(HEADER_SECURITY);
            Preconditions.checkArgument(!StringUtils.isEmpty(usernameHeader), "invalid request");
            String username = new String(Base64.getDecoder().decode(usernameHeader.getBytes()));
            Preconditions.checkArgument(username.equals(requestDto.getUserEmail()) && userService.findByEmail(username) != null, "user not found or wrong user");
            return refillService.callRefillIRefillable(requestDto);
        } catch (Exception e) {
            log.error(e);
            throw new RefillRequestMerchantException(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            RefillRequestMerchantException.class,
    })
    @ResponseBody
    public ErrorInfo RefillException(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception, exception.getMessage());
    }

    @ResponseBody
    @RequestMapping(value = "/dashboard/getAllCurrencies")
    public List getAllCurrencies() {
        return currencyService.findAllCurrenciesWithHidden();
    }

    private void hideEmails(List<ReferralInfoDto> referralInfoDtos) {

        for (ReferralInfoDto dto : referralInfoDtos) {
            String email = dto.getEmail();
            StringBuilder buf = new StringBuilder(email);
            int start = 2;
            int end = email.length() - 5;
            for (int i = start; i < end; i++) {
                buf.setCharAt(i, '*');
            }
            dto.setEmail(buf.toString());
        }
    }

}
