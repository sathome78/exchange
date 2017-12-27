package me.exrates.dao.impl;

import me.exrates.dao.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class ReportDaoImpl implements ReportDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
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
    public List<String> retrieveReportSubscribersList() {
        String sql = "SELECT RS.email FROM REPORT_SUBSCRIBERS RS" +
                " JOIN USER U ON U.email = RS.email " +
                " JOIN USER_ADMIN_AUTHORITY UAA ON UAA.user_id = U.id " +
                " WHERE UAA.admin_authority_id = 11 AND UAA.enabled= 1 ";
        return jdbcTemplate.queryForList(sql, String.class);
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


}
