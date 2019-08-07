package me.exrates.service.impl;

import lombok.RequiredArgsConstructor;
import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.ComissionCountDto;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.merchantStrategy.IWithdrawable;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.math.BigDecimal.*;
import static java.math.BigDecimal.ZERO;
import static me.exrates.model.enums.ActionType.*;
import static me.exrates.model.enums.OperationType.*;

@Service
@RequiredArgsConstructor
@Conditional(MonolitConditional.class)
public class CommissionServiceImpl implements CommissionService {
    @Autowired
    CommissionDao commissionDao;

    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    MerchantServiceContext merchantServiceContext;

    @Autowired
    private MessageSource messageSource;

    @Override
    public Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole) {
        return commissionDao.getCommission(operationType, userRole);
    }

    @Override
    public Commission getDefaultCommission(OperationType operationType) {
        return commissionDao.getDefaultCommission(operationType);
    }

    @Override
    @Transactional
    public BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType) {
        if (!(operationType == OperationType.INPUT || operationType == OperationType.OUTPUT)) {
            throw new IllegalArgumentException("Invalid operation type");
        }
        return commissionDao.getCommissionMerchant(merchant, currency, operationType);
    }

    @Override
    @Transactional
    public BigDecimal getCommissionMerchant(Integer merchantId, Integer currencyId, OperationType operationType) {
        if (!(operationType == OperationType.INPUT || operationType == OperationType.OUTPUT || operationType == OperationType.USER_TRANSFER)) {
            throw new IllegalArgumentException("Invalid operation type: " + operationType);
        }
        return commissionDao.getCommissionMerchant(merchantId, currencyId, operationType);
    }

    @Override
    public List<Commission> getEditableCommissions() {
        return commissionDao.getEditableCommissions();
    }

    @Override
    public List<CommissionShortEditDto> getEditableCommissionsByRole(String roleName, Locale locale) {
        return commissionDao.getEditableCommissionsByRoles(userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), locale);
    }

    @Override
    @Transactional
    public void updateCommission(Integer id, BigDecimal value) {
        commissionDao.updateCommission(id, value);
    }

    @Override
    @Transactional
    public void updateCommission(OperationType operationType, String roleName, BigDecimal value) {
        commissionDao.updateCommission(operationType, userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), value);
    }


    @Override
    @Transactional
    public void updateMerchantCommission(EditMerchantCommissionDto editMerchantCommissionDto) {
        commissionDao.updateMerchantCurrencyCommission(editMerchantCommissionDto);
    }

    @Override
    public BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId) {
        return commissionDao.getMinFixedCommission(currencyId, merchantId);
    }

    @Override
    @Transactional
    public Map<String, String> computeCommissionAndMapAllToString(
            Integer userId,
            BigDecimal amount,
            OperationType type,
            Integer currencyId,
            Integer merchantId,
            Locale locale,
            String destinationTag) {
        Map<String, String> result = new HashMap<>();
        CommissionDataDto commissionData = normalizeAmountAndCalculateCommission(userId, amount, type, currencyId, merchantId, destinationTag);
        result.put("amount", commissionData.getAmount().toPlainString());
        if (!commissionData.getSpecificMerchantComissionCount()) {

            String merchantCommissionRate;
            if(type.equals(INPUT)){
                merchantCommissionRate = BigDecimalProcessing.formatLocale(commissionData.getMerchantCommissionRate(), locale, false)
                                                + commissionData.getMerchantCommissionUnit();
            } else {
                merchantCommissionRate = messageSource.getMessage("merchant.commission.rateWithLimit",
                        new Object[]{BigDecimalProcessing.formatLocale(commissionData.getMerchantCommissionRate(), locale, false)
                                + commissionData.getMerchantCommissionUnit(),
                                String.join("", BigDecimalProcessing.formatLocale(commissionData.getMinMerchantCommissionAmount(), locale, false),
                                        " ", currencyService.getCurrencyName(currencyId))}, locale);
            }
            result.put("merchantCommissionRate", String.join("", "(", merchantCommissionRate, ")"));
        } else {
            result.put("merchantCommissionRate", "");
        }
        result.put("merchantCommissionAmount", commissionData.getMerchantCommissionAmount().toPlainString());
        result.put("companyCommissionRate", String.join("", "(", BigDecimalProcessing.formatLocale(commissionData.getCompanyCommissionRate(), locale, false),
                commissionData.getCompanyCommissionUnit(), ")"));
        result.put("companyCommissionAmount", commissionData.getCompanyCommissionAmount().toPlainString());
        result.put("totalCommissionAmount", commissionData.getTotalCommissionAmount().toPlainString());
        result.put("resultAmount", commissionData.getResultAmount().toPlainString());
        return result;
    }

    @Override
    @Transactional
    public CommissionDataDto normalizeAmountAndCalculateCommission(Integer userId,
                                                                   BigDecimal amount,
                                                                   OperationType type,
                                                                   Integer currencyId,
                                                                   Integer merchantId, String destinationTag) {
        Boolean specMerchantComissionCount = false;
        Commission companyCommission;
        if (type == OperationType.OUTPUT && currencyService.isIco(currencyId)) {
            companyCommission = Commission.zeroComission();
        } else {
            companyCommission = findCommissionByTypeAndRole(type, userService.getUserRoleFromDB(userId));
        }
        BigDecimal companyCommissionRate = companyCommission.getValue();
        String companyCommissionUnit = "%";
        Merchant merchant = merchantService.findById(merchantId);
        if (!(merchant.getProcessType() == MerchantProcessType.CRYPTO) || amount.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal merchantCommissionRate = getCommissionMerchant(merchantId, currencyId, type);
            BigDecimal merchantMinFixedCommission = getMinFixedCommission(currencyId, merchantId);
            BigDecimal merchantCommissionAmount;
            BigDecimal companyCommissionAmount;
            String merchantCommissionUnit = "%";
            if (type == INPUT) {
                int currencyScale = merchantService.getMerchantCurrencyScaleByMerchantIdAndCurrencyId(merchantId, currencyId).getScaleForRefill();
                amount = amount.setScale(currencyScale, ROUND_DOWN);
                merchantCommissionAmount = BigDecimalProcessing.doAction(amount, merchantCommissionRate, MULTIPLY_PERCENT);
                companyCommissionAmount = BigDecimalProcessing.doAction(amount.subtract(merchantCommissionAmount), companyCommissionRate, MULTIPLY_PERCENT);
            } else if (type == OUTPUT) {
                IWithdrawable wMerchant = (IWithdrawable) merchantServiceContext.getMerchantService(merchantId);
                int currencyScale = merchantService.getMerchantCurrencyScaleByMerchantIdAndCurrencyId(merchantId, currencyId).getScaleForWithdraw();
                amount = amount.setScale(currencyScale, ROUND_DOWN);
                companyCommissionAmount = BigDecimalProcessing.doAction(amount, companyCommissionRate, MULTIPLY_PERCENT).setScale(currencyScale, ROUND_UP);
                if (wMerchant.specificWithdrawMerchantCommissionCountNeeded()) {
                    merchantCommissionAmount = wMerchant.countSpecCommission(amount, destinationTag, merchantId);
                    specMerchantComissionCount = true;
                } else {
                    merchantCommissionAmount = BigDecimalProcessing.doAction(amount.subtract(companyCommissionAmount), merchantCommissionRate, MULTIPLY_PERCENT).setScale(currencyScale, ROUND_UP);
                }
                if (merchantCommissionAmount.compareTo(merchantMinFixedCommission) < 0) {
                    merchantCommissionAmount = merchantMinFixedCommission;
                }
            } else if (type == USER_TRANSFER) {
                int currencyScale = merchantService.getMerchantCurrencyScaleByMerchantIdAndCurrencyId(merchantId, currencyId).getScaleForTransfer();
                amount = amount.setScale(currencyScale, ROUND_DOWN);
                companyCommissionAmount = BigDecimal.ZERO;
                merchantCommissionRate = BigDecimalProcessing.doAction(merchantCommissionRate, companyCommissionRate, ADD);
                merchantCommissionAmount = BigDecimalProcessing.doAction(amount, merchantCommissionRate, MULTIPLY_PERCENT).setScale(currencyScale, ROUND_HALF_UP);
                if (merchantCommissionAmount.compareTo(merchantMinFixedCommission) < 0) {
                    merchantCommissionAmount = merchantMinFixedCommission;
                }
            } else {
                throw new IllegalOperationTypeException(type.name());
            }
            BigDecimal totalCommissionAmount = BigDecimalProcessing.doAction(merchantCommissionAmount, companyCommissionAmount, ADD);
            BigDecimal totalAmount = BigDecimalProcessing.doAction(amount, totalCommissionAmount, SUBTRACT);
            if (totalAmount.compareTo(ZERO) <= 0) {
                throw new InvalidAmountException(String.format("Commission %s exceeds amount %s",
                        BigDecimalProcessing.formatNonePoint(totalCommissionAmount, false),
                        BigDecimalProcessing.formatNonePoint(amount, false)));
            }
            return new CommissionDataDto(
                    amount,
                    merchantCommissionRate,
                    merchantMinFixedCommission,
                    merchantCommissionUnit,
                    merchantCommissionAmount,
                    companyCommission,
                    companyCommissionRate,
                    companyCommissionUnit,
                    companyCommissionAmount,
                    totalCommissionAmount,
                    totalAmount,
                    specMerchantComissionCount

            );
        } else {
            return new CommissionDataDto(
                    ZERO,
                    ZERO,
                    ZERO,
                    "",
                    ZERO,
                    companyCommission,
                    companyCommissionRate,
                    companyCommissionUnit,
                    ZERO,
                    ZERO,
                    ZERO,
                    specMerchantComissionCount
            );
        }
    }


    @Override
    @Transactional
    public BigDecimal calculateCommissionForRefillAmount(BigDecimal amount, Integer commissionId) {
        BigDecimal companyCommissionRate = commissionDao.getCommissionById(commissionId).getValue();
        return BigDecimalProcessing.doAction(amount, companyCommissionRate, MULTIPLY_PERCENT);
    }

    @Override
    @Transactional
    public BigDecimal calculateMerchantCommissionForRefillAmount(BigDecimal amount, int merchantId, int currencyId) {
        BigDecimal merchantCommissionPercent = getCommissionMerchant(merchantId, currencyId, INPUT);
        return BigDecimalProcessing.doAction(amount, merchantCommissionPercent, MULTIPLY_PERCENT);
    }

    @Override
    public List<ComissionCountDto> getComissionsCount(LocalDateTime from, LocalDateTime to) {
        return commissionDao.countComissinsByPeriod(from, to);
    }

}
