package me.exrates.model.dto.kyc;

import lombok.Getter;

import java.util.stream.Stream;

public enum VerificationStep {

    NOT_VERIFIED(0),
    LEVEL_ONE(1),
    LEVEL_TWO(2);

    @Getter
    private int step;

    VerificationStep(int step) {
        this.step = step;
    }

    public static VerificationStep of(int step) {
        return Stream.of(VerificationStep.values())
                .filter(verificationStep -> verificationStep.step == step)
                .findFirst()
                .orElse(VerificationStep.NOT_VERIFIED);
    }
}