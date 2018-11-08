package me.exrates.dao.impl;

import me.exrates.dao.ControlPhraseDao;
import me.exrates.dao.exception.ControlPhraseNotFoundException;
import me.exrates.model.ControlPhrase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ControlPhraseDaoImpl extends AbstractRepository<ControlPhrase> implements ControlPhraseDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final static String sql = "UPDATE CONTROL_PHRASE SET PHRASE = :phrase WHERE user_id = :user_id";

    @Autowired
    public ControlPhraseDaoImpl(DataSource masterHikariDataSource, @Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(masterHikariDataSource, ControlPhrase.class);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getByUserId(long userId) throws ControlPhraseNotFoundException {
        ControlPhrase phrase = new ControlPhrase();
        phrase.setId(userId);
        ControlPhrase phrase1 = super.findByParameters(phrase).orElseThrow(() -> new ControlPhraseNotFoundException(userId));
        return phrase1.getPhrase();
    }

    public void updatePharese(long userId, String phrase){
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("phrase", phrase);
            put("user_id", userId);
        }};

        jdbcTemplate.update(sql, params);
    }
}
