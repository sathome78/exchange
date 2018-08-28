package me.exrates.model;

public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String val;

    Gender(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public String getName() {
        return this.name();
    }
}

