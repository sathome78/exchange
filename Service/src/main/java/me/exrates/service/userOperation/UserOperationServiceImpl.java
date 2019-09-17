package me.exrates.service.userOperation;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserDao;
import me.exrates.dao.userOperation.UserOperationDao;
import me.exrates.model.enums.*;
import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
@Log4j2
@Service
public class UserOperationServiceImpl implements UserOperationService {

  @Autowired
  private UserOperationDao userOperationDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private MessageSource messageSource;

  @Override
  public boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority) {
      return userOperationDao.getStatusAuthorityForUserByOperation(userId, userOperationAuthority);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserOperationAuthorityOption> getUserOperationAuthorityOptions(Integer userId, Locale locale) {
    return userOperationDao.getUserOperationAuthorityOption(userId).stream()
            .peek(option -> option.localize(messageSource, locale))
            .collect(Collectors.toList());
  }

  @Override
  public void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId, String currentUserEmail) {
    UserRole currentUserRole = userDao.getUserRoles(currentUserEmail);
    UserRole updatedUserRole = userDao.getUserRoleById(userId);
    if (currentUserRole != UserRole.ADMINISTRATOR && updatedUserRole == UserRole.ADMINISTRATOR) {
      throw new ForbiddenOperationException("Status modification not permitted");
    }
    userOperationDao.updateUserOperationAuthority(options, userId);
  }

  @Override
  public void updateUserOperationAuthority(UserOperationAuthorityOption option, Integer userId) {
    userOperationDao.updateUserOperationAuthority(Collections.singletonList(option), userId);
  }
}
