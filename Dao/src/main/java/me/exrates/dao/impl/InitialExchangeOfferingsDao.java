package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IInitialExchangeOfferings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Repository
public class InitialExchangeOfferingsDao implements IInitialExchangeOfferings {

    private final static String INSERT_EMAIL_TO_INITIAL_EXCHANGE_OFFERINGS = "INSERT INTO USER_INITIAL_EXCHANGE_OFFERINGS (email) VALUES (:email)";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean subscribeOnInitialExchangeOfferings(String email){
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        try {
            return namedParameterJdbcTemplate.update(INSERT_EMAIL_TO_INITIAL_EXCHANGE_OFFERINGS, params) > 0;
        }catch (Exception ex){
            log.error(ex);
            return false;
        }
    }

}
