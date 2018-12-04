package me.exrates.model.enums;

import java.util.Arrays;
import java.util.List;

public enum CurrencyType {

    CRYPTO(Arrays.asList(MerchantProcessType.CRYPTO)), FIAT(Arrays.asList(MerchantProcessType.INVOICE, MerchantProcessType.MERCHANT));

    private List<MerchantProcessType> merchantProcessTypeList;

    CurrencyType(List<MerchantProcessType> merchantProcessTypeList) {
        this.merchantProcessTypeList = merchantProcessTypeList;
    }

    public List<MerchantProcessType> getMerchantProcessTypeList() {
        return merchantProcessTypeList;
    }
}
