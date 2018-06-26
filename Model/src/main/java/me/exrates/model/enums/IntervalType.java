package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * Created by Valk on 27.04.2016.
 */
public enum IntervalType {
    HOUR(ChronoUnit.HOURS),
    DAY(ChronoUnit.DAYS),
    YEAR(ChronoUnit.YEARS),
    MONTH(ChronoUnit.MONTHS);
    
    private TemporalUnit correspondingTimeUnit;

    IntervalType(TemporalUnit correspondingTimeUnit) {
        this.correspondingTimeUnit = correspondingTimeUnit;
    }
    
    public TemporalUnit getCorrespondingTimeUnit() {
        return correspondingTimeUnit;
    }

    public static IntervalType convert(String str) {
        switch (str) {
            case "HOUR":
                return HOUR;
            case "DAY":
                return DAY;
            case "MONTH":
                return MONTH;
            case "YEAR":
                return YEAR;
            default:
                throw new UnsupportedIntervalTypeException(str);
        }
    }
}
