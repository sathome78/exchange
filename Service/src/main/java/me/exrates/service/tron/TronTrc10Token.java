package me.exrates.service.tron;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Getter@Setter
@EqualsAndHashCode@ToString
public class TronTrc10Token {

    private String currencyName;
    private String merchantName;
    private int decimals;
    private String nameDescription; /*shows in token description*/
    private String nameTx; /*shows in transaction data*/
    private int merchantId;
    private int currencyId;
    private String blockchainName;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;

    @PostConstruct
    private void init() {
        merchantId = merchantService.findByName(merchantName).getId();
        currencyId = currencyService.findByName(currencyName).getId();
    }

    public TronTrc10Token(String currencyName, String merchantName, int decimals, String nameDescription, String nameTx, String blockchainName) {
        this.currencyName = currencyName;
        this.merchantName = merchantName;
        this.decimals = decimals;
        this.nameDescription = nameDescription;
        this.nameTx = nameTx;
        this.blockchainName = blockchainName;
    }
}
