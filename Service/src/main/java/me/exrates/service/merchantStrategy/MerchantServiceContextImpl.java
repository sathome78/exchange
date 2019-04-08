package me.exrates.service.merchantStrategy;

import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.MerchantNotFoundException;
import me.exrates.service.exception.MerchantServiceBeanNameNotDefinedException;
import me.exrates.service.exception.MerchantServiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Service
@Conditional(MonolitConditional.class)
public class MerchantServiceContextImpl implements MerchantServiceContext {

    @Autowired
    Map<String, IMerchantService> merchantServiceMap;

    @Autowired
    MerchantService merchantService;

    @Override
    public IMerchantService getMerchantService(String serviceBeanName) {
        if (StringUtils.isEmpty(serviceBeanName)) {
            throw new MerchantServiceBeanNameNotDefinedException("");
        }
        return Optional.ofNullable(merchantServiceMap.get(serviceBeanName))
                .orElseThrow(() -> new MerchantServiceNotFoundException(serviceBeanName));
    }

    @Override
    public IMerchantService getMerchantService(Integer merchantId) {
        Merchant merchant = Optional.ofNullable(merchantService.findById(merchantId))
                .orElseThrow(() -> new MerchantNotFoundException(String.valueOf(merchantId)));
        return getMerchantService(merchant.getServiceBeanName());
    }

    @Override
    public IMerchantService getMerchantServiceByName(String merchantName) {
        Merchant merchant = Optional.ofNullable(merchantService.findByName(merchantName))
                .orElseThrow(() -> new MerchantNotFoundException(merchantName));
        return getMerchantService(merchant.getServiceBeanName());
    }

    @Override
    public IMerchantService getBitcoinServiceByMerchantName(String merchantName) {
        return getMerchantServiceByName(merchantName);
    }
}
