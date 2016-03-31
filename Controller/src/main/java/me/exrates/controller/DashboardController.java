package me.exrates.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;
import me.exrates.model.User;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    @Autowired
    OrderService orderService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    RegisterFormValidation registerFormValidation;

    @Autowired
    LocaleResolver localeResolver;

    private CurrencyPair currentCurrencyPair;

    @RequestMapping(value = {"/dashboard/locale"})
    public void localeSwitcherCommand(){
    }

    @RequestMapping(value = {"/dashboard"})
    public ModelAndView dashboard(@ModelAttribute CurrencyPair currencyPair, Principal principal) {
        ModelAndView model = new ModelAndView();
        model.setViewName("dashboard");

        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        if (currencyPair.getName() == null){
            currencyPair = currencyPairs.get(0);
        }

        for (CurrencyPair currencyPairRecord:currencyPairs){
            if (currencyPairRecord.getName().equals(currencyPair.getName())){
                currencyPair = currencyPairRecord;
            }
        }

        currentCurrencyPair = currencyPair;
        model.addObject("currencyPairs", currencyPairs);
        model.addObject("currencyPair", currencyPair);

        Order lastOrder = dashboardService.getLastClosedOrder(currencyPair);
        model.addObject("lastOrder", lastOrder);
        if (lastOrder.getCurrencyBuy() != 0){
            model.addObject("lastOrderCurrency", currencyService.getCurrencyName(lastOrder.getCurrencyBuy()));
        }


        List<Order> ordersBuy = dashboardService.getAllBuyOrders(currencyPair);
        List<Order> ordersSell = dashboardService.getAllSellOrders(currencyPair);
        model.addObject("ordersBuy", ordersBuy);
        model.addObject("ordersSell", ordersSell);

        Order order = new Order();

        BigDecimal sumAmountBuy = ordersBuy.stream().map(Order::getAmountBuy).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAmountSell = ordersSell.stream().map(Order::getAmountBuy).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addObject("sumAmountBuy", sumAmountBuy);
        model.addObject("sumAmountSell", sumAmountSell);


        List<Map<String, BigDecimal>> list = dashboardService.getAmountsFromClosedOrders(currencyPair);
        BigDecimal sumAmountBuyClosed = new BigDecimal(0.0);
        BigDecimal sumAmountSellClosed = new BigDecimal(0.0);
        for (Map<String, BigDecimal> tempRow : list) {
            sumAmountBuyClosed =  tempRow.get("amount_buy");
            sumAmountSellClosed = tempRow.get("amount_sell");
        }
        model.addObject("sumAmountBuyClosed", sumAmountBuyClosed);
        model.addObject("sumAmountSellClosed", sumAmountSellClosed);

        if (principal != null){
            model.addObject("balanceCurrency1", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()),1));
            model.addObject("balanceCurrency2", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()),2));
        }

        BigDecimal minPrice = dashboardService.getMinPriceByCurrency(currencyPair);
        BigDecimal maxPrice = dashboardService.getMaxPriceByCurrency(currencyPair);
        model.addObject("minPrice",minPrice);
//        order.setAmountSell(minPrice);
        model.addObject("maxPrice",maxPrice);
//        order.setAmountBuy(maxPrice);

        model.addObject(order);
        return model;
    }

    @RequestMapping(value = "/dashboard/chartArray", method = RequestMethod.GET)
    public @ResponseBody ArrayList chartArray() {

        CurrencyPair currencyPair = currentCurrencyPair;
        List<Map<String, Object>> list = dashboardService.getDataForChart(currencyPair);

        ArrayList<ArrayList> arrayListMain = new ArrayList<ArrayList>();

        for (Map<String, Object> tempRow : list) {
            BigDecimal amount = (BigDecimal) tempRow.get("amount");
            BigDecimal amountSell = (BigDecimal) tempRow.get("amount_sell");
            Timestamp timestamp = (Timestamp)tempRow.get("date_final");
            Date date  = new Date(timestamp.getTime());

            if (amount == null){
                continue;
            }
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(timestamp.toString());
            arrayList.add(amount.doubleValue());
            arrayList.add(amount.doubleValue());
            arrayList.add(amount.doubleValue());
            arrayList.add(amount.doubleValue());
            arrayList.add(amountSell.doubleValue());

            arrayListMain.add(arrayList);

        }
        return arrayListMain;
   }

    @RequestMapping(value = "/dashboard/commission/{type}",method = RequestMethod.GET)
    public @ResponseBody BigDecimal  getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "sell" :
                return commissionService.findCommissionByType(OperationType.SELL).getValue();
            case "buy" :
                return commissionService.findCommissionByType(OperationType.BUY).getValue();
            default:
                return null;
        }
    }


    @RequestMapping(value = "/forgotPassword")
    public ModelAndView forgotPassword() {
        ModelAndView model = new ModelAndView();
        model.addObject("user", new User());
        model.setViewName("forgotPassword");

        return model;
    }

    @RequestMapping(value = "/forgotPassword/submit", method = RequestMethod.POST)
    public ModelAndView forgotPasswordSubmit(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {

        String email = user.getEmail();
        registerFormValidation.validateEmail(user, result, localeResolver.resolveLocale(request));
        if(result.hasErrors()){
            model.addObject("user", user);
            model.setViewName("/forgotPassword");
            return model;
        }
        user = userService.findByEmail(email);
        userService.update(user, false, false , true);

        model.setViewName("redirect:/dashboard");

        return model;
    }

    @RequestMapping(value = "/resetPasswordConfirm")
    public ModelAndView resetPasswordConfirm(WebRequest request, @RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            int userId = userService.verifyUserEmail(token).getId();
            User user = userService.getUserById(userId);
            model.addObject("user", user);
            model.setViewName("updatePassword");
            org.springframework.security.core.userdetails.User userSpring = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), false, false, false, false,
                    userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
            Collection<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
            authList.add(new SimpleGrantedAuthority(UserRole.ROLE_CHANGE_PASSWORD.name()));
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userSpring, null, authList);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping(value = "/dashboard/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView updatePassword(HttpServletRequest request, @RequestParam("password") String password) {

        ModelAndView model = new ModelAndView();
        org.springframework.security.core.userdetails.User userSpring = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userSpring.getUsername());
        user.setPassword(password);
        userService.updateUserByAdmin(user);
        new SecurityContextLogoutHandler().logout(request, null, null);
        model.setViewName("redirect:/dashboard");
        return model;
    }


    @RequestMapping(value = "/forgotPassword/submitUpdate", method = RequestMethod.POST)
    public ModelAndView submitUpdate(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {


        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if(result.hasErrors()){
            model.addObject("user", user);
            model.setViewName("updatePassword");
            return model;
        }

        else {

            User userUpdate = userService.getUserById(user.getId());
            userUpdate.setPassword(user.getPassword());

            userService.updateUserByAdmin(userUpdate);
            model.setViewName("redirect:/dashboard");
        }

        return model;
    }
    }
