package me.exrates.dao.userOperation;

import lombok.extern.log4j.Log4j;
import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
@Log4j
@Repository
public class UserOperationDaoImpl implements UserOperationDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority) {
    final String sql = "SELECT user_operation_id FROM USER_OPERATION_AUTHORITY " +
            "WHERE user_id=:userId AND user_operation_id=:userOperationId AND enabled=1";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", userId);
    params.addValue("userOperationId", userOperationAuthority.getOperationId());

    try {
      return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0;
    } catch (EmptyResultDataAccessException ex) {
    return false;
    }
  }

  @Override
  public List<UserOperationAuthorityOption> getUserOperationAuthorityOption(Integer userId) {
    String sql = "SELECT UPA.user_operation_id, UPA.enabled " +
            "FROM USER_OPERATION_AUTHORITY UPA " +
            "JOIN USER_OPERATION UO ON UO.id = UPA.user_operation_id " +
            "WHERE user_id = :user_id";
    Map<String, Integer> params = Collections.singletonMap("user_id", userId);
    return namedParameterJdbcTemplate.query(sql, params, ((rs, rowNum) -> {
      UserOperationAuthorityOption option = new UserOperationAuthorityOption();
      option.setUserOperationAuthority(UserOperationAuthority.convert(rs.getInt("user_operation_id")));
      option.setEnabled(rs.getBoolean("enabled"));
      return option;
    }));
  }

  @Override
  public void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId) {
    String sql = "UPDATE USER_OPERATION_AUTHORITY SET enabled = :enabled WHERE user_id = :user_id " +
            "AND user_operation_id = :user_operation_id";
    Map<String, Object>[] batchValues = options.stream().map(option -> {
      Map<String, Object> optionValues = new HashMap<String, Object>() {{
        put("user_operation_id", option.getUserOperationAuthority().getOperationId());
        put("user_id", userId);
        put("enabled", option.getEnabled());
      }};
      return optionValues;
    }).collect(Collectors.toList()).toArray(new Map[options.size()]);
    namedParameterJdbcTemplate.batchUpdate(sql, batchValues);
  }

}

