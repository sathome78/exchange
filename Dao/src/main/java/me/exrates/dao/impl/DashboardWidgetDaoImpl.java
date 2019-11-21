package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.DashboardWidgetDao;
import me.exrates.model.dto.DashboardWidget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;

@Repository
@Log4j2
public class DashboardWidgetDaoImpl implements DashboardWidgetDao {

    private final NamedParameterJdbcOperations masterDataSource;
    private final NamedParameterJdbcOperations slaveDataSource;

    @Autowired
    public DashboardWidgetDaoImpl(@Qualifier("masterHikariDataSource") NamedParameterJdbcOperations masterDataSource,
                                  @Qualifier("slaveHikariDataSource") NamedParameterJdbcOperations slaveDataSource) {
        this.masterDataSource = masterDataSource;
        this.slaveDataSource = slaveDataSource;
    }

    @Override
    public Collection<DashboardWidget> findByUserId(int userId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USER_ID  + " = :userId";
        try {
            return slaveDataSource.query(sql, Collections.singletonMap("userId", userId), widgetRowMapper());
        } catch (DataAccessException e) {
            log.warn("Failed to find dashboard widgets for user: "  + userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean update(Collection<DashboardWidget> widgets) {
        final String sql = "REPLACE INTO " + TABLE_NAME
                + " VALUES (:userId, :type, :positionX, :positionY, :positionW, :positionH, :dragAndDrop, :resizable, :hidden)";
        SqlParameterSource [] batch = SqlParameterSourceUtils.createBatch(widgets.toArray());
        final int[] updates = masterDataSource.batchUpdate(sql, batch);
        return updates.length == widgets.size();
    }

    private RowMapper<DashboardWidget> widgetRowMapper() {
        return (rs, i) -> DashboardWidget.builder()
                .userId(rs.getInt(COL_USER_ID))
                .type(rs.getString(COL_TYPE))
                .positionX(rs.getInt(COL_POS_X))
                .positionY(rs.getInt(COL_POS_Y))
                .positionW(rs.getInt(COL_POS_W))
                .positionH(rs.getInt(COL_POS_H))
                .dragAndDrop(rs.getBoolean(COL_DRAG_DROP))
                .resizable(rs.getBoolean(COL_RESIZABLE))
                .hidden(rs.getBoolean(COL_HIDDEN))
                .build();
    }
}
