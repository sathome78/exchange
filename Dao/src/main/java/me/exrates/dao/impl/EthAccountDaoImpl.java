package me.exrates.dao.impl;

import me.exrates.dao.EthAccountDao;
import me.exrates.model.dto.EthAccCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class EthAccountDaoImpl implements EthAccountDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_ALL = "SELECT * FROM ETH_SERVICE_ACCOUNTS WHERE active = :isActive";
    private final String SET_STATUS = "UPDATE ETH_SERVICE_ACCOUNTS SET active = :isActive WHERE id = :id";

    @Override
    public List<EthAccCredentials> loadAll(List<Boolean> isActiveStatuses) {
        return jdbcTemplate.query(SELECT_ALL, Collections.singletonMap("isActive", isActiveStatuses), (rs, rowNum) -> {
            EthAccCredentials credentials = new EthAccCredentials();
            credentials.setId(rs.getInt("id"));
            credentials.setActive(rs.getBoolean("active"));
            credentials.setPrivateKey(rs.getString("private_key"));
            credentials.setPublicKey(rs.getString("address"));
            credentials.setUrl(rs.getString("url"));
            return credentials;
        });
    }

    @Override
    public void setStatus(int id, boolean isActive) {
        jdbcTemplate.update(SET_STATUS,
                new HashMap<String, Object>(){{put("isActive", isActive);
                                               put("id", id);}});
    }
}
