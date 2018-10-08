package me.exrates.dao.impl;

import me.exrates.dao.UserVerificationDao;
import me.exrates.model.dto.UserVerificationDto;
import me.exrates.model.enums.VerificationDocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

@Repository
public class UserVerificationDaoImpl implements UserVerificationDao {

    private static final String TABLE_NAME = "USER_VERIFICATION_INFO";
    private static String USER_ID_COL = "user_id";
    private static String DOC_TYPE_COL = "document_type";
    private static String FIRST_NAME_COL = "first_name";
    private static String LAST_NAME_COL = "last_name";
    private static String BORN_COL = "born";
    private static String RES_ADDR_COL = "residential_address";
    private static String POST_CODE_COL = "postal_code";
    private static String COUNTRY_COL = "country";
    private static String CITY_COL = "city";
    private static String DOC_PATH_COL = "path";

    private static final String USER_ID_KEY = "userId";

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Override
    public UserVerificationDto save(UserVerificationDto verificationDto) {
        String sql = "INSERT INTO " + TABLE_NAME + " (" + getInsertColumns() + ")"
                + " VALUES (:userId, :docType, :firstName, :lastName, :born, :resAddr, :postCode, :country, :city, :path)"
                + " ON DUPLICATE KEY UPDATE " + getUpdateColumns();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, verificationDto.getUserId())
                .addValue("docType", verificationDto.getDocumentType(), Types.VARCHAR)
                .addValue("firstName", verificationDto.getFirstName())
                .addValue("lastName", verificationDto.getLastName())
                .addValue("born", getDBDate(verificationDto.getBorn()), Types.DATE)
                .addValue("resAddr", verificationDto.getResidentialAddress())
                .addValue("postCode", verificationDto.getPostalCode())
                .addValue("country", verificationDto.getCountry())
                .addValue("city", verificationDto.getCity())
                .addValue("path", verificationDto.getFilePath());
        int rowsUpdated = namedParameterJdbcTemplate.update(sql, parameters);
        return rowsUpdated > 0  ? verificationDto : null;
    }

    @Override
    public boolean delete(UserVerificationDto verificationDto) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, verificationDto.getUserId());
        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    @Override
    public List<UserVerificationDto> findAllByUserId(Integer userId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, userId);
        return slaveJdbcTemplate.query(sql, parameters, getRowMapper());
    }

    private String getInsertColumns() {
        return String.join(", ", USER_ID_COL, DOC_TYPE_COL, FIRST_NAME_COL, LAST_NAME_COL, BORN_COL,
                RES_ADDR_COL, POST_CODE_COL, COUNTRY_COL, CITY_COL, DOC_PATH_COL);
    }

    private String getUpdateColumns() {
        return FIRST_NAME_COL + "= :firstName, " + LAST_NAME_COL + "= :lastName"
                + BORN_COL + "=:born, " + RES_ADDR_COL + "=:resAddr, " + POST_CODE_COL + "=:postal_code, "
                + COUNTRY_COL + "=:country, " + CITY_COL + "=:city, " + DOC_PATH_COL + "=:path;";
    }

    private java.sql.Date getDBDate(LocalDate when) {
        return java.sql.Date.valueOf(when);
    }

    private RowMapper<UserVerificationDto> getRowMapper() {
        return (rs, rowNum) -> UserVerificationDto
                .builder()
                .userId(rs.getInt(USER_ID_COL))
                .documentType(VerificationDocumentType.of(rs.getString(DOC_TYPE_COL)))
                .firstName(rs.getString(FIRST_NAME_COL))
                .lastName(rs.getString(LAST_NAME_COL))
                .born(rs.getDate(BORN_COL).toLocalDate())
                .residentialAddress(rs.getString(RES_ADDR_COL))
                .postalCode(rs.getString(POST_CODE_COL))
                .country(rs.getString(COUNTRY_COL))
                .city(rs.getString(CITY_COL))
                .filePath(rs.getString(DOC_PATH_COL))
                .build();
    }
}
