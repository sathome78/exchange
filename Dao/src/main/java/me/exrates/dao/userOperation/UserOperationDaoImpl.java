package me.exrates.dao.userOperation;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.MerchantDao;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.MerchantImage;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.MerchantImageShortenedDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.userOperation.UserOperationAuthorityEntity;
import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    final String sql = "SELECT user_id, user_operation_id, enabled FROM USER_OPERATION_AUTHORITY " +
            "WHERE user_id=:userId AND user_operation_id=:userOperationId AND enabled=1";

    Map<String, Integer> namedParameters = new HashMap<>();
    namedParameters.put("userId", userId);
    namedParameters.put("userOperationId", userOperationAuthority.getOperationId());

    return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, i) -> {
      UserOperationAuthorityEntity userOperationAuthorityEntity = new UserOperationAuthorityEntity();
      userOperationAuthorityEntity.setUserId(rs.getInt("user_id"));
      userOperationAuthorityEntity.setUserOperationId(rs.getInt("user_operation_id"));
      return userOperationAuthorityEntity;
    });
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

