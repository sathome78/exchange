package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.RefillRequestDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.*;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.vo.ProfileData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.ActionType.SUBTRACT;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.REFILL_ACCEPTED;
import static me.exrates.model.enums.UserCommentTopicEnum.REFILL_DECLINE;
import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceRequestStatusEnum.EXPIRED;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * created by ValkSam
 */

@Service
@PropertySource(value = {"classpath:/job.properties"})
public class RefillServiceImpl implements RefillService {

  @Value("${invoice.blockNotifyUsers}")
  private Boolean BLOCK_NOTIFYING;

  private static final Logger log = LogManager.getLogger("refill");

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private RefillRequestDao refillRequestDao;

  @Autowired
  private MerchantService merchantService;

  @Autowired
  private CompanyWalletService companyWalletService;

  @Autowired
  private WalletService walletService;

  @Autowired
  private UserService userService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  TransactionDescription transactionDescription;

  @Autowired
  MerchantServiceContext merchantServiceContext;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  private UserFilesService userFilesService;

  @Autowired
  InputOutputService inputOutputService;

  @Override
  @Transactional
  public Map<String, String> createRefillRequest(RefillRequestCreateDto request) {
    ProfileData profileData = new ProfileData(1000);
    Map<String, String> result = null;
    try {
      Integer requestId = createRefill(request);
      profileData.setTime1();
      request.setId(requestId);
      IMerchantService merchantService = merchantServiceContext.getMerchantService(request.getServiceBeanName());
      profileData.setTime2();
      result = merchantService.refill(request);
      String address = result.get("address");
      if (!StringUtils.isEmpty(address)) {
        keepAddress(requestId, address);
      }
      profileData.setTime3();
    } finally {
      profileData.checkAndLog("slow create RefillRequest: " + request + " profile: " + profileData);
    }
    try {
      String notification = sendRefillNotificationAfterCreation(
          request,
          result.get("message"),
          request.getLocale());
      result.put("message", notification);
    } catch (MailException e) {
      log.error(e);
    }
    return result;
  }

