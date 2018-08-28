package me.exrates.model.kyc;

public enum  KycType {
    INDIVIDUAL,
    LEGAL_ENTITY;

    @Override
    public String toString() {
        return this.name();
    }

    public String getName() {
        return this.name();
    }
}
