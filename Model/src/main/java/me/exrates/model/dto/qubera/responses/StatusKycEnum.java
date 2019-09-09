package me.exrates.model.dto.qubera.responses;

import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.exceptions.KycException;

import java.util.Arrays;

public enum StatusKycEnum {
    OK("OK"),
    NONE("NONE"),
    ERROR("ERROR"),
    WARN("WARN");

    private String status;

    StatusKycEnum(String status) {
        this.status = status;
    }

    public static StatusKycEnum of(String name) {
        return Arrays.stream(StatusKycEnum.values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new KycException(ErrorApiTitles.QUBERA_UNKNOWN_KYC_STATUS));
    }
}
