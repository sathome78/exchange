package me.exrates.controller;

import me.exrates.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OnlineRestController {

    @Autowired
    CurrencyService currencyService;

    @ResponseBody
    @RequestMapping(value = "/dashboard/getAllCurrencies")
    public List getAllCurrencies() {
        return currencyService.findAllCurrenciesWithHidden();
    }
}
