package me.exrates.dao.impl;

import me.exrates.dao.ControlPhraseDao;
import me.exrates.dao.exception.PhraseNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ControlPhraseDaoImpl implements ControlPhraseDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final static String updateSql = "UPDATE CONTROL_PHRASE SET PHRASE = :phrase WHERE user_id = :user_id";
    private final static String selectSql = "SELECT phrase FROM CONTROL_PHRASE WHERE user_id = :user_id";

    @Autowired
    public ControlPhraseDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Cacheable(cacheNames = "phrase", key = "#userId")
    public String getByUserId(long userId) {
        System.out.println("cache get");
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
        }};
        try {
            return jdbcTemplate.queryForObject(selectSql, params, String.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @CacheEvict(cacheNames = "phrase", key = "#userId")
    @CachePut(cacheNames = "phrase", key = "#userId", unless = "#phrase == null || #phrase.length() == 0 || #phrase.trim().length() == 0 || #phrase.length() > 20")
    public void updatePhrase(long userId, String phrase) throws PhraseNotAllowedException {
        if (phrase == null || phrase.length() == 0 || phrase.trim().length() == 0 || phrase.length() > 20)
            throw new PhraseNotAllowedException();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("phrase", phrase);
            put("user_id", userId);
        }};

        if(jdbcTemplate.update(updateSql, params) == 0) addPhrase(userId, phrase);
    }

    @CachePut(cacheNames = "phrase", key = "#userId", unless = "#phrase == null || #phrase.length() == 0 || #phrase.trim().length() == 0 || #phrase.length() > 20")
    @Override
    public void addPhrase(long userId, String phrase) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("phrase", phrase);
        }};

        jdbcTemplate.update("INSERT INTO CONTROL_PHRASE(user_id, phrase) values(:user_id, :phrase)", params);
    }

}
