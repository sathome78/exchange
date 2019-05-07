package me.exrates.dao;

import me.exrates.model.dto.MerchantSpecParamDto;

public interface MerchantSpecParamsDao {

    MerchantSpecParamDto getByMerchantNameAndParamName(String merchantName, String paramName);

    MerchantSpecParamDto getByMerchantIdAndParamName(int merchantId, String paramName);

    boolean updateParam(String merchantName, String paramName, String newValue);
}
