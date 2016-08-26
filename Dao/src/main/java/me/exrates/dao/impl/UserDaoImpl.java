package me.exrates.dao.impl;

import me.exrates.dao.UserDao;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserIpState;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    private final String SELECT_USER =
            "SELECT USER.id, u.email AS parent_email, USER.finpassword, USER.nickname, USER.email, USER.password, USER.regdate, " +
            "USER.phone, USER.status, USER_ROLE.name AS role_name FROM USER " +
            "INNER JOIN USER_ROLE ON USER.roleid = USER_ROLE.id LEFT JOIN REFERRAL_USER_GRAPH " +
            "ON USER.id = REFERRAL_USER_GRAPH.child LEFT JOIN USER AS u ON REFERRAL_USER_GRAPH.parent = u.id ";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private RowMapper<User> getUserRowMapper() {
        return (resultSet, i) -> {
            final User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setNickname(resultSet.getString("nickname"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setRegdate(resultSet.getDate("regdate"));
            user.setPhone(resultSet.getString("phone"));
            user.setStatus(UserStatus.values()[resultSet.getInt("status") - 1]);
            user.setRole(UserRole.valueOf(resultSet.getString("role_name")));
            user.setFinpassword(resultSet.getString("finpassword"));
            try {
                user.setParentEmail(resultSet.getString("parent_email")); // May not exist for some users
            } catch (final SQLException e) {/*NOP*/}
            return user;
        };
    }

    public int getIdByEmail(String email) {
        String sql = "SELECT id FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e){
            return 0;
        }
    }

    public boolean create(User user) {
        String sqlUser = "insert into USER(nickname,email,password,phone,status,roleid ) " +
                "values(:nickname,:email,:password,:phone,:status,:roleid)";
        String sqlWallet = "INSERT INTO WALLET (currency_id, user_id) select id, LAST_INSERT_ID() from CURRENCY where name != 'LTC';";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("email", user.getEmail());
        namedParameters.put("nickname", user.getNickname());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        namedParameters.put("password", hashedPassword);
        String phone = user.getPhone();
        if (user.getPhone() != null && user.getPhone().equals("")) {
            phone = null;
        }
        namedParameters.put("phone", phone);
        namedParameters.put("status", String.valueOf(user.getStatus().getStatus()));
        namedParameters.put("roleid", String.valueOf(user.getRole().getRole()));
        jdbcTemplate.update(sqlUser, namedParameters);

        return jdbcTemplate.update(sqlWallet, new HashMap<String, String>()) > 0;
    }

    @Override
    public void createUserDoc(final int userId, final List<Path> paths) {
        final String sql = "INSERT INTO USER_DOC (user_id, path) VALUES (:userId, :path)";
        List<HashMap<String, Object>> collect = paths.stream()
                .map(path -> new HashMap<String, Object>() {
                    {
                        put("userId", userId);
                        put("path", path.toString());
                    }
                }).collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, collect.toArray(new HashMap[paths.size()]));
    }

    @Override
    public List<UserFile> findUserDoc(final int userId) {
        final String sql = "SELECT * FROM USER_DOC where user_id = :userId";
        return jdbcTemplate.query(sql, singletonMap("userId", userId), (resultSet, i) -> {
            final UserFile userFile = new UserFile();
            userFile.setId(resultSet.getInt("id"));
            userFile.setUserId(resultSet.getInt("user_id"));
            userFile.setPath(Paths.get(resultSet.getString("path")));
            return userFile;
        });
    }

    @Override
    public void deleteUserDoc(final int docId) {
        final String sql = "DELETE FROM USER_DOC where id = :id";
        jdbcTemplate.update(sql, singletonMap("id", docId));
    }

    public List<UserRole> getAllRoles() {
        String sql = "select name from USER_ROLE";
        return jdbcTemplate.query(sql, (rs, row) -> {
            UserRole role = UserRole.valueOf(rs.getString("name"));
            return role;
        });
    }

    public UserRole getUserRoles(String email) {
        String sql = "select USER_ROLE.name as role_name from USER " +
                "inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            UserRole role = UserRole.valueOf(rs.getString("role_name"));
            return role;
        }).get(0);
    }

    public boolean addUserRoles(String email, String role) {
        String sql = "insert into USER_ROLE(name, user_id) values(:name,:userid)";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("name", role);
        namedParameters.put("userid", String.valueOf(getIdByEmail(email)));
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public User findByEmail(String email) {
        String sql = SELECT_USER + "WHERE USER.email = :email";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("email", email);
            }
        };
        return jdbcTemplate.queryForObject(sql, params, getUserRowMapper());
    }

    public List<User> getAllUsers() {
        String sql = "select email, password, status, nickname, id from USER";
        return jdbcTemplate.query(sql, (rs, row) -> {
            User user = new User();
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setStatus(UserStatus.values()[rs.getInt("status") - 1]);
            user.setNickname(rs.getString("nickname"));
            user.setId(rs.getInt("id"));
            return user;
        });
    }

    public User getUserById(int id) {
        String sql = SELECT_USER + "WHERE USER.id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(id));
        return jdbcTemplate.queryForObject(sql, namedParameters, getUserRowMapper());
    }

    @Override
    public User getCommonReferralRoot() {
        final String sql = "SELECT USER.id, nickname, email, password, finpassword, regdate, phone, status, USER_ROLE.name as role_name FROM COMMON_REFERRAL_ROOT INNER JOIN USER ON COMMON_REFERRAL_ROOT.user_id = USER.id INNER JOIN USER_ROLE ON USER.roleid = USER_ROLE.id LIMIT 1";
        final List<User> result = jdbcTemplate.query(sql, getUserRowMapper());
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public void updateCommonReferralRoot(final int userId) {
        final String sql = "UPDATE COMMON_REFERRAL_ROOT SET user_id = :id";
        final Map<String, Integer> params = singletonMap("id", userId);
        jdbcTemplate.update(sql, params);
    }

    public List<User> getUsersByRoles(List<UserRole> listRoles) {
        String sql = SELECT_USER + " WHERE USER_ROLE.name IN (:roles)";
        Map<String, List> namedParameters = new HashMap<>();
        List<String> stringList = listRoles.stream().map(Enum::name).collect(Collectors.toList());
        namedParameters.put("roles", stringList);
        return jdbcTemplate.query(sql, namedParameters, getUserRowMapper());
    }

    public String getBriefInfo(int login) {
        return null;
    }

    public boolean ifNicknameIsUnique(String nickname) {
        String sql = "SELECT id FROM USER WHERE nickname = :nickname";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("nickname", nickname);
        return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getInt("id");
            } else return 0;
        }).isEmpty();
    }

    public boolean ifPhoneIsUnique(int phone) {
        return false;
    }

    public boolean ifEmailIsUnique(String email) {
        String sql = "SELECT id FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getInt("id");
            } else return 0;
        }).isEmpty();
    }

    public String getPasswordByEmail(String email) {
        return null;
    }

    public String getIP(int userId) {
        String sql = "SELECT ipaddress FROM USER WHERE id = :userId";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("userId", String.valueOf(userId));
        return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getString("ipaddress");
            }
            return null;
        }).get(0);
    }

    public boolean setIP(int id, String ip) {
        String sql = "UPDATE USER SET ipaddress = :ipaddress WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("ipaddress", ip);
        namedParameters.put("id", String.valueOf(id));
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean addIPToLog(int userId, String ip) {
        String sql = "insert INTO IP_Log (ip,user_id) values(:ip,:userId)";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("ip", ip);
        namedParameters.put("userId", String.valueOf(userId));
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean update(UpdateUserDto user) {
        String sql = "UPDATE USER SET";
        StringBuilder fieldsStr = new StringBuilder(" ");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        /*email is present in UpdateUserDto but used for hold email to send notification only, not for update email*/
        if (user.getPhone() != null) {
            fieldsStr.append("phone = '" + user.getPhone()).append("',");
        }
        if (user.getStatus() != null) {
            fieldsStr.append("status = " + user.getStatus().getStatus()).append(",");
        }
        if (user.getRole() != null) {
            fieldsStr.append("roleid = " + user.getRole().getRole()).append(",");
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            fieldsStr.append("password = '" + passwordEncoder.encode(user.getPassword())).append("',");
        }
        if (user.getFinpassword() != null && !user.getFinpassword().isEmpty()) {
            fieldsStr.append("finpassword = '" + passwordEncoder.encode(user.getFinpassword())).append("',");
        }
        if (fieldsStr.toString().trim().length() == 0) {
            return true;
        }
        sql = sql + fieldsStr.toString().replaceAll(",$", " ") + "WHERE USER.id = :id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("id", user.getId());
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean createTemporalToken(TemporalToken token) {
        String sql = "insert into TEMPORAL_TOKEN(value,user_id,token_type_id,check_ip) values(:value,:user_id,:token_type_id,:check_ip)";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("value", token.getValue());
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("token_type_id", String.valueOf(token.getTokenType().getTokenType()));
        namedParameters.put("check_ip", token.getCheckIp());
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    public TemporalToken verifyToken(String token) {
        String sql = "SELECT * FROM TEMPORAL_TOKEN WHERE VALUE= :value";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("value", token);
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<TemporalToken>() {
            @Override
            public TemporalToken mapRow(ResultSet rs, int rowNumber) throws SQLException {
                TemporalToken temporalToken = new TemporalToken();
                temporalToken.setId(rs.getInt("id"));
                temporalToken.setUserId(rs.getInt("user_id"));
                temporalToken.setValue(token);
                temporalToken.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                temporalToken.setExpired(rs.getBoolean("expired"));
                temporalToken.setTokenType(TokenType.convert(rs.getInt("token_type_id")));
                temporalToken.setCheckIp(rs.getString("check_ip"));
                return temporalToken;
            }
        });
        return result.size() == 1 ? result.get(0) : null;
    }

    public boolean deleteTemporalToken(TemporalToken token) {
        if (token == null) {
            return false;
        }
        String sql = "delete from TEMPORAL_TOKEN where id = :id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("id", String.valueOf(token.getId()));
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token) {
        if (token == null) {
            return false;
        }
        String sql = "delete from TEMPORAL_TOKEN where user_id = :user_id and token_type_id=:token_type_id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("token_type_id", String.valueOf(token.getTokenType().getTokenType()));
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    public List<TemporalToken> getTokenByUserAndType(int userId, TokenType tokenType) {
        String sql = "SELECT * FROM TEMPORAL_TOKEN WHERE user_id= :user_id and token_type_id=:token_type_id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("user_id", String.valueOf(userId));
        namedParameters.put("token_type_id", String.valueOf(tokenType.getTokenType()));
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<TemporalToken>() {
            @Override
            public TemporalToken mapRow(ResultSet rs, int rowNumber) throws SQLException {
                TemporalToken temporalToken = new TemporalToken();
                temporalToken.setId(rs.getInt("id"));
                temporalToken.setUserId(rs.getInt("user_id"));
                temporalToken.setValue(rs.getString("value"));
                temporalToken.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                temporalToken.setExpired(rs.getBoolean("expired"));
                temporalToken.setTokenType(TokenType.convert(rs.getInt("token_type_id")));
                temporalToken.setCheckIp(rs.getString("check_ip"));
                return temporalToken;
            }
        });
        return result;
    }

    public boolean updateUserStatus(User user) {
        String sql = "update USER set status=:status where id=:id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("status", String.valueOf(user.getStatus().getStatus()));
        namedParameters.put("id", String.valueOf(user.getId()));
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    public List<TemporalToken> getAllTokens() {
        String sql = "SELECT * FROM TEMPORAL_TOKEN";
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<TemporalToken>() {
            @Override
            public TemporalToken mapRow(ResultSet rs, int rowNumber) throws SQLException {
                TemporalToken temporalToken = new TemporalToken();
                temporalToken.setId(rs.getInt("id"));
                temporalToken.setUserId(rs.getInt("user_id"));
                temporalToken.setValue(rs.getString("value"));
                temporalToken.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                temporalToken.setExpired(rs.getBoolean("expired"));
                temporalToken.setTokenType(TokenType.convert(rs.getInt("token_type_id")));
                temporalToken.setCheckIp(rs.getString("check_ip"));
                return temporalToken;
            }
        });
        return result;
    }

    public boolean delete(User user) {
        boolean result;
        String sql = "delete from USER where id = :id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("id", String.valueOf(user.getId()));
        result = jdbcTemplate.update(sql, namedParameters) > 0;
        if (!result) {
            LOGGER.warn("requested user deleting was not fulfilled. userId = " + user.getId());
        }
        return result;
    }

    @Override
    public boolean setPreferredLang(int userId, Locale locale) {
        LOGGER.debug(locale);
        LOGGER.debug(userId);
        String sql = "UPDATE USER SET preferred_lang=:preferred_lang WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(userId));
        namedParameters.put("preferred_lang", locale.toString());
        int result = jdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public String getPreferredLang(int userId) {
        String sql = "SELECT preferred_lang FROM USER WHERE id = :id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("id", userId);
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean insertIp(String email, String ip) {
        String sql = "INSERT INTO USER_IP (user_id, ip)" +
                " SELECT id, '" + ip + "'" +
                " FROM USER " +
                " WHERE USER.email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public UserIpDto getUserIpState(String email, String ip) {


        String sql = "SELECT * FROM USER_IP " +
                " WHERE " +
                " user_id = (SELECT USER.id FROM USER WHERE USER.email = :email)" +
                " AND ip=:ip";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("ip", ip);
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<UserIpDto>() {
                @Override
                public UserIpDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    UserIpDto userIpDto = new UserIpDto(rs.getInt("user_id"));
                    userIpDto.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
                    Timestamp ts = rs.getTimestamp("confirm_date");
                    if (ts != null) userIpDto.setConfirmDate(ts.toLocalDateTime());
                    ts = rs.getTimestamp("last_registration_date");
                    if (ts != null) userIpDto.setLastRegistrationDate(ts.toLocalDateTime());
                    if (rs.getInt("confirmed") == 1) {
                        userIpDto.setUserIpState(UserIpState.CONFIRMED);
                    } else {
                        userIpDto.setUserIpState(UserIpState.NOT_CONFIRMED);
                    }
                    return userIpDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return new UserIpDto(findByEmail(email).getId());
        }
    }

    @Override
    public boolean setIpStateConfirmed(int userId, String ip) {
        LOGGER.debug("UID: " + userId);
        LOGGER.debug("IP: " + ip);

        String sql = "UPDATE USER_IP " +
                " SET confirmed = true, confirm_date = NOW() " +
                " WHERE user_id = :user_id AND ip = :ip";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_id", String.valueOf(userId));
        namedParameters.put("ip", ip);
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean setLastRegistrationDate(int userId, String ip) {
        String sql = "UPDATE USER_IP " +
                " SET last_registration_date = NOW() " +
                " WHERE user_id = :user_id AND ip = :ip";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_id", String.valueOf(userId));
        namedParameters.put("ip", ip);
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public List<UserSummaryDto> getUsersSummaryList(String startDate, String endDate) {
        String sql =
                " SELECT  " +
                        "   USER.nickname as user_nickname,  " +
                        "   USER.email as user_email,  " +
                        "   USER.regdate as user_register_date,  " +
                        "   (SELECT ip FROM USER_IP WHERE USER_IP.user_id = USER.id ORDER BY -registration_date DESC LIMIT 1) as user_register_ip, " +
                        "   (SELECT ip FROM USER_IP WHERE USER_IP.user_id = USER.id ORDER BY last_registration_date DESC LIMIT 1) as user_last_entry_ip, " +
                        "   CURRENCY.name as currency_name,  " +
                        "   WALLET.active_balance as active_balance,  " +
                        "   WALLET.reserved_balance as reserved_balance, " +
                        "   (SELECT SUM(INPUT.amount) FROM TRANSACTION INPUT WHERE (INPUT.user_wallet_id = WALLET.id)  " +
                        "         AND (INPUT.operation_type_id=1)  " +
                        "         AND (INPUT.status_id=1)  " +
                        "         AND (INPUT.provided=1) " +
                        "         AND (DATE_FORMAT(INPUT.datetime, '%Y-%m-%d %H:%i:%s') BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s'))) " +
                        "   AS input_amount,  " +
                        "   (SELECT SUM(OUTPUT.amount) FROM TRANSACTION OUTPUT WHERE (OUTPUT.user_wallet_id = WALLET.id)  " +
                        "         AND (OUTPUT.operation_type_id=2)  " +
                        "         AND (OUTPUT.status_id=1)  " +
                        "         AND (OUTPUT.provided=1) " +
                        "         AND (DATE_FORMAT(OUTPUT.datetime, '%Y-%m-%d %H:%i:%s') BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s'))) " +
                        "   AS output_amount     " +
                        " FROM USER  " +
                        "   LEFT JOIN WALLET ON (WALLET.user_id = USER.id) " +
                        "   LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id)";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("start_date", startDate);
        namedParameters.put("end_date", endDate);
        ArrayList<UserSummaryDto> result = (ArrayList<UserSummaryDto>) jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<UserSummaryDto>() {
            @Override
            public UserSummaryDto mapRow(ResultSet rs, int rowNumber) throws SQLException {
                UserSummaryDto userSummaryDto = new UserSummaryDto();
                userSummaryDto.setUserNickname(rs.getString("user_nickname"));
                userSummaryDto.setUserEmail(rs.getString("user_email"));
                userSummaryDto.setCreationDate(rs.getTimestamp("user_register_date") == null ? "" : rs.getTimestamp("user_register_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                userSummaryDto.setRegisteredIp(rs.getString("user_register_ip"));
                userSummaryDto.setLastIp(rs.getString("user_last_entry_ip"));
                userSummaryDto.setCurrencyName(rs.getString("currency_name"));
                userSummaryDto.setActiveBalance(rs.getBigDecimal("active_balance"));
                userSummaryDto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                userSummaryDto.setWalletTurnover(BigDecimalProcessing.doActionLax(rs.getBigDecimal("input_amount"), rs.getBigDecimal("output_amount"), ActionType.SUBTRACT));
                return userSummaryDto;
            }
        });
        return result;
    }

}
