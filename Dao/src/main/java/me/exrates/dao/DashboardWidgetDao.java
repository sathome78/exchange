package me.exrates.dao;

import me.exrates.model.dto.DashboardWidget;

import java.util.Collection;

public interface DashboardWidgetDao {

    String TABLE_NAME = "DASHBOARD_WIDGET";
    String COL_USER_ID = "user_id";
    String COL_TYPE = "widget_name";
    String COL_POS_X = "position_x";
    String COL_POS_Y = "position_y";
    String COL_POS_W = "position_w";
    String COL_POS_H = "position_h";
    String COL_DRAG_DROP = "drag_drop";
    String COL_RESIZABLE = "resizable";
    String COL_HIDDEN = "hidden";

    Collection<DashboardWidget> findByUserId(int userId);

    boolean update(Collection<DashboardWidget> widgets);
}
