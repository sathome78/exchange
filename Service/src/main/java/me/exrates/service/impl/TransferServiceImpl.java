package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.TransferRequestDao;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.PagingData;
import me.exrates.model.TransferRequest;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.VoucherAdminTableDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.VoucherFilterData;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.exception.invoice.TransferRequestAcceptExeption;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.ITransferable;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.vo.ProfileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.USER_TRANSFER;
import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.TRANSFER_VOUCHER;

/**
 * created by ValkSam
 */

@Service
public class TransferServiceImpl implements TransferService {

  private static final Logger log = LogManager.getLogger("transfer");

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private TransferRequestDao transferRequestDao;

  @Autowired
  private WalletService walletService;

  @Autowired
  private CompanyWalletService companyWalletService;

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
  InputOutputService inputOutputService;

  @Autowired
  private MerchantService merchantService;

  @Override
  @Transactional
  public Map<String, Object> createTransferRequest(TransferRequestCreateDto request) {
    ProfileData profileData = new ProfileData(1000);
    try {
      IMerchantService merchantService = merchantServiceContext.getMerchantService(request.getServiceBeanName());
      ITransferable transferMerchantService = (ITransferable) merchantService;
      request.setIsVoucher(transferMerchantService.isVoucher());
      if (transferMerchantService.recipientUserIsNeeded()) {
        checkTransferToSelf(request.getUserId(), request.getRecipientId(), request.getLocale());
      }
      Integer requestId = createTransfer(request);
      request.setId(requestId);
      Map<String, String> data = transferMerchantService.transfer(request);
      request.setHash(data.get("hash"));
      transferRequestDao.setHashById(requestId, data);
      /**/
      String notification = null;
      try {
        notification = sendTransferNotification(
            new TransferRequest(request),
            request.getMerchantDescription(),
            request.getLocale());
      } catch (final MailException e) {
        log.error(e);
      }
      profileData.setTime2();
      BigDecimal newAmount = walletService.getWalletABalance(request.getUserWalletId());
      String currency = request.getCurrencyName();
      String balance = currency + " " + currencyService.amountToString(newAmount, currency);
      Map<String, Object> result = new HashMap<>();
      result.put("message", notification);
      result.put("balance", balance);
      result.put("hash", request.getHash());
      profileData.setTime3();
      return result;
    } finally {
      profileData.checkAndLog("slow create TransferRequest: " + request + " profile: " + profileData);
    }
  }

  private void checkTransferToSelf(Integer userId, Integer recipientId, Locale locale) {
    if (userId.equals(recipientId)) {
      throw new InvalidNicknameException(messageSource
              .getMessage("transfer.selfNickname", null, locale));
    }
  }

  @Transactional(rollbackFor = {Exception.class})
  private Integer createTransfer(TransferRequestCreateDto transferRequestCreateDto) {
    TransferStatusEnum currentStatus = TransferStatusEnum.convert(transferRequestCreateDto.getStatusId());
    Boolean isVoucher = transferRequestCreateDto.getIsVoucher();
    InvoiceActionTypeEnum action = currentStatus.getStartAction(isVoucher);
    InvoiceStatus newStatus = currentStatus.nextState(action);
    transferRequestCreateDto.setStatusId(newStatus.getCode());
    int createdTransferRequestId = 0;
    if (walletService.ifEnoughMoney(
        transferRequestCreateDto.getUserWalletId(),
        transferRequestCreateDto.getAmount())) {
      if ((createdTransferRequestId = transferRequestDao.create(transferRequestCreateDto)) > 0) {
        String description = transactionDescription.get(currentStatus, action);
        if (isVoucher) {
          WalletTransferStatus result = walletService.walletInnerTransfer(
              transferRequestCreateDto.getUserWalletId(),
              transferRequestCreateDto.getAmount().negate(),
              TransactionSourceType.USER_TRANSFER,
              createdTransferRequestId,
              description);
          if (result != SUCCESS) {
            throw new TransferRequestCreationException(result.toString());
          }
        } else {
          walletService.transferCostsToUser(
              transferRequestCreateDto.getUserId(),
              transferRequestCreateDto.getUserWalletId(),
              transferRequestCreateDto.getRecipientId(),
              transferRequestCreateDto.getAmount(),
              transferRequestCreateDto.getCommission(),
              transferRequestCreateDto.getLocale(),
              createdTransferRequestId);
        }
      }
    } else {
      throw new NotEnoughUserWalletMoneyException(transferRequestCreateDto.toString());
    }
    return createdTransferRequestId;
  }

