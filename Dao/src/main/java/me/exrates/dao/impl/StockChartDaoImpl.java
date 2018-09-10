package me.exrates.dao.impl;

import me.exrates.dao.StockChartDao;
import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.enums.ChartResolutionTimeUnit;
import me.exrates.model.enums.IntervalType2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockChartDaoImpl implements StockChartDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ChartTimeFrame> getChartTimeFrames() {
        String sql = "SELECT CTF.time_unit AS chart_time_unit, CTF.time_value AS chart_time_value," +
                "CR.time_unit AS res_time_unit, CR.time_value AS res_time_value FROM CHART_TIME_FRAME CTF " +
                " JOIN CHART_RESOLUTION CR ON CTF.resolution_id = CR.id";
        return jdbcTemplate.query(sql, (rs, row) -> {
            ChartResolution resolution = new ChartResolution(rs.getInt("res_time_value"),
                    ChartResolutionTimeUnit.valueOf(rs.getString("res_time_unit")));
            return new ChartTimeFrame(resolution, rs.getInt("chart_time_value"),
                    IntervalType2.valueOf(rs.getString("chart_time_unit")) );
        });
    }




}
