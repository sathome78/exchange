package me.exrates.model.enums;

import java.util.Arrays;

public enum  RefActionType {
    init, search, toggle;

    public static RefActionType convert(String name) {
        return Arrays.stream(RefActionType.values()).filter(ot -> ot.name().equals(name)).findAny()
                .orElseThrow(RuntimeException::new);
    }
}
