package me.exrates.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Collections.singletonMap;

/**
 * Created by Yuriy Berezin on 16-Oct-18.
 */
@Component
public class WithdrawUtils {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public boolean isValidDestinationAddress(String address) {
        return jdbcTemplate.queryForList("SELECT * FROM REFILL_REQUEST_ADDRESS where address = :address",
                singletonMap("address", address)).isEmpty();
    }

    public boolean isValidDestinationAddress(String localProperty, String address) {

        return !localProperty.equalsIgnoreCase(address);
    }
}
