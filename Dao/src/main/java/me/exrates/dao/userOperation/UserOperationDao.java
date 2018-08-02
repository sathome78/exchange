package me.exrates.dao.userOperation;

import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
public interface UserOperationDao {

  boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);

  List<UserOperationAuthorityOption> getUserOperationAuthorityOption(Integer userId);

  void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId);


}