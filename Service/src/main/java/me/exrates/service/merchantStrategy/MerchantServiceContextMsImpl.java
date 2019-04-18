package me.exrates.service.merchantStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.impl.BitcoinServiceMsImpl;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class MerchantServiceContextMsImpl extends MerchantServiceContextImpl {
    private final RestTemplate restTemplate;
    private final InOutProperties properties;
    private final ObjectMapper mapper;

    @Override
    public IMerchantService getBitcoinServiceByMerchantName(String merchantName) {
        return new BitcoinServiceMsImpl(restTemplate, properties, mapper, merchantName);
    }
}
