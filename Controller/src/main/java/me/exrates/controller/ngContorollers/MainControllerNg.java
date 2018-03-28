package me.exrates.controller.ngContorollers;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.*;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.ngDto.UserSettingsDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.form.NotificationOptionsForm;
import me.exrates.service.*;
import me.exrates.service.notifications.NotificationsSettingsService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

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
     * @param loc - current user locale on client-side
     * @return: list the data to create currency pairs menu
     * @author ValkSam
     */
    @RequestMapping(value = "/public/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<CurrencyPair>> getCurrencyPairNameList(String loc) {
        Locale locale = Locale.forLanguageTag(loc);
        List<CurrencyPair> list = currencyService.getAllCurrencyPairs();
        list.forEach(p -> p.setMarketName(messageSource.getMessage("message.cp.".concat(p.getMarket()), null, locale)));
        return list.stream().collect(Collectors.groupingBy(CurrencyPair::getMarket));
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

}
