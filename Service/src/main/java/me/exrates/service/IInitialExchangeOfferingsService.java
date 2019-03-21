package me.exrates.service;

import me.exrates.dao.IInitialExchangeOfferings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class IInitialExchangeOfferingsService {

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IInitialExchangeOfferings iInitialExchangeOfferings;

    public String subscribeOnInitialExchangeOfferings(HttpServletRequest request, String email, HttpServletResponse response){

        String messageForUser;
        if(iInitialExchangeOfferings.subscribeOnInitialExchangeOfferings(email)){
            messageForUser = messageSource.getMessage("ieo.message.success", null, localeResolver.resolveLocale(request));
            response.setStatus(200);
        }else{
            messageForUser = messageSource.getMessage("ieo.message.email.subscribed", null, localeResolver.resolveLocale(request));
            response.setStatus(500);
        }

        return messageForUser;
    }
}
