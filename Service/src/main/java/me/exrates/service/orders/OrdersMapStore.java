package me.exrates.service.orders;

import com.hazelcast.core.MapStore;
import me.exrates.dao.OrderDao;
import me.exrates.model.newOrders.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@Component
public class OrdersMapStore implements MapStore<Long, Order> {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private OrderDao orderDao;

    public OrdersMapStore() {

    }

    @Transactional
    @Override
    public void store(Long key, Order value) {

    }

    @Transactional
    @Override
    public void storeAll(Map<Long, Order> map) {
        for (Map.Entry<Long, Order> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());
    }

    @Override
    public void delete(Long key) {
        /*not used*/
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        /*not used*/
    }

    @Override
    public Order load(Long key) {
        return null;
    }

    @Override
    public Map<Long, Order> loadAll(Collection<Long> keys) {
        return null;
    }

    @Override
    public Iterable<Long> loadAllKeys() {
        return null;
    }
}
