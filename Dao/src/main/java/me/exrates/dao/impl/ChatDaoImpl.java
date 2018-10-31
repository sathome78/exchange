package me.exrates.dao.impl;

import me.exrates.dao.ChatDao;
import me.exrates.model.ChatMessage;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singletonMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class ChatDaoImpl implements ChatDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ChatDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ChatMessage> findLastMessages(final ChatLang lang, final int messageCount) {
        final String sql = "SELECT c.id, c.user_id, c.body, c.message_time, USER.nickname FROM CHAT_" + lang.val +
                " as c INNER JOIN USER ON c.user_id = USER.id ORDER BY c.id DESC LIMIT :limit";
        return jdbcTemplate.query(sql, singletonMap("limit", messageCount), (resultSet, i) -> {
            final ChatMessage message = new ChatMessage();
            message.setId(resultSet.getLong("id"));
            message.setNickname(resultSet.getString("nickname"));
            message.setUserId(resultSet.getInt("user_id"));
            message.setBody(resultSet.getString("body"));
            message.setTime(resultSet.getTimestamp("message_time").toLocalDateTime());
            return message;
        });
    }

    @Override
    public void persist(final ChatLang lang, final Set<ChatMessage> message) {
        final String sql = "INSERT INTO CHAT_" + lang.val + "(id, user_id, body, message_time) VALUES (:id, :userId, :body, :time) " +
                " ON DUPLICATE KEY UPDATE body = :body";
        final SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(message.toArray());
        jdbcTemplate.batchUpdate(sql, batch);
    }

    @Override
    public ChatMessage persistPublic(ChatLang lang, ChatMessage message) {
        final String sql = "INSERT INTO PUBLIC_CHAT_" + lang.val + "(nickname, body, message_time) " +
                "VALUES (:email, :body, :messageTime) " +
                " ON DUPLICATE KEY UPDATE body = :body, nickname = :email, message_time = :messageTime";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", message.getNickname());
        params.addValue("body", message.getBody());
        params.addValue("messageTime", message.getTime());
        jdbcTemplate.update(sql, params, keyHolder);
        message.setId(keyHolder.getKey().longValue());
        return message;
    }

    @Override
    public void delete(final ChatLang lang, final ChatMessage message) {

        final String sql = "DELETE FROM CHAT_" + lang.val + " WHERE id = :id";
        Map<String, Long> namedParameters = new HashMap<>();
        namedParameters.put("id", message.getId());
        jdbcTemplate.update(sql, namedParameters);

    }

    @Override
    public List<ChatHistoryDto> getPublicChatHistory(ChatLang chatLang) {
        final String sql = "SELECT c.nickname as email, c.body, c.message_time FROM PUBLIC_CHAT_" + chatLang.val +
                " as c ORDER BY c.message_time DESC LIMIT 50";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    @Override
    public List<ChatHistoryDto> getChatHistory(ChatLang chatLang) {
        final String sql = "SELECT c.id, c.body, c.message_time, USER.email FROM CHAT_" + chatLang.val +
                " as c INNER JOIN USER ON c.user_id = USER.id ORDER BY c.message_time ASC";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    @Override
    public List<ChatHistoryDto> getChatHistoryQuick(ChatLang chatLang) {
        final String sql = "SELECT c.id, c.body, c.message_time, USER.email FROM CHAT_" + chatLang.val +
                " as c ORDER BY c.message_time ASC LIMIT 50";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper<ChatHistoryDto> getRowMapper() {
        return (rs, rowNum) -> {
            ChatHistoryDto dto = new ChatHistoryDto();
            dto.setBody(rs.getString("body"));
            dto.setEmail(rs.getString("email"));
            dto.setMessageTime(getMessageTime(rs));
            dto.setWhen(rs.getTimestamp("message_time").toLocalDateTime());
            return dto;
        };
    }

    private String getMessageTime(ResultSet resultSet) throws SQLException {
        Optional<Timestamp> timestamp = Optional.ofNullable(resultSet.getTimestamp("message_time"));
        return timestamp
                .map(ts -> ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(" ");
    }
}
