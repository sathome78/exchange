package me.exrates.service.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.Payment;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.PaginationWrapper;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.UnsupportedMerchantException;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigDecimal.valueOf;
import static java.util.Collections.EMPTY_LIST;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.RefillStatusEnum.ON_BCH_EXAM;

/**
 * created by ValkSam
 */

@Service
@Conditional(MonolitConditional.class)
public class InputOutputServiceImpl implements InputOutputService {

    private static final Logger log = LogManager.getLogger("inputoutput");

    @Autowired
    private MessageSource messageSource;

    @Autowired
    InputOutputDao inputOutputDao;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    MerchantServiceContext merchantServiceContext;

    @Override
    @Transactional(readOnly = true)
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
            CacheData cacheData,
            String email,
            Integer offset, Integer limit,
            Locale locale) {
        List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
                .stream()
                .map(OperationType::getType)
                .collect(Collectors.toList());
        List<MyInputOutputHistoryDto> result = inputOutputDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyInputOutputHistoryDto>() {{
                add(new MyInputOutputHistoryDto(false));
            }};
        } else {
            setAdditionalFields(result, locale);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
            String email,
            Integer offset, Integer limit,
            Locale locale) {
        List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
                .stream()
                .map(OperationType::getType)
                .collect(Collectors.toList());
        List<MyInputOutputHistoryDto> result = inputOutputDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
        setAdditionalFields(result, locale);
        return result;
    }

    private void setAdditionalFields(List<MyInputOutputHistoryDto> inputOutputList, Locale locale) {
        inputOutputList.forEach(inout -> {
            inout.setSummaryStatus(generateAndGetSummaryStatus(inout, locale));
            inout.setButtons(generateAndGetButtonsSet(inout.getStatus(), null, false, locale));
            inout.setAuthorisedUserId(inout.getUserId());
        });
    }

    @Override
    public PaginationWrapper<List<MyInputOutputHistoryDto>> findUnconfirmedInvoices(String userEmail, String currencyName, Integer limit, Integer offset, Locale locale) {
        PaginationWrapper<List<MyInputOutputHistoryDto>> result = inputOutputDao.findUnconfirmedInvoices(userService.getIdByEmail(userEmail),
                currencyService.findByName(currencyName).getId(), limit, offset);
        setAdditionalFields(result.getData(), locale);
        return result;
    }


    @Override
    public List<Map<String, Object>> generateAndGetButtonsSet(
            InvoiceStatus status,
            InvoiceOperationPermission permittedOperation,
            boolean authorisedUserIsHolder,
            Locale locale) {
        if (status == null) return EMPTY_LIST;
        InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue = InvoiceActionTypeEnum.InvoiceActionParamsValue.builder()
                .authorisedUserIsHolder(authorisedUserIsHolder)
                .permittedOperation(permittedOperation)
                .build();
        return status.getAvailableActionList(paramsValue).stream()
                .filter(e -> e.getActionTypeButton() != null)
                .map(e -> new HashMap<String, Object>(e.getActionTypeButton().getProperty()))
                .peek(e -> e.put("buttonTitle", messageSource.getMessage((String) e.get("buttonTitle"), null, locale)))
                .collect(Collectors.toList());
    }


    private String generateAndGetSummaryStatus(MyInputOutputHistoryDto row, Locale locale) {
        log.debug("status1 {}", row);
        switch (row.getSourceType()) {
            case REFILL: {
                RefillStatusEnum status = (RefillStatusEnum) row.getStatus();
                if (status == ON_BCH_EXAM) {
                    IRefillable merchant = (IRefillable) merchantServiceContext
                            .getMerchantService(merchantService.findByName(row.getMerchantName()).getServiceBeanName());
                    String message;
                    Integer confirmationsCount = merchant.minConfirmationsRefill();
                    if (confirmationsCount == null) {
                        message = messageSource.getMessage("merchants.refill.TAKEN_FROM_EXAM", null, locale);
                    } else {
                        String confirmations = row.getConfirmation() == null ? "0" : row.getConfirmation().toString();
                        message = confirmations.concat("/").concat(confirmationsCount.toString());
                    }
                    return message;
                } else {
                    return messageSource.getMessage("merchants.refill.".concat(status.name()), null, locale);
                }
            }
            case WITHDRAW: {
                WithdrawStatusEnum status = (WithdrawStatusEnum) row.getStatus();
                return messageSource.getMessage("merchants.withdraw.".concat(status.name()), null, locale);
            }
            case USER_TRANSFER: {
                TransferStatusEnum status = (TransferStatusEnum) row.getStatus();
                return messageSource.getMessage("merchants.transfer.".concat(status.name()), null, locale);
            }
            default: {
                return row.getTransactionProvided();
            }
        }
    }

    @Override
    @Transactional
    public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail, Locale locale) {
        merchantService.checkMerchantIsBlocked(payment.getMerchant(), payment.getCurrency(), payment.getOperationType());
        OperationType operationType = payment.getOperationType();
        BigDecimal amount = valueOf(payment.getSum());
        Merchant merchant = merchantService.findById(payment.getMerchant());
        Currency currency = currencyService.findById(payment.getCurrency());
        String destination = payment.getDestination();
        String destinationTag = payment.getDestinationTag();
        if (!(merchant.getProcessType() == MerchantProcessType.CRYPTO && operationType == INPUT)) {
            try {
                merchantService.checkAmountForMinSum(merchant.getId(), currency.getId(), amount);
            } catch (EmptyResultDataAccessException e) {
                final String exceptionMessage = "MerchantService".concat(operationType == INPUT ?
                        "Input" : "Output");
                throw new UnsupportedMerchantException(exceptionMessage);
            }
        }
        User user = userService.findByEmail(userEmail);
        Wallet wallet = walletService.findByUserAndCurrency(user, currency);
        CommissionDataDto commissionData = commissionService.normalizeAmountAndCalculateCommission(
                user.getId(),
                amount,
                operationType,
                currency.getId(),
                merchant.getId(), payment.getDestinationTag());
        TransactionSourceType transactionSourceType = operationType.getTransactionSourceType();
        User recipient = null;
        try {
            if (!StringUtils.isEmpty(payment.getRecipient())) {
                recipient = userService.getIdByNickname(payment.getRecipient()) > 0 ?
                        userService.findByNickname(payment.getRecipient()) : userService.findByEmail(payment.getRecipient());
            }
        } catch (RuntimeException e) {
            throw new UserNotFoundException(messageSource.getMessage("transfer.nonExistentUser", new Object[]{payment.getRecipient()}, locale));
        }

        Wallet recipientWallet = recipient == null ? null : walletService.findByUserAndCurrency(recipient, currency);
        CreditsOperation creditsOperation = new CreditsOperation.Builder()
                .initialAmount(commissionData.getAmount())
                .amount(commissionData.getResultAmount())
                .commissionAmount(commissionData.getCompanyCommissionAmount())
                .commission(commissionData.getCompanyCommission())
                .operationType(operationType)
                .user(user)
                .currency(currency)
                .wallet(wallet)
                .merchant(merchant)
                .merchantCommissionAmount(commissionData.getMerchantCommissionAmount())
                .destination(destination)
                .destinationTag(destinationTag)
                .transactionSourceType(transactionSourceType)
                .recipient(recipient)
                .recipientWallet(recipientWallet)
                .build();
        return Optional.of(creditsOperation);
    }

    @Override
    public List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime,
                                                                     List<Integer> userRoleIdList) {
        return inputOutputDao.getInputOutputSummary(startTime, endTime, userRoleIdList);
    }

    @Override
    public List<InOutReportDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime,
                                                                     List<Integer> userRoleIdList) {
        return inputOutputDao.getInputOutputSummaryWithCommissions(startTime, endTime, userRoleIdList);
    }

    @Override
    public Integer getUserInputOutputHistoryCount(String userEmail, Integer currencyId, String currencyName,
                                                  LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                  Integer limit, Integer offset, Locale locale) {
        List<MyInputOutputHistoryDto> items = inputOutputDao.findMyInputOutputHistoryByOperationType(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                getOperationTypesList(),
                locale
        );
        return items.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyInputOutputHistoryDto> getUserInputOutputHistory(String userEmail, Integer currencyId, String currencyName,
                                                                   LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                                   Integer limit, Integer offset, Locale locale) {
        List<MyInputOutputHistoryDto> result = inputOutputDao.findMyInputOutputHistoryByOperationType(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                getOperationTypesList(),
                locale
        );
        setAdditionalFields(result, locale);
        return result;
    }

    private List<Integer> getOperationTypesList() {
        return OperationType.getInputOutputOperationsList()
                .stream()
                .map(OperationType::getType)
                .collect(Collectors.toList());
    }

}
