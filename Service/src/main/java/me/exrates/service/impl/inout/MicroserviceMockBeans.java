package me.exrates.service.impl.inout;

import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.BitcoinService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.lang.reflect.Proxy;

@Service
@Conditional(MicroserviceConditional.class)
public class MicroserviceMockBeans {

    @Bean("bitcoinServiceImpl")
    public BitcoinService btc(){
        return (BitcoinService) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{BitcoinService.class}, (a,b,c) -> null);
    }
}
