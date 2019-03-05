package me.exrates.model.enums;


import java.util.Arrays;

public enum PrecissionsEnum {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    PrecissionsEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public static PrecissionsEnum convert(int value) {
        return Arrays.stream(PrecissionsEnum.values()).filter(ot -> ot.value == value).findAny()
                .orElseThrow(RuntimeException::new);
    }
}
