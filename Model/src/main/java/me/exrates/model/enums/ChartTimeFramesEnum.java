package me.exrates.model.enums;

import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;

import java.util.Arrays;

import static me.exrates.model.enums.IntervalType.*;


public enum ChartTimeFramesEnum {
//    HOUR_2(new ChartTimeFrame(new ChartResolution(1, ChartResolutionTimeUnit.MINUTE), 2, HOUR)),
//    HOUR_12(new ChartTimeFrame(new ChartResolution(10, ChartResolutionTimeUnit.MINUTE), 12, HOUR)),
//    DAY_1(new ChartTimeFrame(new ChartResolution(30, ChartResolutionTimeUnit.MINUTE), 4, DAY)),
//    DAY_3(new ChartTimeFrame(new ChartResolution(60, ChartResolutionTimeUnit.MINUTE), 6, DAY)),
//    DAY_7(new ChartTimeFrame(new ChartResolution(240, ChartResolutionTimeUnit.MINUTE), 7, DAY)),
    DAY_5(new ChartTimeFrame(new ChartResolution(720, ChartResolutionTimeUnit.MINUTE), 5, DAY)),
    MONTH_1(new ChartTimeFrame(new ChartResolution(1, ChartResolutionTimeUnit.DAY), 1, MONTH));



    private ChartTimeFrame timeFrame;

    ChartTimeFramesEnum(ChartTimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public ChartTimeFrame getTimeFrame() {
        return timeFrame;
    }

    public ChartResolution getResolution() {
        return timeFrame.getResolution();
    }

    public static ChartTimeFramesEnum ofResolution(String resolutionString) {
        ChartResolution resolution = ChartResolution.ofString(resolutionString);
        return Arrays.stream(ChartTimeFramesEnum.values()).filter(item -> item.getResolution().equals(resolution))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(resolutionString));
    }
}
