package me.exrates.controller;


import me.exrates.model.Currency;
import me.exrates.model.Order;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CommissionService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;
    //private static final Locale ru = new Locale("ru");

    @RequestMapping(value = "/orders")
    public ModelAndView myOrders() {
        ModelAndView model = new ModelAndView();
        Map<String, List<Order>> orderMap = orderService.getAllOrders();
        model.setViewName("orders");
        model.addObject("orderMap", orderMap);
        Order order = new Order();
        getCurrenciesAndCommission(model, OperationType.SELL);
        model.addObject(order);
        return model;
    }

    @RequestMapping(value = "/orders/submitaccept")
    public ModelAndView submitAcceptOrder(@RequestParam int id, ModelAndView model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        int userId = userService.getIdByEmail(principal.getName());
        Order order = orderService.getOrderById(id);
        int userWalletIdForBuy = walletService.getWalletId(userId, order.getCurrencyBuy());
        if (userWalletIdForBuy != 0) {
            if (walletService.ifEnoughMoney(userWalletIdForBuy, order.getAmountBuy())) {
                model.setViewName("submitacceptorder");
                model.addObject("order", order);
            } else {
                redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
                model.setViewName("redirect:/orders");
            }
        } else {
            redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
            model.setViewName("redirect:/orders");
        }
        return model;
    }

    @RequestMapping(value = "/orders/accept")
    public ModelAndView acceptOrder(@RequestParam int id, ModelAndView model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        int userId = userService.getIdByEmail(principal.getName());
        Order order = orderService.getOrderById(id);
        int userWalletIdForBuy = walletService.getWalletId(userId, order.getCurrencyBuy());
        if (userWalletIdForBuy != 0) {
            if (walletService.ifEnoughMoney(userWalletIdForBuy, order.getAmountBuy())) {
                if (orderService.acceptOrder(userId, id)) {
                    model.setViewName("acceptordersuccess");
                    model.addObject("order", order);
                } else {
                    model.setViewName("DBError");
                }
            } else {
                redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
                model.setViewName("redirect:/orders");
            }
        } else {
            redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
            model.setViewName("redirect:/orders");
        }
        return model;
    }

    @RequestMapping(value = "/order/new")
    public ModelAndView showNewOrderToSellForm(ModelAndView model) {
        getCurrenciesAndCommission(model, OperationType.SELL);
        Order order = new Order();
        order.setOperationType(OperationType.SELL);
        model.setViewName("newordertosell");
        model.addObject(order);
        return model;
    }

    @RequestMapping(value = "/order/submit", method = RequestMethod.POST)
    public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute Order order, BindingResult result, ModelAndView model, Principal principal, HttpServletRequest request) {
        getCurrenciesAndCommission(model, order.getOperationType());
        if (result.hasErrors()) {
            model.setViewName("newordertosell");
        } else {
            int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()), order.getCurrencySell());
            boolean ifEnoughMoney = false;
            if (walletIdFrom != 0) {
                ifEnoughMoney = walletService.ifEnoughMoney(walletIdFrom, order.getAmountSell());
            }
            if (ifEnoughMoney) {
                model.setViewName("submitorder");
            } else {
                model.addObject("notEnoughMoney", messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
                model.setViewName("newordertosell");
            }
        }
        model.addObject("order", order);
        return model;
    }

    @RequestMapping(value = "/order/create", method = RequestMethod.POST)
    public ModelAndView recordOrderToDB(ModelAndView model, @ModelAttribute Order order, Principal principal) {
        int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()), order.getCurrencySell());
        order.setWalletIdSell(walletIdFrom);
        if ((orderService.createOrder(order)) > 0) {
            model.setViewName("ordercreated");
        } else {
            model.setViewName("DBError");
        }
        model.addObject(order);
        return model;
    }

    @RequestMapping(value = "/order/edit", method = RequestMethod.POST)
    public ModelAndView showEditOrderToSellForm(ModelAndView model, @ModelAttribute Order order) {
        model = new ModelAndView("editorder", "order", order);
        getCurrenciesAndCommission(model, order.getOperationType());
        return model;
    }

    @RequestMapping("/myorders")
    public ModelAndView showMyOrders(Principal principal, ModelAndView model) {
        String email = principal.getName();
        Map<String, List<Order>> orderMap = orderService.getMyOrders(email);
        model.addObject("orderMap", orderMap);
        return model;
    }

    @RequestMapping("/myorders/submitdelete")
    public ModelAndView submitDeleteOrder(@RequestParam int id, RedirectAttributes redirectAttributes, ModelAndView model) {
        Order order = orderService.getOrderById(id);
        model.setViewName("submitdeleteorder");
        model.addObject("order", order);
        return model;
    }

    @RequestMapping("/myorders/delete")
    public String deleteOrder(@RequestParam int id, RedirectAttributes redirectAttributes) {
        String msg = null;
        if (orderService.cancellOrder(id)) {
            msg = "delete";
        } else {
            msg = "deletefailed";
        }
        redirectAttributes.addFlashAttribute("msg", msg);
        return "redirect:/myorders";
    }


    @RequestMapping("DBError")
    public String DBerror() {
        return "DBError";
    }

    private void getCurrenciesAndCommission(ModelAndView model, OperationType type) {
        List<Currency> currList = walletService.getCurrencyList();
        BigDecimal commission = commissionService.findCommissionByType(type).getValue();
        model.addObject("currList", currList);
        model.addObject("commission", commission);
    }

}  

