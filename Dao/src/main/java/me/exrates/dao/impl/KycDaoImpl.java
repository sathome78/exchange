package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.KycDao;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class KycDaoImpl implements KycDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public KycDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean updateUserVerification(int userId, KycStatusResponseDto kyc) {
        String sql = "INSERT INTO USER_VERIFICATION_INFO"
                + " (user_id, last_names, first_names, born, document_code, document_type, image_encoded, details)"
                + " VALUES(:user_id, :last_names, :first_names, :born, :document_code, :document_type, :image_encoded, :details)"
                + " ON DUPLICATE KEY UPDATE last_names = :last_names, first_names = :first_names, born = :born, "
                + " document_code = :document_code, document_code = :document_code, details = :details";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("last_names", String.join(" ", kyc.getAnalysisResults().getAnalysisData().getFirstNames()));
        params.addValue("first_names", String.join(" ", kyc.getAnalysisResults().getAnalysisData().getLastNames()));
        params.addValue("born", kyc.getAnalysisResults().getAnalysisData().getDateOfBirth());
        params.addValue("document_code", kyc.getAnalysisResults().getCode());
        params.addValue("document_type", String.join(" ", kyc.getAnalysisResults().getExpectedDocTypes()));
        params.addValue("image_encoded", kyc.getAnalysisResults().getExpectedDocTypes().stream().findFirst().orElse(""));
        params.addValue("details", kyc.getErrorMsg());
        try {
            return jdbcTemplate.update(sql, params) > 0;
        } catch (DataAccessException e) {
            log.error("Failed to update USER_VERIFICATION_INFO with params ", e);
            return false;
        }
    }
}
