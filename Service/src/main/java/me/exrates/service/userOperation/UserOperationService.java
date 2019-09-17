package me.exrates.service.userOperation;

import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;

import java.util.List;
import java.util.Locale;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
public interface UserOperationService {

  boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);

  List<UserOperationAuthorityOption> getUserOperationAuthorityOptions(Integer userId, Locale locale);

  void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId, String currentUserEmail);


    void updateUserOperationAuthority(UserOperationAuthorityOption options, Integer userId);
}
