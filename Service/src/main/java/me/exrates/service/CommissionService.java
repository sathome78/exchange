package me.exrates.service;

import me.exrates.model.Commission;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface CommissionService {

  Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole);

  Commission getDefaultCommission(OperationType operationType);

  BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType);

  BigDecimal getCommissionMerchant(Integer merchantId, Integer currencyId, OperationType operationType);

  List<Commission> getEditableCommissions();

  List<CommissionShortEditDto> getEditableCommissionsByRole(String role, Locale locale);

  void updateCommission(Integer id, BigDecimal value);

  void updateCommission(OperationType operationType, String roleName, BigDecimal value);

  void updateMerchantCommission(EditMerchantCommissionDto editMerchantCommissionDto);

  BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId);

  Map<String, String> computeCommissionAndMapAllToString(Integer userId, BigDecimal amount, OperationType operationType, Integer currencyId, Integer merchantId, Locale locale, String destinationTag);

  CommissionDataDto normalizeAmountAndCalculateCommission(Integer userId, BigDecimal amount, OperationType type, Integer currencyId, Integer merchantId, String destinationTag);

  BigDecimal calculateCommissionForRefillAmount(BigDecimal amount, Integer commissionId);

    @Transactional
    BigDecimal calculateMerchantCommissionForRefillAmount(BigDecimal amount, int merchantId, int currencyId);
}
