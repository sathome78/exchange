package me.exrates;

import me.exrates.service.BitcoinService;
import me.exrates.service.ethereum.EthTokenService;
import me.exrates.service.merchantStrategy.IRefillable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CoinDispatcher {

    private final Map<String, IRefillable> stringIRefillableMap;
    private final Map<String, EthTokenService> stringEthTokenServiceMap;

    private final ApplicationContext applicationContext;

    @Autowired
    public CoinDispatcher(Map<String, IRefillable> stringIRefillableMap, Map<String, EthTokenService> stringEthTokenServiceMap, ApplicationContext applicationContext) {
        this.stringIRefillableMap = stringIRefillableMap;
        this.stringEthTokenServiceMap = stringEthTokenServiceMap;
        this.applicationContext = applicationContext;
    }

    public CoinTester getCoinTester(String merchantName){
        Object service = getService(merchantName);
        if(service instanceof BitcoinService){
            return (CoinTester) applicationContext.getBean(BtcCoinTester.class.getAnnotation(Component.class).value());
        }
        if(service instanceof EthTokenService){
            return (CoinTester) applicationContext.getBean(EthTokenTester.class.getAnnotation(Component.class).value());
        }
        return null;
    }

    public Object getService(String merchantName){
        for (Map.Entry<String, IRefillable> entry : stringIRefillableMap.entrySet()) {
            if(entry.getValue().getMerchantName().equals(merchantName)) return entry.getValue();
        }
        for (Map.Entry<String, EthTokenService> entry : stringEthTokenServiceMap.entrySet()) {
            if(entry.getValue().getMerchantName().equals(merchantName)) return entry.getValue();
        }
        return null;
    }
}
