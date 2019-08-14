package me.exrates.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.exrates.model.vo.BackDealInterval;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ChartResolution {

    FIVE_MINUTES(5, IntervalType.MINUTE, 5),
    FIFTEEN_MINUTES(15, IntervalType.MINUTE, 15),
    THIRTY_MINUTES(30, IntervalType.MINUTE, 30),
    ONE_HOUR(1, IntervalType.HOUR, 60),
    SIX_HOURS(6, IntervalType.HOUR, 6 * 60),
    ONE_DAY(1, IntervalType.DAY, 24 * 60);

    private int value;
    private IntervalType intervalType;
    private long minutesValue;

    public static ChartResolution convert(int value, IntervalType intervalType) {
        return Arrays.stream(ChartResolution.values())
                .filter(cr -> cr.value == value && Objects.equals(cr.intervalType, intervalType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Wrong parameters: %d, %s", value, intervalType.name())));
    }

    public static BackDealInterval ofResolution(String resolution) {
        final int minutes = getMinutes(resolution);

        ChartResolution chartResolution = Arrays.stream(ChartResolution.values())
                .filter(cr -> cr.minutesValue == minutes)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Wrong parameter: %d", minutes)));

        return new BackDealInterval(chartResolution.value, chartResolution.intervalType);
    }

    public static int getMinutes(String resolution) {
        return Objects.equals("D", resolution) ? 1440 : Integer.parseInt(resolution);
    }
}