package me.exrates.dao.impl;

import me.exrates.dao.ControlPhraseDao;
import me.exrates.model.ControlPhrase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ControlPhraseDaoImpl implements ControlPhraseDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final static String sql = "UPDATE CONTROL_PHRASE SET PHRASE = :phrase WHERE user_id = :user_id";

    @Autowired
    public ControlPhraseDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getByUserId(long userId) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
        }};
        return jdbcTemplate.queryForObject(sql, params, ControlPhrase.class).getPhrase();
    }

    public void updatePharese(long userId, String phrase){
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("phrase", phrase);
            put("user_id", userId);
        }};

        jdbcTemplate.update(sql, params);
    }

}
