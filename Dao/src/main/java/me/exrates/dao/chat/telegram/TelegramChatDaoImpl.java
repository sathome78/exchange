package me.exrates.dao.chat.telegram;

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
import java.util.*;

import static java.util.Collections.singletonMap;

@Repository
public class TelegramChatDaoImpl implements TelegramChatDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TelegramChatDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean saveChatMessage(ChatLang lang, ChatHistoryDto message){
        final String sql = "INSERT INTO TELEGRAM_CHAT_" + lang.val + "(chat_id, username, text, message_time, username_reply, text_reply) " +
                "VALUES (:chatId, :email, :body, :messageTime, :messageReplyUsername, :messageReplyText)";
        MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("chatId", message.getChatId());
            params.addValue("email", message.getEmail());
            params.addValue("body", message.getBody());
            params.addValue("messageTime", message.getMessageTime());
            params.addValue("messageReplyUsername", message.getMessageReplyUsername());
            params.addValue("messageReplyText", message.getMessageReplyText());

        return jdbcTemplate.update(sql, params) > 0;
    }

    public List<ChatHistoryDto> getChatHistoryQuick(ChatLang chatLang) {
        final String sql = "SELECT username, text, message_time, username_reply, text_reply " +
                "FROM TELEGRAM_CHAT_" + chatLang.val + " ORDER BY message_time DESC LIMIT 200";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper<ChatHistoryDto> getRowMapper() {
        return (rs, rowNum) -> {
            ChatHistoryDto dto = new ChatHistoryDto();
                dto.setEmail(rs.getString("email"));
                dto.setBody(rs.getString("body"));
                dto.setMessageTime(getMessageTime(rs));
                dto.setWhen(rs.getTimestamp("message_time").toLocalDateTime());
                dto.setMessageReplyUsername(rs.getString("username_reply"));
                dto.setMessageReplyText("text_reply");
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
