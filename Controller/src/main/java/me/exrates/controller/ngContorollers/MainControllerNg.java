package me.exrates.controller.ngContorollers;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.ngDto.UserSettingsDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.form.NotificationOptionsForm;
import me.exrates.service.*;
import me.exrates.service.exception.OrderCancellingException;
import me.exrates.service.exception.OrderCreationException;
import me.exrates.service.exception.OrderNotFoundException;
import me.exrates.service.impl.proxy.ServiceCacheableProxy;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static java.util.Collections.sort;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Maks on 02.02.2018.
 */
@Log4j2
@RequestMapping(value = "/info")
@RestController
@PropertySource(value = {"classpath:about_us.properties"})
public class MainControllerNg {

    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;

    @Autowired
    private ReferralService referralService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;
    @Autowired
    private CommissionService comissionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SendMailService mailService;
    @Autowired
    private ServiceCacheableProxy serviceCacheableProxy;
    @Autowired
    private StopOrderService stopOrderService;

    @Autowired
    LocaleResolver localeResolver;

    /*controllers for testing*/
    @RequestMapping(value = "/public/test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> testNg() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", "ok");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/private/test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> test2Ng() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", "ok");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }


    @RequestMapping("/private/generateReferral")
    public
    @ResponseBody
    Map<String, String> generateReferral() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return singletonMap("referral", referralService.generateReferral(userEmail));
    }

    /**
     * returns map the data to create currency pairs menu
     *  <market name, list<currencyPair name>>
     * @param  - current user locale on client-side
     * @return: list the data to create currency pairs menu
     * @author ValkSam
     */
    /*@RequestMapping(value = "/public/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<CurrencyPair>> getCurrencyPairNameList(String loc) {
        Locale locale = Locale.forLanguageTag(loc);
        List<CurrencyPair> list = currencyService.getAllCurrencyPairs();
        list.forEach(p -> p.setMarketName(messageSource.getMessage("message.cp.".concat(p.getMarket()), null, locale)));
        return list.stream().collect(Collectors.groupingBy(CurrencyPair::getMarket));
    }*/

    @RequestMapping(value = "/public/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyPair> getCurrencyPairNameList() {
        return currencyService.getAllCurrencyPairs();
    }

    @RequestMapping(value = "/private/commission/{type}", method = RequestMethod.GET)
    public BigDecimal getCommissions(@PathVariable("type") String type) {
        UserRole userRole = userService.getUserRoleFromSecurityContext();
        try {
            switch (type) {
                case "sell":
                    return comissionService.findCommissionByTypeAndRole(OperationType.SELL, userRole).getValue();
                case "buy":
                    return comissionService.findCommissionByTypeAndRole(OperationType.BUY, userRole).getValue();
                default:
                    return null;
            }
        } finally {
        }
    }

    @RequestMapping(value = {"/private/open_orders/", "/private/open_orders/{currencyPairId}"},
                                     method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<OrderWideListDto> getOpenOrders(@PathVariable Optional<Integer> currencyPairId,
                                                @RequestParam(name = "baseType", required = false) int id) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        CurrencyPair currencyPair = null;
        if (currencyPairId.isPresent()){
            currencyPair = currencyService.findCurrencyPairById(currencyPairId.get());
        }
        OrderBaseType orderBaseType = OrderBaseType.convert(id);
        switch (orderBaseType) {
            case STOP_LIMIT: {
                return stopOrderService.getMyOrdersWithState(userEmail, currencyPair, OrderStatus.OPENED, null, null,0, -1, locale);
            }
            default: {
                return orderService.getMyOrdersWithState(userEmail, currencyPair, OrderStatus.OPENED, null, null,0, -1, locale);
            }
        }
    }

    @RequestMapping(value = "/private/orderCommissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderCommissionsDto getOrderCommissions() {
        OrderCommissionsDto result = orderService.getCommissionForOrder();
        return result;
    }

    @RequestMapping(value = "/private/orderinfo", method = RequestMethod.GET)
    public OrderInfoDto getOrderInfo(@RequestParam int id) {
        /*todo: check to access only own orders, not all*/
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return orderService.getOrderInfo(id, locale);
    }

    @RequestMapping(value = "/private/order/submitdelete/{orderId}", method = RequestMethod.GET)
    public OrderCreateSummaryDto submitDeleteOrder(@PathVariable Integer orderId,
                                                   @RequestParam(value = "baseType", defaultValue = "1") int typeId,
                                                   HttpServletRequest req) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        long before = System.currentTimeMillis();
        OrderBaseType orderBaseType = OrderBaseType.convert(typeId);
        try {
            OrderCreateDto orderCreateDto;
            switch (orderBaseType) {
                case STOP_LIMIT: {
                    orderCreateDto = stopOrderService.getOrderById(orderId, false);
                    break;
                }
                default: {
                    orderCreateDto = orderService.getMyOrderById(orderId);
                }
            }
            if (orderCreateDto == null) {
                throw new OrderNotFoundException(messageSource.getMessage("orders.getordererror", new Object[]{orderId}, locale));
            }
            orderCreateDto.setOrderBaseType(orderBaseType);
            return new OrderCreateSummaryDto(orderCreateDto, locale);
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            throw e;
        } finally {
            long after = System.currentTimeMillis();

        }
    }

    @DeleteMapping(value = "/private/order/delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> deleteOrder(@RequestBody OrderCreateDto orderCreateDto, HttpServletRequest req) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        long before = System.currentTimeMillis();
        try {
            if (orderCreateDto == null) {
                throw new OrderCreationException(messageSource.getMessage("order.redeleteerror", null, locale));
            }
            boolean result;
            switch (orderCreateDto.getOrderBaseType()) {
                case STOP_LIMIT: {
                    result = stopOrderService.cancelOrder(new ExOrder(orderCreateDto), locale);
                    break;
                }
                default: {
                    result = orderService.cancellOrder(new ExOrder(orderCreateDto), locale);
                }
            }
            if (!result) {
                throw new OrderCancellingException(messageSource.getMessage("myorders.deletefailed", null, locale));
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            throw e;
        } finally {
            long after = System.currentTimeMillis();
        }
    }

    @PostMapping(value = "/public/feedback", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> sendFeedback(@RequestBody Map<String, String> body){
        try {
            mailService.sendFeedbackMail(body.get("name"), body.get("email"), body.get("message"), feedbackEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/public/serverTimezone", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> getServerTimeZone(){
        String timeZone = TimeZone.getDefault().getDisplayName();
        String stz [] = timeZone.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : stz){
            stringBuilder.append(word.charAt(0));
        }
        timeZone = stringBuilder.toString().equalsIgnoreCase("CUT") ? "UTC" : stringBuilder.toString();
        return ResponseEntity.ok().body(Collections.singletonMap("timeZone", timeZone));
    }

    @GetMapping("/public/terms/{language}")
	@ResponseBody
	public Map<String, String> getTerms(@PathVariable String language){
    	Locale locale = new Locale(language);
        Optional<String> result = Optional.ofNullable(messageSource.getMessage("dashboard.termsContent", null, locale));
        return Collections.singletonMap("content", result.orElse(""));
	}

	@GetMapping("/public/privacy/{language}")
	@ResponseBody
	public Map<String, String> getPrivacyPolicy(@PathVariable String language){
		Locale locale = new Locale(language);
		Optional<String> result = Optional.ofNullable(messageSource.getMessage("dashboard.privacyContent", null, locale));
		return Collections.singletonMap("content", result.orElse(""));
	}

    @RequestMapping(value = "/public/twitterTimeLine", method = RequestMethod.GET)
    public List<Tweet> getTwitterTimeline(@RequestParam(value = "size", defaultValue = "50") int size) {
        return serviceCacheableProxy.getTwitterTimeline(size);
    }

    @RequestMapping(value = "/private/myReferralStructure", method = RequestMethod.GET)
    public RefsListContainer getRefferalStructure(@RequestParam("action") String action,
                                                  @RequestParam(value = "userId", required = false) Integer userId,
                                                  @RequestParam(value = "onPage", defaultValue = "20") int onPage,
                                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                                  RefFilterData refFilterData) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        /**/
        return referralService.getRefsContainerForReq(action, userId, userService.getIdByEmail(email), onPage, page, refFilterData);
    }

}
