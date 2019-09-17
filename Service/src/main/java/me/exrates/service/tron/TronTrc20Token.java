package me.exrates.service.tron;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

@Log4j2 // TODO
@Data
public class TronTrc20Token implements TronTrc20TokenService{
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

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }
}
