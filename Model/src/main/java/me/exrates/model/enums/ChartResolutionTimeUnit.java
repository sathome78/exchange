package me.exrates.model.enums;

import java.util.Arrays;

public enum ChartResolutionTimeUnit {
    MINUTE("", 60), DAY("D", 86400),WEAK("W", 604800);

   private String shortName;

   private Integer secondsValue;

    ChartResolutionTimeUnit(String shortName, Integer secondsValue) {
        this.shortName = shortName;
        this.secondsValue = secondsValue;
    }

    public Integer getSecondsValue() {
        return secondsValue;
    }

    public String getShortName() {
        return shortName;
    }

    public static ChartResolutionTimeUnit fromShortName(String shortName) {
        return Arrays.stream(ChartResolutionTimeUnit.values()).filter(item -> item.getShortName().equals(shortName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(shortName));
    }
}
