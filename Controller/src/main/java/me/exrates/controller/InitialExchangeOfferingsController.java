package me.exrates.controller;

import me.exrates.dao.IInitialExchangeOfferings;
import me.exrates.dao.impl.InitialExchangeOfferingsDao;
import me.exrates.service.IInitialExchangeOfferingsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/ieo")
public class InitialExchangeOfferingsController {

    @Autowired
    private IInitialExchangeOfferingsService iInitialExchangeOfferingsService;

    @PostMapping(value = "/subscribe", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String subscribeOnInitialExchangeOfferings(HttpServletRequest request, HttpServletResponse response, String email){

        return new JSONObject(){{put("message", iInitialExchangeOfferingsService.subscribeOnInitialExchangeOfferings(request, email, response));}}.toString();
    }

}
