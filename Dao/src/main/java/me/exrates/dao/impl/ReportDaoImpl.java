package me.exrates.dao.impl;

import me.exrates.dao.ReportDao;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.enums.AdminAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReportDaoImpl implements ReportDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public String retrieveReportMailingTime() {
        String sql = "SELECT param_value FROM REPORT_MAILING_PARAMS WHERE param_name = 'MAIL_TIME' ";
        return jdbcTemplate.queryForObject(sql, String.class);
    }


    @Override
    public void updateReportMailingTime(String newMailTime) {
        String sql = "UPDATE REPORT_MAILING_PARAMS SET param_value = :mail_time WHERE param_name = 'MAIL_TIME' ";
        namedParameterJdbcTemplate.update(sql, Collections.singletonMap("mail_time", newMailTime));
    }

    @Override
    public boolean isReportMailingEnabled() {
        String sql = "SELECT param_value FROM REPORT_MAILING_PARAMS WHERE param_name = 'ENABLE_REPORT_MAILING' ";
        return Boolean.parseBoolean(jdbcTemplate.queryForObject(sql, String.class));
    }

    @Override
    public void updateReportMailingEnableStatus(boolean newStatus) {
        String sql = "UPDATE REPORT_MAILING_PARAMS SET param_value = :enable_status WHERE param_name = 'ENABLE_REPORT_MAILING' ";
        namedParameterJdbcTemplate.update(sql, Collections.singletonMap("enable_status", String.valueOf(newStatus)));
    }


    @Override
    public List<String> retrieveReportSubscribersList(boolean selectWithPremissions) {
        final String premissionsClause = String.join(" ", " JOIN USER U ON U.email = RS.email ",
                " JOIN USER_ADMIN_AUTHORITY UAA ON UAA.user_id = U.id ",
                " WHERE UAA.admin_authority_id = ? AND UAA.enabled = TRUE ");
        String sql = "SELECT RS.email FROM REPORT_SUBSCRIBERS RS ";
        Object[] params = null;
        if (selectWithPremissions) {
            sql = sql.concat(premissionsClause);
            params = new Object[]{AdminAuthority.SEE_REPORTS.getAuthority()};
        }
        return jdbcTemplate.queryForList(sql, params, String.class);
    }

    @Override
    public void addReportSubscriber(String email) {
        String sql = "INSERT INTO REPORT_SUBSCRIBERS (email) VALUES (:email)";
        namedParameterJdbcTemplate.update(sql, Collections.singletonMap("email", email));
    }

    @Override
    public void deleteReportSubscriber(String email) {
        String sql = "DELETE FROM REPORT_SUBSCRIBERS WHERE email = :email";
        namedParameterJdbcTemplate.update(sql, Collections.singletonMap("email", email));
    }

    @Override
    public List<ReportDto> getBalancesReportsNames(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = "SELECT br.id, br.file_name" +
                " FROM BALANCES_REPORT br" +
                " WHERE br.created_at BETWEEN :from_date AND :to_date";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("from_date", Timestamp.valueOf(fromDate));
                put("to_date", Timestamp.valueOf(toDate));
            }
        };

        try {
            return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> ReportDto.builder()
                    .id(rs.getInt("id"))
                    .fileName(rs.getString("file_name"))
                    .build());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public ReportDto getBalancesReportById(int id) {
        String sql = "SELECT br.file_name, br.content, br.created_at" +
                " FROM BALANCES_REPORT br" +
                " WHERE br.id = :id";

        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), (rs, row) -> ReportDto.builder()
                .fileName(rs.getString("file_name"))
                .content(rs.getBytes("content"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build());
    }

    @Override
    public ReportDto getBalancesReportByTime(LocalDateTime fromTime, LocalDateTime toTime) {
        String sql = "SELECT br.content," +
                "br.created_at" +
                " FROM BALANCES_REPORT br" +
                " WHERE br.created_at BETWEEN :from_time AND :to_time";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("from_time", Timestamp.valueOf(fromTime));
                put("to_time", Timestamp.valueOf(toTime));
            }
        };

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, row) -> ReportDto.builder()
                    .content(rs.getBytes("content"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<ReportDto> getInOutReportsNames(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = "SELECT ior.id, ior.file_name" +
                " FROM INPUT_OUTPUT_REPORT ior" +
                " WHERE ior.created_at BETWEEN :from_date AND :to_date";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("from_date", Timestamp.valueOf(fromDate));
                put("to_date", Timestamp.valueOf(toDate));
            }
        };

        try {
            return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> ReportDto.builder()
                    .id(rs.getInt("id"))
                    .fileName(rs.getString("file_name"))
                    .build());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public ReportDto getInOutReportById(Integer id) {
        String sql = "SELECT ior.file_name, ior.content, ior.created_at" +
                " FROM INPUT_OUTPUT_REPORT ior" +
                " WHERE ior.id = :id";

        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), (rs, row) -> ReportDto.builder()
                .fileName(rs.getString("file_name"))
                .content(rs.getBytes("content"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build());
    }
}
