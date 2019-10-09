package me.exrates.model.enums;

import java.util.Arrays;

public enum MerchantKycToggleField {

    WITHDRAW("kyc_withdraw"),
    REFILL("kyc_refill");

    private String fieldName;

    MerchantKycToggleField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static MerchantKycToggleField convert(String type) {
        return Arrays.stream(MerchantKycToggleField.values())
                .filter(val -> val.fieldName.equals(type))
                .findAny().orElseThrow(() -> new RuntimeException(type));
    }
}
