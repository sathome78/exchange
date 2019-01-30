package me.exrates.service.monero;

import lombok.EqualsAndHashCode;
import me.exrates.service.monero.utils.MoneroUtils;

@EqualsAndHashCode
public class MoneroAddress {
    private String standardAddress;

    public MoneroAddress(String standardAddress) {
        MoneroUtils.validateStandardAddress(standardAddress);
        this.standardAddress = standardAddress;
    }

    public String getStandardAddress() {
        return this.standardAddress;
    }

    public String toString() {
        return this.standardAddress;
    }

}
