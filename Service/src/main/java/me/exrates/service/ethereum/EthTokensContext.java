package me.exrates.service.ethereum;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 24.01.2018.
 */
@Log4j2
@Component
@Conditional(MonolitConditional.class)
public class EthTokensContext {

    @Autowired
    Map<String, EthTokenService> merchantServiceMap;

    Map<Integer, EthTokenService> merchantMapByCurrencies = new HashMap<>();
    Map<String, Integer> contractAddressByCurrencies = new HashMap<>();

    @PostConstruct
    private void init() {
        merchantServiceMap.forEach((k, v) -> {
            merchantMapByCurrencies.put(v.currencyId(), v);
            v.getContractAddress().forEach((address) -> {
                contractAddressByCurrencies.put(address, v.currencyId());
            });
        });
    }

    public EthTokenService getByCurrencyId(int currencyId) {
        return merchantMapByCurrencies.get(currencyId);
    }

    public boolean isContract(String contract) {
        if (contractAddressByCurrencies.get(contract) == null) {
            return false;
        } else {
            return true;
        }
    }

    public EthTokenService getByContract(String contract) {
        return getByCurrencyId(contractAddressByCurrencies.get(contract));
    }

}
