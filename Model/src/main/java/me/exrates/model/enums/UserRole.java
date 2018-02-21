package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedUserRoleIdException;

import java.util.Arrays;

public enum UserRole {

  ADMINISTRATOR(1),
  ACCOUNTANT(2),
  ADMIN_USER(3),
  USER(4),
  ROLE_CHANGE_PASSWORD(5),
  EXCHANGE(6),
  VIP_USER(7),
  TRADER(8),
  FIN_OPERATOR(9),
  BOT_TRADER(10, false);

  private final int role;

  private final boolean showExtendedOrderInfo;

  UserRole(int role, boolean showExtendedOrderInfo) {
    this.role = role;
    this.showExtendedOrderInfo = showExtendedOrderInfo;
  }

  UserRole(int role) {
    this(role, true);
  }

  public int getRole() {
    return role;
  }

  public boolean showExtendedOrderInfo() {
    return showExtendedOrderInfo;
  }

  public static UserRole convert(int id) {
    return Arrays.stream(UserRole.class.getEnumConstants())
        .filter(e -> e.role == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedUserRoleIdException(String.valueOf(id)));
  }

  @Override
  public String toString() {
    return this.name();
  }
}