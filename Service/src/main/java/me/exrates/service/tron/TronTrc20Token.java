package me.exrates.service.tron;

import lombok.Data;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Data
public class TronTrc20Token {
//TODO correct names:

    private String merchantName;
    private String currencyName;

    private int merchantId;
    private int currencyId;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;

    @PostConstruct
    private void init() {
        merchantId = merchantService.findByName(merchantName).getId();
        currencyId = currencyService.findByName(currencyName).getId();
    }

    public TronTrc20Token(String merchantName, String currencyName){
        this.merchantName = merchantName;
        this.currencyName = currencyName;
    }

}
