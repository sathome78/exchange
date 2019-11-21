package me.exrates.service;

import me.exrates.model.dto.DashboardWidget;

import java.math.BigDecimal;
import java.util.Collection;


public interface DashboardService {

    BigDecimal getBalanceByCurrency(int userId, int currencyId);

    Collection<DashboardWidget> findAllByUserId(int userId);

    boolean update(int userId, Collection<DashboardWidget> widgets);

}
