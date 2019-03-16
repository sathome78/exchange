package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.BitcoinService;
import me.exrates.service.CryptoCurrencyBalances;
import me.exrates.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@Conditional(MonolitConditional.class)
public class CryptoCurrencyBalancesImpl implements CryptoCurrencyBalances {

    @Autowired
    Map<String, BitcoinService> bitcoinServiceMap;

    @Autowired
    MerchantService merchantService;

    public Map<Integer, String> getBalances() {

        List<Merchant> merchants = merchantService.findAll();
        Map<Integer, String> mapBalances = new HashMap<>();
        bitcoinServiceMap.entrySet().parallelStream().forEach(entry -> {
            try {
                String balance = entry.getValue().getWalletInfo().getBalance();
                if (balance != null){
                    mapBalances.put(merchants.stream().filter(m -> m.getServiceBeanName().equals(entry.getKey())).findFirst().get().getId()
                            , balance);
                }
            }catch (Exception e){
                log.error(e);
            }
        });

        return  mapBalances;
    }

}
