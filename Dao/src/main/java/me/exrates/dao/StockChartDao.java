package me.exrates.dao;

import me.exrates.model.chart.ChartTimeFrame;

import java.util.List;

@Deprecated
public interface StockChartDao {
    List<ChartTimeFrame> getChartTimeFrames();
}
