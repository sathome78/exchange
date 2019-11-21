CREATE TABLE IF NOT EXISTS DASHBOARD_WIDGET (
    user_id INTEGER NOT NULL,
    widget_name VARCHAR(255) NOT NULL,
    position_x INTEGER DEFAULT 0,
    position_y INTEGER DEFAULT 0,
    position_w INTEGER DEFAULT 0,
    position_h INTEGER DEFAULT 0,
    drag_drop tinyint default 0,
    resizable tinyint default 0,
    hidden tinyint default 0,
    FOREIGN KEY (user_id) REFERENCES USER (id) ON DELETE CASCADE,
    UNIQUE (user_id, widget_name)
);
