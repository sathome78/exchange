package me.exrates.model.enums;

public enum OrderTableEnum {

    EXORDERS,
    ORDERS,
    BOT_ORDERS;

    public static OrderTableEnum getTableNameByRole(UserRole role) {
        return role == UserRole.BOT_TRADER ? BOT_ORDERS : ORDERS;
    }

    public static OrderTableEnum getTableNameByStatusAndRole(OrderStatus orderStatus, UserRole role) {
        return OrderStatus.OPENED == orderStatus ? EXORDERS : getTableNameByRole(role);
    }
}
