package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.DashboardDao;
import me.exrates.dao.DashboardWidgetDao;
import me.exrates.dao.OrderDao;
import me.exrates.model.dto.DashboardWidget;
import me.exrates.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;

@Service
@Log4j2
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDao dashboardDao;
    private final DashboardWidgetDao dashboardWidgetDao;
    private final OrderDao orderDao;

    @Autowired
    public DashboardServiceImpl(DashboardDao dashboardDao,
                                DashboardWidgetDao dashboardWidgetDao,
                                OrderDao orderDao) {
        this.dashboardDao = dashboardDao;
        this.dashboardWidgetDao = dashboardWidgetDao;
        this.orderDao = orderDao;
    }

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId) {
        log.info("Begin 'getBalanceByCurrency' method");
        return dashboardDao.getBalanceByCurrency(userId, currencyId);
    }

    @Override
    public Collection<DashboardWidget> findAllByUserId(int userId) {
        return dashboardWidgetDao.findByUserId(userId);
    }

    @Override
    public boolean update(int userId, Collection<DashboardWidget> widgets) {
        widgets.stream()
                .peek(w -> w.setUserId(userId));
        return dashboardWidgetDao.update(widgets);
    }
}
