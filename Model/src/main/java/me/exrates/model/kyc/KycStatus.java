package me.exrates.model.kyc;

public enum KycStatus {
    IN_PROGRESS,
    NEED_CHECK,
    NOT_VERIFIED,
    APPROVED,
    REJECTED;

    @Override
    public String toString() {
        return this.name();
    }

    public String getName() {
        return this.name();
    }
}
