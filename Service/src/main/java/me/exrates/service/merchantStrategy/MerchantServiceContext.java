package me.exrates.service.merchantStrategy;

/**
 * Created by ValkSam on 25.03.2017.
 */
public interface MerchantServiceContext {

  IMerchantService getMerchantService(String serviceBeanName);

  IMerchantService getMerchantService(Integer merchantId);

  IMerchantService getMerchantServiceByName(String merchantName);

  IMerchantService getBitcoinServiceByMerchantName(String merchantName);
}
