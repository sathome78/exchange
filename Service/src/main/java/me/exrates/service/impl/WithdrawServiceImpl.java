package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PagingData;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.MerchantCurrencyAutoParamDto;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.WithdrawRequestsAdminTableDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.*;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.WithdrawRequestCreationException;
import me.exrates.service.exception.WithdrawRequestRevokeException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.REVOKE;

/**
 * @author ValkSam
 */
@Log4j2
public class WithdrawServiceImpl extends BaseWithdrawServiceImpl implements WithdrawService {

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private WithdrawRequestDao withdrawRequestDao;

  @Autowired
  private WalletService walletService;

  @Autowired
  private UserService userService;

  @Autowired
  private NotificationService notificationService;

  @Override
  @Transactional
  public List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList) {
    log.error("NOT IMPLEMENTED");
    throw new NotImplimentedMethod("method NOT IMPLEMENTED !");
  }

  @Override
  @Transactional(readOnly = true)
  public MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId) {
    return merchantDao.findAutoWithdrawParamsByMerchantAndCurrency(merchantId, currencyId);
  }

  @Override
  @Transactional
  public Map<String, String> withdrawRequest(
      CreditsOperation creditsOperation,
      WithdrawData withdrawData,
      String userEmail,
      Locale locale) {
    MerchantCurrencyAutoParamDto autoParamDto = getAutoWithdrawParamsByMerchantAndCurrency(
        creditsOperation.getMerchant().getId(),
        creditsOperation.getCurrency().getId());
    WithdrawStatusEnum withdrawRequestStatus = ((WithdrawStatusEnum) WithdrawStatusEnum.getBeginState()).getStartState(
        autoParamDto.getWithdrawAutoEnabled(),
        creditsOperation.getFullAmount(),
        autoParamDto.getWithdrawAutoThresholdAmount());
    WithdrawRequestCreateDto request = new WithdrawRequestCreateDto();
    request.setUserId(creditsOperation.getUser().getId());
    request.setUserEmail(creditsOperation.getUser().getEmail());
    request.setUserWalletId(creditsOperation.getWallet().getId());
    request.setCurrencyId(creditsOperation.getCurrency().getId());
    request.setAmount(creditsOperation.getFullAmount());
    request.setUserId(creditsOperation.getWallet().getUser().getId());
    request.setCommission(creditsOperation.getCommissionAmount());
    request.setCommissionId(creditsOperation.getCommission().getId());
    if (creditsOperation.getDestination().isPresent() && !creditsOperation.getDestination().get().isEmpty()) {
      request.setDestinationWallet(creditsOperation.getDestination().get());
    } else {
      request.setDestinationWallet(withdrawData.getUserAccount());
    }
    request.setMerchantId(creditsOperation.getMerchant().getId());
    creditsOperation
        .getMerchantImage()
        .ifPresent(request::setMerchantImage);
    request.setStatusId(withdrawRequestStatus.getCode());
    request.setRecipientBankName(withdrawData.getRecipientBankName());
    request.setRecipientBankCode(withdrawData.getRecipientBankCode());
    request.setUserFullName(withdrawData.getUserFullName());
    request.setRemark(withdrawData.getRemark());
    Integer requestId = createWithdrawRequest(request);
    request.setId(requestId);
    /**/
    String notification = null;
    String delayDescription = convertWithdrawAutoToString(autoParamDto.getWithdrawAutoDelaySeconds(), locale);
    try {
      notification = sendWithdrawalNotification(
          new WithdrawRequest(request),
          creditsOperation.getMerchant().getDescription(),
          delayDescription,
          locale);
    } catch (final MailException e) {
      log.error(e);
    }
    final BigDecimal newAmount = walletService.getWalletABalance(request.getUserWalletId());
    final String currency = creditsOperation.getCurrency().getName();
    final String balance = currency + " " + currencyService.amountToString(newAmount, currency);
    final Map<String, String> result = new HashMap<>();
    result.put("success", notification);
    result.put("balance", balance);
    return result;
  }

  @Transactional(rollbackFor = {Exception.class})
  private Integer createWithdrawRequest(WithdrawRequestCreateDto withdrawRequestCreateDto) {
    int createdWithdrawRequestId = 0;
    if (walletService.ifEnoughMoney(
        withdrawRequestCreateDto.getUserWalletId(),
        withdrawRequestCreateDto.getAmount())) {
      if ((createdWithdrawRequestId = withdrawRequestDao.create(withdrawRequestCreateDto)) > 0) {
        WalletTransferStatus result = walletService.walletInnerTransfer(
            withdrawRequestCreateDto.getUserWalletId(),
            withdrawRequestCreateDto.getAmount().negate(),
            TransactionSourceType.WITHDRAW,
            createdWithdrawRequestId);
        if (result != WalletTransferStatus.SUCCESS) {
          throw new WithdrawRequestCreationException(result.toString());
        }
      }
    } else {
      throw new NotEnoughUserWalletMoneyException("");
    }
    return createdWithdrawRequestId;
  }

  private String convertWithdrawAutoToString(Integer seconds, Locale locale) {
    if (seconds <= 0) {
      return "";
    }
    if (seconds > 60 * 60 - 1) {
      return String.valueOf(Math.round(seconds / (60 * 60)))
          .concat(" ")
          .concat(messageSource.getMessage("merchant.withdrawAutoDelayHour", null, locale));
    }
    if (seconds > 59) {
      return String.valueOf(Math.round(seconds / 60))
          .concat(" ")
          .concat(messageSource.getMessage("merchant.withdrawAutoDelayMinute", null, locale));
    }
    return String.valueOf(seconds)
        .concat(" ")
        .concat(messageSource.getMessage("merchant.withdrawAutoDelaySecond", null, locale));
  }

  @Override
  public Map<String, String> acceptWithdrawalRequest(int requestId, Locale locale, Principal principal) {
    log.error("NOT IMPLEMENTED");
    throw new NotImplimentedMethod("method NOT IMPLEMENTED !");
  }

  @Override
  public Map<String, Object> declineWithdrawalRequest(int requestId, Locale locale, String email) {
    log.error("NOT IMPLEMENTED");
    throw new NotImplimentedMethod("method NOT IMPLEMENTED !");
  }

  @Override
  public List<WithdrawRequest> findAllWithdrawRequests() {
    log.error("NOT IMPLEMENTED");
    throw new NotImplimentedMethod("method NOT IMPLEMENTED !");
  }

  @Override
  public DataTable<List<WithdrawRequest>> findWithdrawRequestsByStatus(Integer requestStatus, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData, String userEmail) {
    log.error("NOT IMPLEMENTED");
    throw new NotImplimentedMethod("method NOT IMPLEMENTED !");
  }

  @Override
  public DataTable<List<WithdrawRequestsAdminTableDto>> findWithdrawRequestByStatusList(
      List<Integer> requestStatus,
      DataTableParams dataTableParams,
      WithdrawFilterData withdrawFilterData,
      String authorizedUserEmail,
      Locale locale) {
    Integer authorizedUserId = userService.getIdByEmail(authorizedUserEmail);
    PagingData<List<WithdrawRequestFlatDto>> result = withdrawRequestDao.getFlatByStatus(
        requestStatus,
        authorizedUserId,
        dataTableParams,
        withdrawFilterData);
    DataTable<List<WithdrawRequestsAdminTableDto>> output = new DataTable<>();
    output.setData(result.getData().stream()
        .map(e -> new WithdrawRequestsAdminTableDto(e, withdrawRequestDao.getAdditionalDataForId(e.getId())))
        .peek(e -> e.setButtons(
            generateAndGetButtonsSet(
                e.getStatus(),
                authorizedUserId.equals(e.getAdminHolderId()),
                locale)
        ))
        .collect(Collectors.toList())
    );
    output.setRecordsTotal(result.getTotal());
    output.setRecordsFiltered(result.getFiltered());
    return output;
  }

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
    List<MyInputOutputHistoryDto> result = withdrawRequestDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<MyInputOutputHistoryDto>() {{
        add(new MyInputOutputHistoryDto(false));
      }};
    } else {
      result.forEach(e ->
      {
        e.setSummaryStatus(generateAndGetSummaryStatus(e, locale));
        e.setButtons(generateAndGetButtonsSet(e.getStatus(), false, locale));
        e.setAuthorisedUserId(e.getUserId());
      });
    }
    return result;
  }

  @Override
  @Transactional
  public void revokeWithdrawalRequest(int requestId) {
    WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    WithdrawStatusEnum newStatus = (WithdrawStatusEnum) withdrawRequest.getStatus().nextState(REVOKE);
    withdrawRequestDao.setStatusById(requestId, newStatus);
    Integer userWalletId = walletService.getWalletId(withdrawRequest.getUserId(), withdrawRequest.getCurrencyId());
    WalletTransferStatus result = walletService.walletInnerTransfer(
        userWalletId,
        withdrawRequest.getAmount(),
        TransactionSourceType.WITHDRAW,
        withdrawRequest.getId());
    if (result != WalletTransferStatus.SUCCESS) {
      throw new WithdrawRequestRevokeException(result.toString());
    }
  }
}
