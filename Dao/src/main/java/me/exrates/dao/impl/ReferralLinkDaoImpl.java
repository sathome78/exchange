package me.exrates.dao.impl;

import me.exrates.dao.ReferralLinkDao;
import me.exrates.model.dto.referral.UserReferralLink;
import me.exrates.model.referral.ReferralLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class ReferralLinkDaoImpl implements ReferralLinkDao {

    private static final String columns = "user_id, name, link, created_at, main";

    private final NamedParameterJdbcTemplate masterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveJdbcTemplate;

    private final RowMapper<ReferralLink> referralLinkRowMapper = (rs, row) -> {
        ReferralLink referralLink = new ReferralLink();
        referralLink.setUserId(rs.getInt("user_id"));
        referralLink.setName(rs.getString("name"));
        referralLink.setLink(rs.getString("link"));
        referralLink.setCreatedAt(rs.getTimestamp("created_at"));
        referralLink.setMain(rs.getBoolean("main"));
        return referralLink;
    };

    @Autowired
    public ReferralLinkDaoImpl(NamedParameterJdbcTemplate masterJdbcTemplate,
                               NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    @Override
    public Optional<ReferralLink> findByUserIdAndLink(int userId, String link) {
        final String sql = "SELECT " + columns + " FROM REFERRAL_LINK WHERE link = :link AND user_id = :user_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("link", link);
        }};
        try {
            return Optional.of(slaveJdbcTemplate.queryForObject(sql, params, referralLinkRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReferralLink> findByLink(String link) {
        final String sql = "SELECT " + columns + " FROM REFERRAL_LINK WHERE link = :link";
        try {
            return Optional.of(slaveJdbcTemplate.queryForObject(sql,
                    Collections.singletonMap("link", link),
                    referralLinkRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ReferralLink> findByUserId(int userId) {
        final String sql = "SELECT " + columns + " FROM REFERRAL_LINK WHERE user_id = :user_id";
        return slaveJdbcTemplate.query(sql, Collections.singletonMap("user_id", userId), referralLinkRowMapper);
    }

    @Override
    public List<ReferralLink> findByListUserId(List<Integer> userIds) {
        final String sql = "SELECT " + columns + " FROM REFERRAL_LINK WHERE user_id in(:user_ids)";
        return slaveJdbcTemplate.query(sql, Collections.singletonMap("user_ids", userIds), referralLinkRowMapper);
    }

    @Override
    public List<String> findUsersLinks(List<String> links) {
        final String sql = "SELECT id FROM USER WHERE invite_referral_link in (:links)";
        return slaveJdbcTemplate.queryForList(sql, Collections.singletonMap("links", links), String.class);
    }

    @Override
    public List<UserReferralLink> findUsersByLink(String link) {
        return null;
    }

    @Override
    public boolean createReferralLink(ReferralLink referralLink) {
        final String sql = "INSERT INTO REFERRAL_LINK(" + columns + ") VALUES (:user_id, :name, :link, now(), :main)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", referralLink.getUserId());
            put("name", referralLink.getName());
            put("link", referralLink.getLink());
            put("main", referralLink.isMain());
        }};
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateReferralLink(ReferralLink referralLink) {
        final String sql = "UPDATE REFERRAL_LINK SET name = :name";
        return masterJdbcTemplate.update(sql, Collections.singletonMap("name", referralLink.getName())) > 0;
    }

    @Override
    public boolean deleteReferralLink(ReferralLink referralLink) {
        final String sql = "DELETE FROM REFERRAL_LINK WHERE user_id = :user_id AND link = :link";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", referralLink.getUserId());
            put("link", referralLink.getLink());
        }};
        return masterJdbcTemplate.update(sql, params) > 0;
    }
}
