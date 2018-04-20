package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

/**
 * Created by Valk on 27.04.2016.
 */
public enum IntervalType {
    HOUR(ChronoUnit.HOURS, 10L, true, "H"),
    DAY(ChronoUnit.DAYS, 300L, false, "D"),
    WEAK(ChronoUnit.WEEKS, 2100L, false, "W"),
    YEAR(ChronoUnit.YEARS, 86400L, false, "Y"),
    MONTH(ChronoUnit.MONTHS, 3600L, false, "M");
    
    private TemporalUnit correspondingTimeUnit;

    private Long chartRefreshInterval;

    private boolean chartLazyUpdate;

    private String shortName;

    IntervalType(TemporalUnit correspondingTimeUnit, Long chartRefreshInterval, boolean chartLazyUpdate, String shortName) {
        this.correspondingTimeUnit = correspondingTimeUnit;
        this.chartRefreshInterval = chartRefreshInterval;
        this.chartLazyUpdate = chartLazyUpdate;
        this.shortName = shortName;
    }
    
    public TemporalUnit getCorrespondingTimeUnit() {
        return correspondingTimeUnit;
    }

    public Long getChartRefreshInterval() {
        return chartRefreshInterval;
    }

    public boolean isChartLazyUpdate() {
        return chartLazyUpdate;
    }

    public String getShortName() {
        return shortName;
    }

    public static IntervalType fromShortName(String shortName) {
        return Arrays.stream(IntervalType.values()).filter(item -> item.getShortName().equals(shortName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(shortName));

    }

}
