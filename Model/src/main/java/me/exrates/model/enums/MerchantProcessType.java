package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedProcessTypeException;

import java.util.Arrays;
import java.util.List;

public enum MerchantProcessType {
  MERCHANT, CRYPTO, INVOICE, TRANSFER;
  
  public static MerchantProcessType convert(String type) {
    return Arrays.stream(MerchantProcessType.values())
            .filter(val -> val.name().equals(type))
            .findAny().orElseThrow(() -> new UnsupportedProcessTypeException(type));
  }

  @Override
  public String toString() {
    return this.name();
  }

  public static List<MerchantProcessType> getAllCoinsTypes() {
    return Arrays.asList(MerchantProcessType.MERCHANT, MerchantProcessType.INVOICE, MerchantProcessType.CRYPTO);
  }
}
