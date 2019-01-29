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
import org.telegram.telegrambots.api.objects.Message;

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
        final String sql = "INSERT INTO TELEGRAM_CHAT_" + lang.val +
                "(message_id, chat_id, telegram_user_id, username, text, message_time, telegram_user_reply_id, message_reply_id, username_reply, text_reply) " +
                "VALUES (:messageId, :chatId, :telegramUserId, :email, :body, :messageTime, :telegramUserReplyId, :messageReplyId, :messageReplyUsername, :messageReplyText)";
        MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("messageId", message.getMessageId());
            params.addValue("chatId", message.getChatId());
            params.addValue("telegramUserId", message.getTelegramUserId());
            params.addValue("email", message.getEmail());
            params.addValue("body", message.getBody());
            params.addValue("messageTime", message.getMessageTime());
            params.addValue("telegramUserReplyId", message.getTelegramUserReplyId());
            params.addValue("messageReplyId", message.getMessageReplyId());
            params.addValue("messageReplyUsername", message.getMessageReplyUsername());
            params.addValue("messageReplyText", message.getMessageReplyText());

        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean updateChatMessage(ChatLang lang, ChatHistoryDto message){
        final String sqlUpdateEditedMessage = "UPDATE TELEGRAM_CHAT_" + lang.val + " SET text = :body " +
                "WHERE chat_id = :chatId AND message_id = :messageId";

        final String sqlUpdateTextInReplyMessage = "UPDATE TELEGRAM_CHAT_" + lang.val + " SET text_reply = :body " +
                "WHERE chat_id = :chatId AND message_reply_id = :messageId";

        MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("messageId", message.getMessageId());
            params.addValue("chatId", message.getChatId());
            params.addValue("body", message.getBody());

        jdbcTemplate.update(sqlUpdateTextInReplyMessage, params);

        return jdbcTemplate.update(sqlUpdateEditedMessage, params) > 0;
    }

    public List<ChatHistoryDto> getChatHistoryQuick(ChatLang chatLang) {
        final String sql = "SELECT * FROM (SELECT id, username, text, message_time,  username_reply, text_reply " +
                "FROM TELEGRAM_CHAT_" + chatLang.val + " ORDER BY message_time DESC LIMIT 200) chat ORDER BY id ASC";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper<ChatHistoryDto> getRowMapper() {
        return (rs, rowNum) -> {
            ChatHistoryDto dto = new ChatHistoryDto();
                dto.setEmail(rs.getString("username"));
                dto.setBody(rs.getString("text"));
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
                .map(ts -> ts.toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME))
                .orElse(" ");
    }
}
