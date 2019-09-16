package me.exrates.model.dto.kyc;

import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public enum KycProvider {

    SHUFTI_PRO("SHUFTI_PRO"),
    QUBERA("QUBERA"),
    UNDEFINED("UNDEFINED");

    private String name;

    public static KycProvider of(String name) {
        return Stream.of(KycProvider.values())
                .filter(provider -> provider.name.equals(name))
                .findFirst()
                .orElse(KycProvider.UNDEFINED);
    }
}