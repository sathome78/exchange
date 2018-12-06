package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedTransferProcessTypeException;

import java.util.Arrays;

public enum TransferTypeVoucher {
    VOUCHER, INNER_VOUCHER, TRANSFER;

    public static TransferTypeVoucher convert(String name) {
        return Arrays.stream(TransferTypeVoucher.class.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new UnsupportedTransferProcessTypeException("Error transfer type - " + name));
    }
}