  @Override
  @Transactional
  public Integer createRefillRequestByFact(RefillRequestAcceptDto requestAcceptDto) {
    String address = requestAcceptDto.getAddress();
    Integer currencyId = requestAcceptDto.getCurrencyId();
    Integer merchantId = requestAcceptDto.getMerchantId();
    BigDecimal amount = requestAcceptDto.getAmount();
    Integer userId = getUserIdByMerchantIdAndCurrencyIdAndAddress(address, merchantId, currencyId)
        .orElseThrow(() -> new CreatorForTheRefillRequestNotDefinedException(String.format("address: %s currency: %s merchant: %s amount: %s",
            address, currencyId, merchantId, amount)));
    Locale locale = new Locale(userService.getPreferedLang(userId));
    Integer commissionId = commissionService.findCommissionByTypeAndRole(INPUT, userService.getUserRoleFromDB(userId)).getId();
    RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_FACT);
    RefillRequestCreateDto request = new RefillRequestCreateDto();
    request.setUserId(userId);
    request.setStatus(beginStatus);
    request.setCurrencyId(currencyId);
    request.setMerchantId(merchantId);
    request.setAmount(amount);
    request.setCommissionId(commissionId);
    Integer requestId = createRefillByFact(request);
    request.setId(requestId);
    try {
      sendRefillNotificationAfterCreationByFact(
          request,
          locale);
    } catch (MailException e) {
      log.error(e);
    }
    return requestId;
  }

  @Override
  @Transactional
  public void confirmRefillRequest(InvoiceConfirmData invoiceConfirmData, Locale locale) {
    Integer requestId = invoiceConfirmData.getInvoiceId();
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
    RefillStatusEnum currentStatus = refillRequest.getStatus();
    InvoiceActionTypeEnum action = CONFIRM_USER;
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    /**/
    MultipartFile receiptScan = invoiceConfirmData.getReceiptScan();
    boolean emptyFile = receiptScan == null || receiptScan.isEmpty();
    if (emptyFile) {
      throw new FileLoadingException(messageSource.getMessage("refill.receiptScan.absent", null, locale));
    }
    if (!userFilesService.checkFileValidity(receiptScan) || receiptScan.getSize() > 1048576L) {
      throw new FileLoadingException(messageSource.getMessage("merchants.errorUploadReceipt", null, locale));
    }
    try {
      String scanPath = userFilesService.saveReceiptScan(refillRequest.getUserId(), refillRequest.getId(), receiptScan);
      invoiceConfirmData.setReceiptScanPath(scanPath);
    } catch (IOException e) {
      throw new FileLoadingException(messageSource.getMessage("merchants.errorUploadReceipt", null, locale));
    }
    refillRequestDao.setStatusAndConfirmationDataById(requestId, newStatus, invoiceConfirmData);
  }

  private Optional<Integer> getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId) {
    List<InvoiceStatus> statusList = RefillStatusEnum.getAvailableForActionStatusesList(START_BCH_EXAMINE);
    return refillRequestDao.findIdWithoutConfirmationsByMerchantIdAndCurrencyIdAndAddressAndStatusId(
        address,
        merchantId,
        currencyId,
        statusList.stream().map(InvoiceStatus::getCode).collect(Collectors.toList()));
  }

  /**
   * findUnpaidBtcPayments
   */
  @Override
  @Transactional
  public List<RefillRequestFlatDto> getInPendingByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId) {
    List<InvoiceStatus> statusList = RefillStatusEnum.getAvailableForActionStatusesList(START_BCH_EXAMINE);
    return refillRequestDao.findAllWithoutConfirmationsByMerchantIdAndCurrencyIdAndStatusId(
        merchantId,
        currencyId,
        statusList.stream().map(InvoiceStatus::getCode).collect(Collectors.toList()));
  }

  /**
   * findUnconfirmedBtcPayments
   */
  @Override
  @Transactional
  public List<RefillRequestFlatDto> getInExamineByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId) {
    List<InvoiceStatus> statusList = RefillStatusEnum.getAvailableForActionStatusesList(ACCEPT_AUTO);
    return refillRequestDao.findAllWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(
        merchantId,
        currencyId,
        statusList);
  }

  @Override
  @Transactional
  public Optional<Integer> getUserIdByMerchantIdAndCurrencyIdAndAddress(String address, Integer merchantId, Integer currencyId) {
    return refillRequestDao.findUserIdByMerchantIdAndCurrencyIdAndAddress(address, merchantId, currencyId);
  }

  /**
   *
   * markStartConfirmationProcessing
   */
  @Override
  @Transactional
  public void putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto onBchExamDto) throws RefillRequestNotFountException {
    Optional<Integer> requestIdOptional = getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(
        onBchExamDto.getAddress(),
        onBchExamDto.getMerchantId(),
        onBchExamDto.getCurrencyId());
    if (requestIdOptional.isPresent()) {
      Integer requestId = requestIdOptional.get();
      onBchExamDto.setRequestId(requestId);
      RefillRequestFlatDto refillRequestFlatDto = putOnBchExam(onBchExamDto);
      /**/
      Locale locale = new Locale(userService.getPreferedLang(refillRequestFlatDto.getUserId()));
      String title = messageSource.getMessage("refill.accepted.title", new Integer[]{requestId}, locale);
      String comment = messageSource.getMessage("merchants.refillNotification.".concat(refillRequestFlatDto.getStatus().name()),
          new Integer[]{requestId},
          locale);
      String userEmail = userService.getEmailById(refillRequestFlatDto.getUserId());
      userService.addUserComment(REFILL_ACCEPTED, comment, userEmail, false);
      notificationService.notifyUser(refillRequestFlatDto.getUserId(), NotificationEvent.IN_OUT, title, comment);
    } else {
      throw new RefillRequestNotFountException(onBchExamDto.toString());
    }
  }

  private RefillRequestFlatDto putOnBchExam(RefillRequestPutOnBchExamDto onBchExamDto) {
    Integer requestId = onBchExamDto.getRequestId();
    String hash = onBchExamDto.getHash();
    BigDecimal amount = onBchExamDto.getAmount();
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
    RefillStatusEnum currentStatus = refillRequest.getStatus();
    InvoiceActionTypeEnum action = START_BCH_EXAMINE;
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    refillRequestDao.setStatusById(requestId, newStatus);
    refillRequestDao.setHashById(requestId, hash);
    refillRequest.setStatus(newStatus);
    refillRequest.setHash(hash);
    refillRequestDao.setConfirmationsNumberByRequestId(requestId, amount, 0);
    return refillRequest;
  }

  /**
   * changeTransactionConfidenceForPendingPayment
   * updateFactAmountForPendingPayment
   * updatePendingPaymentHash
   */
  @Override
  @Transactional
  public void setConfirmationCollectedNumber(RefillRequestSetConfirmationsNumberDto confirmationsNumberDto) {
    Integer requestId = confirmationsNumberDto.getRequestId();
    String hash = confirmationsNumberDto.getHash();
    BigDecimal amount = confirmationsNumberDto.getAmount();
    Integer confirmations = confirmationsNumberDto.getConfirmations();
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
    RefillStatusEnum currentStatus = refillRequest.getStatus();
    if (!currentStatus.availableForAction(ACCEPT_AUTO)) {
      throw new RefillRequestIllegalStatusException(refillRequest.toString());
    }
    if (!hash.equals(refillRequest.getHash())) {
      refillRequestDao.setHashById(requestId, hash);
    }
    refillRequestDao.setConfirmationsNumberByRequestId(requestId, amount, confirmations);
  }

  @Override
  @Transactional
  public void autoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestNotFountException {
    Integer requestId = requestAcceptDto.getRequestId();
    if (requestId == null) {
      Optional<Integer> requestIdOptional = getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(
          requestAcceptDto.getAddress(),
          requestAcceptDto.getMerchantId(),
          requestAcceptDto.getCurrencyId());
      if (requestIdOptional.isPresent()) {
        requestId = requestIdOptional.get();
      }
    }
    if (requestId != null) {
      requestAcceptDto.setRequestId(requestId);
      RefillRequestFlatDto refillRequestFlatDto = acceptRefill(requestAcceptDto);
      /**/
      Locale locale = new Locale(userService.getPreferedLang(refillRequestFlatDto.getUserId()));
      String title = messageSource.getMessage("refill.accepted.title", new Integer[]{requestId}, locale);
      String comment = messageSource.getMessage("merchants.refillNotification.".concat(refillRequestFlatDto.getStatus().name()),
          new Integer[]{requestId},
          locale);
      String userEmail = userService.getEmailById(refillRequestFlatDto.getUserId());
      userService.addUserComment(REFILL_ACCEPTED, comment, userEmail, false);
      notificationService.notifyUser(refillRequestFlatDto.getUserId(), NotificationEvent.IN_OUT, title, comment);
    } else {
      throw new RefillRequestNotFountException(requestAcceptDto.toString());
    }
  }

  @Override
  @Transactional
  public void acceptRefillRequest(RefillRequestAcceptDto requestAcceptDto) {
    Integer requestId = requestAcceptDto.getRequestId();
    RefillRequestFlatDto refillRequestFlatDto = acceptRefill(requestAcceptDto);
      /**/
    Locale locale = new Locale(userService.getPreferedLang(refillRequestFlatDto.getUserId()));
    String title = messageSource.getMessage("refill.accepted.title", new Integer[]{requestId}, locale);
    String comment = messageSource.getMessage("merchants.refillNotification.".concat(refillRequestFlatDto.getStatus().name()),
        new Integer[]{requestId},
        locale);
    String userEmail = userService.getEmailById(refillRequestFlatDto.getUserId());
    userService.addUserComment(REFILL_ACCEPTED, comment, userEmail, false);
    notificationService.notifyUser(refillRequestFlatDto.getUserId(), NotificationEvent.IN_OUT, title, comment);
  }

  private RefillRequestFlatDto acceptRefill(RefillRequestAcceptDto requestAcceptDto) {
    ProfileData profileData = new ProfileData(1000);
    Integer requestId = requestAcceptDto.getRequestId();
    BigDecimal factAmount = requestAcceptDto.getAmount();
    Integer requesterAdminId = requestAcceptDto.getRequesterAdminId();
    String remark = requestAcceptDto.getRemark();
    String merchantTransactionId = requestAcceptDto.getMerchantTransactionId();
    String hash = requestAcceptDto.getHash();
    try {
      RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
          .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
      RefillStatusEnum currentStatus = refillRequest.getStatus();
      InvoiceActionTypeEnum action = refillRequest.getStatus().availableForAction(ACCEPT_HOLDED) ? ACCEPT_HOLDED : ACCEPT_AUTO;
      RefillStatusEnum newStatus = requesterAdminId == null ?
          (RefillStatusEnum) currentStatus.nextState(action) :
          checkPermissionOnActionAndGetNewStatus(requesterAdminId, refillRequest, action);
      refillRequestDao.setStatusById(requestId, newStatus);
      refillRequestDao.setHolderById(requestId, requesterAdminId);
      refillRequestDao.setRemarkById(requestId, remark);
      refillRequestDao.setMerchantTransactionIdById(requestId, merchantTransactionId);
      refillRequestDao.setHashById(requestId, hash);
      refillRequest.setStatus(newStatus);
      refillRequest.setAdminHolderId(requesterAdminId);
      refillRequest.setRemark(remark);
      refillRequest.setMerchantTransactionId(merchantTransactionId);
      refillRequest.setHash(hash);
      profileData.setTime1();
      /**/
      Integer userWalletId = walletService.getWalletId(refillRequest.getUserId(), refillRequest.getCurrencyId());
      /**/
      BigDecimal commission = commissionService.calculateCommissionForRefillAmount(factAmount, refillRequest.getCommissionId());
      BigDecimal amountToEnroll = BigDecimalProcessing.doAction(factAmount, commission, SUBTRACT);
      /**/
      WalletOperationData walletOperationData = new WalletOperationData();
      walletOperationData.setOperationType(INPUT);
      walletOperationData.setWalletId(userWalletId);
      walletOperationData.setAmount(amountToEnroll);
      walletOperationData.setBalanceType(ACTIVE);
      walletOperationData.setCommission(new Commission(refillRequest.getCommissionId()));
      walletOperationData.setCommissionAmount(commission);
      walletOperationData.setSourceType(TransactionSourceType.REFILL);
      walletOperationData.setSourceId(refillRequest.getId());
      String description = transactionDescription.get(currentStatus, action);
      walletOperationData.setDescription(description);
      WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
      if (walletTransferStatus != SUCCESS) {
        throw new RefillRequestRevokeException(walletTransferStatus.name());
      }
      profileData.setTime2();
      CompanyWallet companyWallet = companyWalletService.findByCurrency(new Currency(refillRequest.getCurrencyId()));
      companyWalletService.deposit(
          companyWallet,
          amountToEnroll,
          walletOperationData.getCommissionAmount()
      );
      profileData.setTime3();
      return refillRequest;
    } finally {
      profileData.checkAndLog("slow accept RefillRequest: " + requestId + " profile: " + profileData);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public RefillRequestFlatDto getFlatById(Integer id) {
    return refillRequestDao.getFlatById(id)
        .orElseThrow(() -> new InvoiceNotFoundException(id.toString()));
  }

  @Override
  @Transactional
  public void revokeRefillRequest(int requestId) {
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
    RefillStatusEnum currentStatus = refillRequest.getStatus();
    InvoiceActionTypeEnum action = REVOKE;
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    refillRequestDao.setStatusById(requestId, newStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceBank> findBanksForCurrency(Integer currencyId) {
    return refillRequestDao.findInvoiceBanksByCurrency(currencyId);
  }

  @Override
  @Transactional
  public Map<String, String> correctAmountAndCalculateCommission(
      Integer userId,
      BigDecimal amount,
      Integer currencyId,
      Integer merchantId,
      Locale locale) {

    OperationType operationType = INPUT;
    BigDecimal addition = currencyService.computeRandomizedAddition(currencyId, operationType);
    amount = amount.add(addition);
    merchantService.checkAmountForMinSum(merchantId, currencyId, amount);
    Map<String, String> result = commissionService.computeCommissionAndMapAllToString(userId, amount, operationType, currencyId, merchantId, locale);
    result.put("addition", addition.toString());
    return result;
  }

  @Override
  @Transactional
  public Integer clearExpiredInvoices() throws Exception {
    List<Integer> invoiceRequestStatusIdList = InvoiceRequestStatusEnum.getAvailableForActionStatusesList(EXPIRE).stream()
        .map(InvoiceStatus::getCode)
        .collect(Collectors.toList());
    List<OperationUserDto> userForNotificationList = new ArrayList<>();
    List<MerchantCurrencyLifetimeDto> merchantCurrencyList = merchantService.getMerchantCurrencyWithRefillLifetime();
    for (MerchantCurrencyLifetimeDto merchantCurrency : merchantCurrencyList) {
      Integer intervalHours = merchantCurrency.getRefillLifetimeHours();
      Integer merchantId = merchantCurrency.getMerchantId();
      Integer currencyId = merchantCurrency.getCurrencyId();
      Optional<LocalDateTime> nowDate = refillRequestDao.getAndBlockByIntervalAndStatus(
          merchantId,
          currencyId,
          intervalHours,
          invoiceRequestStatusIdList);
      if (nowDate.isPresent()) {
        refillRequestDao.setNewStatusByDateIntervalAndStatus(
            merchantId,
            currencyId,
            nowDate.get(),
            intervalHours,
            EXPIRED.getCode(),
            invoiceRequestStatusIdList);
        userForNotificationList.addAll(refillRequestDao.findInvoicesListByStatusChangedAtDate(
            merchantId,
            currencyId,
            EXPIRED.getCode(),
            nowDate.get()));
      }
    }
    if (!BLOCK_NOTIFYING) {
      for (OperationUserDto invoice : userForNotificationList) {
        notificationService.notifyUser(invoice.getUserId(), NotificationEvent.IN_OUT, "merchants.refillNotification.header",
            "merchants.refillNotification." + EXPIRED, new Integer[]{invoice.getId()});
      }
    }
    return userForNotificationList.size();
  }

  @Override
  @Transactional
  public DataTable<List<RefillRequestsAdminTableDto>> getRefillRequestByStatusList(
      List<Integer> requestStatus,
      DataTableParams dataTableParams,
      RefillFilterData refillFilterData,
      String authorizedUserEmail,
      Locale locale) {
    Integer authorizedUserId = userService.getIdByEmail(authorizedUserEmail);
    PagingData<List<RefillRequestFlatDto>> result = refillRequestDao.getPermittedFlatByStatus(
        requestStatus,
        authorizedUserId,
        dataTableParams,
        refillFilterData);
    DataTable<List<RefillRequestsAdminTableDto>> output = new DataTable<>();
    output.setData(result.getData().stream()
        .map(e -> new RefillRequestsAdminTableDto(e, refillRequestDao.getAdditionalDataForId(e.getId())))
        .peek(e -> e.setButtons(
            inputOutputService.generateAndGetButtonsSet(
                e.getStatus(),
                e.getInvoiceOperationPermission(),
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
  public boolean checkInputRequestsLimit(int currencyId, String email) {
    return refillRequestDao.checkInputRequests(currencyId, email);
  }

  @Override
  @Transactional
  public void revokeWithdrawalRequest(int requestId) {
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
    RefillStatusEnum currentStatus = refillRequest.getStatus();
    InvoiceActionTypeEnum action = REVOKE;
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    refillRequestDao.setStatusById(requestId, newStatus);
  }

  @Override
  @Transactional
  public void takeInWorkRefillRequest(int requestId, Integer requesterAdminId) {
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
    InvoiceActionTypeEnum action = TAKE_TO_WORK;
    RefillStatusEnum newStatus = checkPermissionOnActionAndGetNewStatus(requesterAdminId, refillRequest, action);
    refillRequestDao.setStatusById(requestId, newStatus);
    /**/
    refillRequestDao.setHolderById(requestId, requesterAdminId);
  }

  @Override
  @Transactional
  public void returnFromWorkRefillRequest(int requestId, Integer requesterAdminId) {
    RefillRequestFlatDto withdrawRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
    InvoiceActionTypeEnum action = RETURN_FROM_WORK;
    RefillStatusEnum newStatus = checkPermissionOnActionAndGetNewStatus(requesterAdminId, withdrawRequest, action);
    refillRequestDao.setStatusById(requestId, newStatus);
    /**/
    refillRequestDao.setHolderById(requestId, null);
  }

  @Override
  @Transactional
  public void declineRefillRequest(int requestId, Integer requesterAdminId, String comment) {
    ProfileData profileData = new ProfileData(1000);
    try {
      RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
          .orElseThrow(() -> new InvoiceNotFoundException(String.format("refill request id: %s", requestId)));
      RefillStatusEnum currentStatus = refillRequest.getStatus();
      InvoiceActionTypeEnum action = refillRequest.getStatus().availableForAction(DECLINE_HOLDED) ? DECLINE_HOLDED : DECLINE;
      RefillStatusEnum newStatus = checkPermissionOnActionAndGetNewStatus(requesterAdminId, refillRequest, action);
      refillRequestDao.setStatusById(requestId, newStatus);
      refillRequestDao.setHolderById(requestId, requesterAdminId);
      profileData.setTime1();
      /**/
      Locale locale = new Locale(userService.getPreferedLang(refillRequest.getUserId()));
      String title = messageSource.getMessage("refill.declined.title", new Integer[]{requestId}, locale);
      if (StringUtils.isEmpty(comment)) {
        comment = messageSource.getMessage("merchants.refillNotification.".concat(newStatus.name()), new Integer[]{requestId}, locale);
      }
      String userEmail = userService.getEmailById(refillRequest.getUserId());
      userService.addUserComment(REFILL_DECLINE, comment, userEmail, false);
      notificationService.notifyUser(refillRequest.getUserId(), NotificationEvent.IN_OUT, title, comment);
      profileData.setTime3();
    } finally {
      profileData.checkAndLog("slow decline RefillRequest: " + requestId + " profile: " + profileData);
    }
  }

  private Integer createRefill(RefillRequestCreateDto request) {
    RefillStatusEnum currentStatus = request.getStatus();
    Merchant merchant = merchantDao.findById(request.getMerchantId());
    InvoiceActionTypeEnum action = currentStatus.getStartAction(merchant);
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    request.setStatus(newStatus);
    return refillRequestDao.create(request);
  }

  private Integer createRefillByFact(RefillRequestCreateDto request) {
    return refillRequestDao.create(request);
  }

  private void keepAddress(Integer requestId, String address) {
    refillRequestDao.setAddressById(requestId, address);
  }

  private String sendRefillNotificationAfterCreation(
      RefillRequestCreateDto request,
      String addMessage,
      Locale locale) {
    String title = messageSource.getMessage("merchants.refillNotification.header", null, locale);
    Integer lifetime = merchantService.getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(request.getMerchantId(), request.getCurrencyId()).getRefillLifetimeHours();
    String mainNotificationMessageCodeSuffix = lifetime == 0 ? "" : ".lifetime";
    String mainNotificationMessageCode = "merchants.refillNotification.".concat(request.getStatus().name()).concat(mainNotificationMessageCodeSuffix);
    Object[] messageParams = {
        request.getId(),
        request.getMerchantDescription(),
        lifetime
    };
    String mainNotification = messageSource.getMessage(mainNotificationMessageCode, messageParams, locale);
    String fullNotification = StringUtils.isEmpty(addMessage) ? mainNotification : mainNotification.concat("<br>").concat("<br>").concat(addMessage);
    notificationService.notifyUser(request.getUserId(), NotificationEvent.IN_OUT, title, fullNotification);
    return fullNotification;
  }

  private String sendRefillNotificationAfterCreationByFact(
      RefillRequestCreateDto request,
      Locale locale) {
    String title = messageSource.getMessage("merchants.refillNotification.header", null, locale);
    String mainNotificationMessageCodeSuffix = "";
    String mainNotificationMessageCode = "merchants.refillNotification.".concat(request.getStatus().name()).concat(mainNotificationMessageCodeSuffix);
    Object[] messageParams = {
        request.getId(),
        request.getMerchantDescription()
    };
    String notification = messageSource.getMessage(mainNotificationMessageCode, messageParams, locale);
    notificationService.notifyUser(request.getUserId(), NotificationEvent.IN_OUT, title, notification);
    return notification;
  }

  private RefillStatusEnum checkPermissionOnActionAndGetNewStatus(Integer requesterAdminId, RefillRequestFlatDto refillRequest, InvoiceActionTypeEnum action) {
    Boolean requesterAdminIsHolder = requesterAdminId.equals(refillRequest.getAdminHolderId());
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
        requesterAdminId,
        refillRequest.getCurrencyId(),
        REFILL
    );
    InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue = InvoiceActionTypeEnum.InvoiceActionParamsValue.builder()
        .authorisedUserIsHolder(requesterAdminIsHolder)
        .permittedOperation(permission)
        .build();
    return (RefillStatusEnum) refillRequest.getStatus().nextState(action, paramsValue);
  }


}
