package me.exrates.dao.impl;

import me.exrates.dao.KycDao;
import me.exrates.dao.WorldCheckDao;
import me.exrates.model.kyc.WorldCheck;
import me.exrates.model.kyc.WorldCheckStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WorldCheckDaoImpl implements WorldCheckDao {

    private static final Logger LOG = LogManager.getLogger(KycDao.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int save(int userId, WorldCheck worldCheck) {
        String SQL = "insert into WORLD_CHECK(user_id,world_check_status,admin) values(:user_id,:world_check_status,:admin)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("world_check_status", worldCheck.getStatus().getName());
        params.addValue("admin", worldCheck.getAdmin());
        KeyHolder kycKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(SQL, params, kycKeyHolder);
        return kycKeyHolder.getKey().intValue();
    }

    @Override
    public WorldCheck getWorldCheck(int userId) {
        String SQL = "select * from WORLD_CHECK where user_id = :user_id";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(SQL, params, (rs, row) -> {
                WorldCheck wc = new WorldCheck();
                wc.setId(rs.getInt("id"));
                wc.setUserId(rs.getInt("user_id"));
                if (rs.getString("admin") != null) {
                    wc.setAdmin(rs.getString("admin"));
                }
                wc.setStatus(Enum.valueOf(WorldCheckStatus.class, rs.getString("world_check_status")));
                return wc;
            });
        } catch (EmptyResultDataAccessException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public void setStatus(int userId, WorldCheckStatus status, String admin) {
        String SQL = "UPDATE WORLD_CHECK SET world_check_status = :world_check_status, admin = :admin WHERE user_id = :user_id";
        Map<String, Object> params = new HashMap<>();
        params.put("world_check_status", status.getName());
        params.put("admin", admin);
        params.put("user_id", userId);
        namedParameterJdbcTemplate.update(SQL, params);
    }
}
