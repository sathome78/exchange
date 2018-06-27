package me.exrates.dao;

import me.exrates.model.dto.MerchantSpecParamDto;

/**
 * Created by maks on 09.06.2017.
 */
public interface MerchantSpecParamsDao {

    MerchantSpecParamDto getByMerchantNameAndParamName(String merchantName, String paramName);

    MerchantSpecParamDto getByMerchantIdAndParamName(int merchantId, String paramName);

    boolean updateParam(String merchantName, String paramName, String newValue);
}
