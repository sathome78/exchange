package me.exrates.dao.impl;

import me.exrates.dao.UserDao;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.dao.exception.notfound.UserRoleNotFoundException;
import me.exrates.model.AdminAuthorityOption;
import me.exrates.model.Comment;
import me.exrates.model.PagingData;
import me.exrates.model.Policy;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserBalancesDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserIpReportDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.model.dto.UserShortDto;
import me.exrates.model.dto.UsersInfoDto;
import me.exrates.model.dto.ieo.IeoUserStatus;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.AdminAuthority;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.PolicyEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserIpState;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    private final String SELECT_USER =
            "SELECT USER.id, u.email AS parent_email, USER.finpassword, USER.nickname, USER.email, USER.password, USER.regdate, " +
                    "USER.phone, USER.status, USER.kyc_status, USER_ROLE.name AS role_name, USER.country AS country, USER.pub_id FROM USER " +
                    "INNER JOIN USER_ROLE ON USER.roleid = USER_ROLE.id LEFT JOIN REFERRAL_USER_GRAPH " +
                    "ON USER.id = REFERRAL_USER_GRAPH.child LEFT JOIN USER AS u ON REFERRAL_USER_GRAPH.parent = u.id ";

    private final String SELECT_COUNT = "SELECT COUNT(*) FROM USER " +
            "INNER JOIN USER_ROLE ON USER.roleid = USER_ROLE.id LEFT JOIN REFERRAL_USER_GRAPH " +
            "ON USER.id = REFERRAL_USER_GRAPH.child LEFT JOIN USER AS u ON REFERRAL_USER_GRAPH.parent = u.id ";

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate masterTemplate;

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Comment> userCommentRowMapper = (resultSet, i) -> {
        Comment comment = new Comment();
        comment.setId(resultSet.getInt("id"));
        comment.setUser(getUserById(resultSet.getInt("user_id")));
        comment.setComment(resultSet.getString("users_comment"));
        comment.setCreator(getUserById(resultSet.getInt("user_creator_id")));
        comment.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
        comment.setEditTime(resultSet.getTimestamp("edit_time").toLocalDateTime());
        comment.setMessageSent(resultSet.getBoolean("message_sent"));
        return comment;
    };

    private RowMapper<User> getUserRowMapper() {
        return (resultSet, i) -> {
            final User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setNickname(resultSet.getString("nickname"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setRegdate(resultSet.getDate("regdate"));
            user.setPhone(resultSet.getString("phone"));
            user.setUserStatus(UserStatus.values()[resultSet.getInt("status") - 1]);
            user.setRole(UserRole.valueOf(resultSet.getString("role_name")));
            user.setFinpassword(resultSet.getString("finpassword"));
            user.setKycStatus(resultSet.getString("kyc_status"));
            user.setCountry(resultSet.getString("country"));
            user.setPublicId(resultSet.getString("pub_id"));
            try {
                user.setParentEmail(resultSet.getString("parent_email")); // May not exist for some users
            } catch (final SQLException e) {/*NOP*/}
            return user;
        };
    }

    private RowMapper<User> getUserRowMapperWithoutRoleAndParentEmail() {
        return (resultSet, i) -> {
            final User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setNickname(resultSet.getString("nickname"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setRegdate(resultSet.getDate("regdate"));
            user.setPhone(resultSet.getString("phone"));
            user.setUserStatus(UserStatus.values()[resultSet.getInt("status") - 1]);
            user.setFinpassword(resultSet.getString("finpassword"));
            user.setKycStatus(resultSet.getString("kyc_status"));
            return user;
        };
    }

    public int getIdByEmail(String email) {
        String sql = "SELECT id FROM USER WHERE email = :email";

        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);

        try {
            return masterTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            throw new UserNotFoundException(String.format("User: %s not found", email));
        }
    }

    @Override
    public int getIdByNickname(String nickname) {
        String sql = "SELECT id FROM USER WHERE nickname = :nickname";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("nickname", nickname);
        try {
            return masterTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public boolean setNickname(String newNickName, String userEmail) {
        String sql = "UPDATE USER SET nickname=:nickname WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("nickname", newNickName);
        namedParameters.put("email", userEmail);
        int result = masterTemplate.update(sql, namedParameters);
        return result > 0;
    }

    public boolean create(User user) {
        String sqlUser = "insert into USER(pub_id, nickname,email,password,phone,status,roleid ) " +
                "values(SUBSTRING(MD5(:email), 1, 20), :nickname, :email, :password, :phone, :status, :roleid)";
        String sqlWallet = "INSERT INTO WALLET (currency_id, user_id) select id, :user_id from CURRENCY;";
        String sqlNotificationOptions = "INSERT INTO NOTIFICATION_OPTIONS(notification_event_id, user_id, send_notification, send_email) " +
                "select id, :user_id, default_send_notification, default_send_email FROM NOTIFICATION_EVENT; ";
        String sqlUserOperationAuthority = "INSERT INTO USER_OPERATION_AUTHORITY (user_id, user_operation_id) " +
                "SELECT :user_id, id FROM USER_OPERATION;";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("email", user.getEmail());
        namedParameters.put("nickname", user.getNickname());
        if (user.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            namedParameters.put("password", hashedPassword);
        } else {
            namedParameters.put("password", user.getPassword());
        }
        String phone = user.getPhone();
        if (user.getPhone() != null && user.getPhone().equals("")) {
            phone = null;
        }
        namedParameters.put("phone", phone);
        namedParameters.put("status", String.valueOf(user.getUserStatus().getStatus()));
        namedParameters.put("roleid", String.valueOf(user.getRole().getRole()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterTemplate.update(sqlUser, new MapSqlParameterSource(namedParameters), keyHolder);
        int userId = keyHolder.getKey().intValue();
        Map<String, Integer> userIdParamMap = Collections.singletonMap("user_id", userId);

        return masterTemplate.update(sqlWallet, userIdParamMap) > 0
                && masterTemplate.update(sqlNotificationOptions, userIdParamMap) > 0
                && masterTemplate.update(sqlUserOperationAuthority, userIdParamMap) > 0;
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
        masterTemplate.batchUpdate(sql, collect.toArray(new HashMap[paths.size()]));
    }

    @Override
    public void setUserAvatar(int userId, String path) {
        final String sql = "UPDATE USER SET avatar_path = :path WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("path", path);
        params.put("id", userId);
        masterTemplate.update(sql, params);
    }

    @Override
    public List<UserFile> findUserDoc(final int userId) {
        final String sql = "SELECT * FROM USER_DOC where user_id = :userId";
        return masterTemplate.query(sql, singletonMap("userId", userId), (resultSet, i) -> {
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
        masterTemplate.update(sql, singletonMap("id", docId));
    }

    public List<UserRole> getAllRoles() {
        String sql = "select name from USER_ROLE";
        return masterTemplate.query(sql, (rs, row) -> {
            UserRole role = UserRole.valueOf(rs.getString("name"));
            return role;
        });
    }

    public UserRole getUserRoles(String email) {
        String sql = "select USER_ROLE.name as role_name from USER " +
                "inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.email = :email ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return masterTemplate.query(sql, namedParameters, (rs, row) -> {
            UserRole role = UserRole.valueOf(rs.getString("role_name"));
            return role;
        }).get(0);
    }

    @Override
    public UserRole getUserRoleById(Integer id) {
        String sql = "select USER_ROLE.name as role_name from USER " +
                "inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.id = :id ";

        Map<String, Integer> namedParameters = Collections.singletonMap("id", id);

        try {
            return masterTemplate.queryForObject(sql, namedParameters, (rs, row) -> UserRole.valueOf(rs.getString("role_name")));
        } catch (Exception ex) {
            throw new UserRoleNotFoundException(String.format("User role for user: %d not found", id));
        }
    }

    @Override
    public List<String> getUserRoleAndAuthorities(String email) {
        String sql = "select USER_ROLE.name as role_name from USER " +
                "inner join USER_ROLE on USER.roleid = USER_ROLE.id " +
                "where USER.email = :email " +
                "UNION " +
                "SELECT ADMIN_AUTHORITY.name AS role_name from USER " +
                "inner join USER_ADMIN_AUTHORITY on USER_ADMIN_AUTHORITY.user_id = USER.id " +
                "inner join ADMIN_AUTHORITY on USER_ADMIN_AUTHORITY.admin_authority_id = ADMIN_AUTHORITY.id " +
                "where USER.email = :email AND USER_ADMIN_AUTHORITY.enabled = 1 ";
        Map<String, String> namedParameters = Collections.singletonMap("email", email);
        return masterTemplate.query(sql, namedParameters, (rs, row) -> rs.getString("role_name"));
    }

    @Override
    public List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId) {
        String sql = "SELECT USER_ADMIN_AUTHORITY.admin_authority_id, USER_ADMIN_AUTHORITY.enabled FROM USER_ADMIN_AUTHORITY " +
                "JOIN ADMIN_AUTHORITY ON ADMIN_AUTHORITY.id = USER_ADMIN_AUTHORITY.admin_authority_id AND ADMIN_AUTHORITY.hidden != 1 " +
                "WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return masterTemplate.query(sql, params, ((rs, rowNum) -> {
            AdminAuthorityOption option = new AdminAuthorityOption();
            option.setAdminAuthority(AdminAuthority.convert(rs.getInt("admin_authority_id")));
            option.setEnabled(rs.getBoolean("enabled"));
            return option;
        }));
    }

    @Override
    public boolean createAdminAuthoritiesForUser(Integer userId, UserRole role) {
        String sql = "INSERT INTO USER_ADMIN_AUTHORITY SELECT :user_id, admin_authority_id, enabled " +
                "FROM ADMIN_AUTHORITY_ROLE_DEFAULTS " +
                "WHERE role_id = :role_id";
        Map<String, Integer> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("role_id", role.getRole());
        return masterTemplate.update(sql, params) > 0;

    }

    @Override
    public boolean hasAdminAuthorities(Integer userId) {
        String sql = "SELECT COUNT(*) FROM USER_ADMIN_AUTHORITY WHERE user_id = :user_id ";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        Integer count = masterTemplate.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    @Override
    public void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId) {
        String sql = "UPDATE USER_ADMIN_AUTHORITY SET enabled = :enabled WHERE user_id = :user_id " +
                "AND admin_authority_id = :admin_authority_id";
        Map<String, Object>[] batchValues = options.stream().map(option -> {
            Map<String, Object> optionValues = new HashMap<String, Object>() {{
                put("admin_authority_id", option.getAdminAuthority().getAuthority());
                put("user_id", userId);
                put("enabled", option.getEnabled());
            }};
            return optionValues;
        }).collect(Collectors.toList()).toArray(new Map[options.size()]);
        masterTemplate.batchUpdate(sql, batchValues);
    }

    @Override
    public boolean removeUserAuthorities(Integer userId) {
        String sql = "DELETE FROM USER_ADMIN_AUTHORITY WHERE user_id = :user_id ";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return masterTemplate.update(sql, params) > 0;
    }

  /*public boolean addUserRoles(String email, String role) {
    String sql = "insert into USER_ROLE(name, user_id) values(:name,:userid)";
    Map<String, String> namedParameters = new HashMap<>();
    namedParameters.put("name", role);
    namedParameters.put("userid", String.valueOf(getIdByEmail(email)));
    return masterTemplate.update(sql, namedParameters) > 0;
  }*/

    @Override
    public User findByEmail(String email) {
        String sql = SELECT_USER + "WHERE USER.email = :email";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("email", email);
            }
        };
        try {
            return masterTemplate.queryForObject(sql, params, getUserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("email: %s", email));
        }
    }

    @Override
    public Optional<String> findKycReferenceByEmail(String email) {
        String sql = "SELECT kyc_reference FROM USER WHERE USER.email = :email";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("email", email);
            }
        };
        try {
            return Optional.ofNullable(masterTemplate.queryForObject(sql, params, String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserShortDto findShortByEmail(String email) {
        String sql = "SELECT id, email, password, status FROM USER WHERE email = :email";
        try {
            return masterTemplate.queryForObject(sql, Collections.singletonMap("email", email), (rs, rowNum) -> {
                UserShortDto dto = new UserShortDto();
                dto.setEmail(rs.getString("email"));
                dto.setId(rs.getInt("id"));
                dto.setPassword(rs.getString("password"));
                dto.setStatus(UserStatus.convert(rs.getInt("status")));
                return dto;
            });
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("email: %s", email));
        }
    }

    @Override
    public boolean updateKycStatusByEmail(String email, String result) {
        final String sql = "UPDATE USER SET kyc_status = :value WHERE email = :email";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("value", result);
        params.addValue("email", email);

        try {
            return masterTemplate.update(sql, params) > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public User findByNickname(String nickname) {
        String sql = SELECT_USER + "WHERE USER.nickname = :nickname";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("nickname", nickname);
            }
        };
        try {
            return masterTemplate.queryForObject(sql, params, getUserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("nickname: %s", nickname));
        }
    }

    public List<User> getAllUsers() {
        String sql = "select email, password, status, nickname, id from USER";
        return masterTemplate.query(sql, (rs, row) -> {
            User user = new User();
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setUserStatus(UserStatus.values()[rs.getInt("status") - 1]);
            user.setNickname(rs.getString("nickname"));
            user.setId(rs.getInt("id"));
            return user;
        });
    }

    public User getUserById(int id) {
        String sql = SELECT_USER + "WHERE USER.id = :id";

        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(id));

        try {
            return masterTemplate.queryForObject(sql, namedParameters, getUserRowMapper());
        } catch (Exception ex) {
            throw new UserNotFoundException(String.format("User: %d not found", id));
        }
    }

    public User getUserByTemporalToken(String token) {
        String sql = "SELECT * FROM USER WHERE USER.id =(SELECT TEMPORAL_TOKEN.user_id FROM TEMPORAL_TOKEN WHERE TEMPORAL_TOKEN.value=:token_value)";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("token_value", token);
        return masterTemplate.query(sql, namedParameters, getUserRowMapperWithoutRoleAndParentEmail()).get(0);
    }

    @Override
    public User getCommonReferralRoot() {
        final String sql = "SELECT USER.id, nickname, email, password, finpassword, regdate, phone, status, USER_ROLE.name as role_name, USER.pub_id FROM COMMON_REFERRAL_ROOT INNER JOIN USER ON COMMON_REFERRAL_ROOT.user_id = USER.id INNER JOIN USER_ROLE ON USER.roleid = USER_ROLE.id LIMIT 1";
        final List<User> result = masterTemplate.query(sql, getUserRowMapper());
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public void updateCommonReferralRoot(final int userId) {
        final String sql = "UPDATE COMMON_REFERRAL_ROOT SET user_id = :id";
        final Map<String, Integer> params = singletonMap("id", userId);
        masterTemplate.update(sql, params);
    }

    public List<User> getUsersByRoles(List<UserRole> listRoles) {
        String sql = SELECT_USER + " WHERE USER_ROLE.name IN (:roles)";
        Map<String, List> namedParameters = new HashMap<>();
        List<String> stringList = listRoles.stream().map(Enum::name).collect(Collectors.toList());
        namedParameters.put("roles", stringList);
        return masterTemplate.query(sql, namedParameters, getUserRowMapper());
    }

    @Override
    public PagingData<List<User>> getUsersByRolesPaginated(List<UserRole> roles, int offset, int limit,
                                                           String orderColumnName, String orderDirection,
                                                           String searchValue) {
        StringJoiner sqlJoiner = new StringJoiner(" ");
        String whereClause = "WHERE USER_ROLE.name IN (:roles)";
        sqlJoiner.add(SELECT_USER)
                .add(whereClause);
        String searchClause = "";
        if (!(searchValue == null || searchValue.isEmpty())) {
            searchClause = "AND (CONVERT(USER.nickname USING utf8) LIKE :searchValue OR CONVERT(USER.email USING utf8) LIKE :searchValue " +
                    "OR CONVERT(USER.regdate USING utf8) LIKE :searchValue)";
        }
        sqlJoiner.add(searchClause).add("ORDER BY").add("USER." + orderColumnName).add(orderDirection)
                .add("LIMIT").add(String.valueOf(limit))
                .add("OFFSET").add(String.valueOf(offset));
        String sql = sqlJoiner.toString();
        LOGGER.debug(sql);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("roles", roles.stream().map(Enum::name).collect(Collectors.toList()));
        namedParameters.put("searchValue", "%" + searchValue + "%");
        String selectCountSql = new StringJoiner(" ")
                .add(SELECT_COUNT)
                .add(whereClause).add(searchClause).toString();
        LOGGER.debug(selectCountSql);
        List<User> selectedUsers = masterTemplate.query(sql, namedParameters, getUserRowMapper());
        Integer total = masterTemplate.queryForObject(selectCountSql, namedParameters, Integer.class);
        PagingData<List<User>> result = new PagingData<>();
        result.setData(selectedUsers);
        result.setFiltered(total);
        result.setTotal(total);
        return result;
    }

    public boolean ifNicknameIsUnique(String nickname) {
        String sql = "SELECT id FROM USER WHERE nickname = :nickname";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("nickname", nickname);
        return masterTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getInt("id");
            } else return 0;
        }).isEmpty();
    }

    public boolean ifEmailIsUnique(String email) {
        String sql = "SELECT id FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return masterTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getInt("id");
            } else return 0;
        }).isEmpty();
    }

    public String getIP(int userId) {
        String sql = "SELECT ipaddress FROM USER WHERE id = :userId";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("userId", String.valueOf(userId));
        return masterTemplate.query(sql, namedParameters, (rs, row) -> {
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
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    public boolean addIPToLog(int userId, String ip) {
        String sql = "insert INTO IP_Log (ip,user_id) values(:ip,:userId)";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("ip", ip);
        namedParameters.put("userId", String.valueOf(userId));
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    public boolean update(UpdateUserDto user) {
        LOGGER.debug("Updating user: " + user.getEmail() +
                ", newRole: " + user.getRole() + ", newStatus: " + user.getStatus());

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
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    public boolean createTemporalToken(TemporalToken token) {
        String sql = "insert into TEMPORAL_TOKEN(value,user_id,token_type_id,check_ip) values(:value,:user_id,:token_type_id,:check_ip)";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("value", token.getValue());
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("token_type_id", String.valueOf(token.getTokenType().getTokenType()));
        namedParameters.put("check_ip", token.getCheckIp());
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    public TemporalToken verifyToken(String token) {
        String sql = "SELECT * FROM TEMPORAL_TOKEN WHERE VALUE= :value";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("value", token);
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) masterTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<TemporalToken>() {
            @Override
            public TemporalToken mapRow(ResultSet rs, int rowNumber) throws SQLException {
                TemporalToken temporalToken = new TemporalToken();
                temporalToken.setId(rs.getInt("id"));
                temporalToken.setUserId(rs.getInt("user_id"));
                temporalToken.setValue(token);
                temporalToken.setAlreadyUsed(rs.getBoolean("already_used"));
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
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean deleteTemporalToken(String tempToken) {
        String sql = "delete from TEMPORAL_TOKEN where value = :token";
        Map<String, String> namedParameters = new HashMap<>(1);
        namedParameters.put("token", tempToken);
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    public boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token) {
        if (token == null) {
            return false;
        }
        String sql = "delete from TEMPORAL_TOKEN where user_id = :user_id and token_type_id=:token_type_id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("token_type_id", String.valueOf(token.getTokenType().getTokenType()));
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    public List<TemporalToken> getTokenByUserAndType(int userId, TokenType tokenType) {
        String sql = "SELECT * FROM TEMPORAL_TOKEN WHERE user_id= :user_id and token_type_id=:token_type_id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("user_id", String.valueOf(userId));
        namedParameters.put("token_type_id", String.valueOf(tokenType.getTokenType()));
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) masterTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<TemporalToken>() {
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

    @Override
    public boolean updateUserStatus(User user) {
        String sql = "update USER set status=:status where id=:id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("status", String.valueOf(user.getUserStatus().getStatus()));
        namedParameters.put("id", String.valueOf(user.getId()));
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    public List<TemporalToken> getAllTokens() {
        String sql = "SELECT * FROM TEMPORAL_TOKEN";
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) masterTemplate.query(sql, new BeanPropertyRowMapper<TemporalToken>() {
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
        result = masterTemplate.update(sql, namedParameters) > 0;
        if (!result) {
            LOGGER.warn("requested user deleting was not fulfilled. userId = " + user.getId());
        }
        return result;
    }

    @Override
    public boolean setPreferredLang(int userId, Locale locale) {
        String sql = "UPDATE USER SET preferred_lang=:preferred_lang WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(userId));
        namedParameters.put("preferred_lang", locale.toString());
        int result = masterTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public String getPreferredLang(int userId) {
        String sql = "SELECT preferred_lang FROM USER WHERE id = :id";

        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("id", userId);

        try {
            return masterTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException ex) {
            return Locale.ENGLISH.getLanguage();
        }
    }

    @Override
    public String getPreferredLangByEmail(String email) {
        String sql = "SELECT preferred_lang FROM USER WHERE email = :email";

        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);

        try {
            return masterTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException ex) {
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
        return masterTemplate.update(sql, namedParameters) > 0;
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
            return masterTemplate.queryForObject(sql, namedParameters, new RowMapper<UserIpDto>() {
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
        String sql = "UPDATE USER_IP " +
                " SET confirmed = true, confirm_date = NOW() " +
                " WHERE user_id = :user_id AND ip = :ip";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_id", String.valueOf(userId));
        namedParameters.put("ip", ip);
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean setLastRegistrationDate(int userId, String ip) {
        String sql = "UPDATE USER_IP " +
                " SET last_registration_date = NOW() " +
                " WHERE user_id = :user_id AND ip = :ip";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_id", String.valueOf(userId));
        namedParameters.put("ip", ip);
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails) {
        String sql = "SELECT USER.id AS user_id, USER.nickname AS user_nickname, USER.email AS user_email, USER_ROLE.name AS user_role FROM USER " +
                "INNER JOIN USER_ROLE ON USER_ROLE.id = USER.roleid " +
                "WHERE USER.email IN (:emails)";
        Map<String, Object> params = Collections.singletonMap("emails", emails);
        return masterTemplate.query(sql, params, (resultSet, i) -> {
            UserSessionInfoDto userSessionInfoDto = new UserSessionInfoDto();
            userSessionInfoDto.setUserId(resultSet.getInt("user_id"));
            userSessionInfoDto.setUserNickname(resultSet.getString("user_nickname"));
            userSessionInfoDto.setUserEmail(resultSet.getString("user_email"));
            userSessionInfoDto.setUserRole(UserRole.valueOf(resultSet.getString("user_role")));
            return userSessionInfoDto;
        });
    }

    @Override
    public Long saveTemporaryPassword(Integer userId, String password, Integer tokenId) {
        String sql = "INSERT INTO API_TEMP_PASSWORD(user_id, password, date_creation, temporal_token_id) VALUES (:userId, :password, NOW(), :tokenId);";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("userId", userId);
        namedParameters.put("password", encodedPassword);
        namedParameters.put("tokenId", tokenId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterTemplate.update(sql, new MapSqlParameterSource(namedParameters), keyHolder);
        return (Long) keyHolder.getKey();
    }


    @Override
    public boolean deleteTemporaryPassword(Long id) {
        String sql = "DELETE FROM API_TEMP_PASSWORD WHERE id = :id";
        Map<String, Long> namedParameters = Collections.singletonMap("id", id);
        return masterTemplate.update(sql, namedParameters) > 0;
    }


    @Override
    public Collection<Comment> getUserComments(int id) {

        String sql = "SELECT id, user_id, users_comment, user_creator_id, creation_time, edit_time, message_sent  FROM USER_COMMENT " +
                "WHERE user_id = :user_id";
        Map<String, Object> params = Collections.singletonMap("user_id", id);
        return masterTemplate.query(sql, params, userCommentRowMapper);
    }

    @Override
    public Optional<Comment> getCommentById(int id) {
        String sql = "SELECT id, user_id, users_comment, user_creator_id, creation_time, edit_time, message_sent  FROM USER_COMMENT " +
                "WHERE id = :id";
        Map<String, Object> params = Collections.singletonMap("id", id);
        try {
            return Optional.of(masterTemplate.queryForObject(sql, params, userCommentRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean addUserComment(Comment comment) {
        String sql = "INSERT INTO USER_COMMENT (user_id, users_comment, user_creator_id, message_sent, topic_id) " +
                "VALUES (:user_id, :comment, :user_creator_id, :message_sent, :topic_id);";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", comment.getUser().getId());
        namedParameters.put("comment", comment.getComment());
        namedParameters.put("user_creator_id", comment.getCreator() == null ? -1 : comment.getCreator().getId());
        namedParameters.put("message_sent", comment.isMessageSent());
        namedParameters.put("topic_id", comment.getUserCommentTopic().getCode());
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public void editUserComment(int id, String newComment, boolean sendMessage) {
        String sql = "UPDATE USER_COMMENT SET users_comment = :new_comment, message_sent = :message_sent WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("message_sent", sendMessage);
        params.put("new_comment", newComment);
        masterTemplate.update(sql, params);
    }

    @Override
    public boolean deleteUserComment(int id) {
        String sql = "DELETE FROM USER_COMMENT WHERE USER_COMMENT.id = :id; ";
        Map<String, Integer> namedParameters = Collections.singletonMap("id", id);
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public void setCurrencyPermissionsByUserId(Integer userId, List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList) {
        String sql = "DELETE FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION WHERE user_id=:user_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
        }};
        masterTemplate.update(sql, params);

        sql = "INSERT INTO USER_CURRENCY_INVOICE_OPERATION_PERMISSION " +
                " (user_id, currency_id, invoice_operation_permission_id, operation_direction, operation_direction_id) " +
                " VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserCurrencyOperationPermissionDto dto = userCurrencyOperationPermissionDtoList.get(i);
                ps.setInt(1, userId);
                ps.setInt(2, dto.getCurrencyId());
                ps.setInt(3, dto.getInvoiceOperationPermission().getCode());
                ps.setString(4, dto.getInvoiceOperationDirection().name());
                ps.setInt(5, dto.getInvoiceOperationDirection().getId());
            }

            @Override
            public int getBatchSize() {
                return userCurrencyOperationPermissionDtoList.size();
            }
        });

    }

    @Override
    public InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
            Integer userId,
            Integer currencyId,
            InvoiceOperationDirection invoiceOperationDirection) {
        String sql = "SELECT invoice_operation_permission_id " +
                " FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION " +
                " WHERE user_id = :user_id AND currency_id = :currency_id AND operation_direction = :operation_direction";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("currency_id", currencyId);
            put("operation_direction", invoiceOperationDirection.name());
        }};
        return masterTemplate.queryForObject(sql, params, (rs, idx) ->
                InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
    }

    @Override
    public String getEmailById(Integer id) {
        String sql = "SELECT email FROM USER WHERE id = :id";

        try {
            return masterTemplate.queryForObject(sql, Collections.singletonMap("id", id), String.class);
        } catch (Exception ex) {
            throw new UserNotFoundException(String.format("User: %d not found", id));
        }
    }

    @Override
    public String getEmailByPubId(String pubId) {
        String sql = "SELECT U.email FROM USER U WHERE U.pub_id = :id";

        try {
            return masterTemplate.queryForObject(sql, Collections.singletonMap("id", pubId), String.class);
        } catch (Exception ex) {
            throw new UserNotFoundException(String.format("User: %s not found", pubId));
        }
    }

    @Override
    public String getPubIdByEmail(String email) {
        String sql = "SELECT U.pub_id FROM USER U WHERE U.email = :email";

        try {
            return masterTemplate.queryForObject(sql, Collections.singletonMap("email", email), String.class);
        } catch (Exception ex) {
            throw new UserNotFoundException(String.format("User: %s not found", email));
        }
    }

    @Override
    public UserRole getUserRoleByEmail(String email) {
        String sql = "select USER_ROLE.name as role_name from USER " +
                "inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.email = :email ";

        Map<String, String> namedParameters = Collections.singletonMap("email", email);

        try {
            return masterTemplate.queryForObject(sql, namedParameters, (rs, row) ->
                    UserRole.valueOf(rs.getString("role_name")));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void savePollAsDoneByUser(String email) {
        String sql = "UPDATE USER SET USER.tmp_poll_passed = 1 " +
                " WHERE USER.email = :email ";
        Map<String, String> namedParameters = Collections.singletonMap("email", email);
        masterTemplate.update(sql, namedParameters);
    }

    @Override
    public boolean checkPollIsDoneByUser(String email) {
        String sql = "SELECT tmp_poll_passed = 1 " +
                "  FROM USER " +
                "  WHERE USER.email = :email ";
        Map<String, String> namedParameters = Collections.singletonMap("email", email);
        return masterTemplate.queryForObject(sql, namedParameters, Boolean.class);
    }

    @Override
    public String getPinByEmailAndEvent(String email, NotificationMessageEventEnum event) {
        final String sql = String.format("SELECT %s_pin FROM USER " +
                " WHERE email = :email ", event.name().toLowerCase());
        return masterTemplate.queryForObject(sql, Collections.singletonMap("email", email), String.class);
    }

    @Override
    public void updatePinByUserEmail(String userEmail, String pin, NotificationMessageEventEnum event) {
        String sql = String.format("UPDATE USER SET %s_pin = :pin " +
                "WHERE USER.email = :email", event.name().toLowerCase());
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("email", userEmail);
            put("pin", pin);
        }};
        masterTemplate.update(sql, namedParameters);
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public UsersInfoDto getUsersInfo(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles) {
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("start_time", Timestamp.valueOf(startTime));
            put("end_time", Timestamp.valueOf(endTime));
            put("user_roles", userRoles.stream().map(UserRole::getRole).collect(toList()));
        }};

        String sql = "SELECT u.id AS new_users" +
                " FROM USER u" +
                " WHERE u.regdate BETWEEN :start_time AND :end_time AND u.roleid IN (:user_roles)";

        final List<Integer> newUsersCount = masterTemplate.queryForList(sql, namedParameters, Integer.class);

        sql = "SELECT u.id AS all_users" +
                " FROM USER u" +
                " WHERE u.roleid IN (:user_roles)";

        final List<Integer> allUsersCount = masterTemplate.queryForList(sql, namedParameters, Integer.class);

        sql = "SELECT u.id AS active_users" +
                " FROM USER u" +
                " JOIN EXORDERS o ON o.user_id = u.id" +
                " WHERE u.roleid IN (:user_roles) AND o.status_id = 3 AND o.date_acception IS NOT NULL AND o.date_acception BETWEEN :start_time AND :end_time" +
                " GROUP BY u.id";

        final List<Integer> activeUsers = masterTemplate.queryForList(sql, namedParameters, Integer.class);

        sql = "SELECT u.id AS one_or_more_success_input_users" +
                " FROM USER u" +
                " JOIN WALLET w ON w.user_id = u.id" +
                " JOIN TRANSACTION input ON input.user_wallet_id = w.id" +
                " WHERE u.roleid IN (:user_roles) AND input.source_type = 'REFILL' AND input.operation_type_id = 1 AND input.status_id = 1" +
                " AND input.provided = 1 AND input.datetime BETWEEN :start_time AND :end_time" +
                " GROUP BY u.id";

        final List<Integer> successInputUsers = masterTemplate.queryForList(sql, namedParameters, Integer.class);

        sql = "SELECT u.id AS one_or_more_success_output_users" +
                " FROM USER u" +
                " JOIN WALLET w ON w.user_id = u.id" +
                " JOIN TRANSACTION output ON output.user_wallet_id = w.id" +
                " WHERE u.roleid IN (:user_roles) AND output.source_type = 'WITHDRAW' AND output.operation_type_id = 2 AND output.status_id = 1" +
                " AND output.provided = 1 AND output.datetime BETWEEN :start_time AND :end_time" +
                " GROUP BY u.id";

        final List<Integer> successOutputUsers = masterTemplate.queryForList(sql, namedParameters, Integer.class);

        return UsersInfoDto.builder()
                .newUsers(newUsersCount.size())
                .allUsers(allUsersCount.size())
                .activeUsers(activeUsers.size())
                .oneOrMoreSuccessInputUsers(successInputUsers.size())
                .oneOrMoreSuccessOutputUsers(successOutputUsers.size())
                .build();
    }

    @Override
    public List<UserBalancesDto> getUserBalances(List<UserRole> userRoles) {
        String sql = "SELECT u.id AS user_id, " +
                "cur.name AS currency_name, " +
                "w.active_balance, " +
                "w.reserved_balance" +
                " FROM USER u" +
                " JOIN WALLET w ON w.user_id = u.id" +
                " JOIN CURRENCY cur on cur.id = w.currency_id" +
                " WHERE u.roleid IN (:user_roles) AND w.active_balance IS NOT NULL AND w.reserved_balance IS NOT NULL AND (w.active_balance <> 0 OR w.reserved_balance <> 0)";

        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_roles", userRoles.stream().map(UserRole::getRole).collect(toList()));
        }};
        return masterTemplate.query(sql, namedParameters, (rs, idx) -> UserBalancesDto.builder()
                .userId(rs.getInt("user_id"))
                .currencyName(rs.getString("currency_name"))
                .activeBalance(rs.getBigDecimal("active_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .build());
    }

    @Override
    public String getPassword(int userId) {
        String sql = "SELECT password FROM USER WHERE USER.id =:id";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("id", userId);
        }};
        return masterTemplate.queryForObject(sql, namedParameters, String.class);
    }

    @Override
    public Integer updateGaTag(String gatag, String userName) {
        String sql = "UPDATE USER SET GA=:ga WHERE email=:email";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("ga", gatag);
            put("email", userName);
        }};
        return masterTemplate.update(sql, namedParameters);
    }

    @Transactional(readOnly = true)
    @Override
    public int getVerificationStep(String userEmail) {
        String sql = "SELECT u.kyc_verification_step FROM USER u WHERE u.email =:email";

        return masterTemplate.queryForObject(sql, Collections.singletonMap("email", userEmail), Integer.class);
    }

    @Override
    public boolean userExistByEmail(String email) {
        return this.jdbcTemplate.queryForObject("SELECT CASE WHEN count(id) > 0 THEN TRUE ELSE FALSE END FROM USER WHERE email = ?", Boolean.class, email);
    }

    @Override
    public String getAvatarPath(Integer userId) {
        String sql = "SELECT avatar_path FROM USER where id = :id";
        Map<String, Integer> params = Collections.singletonMap("id", userId);
        return masterTemplate.queryForObject(sql, params, (resultSet, row) -> resultSet.getString("avatar_path"));
    }

    @Override
    public List<Integer> findFavouriteCurrencyPairsById(int userId) {
        String sql = "SELECT currency_pair_id FROM USER_FAVORITE_CURRENCY_PAIRS WHERE user_id = :userId";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", userId);
        }};
        return masterTemplate.queryForList(sql, params, Integer.class);
    }

    @Override
    public boolean manageUserFavouriteCurrencyPair(int userId, int currencyPairId, boolean delete) {
        String sql = "INSERT IGNORE INTO USER_FAVORITE_CURRENCY_PAIRS (user_id, currency_pair_id) " +
                " VALUES(:userId, :currencyPairId)";
        if (delete) {
            sql = "DELETE FROM USER_FAVORITE_CURRENCY_PAIRS WHERE user_id = :userId AND currency_pair_id = :currencyPairId";
        }
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", userId);
            put("currencyPairId", currencyPairId);
        }};
        return masterTemplate.update(sql, params) >= 0;
    }

    @Transactional
    @Override
    public int updateReferenceId(String referenceId, String userEmail) {
        final String sql = "UPDATE USER SET kyc_reference = :kyc_reference WHERE email = :email";

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("kyc_reference", referenceId);
            put("email", userEmail);
        }};
        return masterTemplate.update(sql, params);
    }

    @Transactional(readOnly = true)
    @Override
    public String getReferenceIdByUserEmail(String userEmail) {
        String sql = "SELECT u.kyc_reference FROM USER u WHERE u.email =:email";

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("email", userEmail);
        }};
        return masterTemplate.queryForObject(sql, params, String.class);
    }

    @Transactional(readOnly = true)
    @Override
    public String getEmailByReferenceId(String referenceId) {
        String sql = "SELECT u.email FROM USER u WHERE u.kyc_reference =:kyc_reference";

        return masterTemplate.queryForObject(sql, Collections.singletonMap("kyc_reference", referenceId), String.class);
    }

    @Transactional
    @Override
    public int updateVerificationStep(String userEmail) {
        final String sql = "UPDATE USER SET kyc_verification_step = kyc_verification_step + 1 WHERE email = :email";

        return masterTemplate.update(sql, Collections.singletonMap("email", userEmail));
    }

    @Override
    public TemporaryPasswordDto getTemporaryPasswordById(Long id) {
        String sql = "SELECT id, user_id, password, date_creation, temporal_token_id FROM API_TEMP_PASSWORD WHERE id = :id";
        Map<String, Long> namedParameters = Collections.singletonMap("id", id);
        return masterTemplate.queryForObject(sql, namedParameters, (resultSet, row) -> {
            TemporaryPasswordDto dto = new TemporaryPasswordDto();
            dto.setId(resultSet.getLong("id"));
            dto.setUserId(resultSet.getInt("user_id"));
            dto.setPassword(resultSet.getString("password"));
            dto.setDateCreation(resultSet.getTimestamp("date_creation").toLocalDateTime());
            dto.setTemporalTokenId(resultSet.getInt("user_id"));
            return dto;
        });

    }

    @Override
    public boolean updateUserPasswordFromTemporary(Long tempPassId) {
        String sql = "UPDATE USER SET USER.password = " +
                "(SELECT password FROM API_TEMP_PASSWORD WHERE id = :tempPassId) " +
                "WHERE USER.id = (SELECT user_id FROM API_TEMP_PASSWORD WHERE id = :tempPassId);\n";
        Map<String, Long> namedParameters = Collections.singletonMap("tempPassId", tempPassId);
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean tempDeleteUserWallets(int userId) {
        String sql = "DELETE FROM WALLET WHERE user_id = :id; ";
        Map<String, Integer> namedParameters = Collections.singletonMap("id", userId);
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean tempDeleteUser(int id) {
        String sql = "DELETE FROM USER WHERE USER.id = :id; ";
        Map<String, Integer> namedParameters = Collections.singletonMap("id", id);
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public Integer retrieveNicknameSearchLimit() {
        String sql = "SELECT param_value FROM API_PARAMS WHERE param_name = 'NICKNAME_SEARCH_LIMIT'";
        return masterTemplate.queryForObject(sql, Collections.EMPTY_MAP, Integer.class);
    }

    @Override
    public List<String> findNicknamesByPart(String part, Integer limit) {
        String sql = "SELECT DISTINCT nickname FROM " +
                "  (SELECT nickname FROM USER WHERE nickname LIKE :part_begin " +
                "  UNION " +
                "  SELECT nickname FROM USER WHERE nickname LIKE :part_middle) AS nicks " +
                "  LIMIT :lim ";
        Map<String, Object> params = new HashMap<>();
        params.put("part_begin", part + "%");
        params.put("part_middle", "%" + part + "%");
        params.put("lim", limit);
        try {
            return masterTemplate.query(sql, params, (rs, rowNum) -> rs.getString("nickname"));
        } catch (EmptyResultDataAccessException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public boolean updateLast2faNotifyDate(String email) {
        String sql = "UPDATE USER SET USER.2fa_last_notify_date =:date " +
                "WHERE USER.email = :email";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("email", email);
            put("date", LocalDate.now());
        }};
        return masterTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public List<UserIpReportDto> getUserIpReportByRoleList(List<Integer> userRoleList) {

        String sql = "SELECT U.id, U.nickname, U.email, U.regdate, f_ip.ip AS first_ip, l_ip.ip AS last_ip, l_ip.last_registration_date " +
                "FROM USER U " +
                "    JOIN (SELECT user_id, MAX(last_registration_date) AS last_date, MIN(registration_date) AS first_date " +
                "            FROM USER_IP GROUP BY user_id) AS agg ON U.id = agg.user_id " +
                "JOIN USER_IP f_ip ON U.id = f_ip.user_id AND f_ip.registration_date = agg.first_date " +
                "LEFT JOIN USER_IP l_ip ON U.id = l_ip.user_id AND l_ip.last_registration_date = agg.last_date ";
        String whereClause = "";
        Map<String, Object> params = new HashMap<>();
        if (userRoleList.size() > 0) {
            whereClause = " WHERE U.roleid IN (:roles)";
            params.put("roles", userRoleList);
        }
        return masterTemplate.query(sql + whereClause, params, (rs, rowNum) -> {
            UserIpReportDto dto = new UserIpReportDto();
            dto.setOrderNum(rowNum + 1);
            dto.setId(rs.getInt("id"));
            dto.setEmail(rs.getString("email"));
            dto.setNickname(rs.getString("nickname"));
            Timestamp creationTime = rs.getTimestamp("regdate");
            dto.setCreationTime(creationTime == null ? null : creationTime.toLocalDateTime());
            dto.setFirstIp(rs.getString("first_ip"));
            dto.setLastIp(rs.getString("last_ip"));
            Timestamp lastLoginTime = rs.getTimestamp("last_registration_date");
            dto.setLastLoginTime(lastLoginTime == null ? null : lastLoginTime.toLocalDateTime());
            return dto;
        });
    }

    @Override
    public Integer getNewRegisteredUserNumber(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM USER WHERE regdate BETWEEN STR_TO_DATE(:start_time, '%Y-%m-%d %H:%i:%s') " +
                "   AND STR_TO_DATE(:end_time, '%Y-%m-%d %H:%i:%s') ";
        Map<String, Timestamp> params = new HashMap<>();
        params.put("start_time", Timestamp.valueOf(startTime));
        params.put("end_time", Timestamp.valueOf(endTime));
        return masterTemplate.queryForObject(sql, params, Integer.class);
    }

    @Override
    public long countUserEntrance(String email) {
        final String sql = "SELECT COUNT(UI.user_id) FROM USER_IP UI JOIN USER U ON U.id=UI.user_id WHERE U.email =:email";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("email", email);
        }};
        return masterTemplate.queryForObject(sql, namedParameters, Long.class);
    }

    @Transactional(readOnly = true)
    public Integer getUserIdByGaTag(String gaTag) {
        String sql = "SELECT u.id FROM USER u WHERE u.ga =:ga";

        return masterTemplate.queryForObject(sql, Collections.singletonMap("ga", gaTag), Integer.class);

    }

    @Override
    public String getKycStatusByEmail(String email) {
        String sql = "SELECT u.kyc_status FROM USER u WHERE u.email =:email";
        return masterTemplate.queryForObject(sql, Collections.singletonMap("email", email), String.class);
    }

    @Override
    public boolean updatePrivacyDataAndKycReferenceIdByEmail(String email, String refernceUID, String country,
                                                             String firstName, String lastName, Date birthDay) {
        String sql = "UPDATE USER SET kyc_reference = :value, country = :country," +
                "firstName = :firstName, lastName = :lastName, birthDay = :birthDay WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("value", refernceUID);
        params.addValue("email", email);
        params.addValue("country", country);
        params.addValue("firstName", firstName);
        params.addValue("lastName", lastName);
        params.addValue("birthDay", birthDay);
        try {
            return masterTemplate.update(sql, params) > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Optional<User> findByKycReferenceId(String referenceId) {
        String sql = SELECT_USER + "WHERE USER.kyc_reference = :referenceId";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("referenceId", referenceId);
            }
        };
        try {
            return Optional.ofNullable(masterTemplate.queryForObject(sql, params, getUserRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public String findKycReferenceByUserEmail(String email) {
        String sql = "SELECT kyc_reference FROM USER WHERE email = :email";
        return masterTemplate.queryForObject(sql, Collections.singletonMap("email", email), String.class);
    }

    @Override
    public List<Policy> getAllPoliciesByUserId(String id) {

        String sql = "SELECT p.* FROM POLICY p " +
                "INNER JOIN USER_POLICES up ON up.policy_id = p.id " +
                "WHERE up.user_id = :id";

        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("id", id);
            }
        };
        return masterTemplate.query(sql, params, (rs, rowNum) -> {
            Policy policy = new Policy();
            policy.setId(rs.getInt("id"));
            policy.setName(rs.getString("name"));
            policy.setTitle(rs.getString("title"));
            policy.setDesciption(rs.getString("description"));
            return policy;
        });
    }

    @Override
    public boolean existPolicyByUserIdAndPolicy(int id, String policyName) {

        String sql = "SELECT COUNT(*) FROM POLICY p " +
                "INNER JOIN USER_POLICES up ON up.policy_id = p.id " +
                "WHERE up.user_id = :id AND p.name = :policy LIMIT 1";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("id", String.valueOf(id));
                put("policy", policyName);
            }
        };

        return masterTemplate.queryForObject(sql, params, Integer.class) > 0;
    }

    @Override
    public boolean updateUserPolicyByEmail(String email, PolicyEnum policyEnum) {
        String sql = "INSERT INTO USER_POLICES (user_id, policy_id) VALUES (" +
                "(SELECT u.id FROM USER u WHERE u.email = :email), " +
                "(SELECT p.id FROM POLICY p WHERE p.name = :policyName)" +
                ")";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("policyName", policyEnum.getName());

        try {
            return masterTemplate.update(sql, params) > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public IeoUserStatus findIeoUserStatusByEmail(String email) {
        // todo implement

        return null;
    }

    @Override
    public boolean updateUserRole(int userId, UserRole userRole) {
        String sql = "UPDATE USER SET roleid= :role_id WHERE id = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("role_id", userRole.getRole());
        params.addValue("user_id", userId);
        return masterTemplate.update(sql, params) > 0;
    }

}