  @Override
  @Transactional
  public List<MerchantCurrency> retrieveAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies) {
    merchantCurrencies.forEach(e -> {
      IMerchantService merchantService = merchantServiceContext.getMerchantService(e.getMerchantId());
      if (merchantService instanceof ITransferable) {
        e.setRecipientUserIsNeeded(((ITransferable) merchantService).recipientUserIsNeeded());
        e.setProcessType(((ITransferable) merchantService).processType().name());
      }
    });
    return merchantCurrencies;
  }

  @Transactional
  @Override
  public void revokeByUser(int requestId, Principal principal) {
    TransferRequestFlatDto transferRequest = transferRequestDao.getFlatByIdAndBlock(requestId)
            .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    
    if (principal == null || !getUserEmailByTrnasferId(requestId).equals(principal.getName())) {
      throw new TransferRequestRevokeException();
    }
    TransferStatusEnum currentStatus = transferRequest.getStatus();
    TransferStatusEnum newStatus = (TransferStatusEnum) currentStatus.nextState(REVOKE);
    revokeTransferRequest(transferRequest, REVOKE, newStatus);
  }

  @Transactional
  @Override
  public void revokeByAdmin(int requestId, Principal principal) {
    TransferRequestFlatDto transferRequest = transferRequestDao.getFlatByIdAndBlock(requestId)
            .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
            userService.getIdByEmail(principal.getName()),
            transferRequest.getCurrencyId(),
            TRANSFER_VOUCHER
    );
    if (permission != null && REVOKE_ADMIN.getOperationPermissionOnlyList().contains(permission)) {

    }
    TransferStatusEnum currentStatus = transferRequest.getStatus();
    TransferStatusEnum newStatus = (TransferStatusEnum) currentStatus.nextState(REVOKE, InvoiceActionParamsValue.builder()
            .authorisedUserIsHolder(true)
            .permittedOperation(permission)
            .availableForCurrentContext(false).build());
    revokeTransferRequest(transferRequest, REVOKE_ADMIN, newStatus);
  }

  @Transactional
  private void revokeTransferRequest(TransferRequestFlatDto transferRequest, InvoiceActionTypeEnum action, TransferStatusEnum newStatus) {
    transferRequestDao.setStatusById(transferRequest.getId(), newStatus);
    /**/
    Integer userWalletId = walletService.getWalletId(transferRequest.getUserId(), transferRequest.getCurrencyId());
    String description = transactionDescription.get(transferRequest.getStatus(), action);
    WalletTransferStatus result = walletService.walletInnerTransfer(
        userWalletId,
        transferRequest.getAmount(),
        TransactionSourceType.USER_TRANSFER,
        transferRequest.getId(),
        description);
    if (result != SUCCESS) {
      throw new TransferRequestRevokeException(result.toString());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransferRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses) {
    return transferRequestDao.findRequestsByStatusAndMerchant(merchantId, statuses);
  }

  @Override
  @Transactional(readOnly = true)
  public TransferRequestFlatDto getFlatById(Integer id) {
    return transferRequestDao.getFlatById(id)
        .orElseThrow(() -> new TransferRequestNotFoundException(id.toString()));
  }

  private String sendTransferNotification(
      TransferRequest transferRequest,
      String merchantDescription,
      Locale locale) {
    final String notification;
    final Object[] messageParams = {
        transferRequest.getId(),
        merchantDescription
    };
    String notificationMessageCode;
    notificationMessageCode = "merchants.transferNotification.".concat(transferRequest.getStatus().name());
    notification = messageSource
        .getMessage(notificationMessageCode, messageParams, locale);
    notificationService.notifyUser(transferRequest.getUserEmail(), NotificationEvent.IN_OUT,
        "merchants.transferNotification.header", notificationMessageCode, messageParams);
    return notification;
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, String> correctAmountAndCalculateCommissionPreliminarily(
      Integer userId,
      BigDecimal amount,
      Integer currencyId,
      Integer merchantId,
      Locale locale) {
    OperationType operationType = USER_TRANSFER;
    BigDecimal addition = currencyService.computeRandomizedAddition(currencyId, operationType);
    amount = amount.add(addition);
    merchantService.checkAmountForMinSum(merchantId, currencyId, amount);
    Map<String, String> result = commissionService.computeCommissionAndMapAllToString(userId, amount, operationType, currencyId, merchantId, locale, null);
    result.put("addition", addition.toString());
    return result;
  }

  @Override
  public Optional<TransferRequestFlatDto> getByHashAndStatus(String code, Integer requiredStatus, boolean block) {
    return transferRequestDao.getFlatByHashAndStatus(code, requiredStatus, block);
  }

  @Override
  public boolean checkRequest(TransferRequestFlatDto transferRequestFlatDto, String userEmail) {
    ITransferable merchantService = (ITransferable) merchantServiceContext.getMerchantService(transferRequestFlatDto.getMerchantId());
    return !merchantService.recipientUserIsNeeded() || transferRequestFlatDto.getRecipientId().equals(userService.getIdByEmail(userEmail));
  }

  @Transactional
  @Override
  public TransferDto performTransfer(TransferRequestFlatDto dto, Locale locale, InvoiceActionTypeEnum action) {
    checkTransferToSelf(dto.getUserId(), dto.getRecipientId(), locale);
    IMerchantService merchantService = merchantServiceContext.getMerchantService(dto.getMerchantId());
    if (!(merchantService instanceof ITransferable)) {
      throw new MerchantException("not supported merchant");
    }
    if (((ITransferable) merchantService).isVoucher() && !((ITransferable) merchantService).recipientUserIsNeeded()) {
      dto.setRecipientId(userService.getIdByEmail(dto.getInitiatorEmail()));
      transferRequestDao.setRecipientById(dto.getId(), dto.getRecipientId());
    }
    TransferStatusEnum currentStatus = dto.getStatus();
    TransferStatusEnum newStatus = (TransferStatusEnum) currentStatus.nextState(action);
    if (!newStatus.isEndStatus()) {
      throw new TransferRequestAcceptExeption("invalid new status " + newStatus);
    }
    int walletId = walletService.getWalletIdAndBlock(dto.getUserId(), dto.getCurrencyId());
    WalletTransferStatus result = walletService.walletInnerTransfer(
            walletId,
            dto.getAmount(),
            TransactionSourceType.USER_TRANSFER,
            dto.getId(),
            transactionDescription.get(currentStatus, action));
    if (result != SUCCESS) {
      throw new WithdrawRequestPostException(result.name());
    }
    TransferDto resDto = walletService.transferCostsToUser(walletId, dto.getRecipientId(), dto.getAmount(), dto.getCommissionAmount(), locale, dto.getId());
    transferRequestDao.setStatusById(dto.getId(), newStatus);
    return resDto;
  }

  @Override
  public String getUserEmailByTrnasferId(int id) {
    return transferRequestDao.getCreatorEmailById(id);
  }

  @Override
  @Transactional
  public DataTable<List<VoucherAdminTableDto>> getAdminVouchersList(
          DataTableParams dataTableParams,
          VoucherFilterData withdrawFilterData,
          String authorizedUserEmail,
          Locale locale) {
    Integer authorizedUserId = userService.getIdByEmail(authorizedUserEmail);
    PagingData<List<TransferRequestFlatDto>> result = transferRequestDao.getPermittedFlat(
            authorizedUserId,
            dataTableParams,
            withdrawFilterData);
    DataTable<List<VoucherAdminTableDto>> output = new DataTable<>();
    output.setData(result.getData().stream()
            .map(VoucherAdminTableDto::new)
            .peek(e -> e.setButtons(
                    inputOutputService.generateAndGetButtonsSet(
                            e.getStatus(),
                            e.getInvoiceOperationPermission(),
                            true,
                            locale)
            ))
            .collect(Collectors.toList())
    );
    output.setRecordsTotal(result.getTotal());
    output.setRecordsFiltered(result.getFiltered());
    return output;
  }


  @Override
  public String getHash(Integer id, Principal principal) {
    TransferRequestFlatDto dto = getFlatById(id);
    if (dto == null || !dto.getCreatorEmail().equals(principal.getName())
            || !dto.getStatus().availableForAction(PRESENT_VOUCHER)) {
      throw new InvoiceNotFoundException("");
    }
    return transferRequestDao.getHashById(id);
  }
}
