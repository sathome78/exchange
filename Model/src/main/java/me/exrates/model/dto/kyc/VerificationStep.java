package me.exrates.model.dto.kyc;

import lombok.Getter;

import java.util.stream.Stream;

public enum VerificationStep {

    NOT_VERIFIED(0),
    VERIFIED_STEP_1(1),
    VERIFIED_STEP_2(2);

    @Getter
    private int step;

    VerificationStep(int step) {
        this.step = step;
    }

    public static VerificationStep of(int step) {
        return Stream.of(VerificationStep.values())
                .filter(verificationStep -> verificationStep.step == step)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Step %d has not found", step)));
    }
}