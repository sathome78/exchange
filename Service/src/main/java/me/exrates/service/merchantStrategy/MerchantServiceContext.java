package me.exrates.service.merchantStrategy;

public interface MerchantServiceContext {

  IMerchantService getMerchantService(String serviceBeanName);

  IMerchantService getMerchantService(Integer merchantId);

  IMerchantService getMerchantServiceByName(String merchantName);

  IMerchantService getBitcoinServiceByMerchantName(String merchantName);
}
