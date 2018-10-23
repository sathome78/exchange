package me.exrates.dao.impl;

import me.exrates.dao.G2faDao;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class G2faDaoImpl implements G2faDao {

    private final NamedParameterJdbcTemplate npJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public G2faDaoImpl(NamedParameterJdbcTemplate npJdbcTemplate,
                       JdbcTemplate jdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getGoogleAuthSecretCodeByUser(Integer userId) {
        String sql = "SELECT secret_code FROM 2FA_GOOGLE_AUTHENTICATOR WHERE user_id = :user_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        try {
            return npJdbcTemplate.queryForObject(sql, namedParameters, String.class);
        }catch (EmptyResultDataAccessException e){
            return "";
        }
    }

    @Override
    public void setGoogleAuthSecretCode(Integer userId) {
        String sql = "UPDATE 2FA_GOOGLE_AUTHENTICATOR SET secret_code = :secret WHERE user_id = :user_id" ;
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("secret", Base32.random());
        }};
        npJdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public void set2faGoogleAuthenticator(Integer userId) {
        String sql = "INSERT INTO 2FA_GOOGLE_AUTHENTICATOR (user_id, enable, secret_code) VALUES (:user_id, false, :secret) ";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("secret", Base32.random());
        }};
        npJdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public void setEnable2faGoogleAuth(Integer userId, Boolean connection) {
        String sql = "UPDATE 2FA_GOOGLE_AUTHENTICATOR SET enable =:connection WHERE user_id = :user_id";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("connection", connection);
        }};
        npJdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public boolean isGoogleAuthenticatorEnable(Integer userId) {
        String sql = "SELECT enable FROM 2FA_GOOGLE_AUTHENTICATOR WHERE user_id = :user_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        try {
            return npJdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        }catch (EmptyResultDataAccessException e){
            return false;
        }
    }

    @Override
    public Set<Integer> getUsersWithout2faGoogleAuth() {
        String sql = "SELECT ga.user_id FROM 2FA_GOOGLE_AUTHENTICATOR ga WHERE ga.enable = FALSE";
        try {
            return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class));
        }catch (EmptyResultDataAccessException ex){
            return Collections.emptySet();
        }
    }
}
