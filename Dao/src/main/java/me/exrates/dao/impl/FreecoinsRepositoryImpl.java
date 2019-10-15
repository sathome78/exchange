package me.exrates.dao.impl;

import me.exrates.dao.FreecoinsRepository;
import me.exrates.model.dto.freecoins.GiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayStatus;
import me.exrates.model.dto.freecoins.ReceiveResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class FreecoinsRepositoryImpl implements FreecoinsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public FreecoinsRepositoryImpl(@Qualifier("masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int saveClaim(GiveawayResultDto giveawayResultDto) {
        final String sql = "INSERT IGNORE INTO FREE_COINS_CLAIM (currency_name, amount, partial_amount, total_quantity, single, " +
                "time_range, creator_email, status) " +
                "VALUES (:currency_name, :amount, :partial_amount, :total_quantity, :single, :time_range, :creator_email, :status)";

        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_name", giveawayResultDto.getCurrencyName())
                .addValue("amount", giveawayResultDto.getAmount())
                .addValue("partial_amount", giveawayResultDto.getPartialAmount())
                .addValue("total_quantity", giveawayResultDto.getTotalQuantity())
                .addValue("single", giveawayResultDto.isSingle())
                .addValue("time_range", giveawayResultDto.getTimeRange())
                .addValue("creator_email", giveawayResultDto.getCreatorEmail())
                .addValue("status", giveawayResultDto.getStatus().name());

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        if (jdbcTemplate.update(sql, params, keyHolder) > 0) {
            return keyHolder.getKey().intValue();
        }
        return 0;
    }

    @Override
    public GiveawayResultDto getClaim(int giveawayId) {
        final String sql = "SELECT fcc.id, fcc.currency_name, cur.description AS currency_description, fcc.amount, " +
                "fcc.partial_amount, fcc.total_quantity, fcc.single, fcc.time_range, fcc.creator_email, fcc.created_at, fcc.status " +
                "FROM FREE_COINS_CLAIM fcc " +
                "JOIN CURRENCY cur ON cur.name = fcc.currency_name " +
                "WHERE fcc.id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", giveawayId);

        try {
            return jdbcTemplate.queryForObject(sql, params, getGiveawayResultRowMapper());
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<GiveawayResultDto> getAllCreatedClaims() {
        final String sql = "SELECT fcc.id, fcc.currency_name, cur.description AS currency_description, fcc.amount, " +
                "fcc.partial_amount, fcc.total_quantity, fcc.single, fcc.time_range, fcc.creator_email, fcc.created_at, fcc.status " +
                "FROM FREE_COINS_CLAIM fcc " +
                "JOIN CURRENCY cur ON cur.name = fcc.currency_name " +
                "WHERE fcc.status = :status AND fcc.total_quantity <> 0";

        Map<String, Object> params = new HashMap<>();
        params.put("status", GiveawayStatus.CREATED.name());

        return jdbcTemplate.query(sql, params, getGiveawayResultRowMapper());
    }

    @Override
    public boolean updateTotalQuantity(int giveawayId) {
        final String sql = "UPDATE FREE_COINS_CLAIM fcc " +
                "SET fcc.total_quantity = fcc.total_quantity - 1 " +
                "WHERE fcc.id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", giveawayId);

        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateStatus(int giveawayId, GiveawayStatus status) {
        final String sql = "UPDATE FREE_COINS_CLAIM fcc " +
                "SET fcc.status = :status " +
                "WHERE fcc.id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", giveawayId);
        params.put("status", status.name());

        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public int saveProcess(ReceiveResultDto receiveResultDto) {
        final String sql = "INSERT IGNORE INTO FREE_COINS_PROCESS (giveaway_id, receiver_email, received, last_received) " +
                "VALUES (:giveaway_id, :receiver_email, :received, :last_received)";

        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("giveaway_id", receiveResultDto.getGiveawayId())
                .addValue("receiver_email", receiveResultDto.getReceiverEmail())
                .addValue("received", receiveResultDto.isReceived())
                .addValue("last_received", Objects.nonNull(receiveResultDto.getLastReceived())
                        ? Timestamp.valueOf(receiveResultDto.getLastReceived())
                        : null);

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        if (jdbcTemplate.update(sql, params, keyHolder) > 0) {
            return keyHolder.getKey().intValue();
        }
        return 0;
    }

    @Override
    public boolean updateProcess(ReceiveResultDto receiveResultDto) {
        final String sql = "UPDATE FREE_COINS_PROCESS fcp " +
                "SET fcp.received = :received, fcp.last_received = :last_received " +
                "WHERE fcp.id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", receiveResultDto.getId());
        params.put("received", receiveResultDto.isReceived());
        params.put("last_received", Objects.nonNull(receiveResultDto.getLastReceived())
                ? Timestamp.valueOf(receiveResultDto.getLastReceived())
                : null);

        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public ReceiveResultDto getProcess(int giveawayId, String receiverEmail) {
        final String sql = "SELECT fcp.id, fcp.giveaway_id, fcp.receiver_email, fcp.received, fcp.last_received " +
                "FROM FREE_COINS_PROCESS fcp " +
                "WHERE fcp.giveaway_id = :giveaway_id AND fcp.receiver_email = :receiver_email";

        Map<String, Object> params = new HashMap<>();
        params.put("giveaway_id", giveawayId);
        params.put("receiver_email", receiverEmail);

        try {
            return jdbcTemplate.queryForObject(sql, params, getReceiveResultRowMapper());
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<ReceiveResultDto> getAllUserProcess(String receiverEmail) {
        final String sql = "SELECT fcp.id, fcp.giveaway_id, fcp.receiver_email, fcp.received, fcp.last_received " +
                "FROM FREE_COINS_PROCESS fcp " +
                "JOIN FREE_COINS_CLAIM fcc on fcc.id = fcp.giveaway_id " +
                "WHERE fcp.receiver_email = :receiver_email AND fcc.status = :status AND fcc.total_quantity <> 0";

        Map<String, Object> params = new HashMap<>();
        params.put("receiver_email", receiverEmail);
        params.put("status", GiveawayStatus.CREATED.name());

        return jdbcTemplate.query(sql, params, getReceiveResultRowMapper());
    }

    @Override
    public boolean updateStatuses() {
        final String sql = "UPDATE FREE_COINS_CLAIM fcc " +
                "SET fcc.status = :status " +
                "WHERE fcc.total_quantity = 0";

        Map<String, Object> params = new HashMap<>();
        params.put("status", GiveawayStatus.CLOSED.name());

        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public List<GiveawayResultDto> getAllClaims() {
        final String sql = "SELECT fcc.id, fcc.currency_name, cur.description AS currency_description, fcc.amount, " +
                "fcc.partial_amount, fcc.total_quantity, fcc.single, fcc.time_range, fcc.creator_email, fcc.created_at, fcc.status " +
                "FROM FREE_COINS_CLAIM fcc " +
                "JOIN CURRENCY cur ON cur.name = fcc.currency_name";

        return jdbcTemplate.query(sql, getGiveawayResultRowMapper());
    }

    @Override
    public int getUniqueAcceptorsByClaimId(int giveawayId) {
        final String sql = "SELECT COUNT(fcp.id) " +
                "FROM FREE_COINS_PROCESS fcp " +
                "WHERE fcp.giveaway_id = :giveaway_id";

        Map<String, Object> params = new HashMap<>();
        params.put("giveaway_id", giveawayId);

        try {
            return jdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception ex) {
            return 0;
        }
    }

    private RowMapper<GiveawayResultDto> getGiveawayResultRowMapper() {
        return (rs, i) -> GiveawayResultDto.builder()
                .id(rs.getInt("id"))
                .currencyName(rs.getString("currency_name"))
                .currencyDescription(rs.getString("currency_description"))
                .amount(rs.getBigDecimal("amount"))
                .partialAmount(rs.getBigDecimal("partial_amount"))
                .totalQuantity(rs.getInt("total_quantity"))
                .isSingle(rs.getBoolean("single"))
                .timeRange(rs.getInt("time_range"))
                .creatorEmail(rs.getString("creator_email"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .status(Objects.nonNull(rs.getString("status"))
                        ? GiveawayStatus.valueOf(rs.getString("status"))
                        : null)
                .build();
    }

    private RowMapper<ReceiveResultDto> getReceiveResultRowMapper() {
        return (rs, i) -> ReceiveResultDto.builder()
                .id(rs.getInt("id"))
                .giveawayId(rs.getInt("giveaway_id"))
                .receiverEmail(rs.getString("receiver_email"))
                .received(rs.getBoolean("received"))
                .lastReceived(rs.getTimestamp("last_received").toLocalDateTime())
                .build();
    }
}