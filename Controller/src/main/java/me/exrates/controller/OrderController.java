package me.exrates.controller;


import me.exrates.controller.exception.*;
import me.exrates.controller.validator.OrderValidator;
import me.exrates.model.*;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.dto.OrderWideListDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TokenType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    OrderValidator orderValidator;

    @RequestMapping(value = "/orders")
    public ModelAndView orderBuySellList(Principal principal, @ModelAttribute ExOrder exOrder, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView model = new ModelAndView();
        model.setViewName("orders");
        //
        CurrencyPair activeCurrencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        //
        List<OrderListDto> sellOrdersList = orderService.getOrdersSell(activeCurrencyPair);
        List<OrderListDto> buyOrdersList = orderService.getOrdersBuy(activeCurrencyPair);
        model.addObject("sellOrdersList", sellOrdersList);
        model.addObject("buyOrdersList", buyOrdersList);
        //
        int userId = userService.getIdByEmail(principal.getName());
        int currencyBaseId = activeCurrencyPair.getCurrency1().getId();
        int currencyConvertId = activeCurrencyPair.getCurrency2().getId();
        int currencyBaseWalletId = walletService.getWalletId(userId, currencyBaseId);
        int currencyConvertWalletId = walletService.getWalletId(userId, currencyConvertId);
        //
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setCurrencyPair(activeCurrencyPair);
        orderCreateDto.setWalletIdCurrencyBase(currencyBaseWalletId);
        orderCreateDto.setCurrencyBaseBalance(walletService.getWalletABalance(currencyBaseWalletId));
        orderCreateDto.setWalletIdCurrencyConvert(currencyConvertWalletId);
        orderCreateDto.setCurrencyConvertBalance(walletService.getWalletABalance(currencyConvertWalletId));
        Commission commission = commissionService.findCommissionByType(OperationType.BUY);
        orderCreateDto.setComissionForBuyId(commission.getId());
        orderCreateDto.setComissionForBuyRate(commission.getValue());
        commission = commissionService.findCommissionByType(OperationType.SELL);
        orderCreateDto.setComissionForSellId(commission.getId());
        orderCreateDto.setComissionForSellRate(commission.getValue());
        /**/
        if (exOrder != null) {
            orderCreateDto.setOperationType(exOrder.getOperationType());
            orderCreateDto.setAmount(exOrder.getAmountBase());
            orderCreateDto.setExchangeRate(exOrder.getExRate());
        }
        model.addObject("orderCreateDto", orderCreateDto);
        //
        return model;
    }

    /*CHECK FIN PASSWORD*/

    @RequestMapping(value = "/checkfinpass", method = RequestMethod.POST)
    @ResponseBody
    public void checkFinPassword(User user, HttpServletRequest request) {
        String enteredFinPassword = user.getFinpassword();
        User storedUser = userService.getUserById(userService.getIdByEmail(user.getEmail()));
        boolean isNotConfirmedToken = userService.getTokenByUserAndType(storedUser, TokenType.CHANGE_FIN_PASSWORD).size() > 0;
        if (isNotConfirmedToken) {
            throw new NotConfirmedFinPasswordException(messageSource.getMessage("admin.notconfirmedfinpassword", null, localeResolver.resolveLocale(request)));
        }
        String currentFinPassword = storedUser.getFinpassword();
        if (currentFinPassword == null || currentFinPassword.isEmpty()) {
            throw new AbsentFinPasswordException(messageSource.getMessage("admin.absentfinpassword", null, localeResolver.resolveLocale(request)));
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authSuccess = passwordEncoder.matches(enteredFinPassword, currentFinPassword);
        if (!authSuccess) {
            throw new WrongFinPasswordException(messageSource.getMessage("admin.wrongfinpassword", null, localeResolver.resolveLocale(request)));
        }
    }

   /*ACCEPT ORDER ...*/

    /* check if enough money to accept
    * */
    @RequestMapping(value = "/orders/submitaccept/check")
    @ResponseBody
    public void checkSubmitAcceptOrder(@RequestParam int id, Principal principal, HttpServletRequest request) {
        int userId = userService.getIdByEmail(principal.getName());
        ExOrder exOrder = orderService.getOrderById(id);
        int walletForCheck;
        BigDecimal amountForCheck;
        if (exOrder.getOperationType() == OperationType.BUY) {
            Currency currencyBase = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId()).getCurrency1();
            walletForCheck = walletService.getWalletId(userId, currencyBase.getId());
            amountForCheck = exOrder.getAmountBase();
        } else {
            Currency currencyConvert = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId()).getCurrency2();
            walletForCheck = walletService.getWalletId(userId, currencyConvert.getId());
            BigDecimal comissionRateForAcceptor = commissionService.findCommissionByType(OperationType.BUY).getValue();
            BigDecimal amountComissionForAcceptor = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), comissionRateForAcceptor, ActionType.MULTIPLY_PERCENT);
            amountForCheck = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), amountComissionForAcceptor, ActionType.ADD);
        }
        if ((walletForCheck == 0) || !walletService.ifEnoughMoney(walletForCheck, amountForCheck)) {
            throw new NotEnoughUserWalletMoneyException(messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
        }
    }

    /* after success checking for enough money to accept
    to pass to form for control and submit accept
    * */
    @RequestMapping(value = "/orders/submitaccept")
    public ModelAndView submitAcceptOrder(@RequestParam int id, ModelAndView model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        ExOrder exOrder = orderService.getOrderById(id);
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId());
        //
        int userId = userService.getIdByEmail(principal.getName());
        int currencyBaseId = currencyPair.getCurrency1().getId();
        int currencyConvertId = currencyPair.getCurrency2().getId();
        int currencyBaseWalletId = walletService.getWalletId(userId, currencyBaseId);
        int currencyConvertWalletId = walletService.getWalletId(userId, currencyConvertId);
        //
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderId(exOrder.getId());
        if (exOrder.getOperationType() == OperationType.BUY) {
            orderCreateDto.setOperationType(OperationType.SELL);
        } else {
            orderCreateDto.setOperationType(OperationType.BUY);
        }
        orderCreateDto.setExchangeRate(exOrder.getExRate());
        /**/
        orderCreateDto.setCurrencyPair(currencyPair);
        orderCreateDto.setWalletIdCurrencyBase(currencyBaseWalletId);
        orderCreateDto.setCurrencyBaseBalance(walletService.getWalletABalance(currencyBaseWalletId));
        orderCreateDto.setWalletIdCurrencyConvert(currencyConvertWalletId);
        orderCreateDto.setCurrencyConvertBalance(walletService.getWalletABalance(currencyConvertWalletId));
        Commission commission = commissionService.findCommissionByType(OperationType.BUY);
        orderCreateDto.setComissionForBuyId(commission.getId());
        orderCreateDto.setComissionForBuyRate(commission.getValue());
        commission = commissionService.findCommissionByType(OperationType.SELL);
        orderCreateDto.setComissionForSellId(commission.getId());
        orderCreateDto.setComissionForSellRate(commission.getValue());
        orderCreateDto.setAmount(exOrder.getAmountBase());
        orderCreateDto.setTotal(exOrder.getAmountConvert());
        /**/
        if (orderCreateDto.getOperationType() == OperationType.BUY) {
            orderCreateDto.setComissionId(orderCreateDto.getComissionForBuyId());
            orderCreateDto.setComission(BigDecimalProcessing.doAction(exOrder.getAmountConvert(), orderCreateDto.getComissionForBuyRate(), ActionType.MULTIPLY_PERCENT));
            orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(exOrder.getAmountConvert(), orderCreateDto.getComission(), ActionType.ADD));
        } else {
            orderCreateDto.setComissionId(orderCreateDto.getComissionForSellId());
            orderCreateDto.setComission(BigDecimalProcessing.doAction(exOrder.getAmountConvert(), orderCreateDto.getComissionForSellRate(), ActionType.MULTIPLY_PERCENT));
            orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(exOrder.getAmountConvert(), orderCreateDto.getComission().negate(), ActionType.ADD));
        }
        /**/
        model.addObject("orderCreateDto", orderCreateDto);
        /**/
        model.setViewName("submitacceptorder");
        return model;
    }

    /* after succes checking for enough money for accept
    and after submit accept
    try to fix operation in db. It's possible that error occures (for example because balance has changed)
    * */
    @RequestMapping(value = "/orders/accept")
    @ResponseBody
    public void acceptOrder(@RequestParam int id, ModelAndView model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        int userId = userService.getIdByEmail(principal.getName());
        try {
            orderService.acceptOrder(userId, id);
        } catch (Exception e) {
            throw e;
        }
    }

    /*show message form after success accept
    * */
    @RequestMapping(value = "/orders/acceptordersuccess")
    public ModelAndView acceptOrderSuccess(ModelAndView model) {
        model.setViewName("acceptordersuccess");
        return model;
    }

    /*... ACCEPT ORDER*/

    /*CREATE ORDER....*/
    /* after filling fields of creation form on orders page
    shows form to submit new order if all fields are filled correct
    * */
    @RequestMapping(value = "/order/submit", method = RequestMethod.POST)
    public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute OrderCreateDto orderCreateDto,
                                             BindingResult result, ModelAndView model) {
        orderValidator.validate(orderCreateDto, result);
        if (result.hasErrors()) {
            model.setViewName("newordertosell");
        } else {
            //final amounts calculated here (not by javascript) and transfere to submit form
            orderCreateDto.calculateAmounts();
            model.addObject("orderCreateDto", orderCreateDto);
            //
            model.setViewName("submitorder");
        }
        model.addObject("orderCreateDto", orderCreateDto);
        return model;
    }

    /* after submit create order
    to try to fix operation in db. It's possible that error occures (for example because balance has changed)
    * */
    @RequestMapping(value = "/orders/create")
    @ResponseBody
    public void recordOrderToDB(OrderCreateDto orderCreateDto, Principal principal, HttpServletRequest request) {
        int userId = userService.getIdByEmail(principal.getName());
        try {
            if ((orderService.createOrder(userId, orderCreateDto)) <= 0) {
                throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, localeResolver.resolveLocale(request)));
            }
        } catch (NotEnoughUserWalletMoneyException e) {
            throw new NotEnoughUserWalletMoneyException(messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
        }
    }

    /*to show message form after success accept
    * */
    @RequestMapping(value = "/orders/createordersuccess")
    public ModelAndView createOrderSuccess(ModelAndView model) {
        model.setViewName("ordercreated");
        return model;
    }

    /* ... CREATE ORDER*/

    /*
    if need to edit created order before final submit
    * */
    @RequestMapping(value = "/order/edit", method = RequestMethod.POST)
    public ModelAndView showEditOrderToSellForm(@Valid @ModelAttribute OrderCreateDto orderCreateDto, ModelAndView model) {
        model.setViewName("newordertosell");
        model.addObject("orderCreateDto", orderCreateDto);
        return model;
    }

    @RequestMapping("/myorders")
    public ModelAndView showMyOrders(Principal principal, ModelAndView model, HttpServletRequest request) {
        String email = principal.getName();
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        Map<String, List<OrderWideListDto>> orderMap = orderService.getMyOrders(email, currencyPair, localeResolver.resolveLocale(request));
        model.addObject("orderMap", orderMap);
        return model;
    }

    @RequestMapping("/myorders/submitdelete")
    public ModelAndView submitDeleteOrder(@RequestParam int id, ModelAndView model) {
        /**/
        ExOrder exOrder = orderService.getOrderById(id);
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId());
        /**/
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderId(exOrder.getId());
        orderCreateDto.setUserId(exOrder.getUserId());
        orderCreateDto.setOperationType(exOrder.getOperationType());
        orderCreateDto.setStatus(exOrder.getStatus());
        orderCreateDto.setExchangeRate(exOrder.getExRate());
        /**/
        orderCreateDto.setCurrencyPair(currencyPair);
        orderCreateDto.setAmount(exOrder.getAmountBase());
        orderCreateDto.setTotal(exOrder.getAmountConvert());
        /**/
        orderCreateDto.setComission(exOrder.getCommissionFixedAmount());
        if (orderCreateDto.getOperationType() == OperationType.SELL) {
            orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(exOrder.getAmountConvert(), orderCreateDto.getComission().negate(), ActionType.ADD));
        } else {
            orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(exOrder.getAmountConvert(), orderCreateDto.getComission(), ActionType.ADD));
        }
        /**/
        model.addObject("orderCreateDto", orderCreateDto);
        /**/
        model.setViewName("submitdeleteorder");
        return model;
    }

    @RequestMapping("/myorders/delete")
    public String deleteOrder(@ModelAttribute OrderCreateDto orderCreateDto, RedirectAttributes redirectAttributes) {
        String msg = null;
        if (orderService.cancellOrder(new ExOrder(orderCreateDto))) {
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

    /*
    error handlers for this controller
    * */

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotEnoughMoneyException.class)
    @ResponseBody
    public ErrorInfo notEnoughMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotEnoughUserWalletMoneyException.class)
    @ResponseBody
    public ErrorInfo NotEnoughUserWalletMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotAcceptableOrderException.class)
    @ResponseBody
    public ErrorInfo NotAcceptableOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotCreatableOrderException.class)
    @ResponseBody
    public ErrorInfo NotCreatableOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(WrongFinPasswordException.class)
    @ResponseBody
    public ErrorInfo WrongFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AbsentFinPasswordException.class)
    @ResponseBody
    public ErrorInfo AbsentFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotConfirmedFinPasswordException.class)
    @ResponseBody
    public ErrorInfo NotConfirmedFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}

