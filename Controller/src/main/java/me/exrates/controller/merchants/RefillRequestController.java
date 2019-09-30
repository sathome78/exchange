package me.exrates.controller.merchants;

import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceBank;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.User;
import me.exrates.model.dto.*;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.PaginationWrapper;
import me.exrates.service.*;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.process.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.userOperation.UserOperationService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * created by ValkSam
 */
@Controller
public class RefillRequestController {

    private static final Logger log = LogManager.getLogger("refill");

    @Autowired
    private MessageSource messageSource;

    @Autowired
    RefillService refillService;

    @Autowired
    UserService userService;

    @Autowired
    private UserOperationService userOperationService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    private InputOutputService inputOutputService;
    @Autowired
    private LocaleResolver localeResolver;

  @RequestMapping(value = "/refill/request/create", method = POST)
  @ResponseBody
  public Map<String, Object> createRefillRequest(
          @RequestBody RefillRequestParamsDto requestParamsDto,
          Principal principal,
          Locale locale, HttpServletRequest servletRequest) {
    if (requestParamsDto.getOperationType() != INPUT) {
      throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
    }
    boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(servletRequest.getUserPrincipal().getName()), UserOperationAuthority.INPUT);
    if(!accessToOperationForUser) {
      throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, localeResolver.resolveLocale(servletRequest)));
    }
    if (!refillService.checkInputRequestsLimit(requestParamsDto.getCurrency(), principal.getName())) {
      throw new RequestLimitExceededException(messageSource.getMessage("merchants.InputRequestsLimit", null, locale));
    }

    User user = userService.findByEmail(principal.getName());
    MerchantCurrency merchantCurrency = merchantService
            .findByMerchantAndCurrency(requestParamsDto.getMerchant(), requestParamsDto.getCurrency()).orElseThrow(() -> new RuntimeException("merchant not found"));
    refillService.setNeedKyc(merchantCurrency, user);

    if (merchantCurrency.getNeedKycRefill()) {
        throw new UserOperationAccessException("Need to pass KYC");
    }

    Boolean forceGenerateNewAddress = requestParamsDto.getGenerateNewAddress() != null && requestParamsDto.getGenerateNewAddress();
    if (!forceGenerateNewAddress) {
      Optional<String> address = refillService.getAddressByMerchantIdAndCurrencyIdAndUserId(
          requestParamsDto.getMerchant(),
          requestParamsDto.getCurrency(),
          userService.getIdByEmail(principal.getName())
      );
      if (address.isPresent()) {
        String message = messageSource.getMessage("refill.messageAboutCurrentAddress", new String[]{address.get()}, locale);
        return new HashMap<String, Object>() {{
          put("address", address.get());
          put("message", message);
          put("qr", address.get());
        }};
      }
    }
    RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
    Payment payment = new Payment(INPUT);
    payment.setCurrency(requestParamsDto.getCurrency());
    payment.setMerchant(requestParamsDto.getMerchant());
    payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());

      CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, principal.getName(), locale)
        .orElseThrow(InvalidAmountException::new);
    RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
    return refillService.createRefillRequest(request);
  }




    @RequestMapping(value = "/refill/request/revoke", method = POST)
    @ResponseBody
    public void revokeWithdrawRequest(
            @RequestParam Integer id) {
        refillService.revokeRefillRequest(id);
    }

    @RequestMapping(value = "/refill/request/info", method = GET)
    @ResponseBody
    public RefillRequestFlatDto getInfoRefill(
            @RequestParam Integer id) {
        return refillService.getFlatById(id);
    }


    @RequestMapping(value = "/refill/request/confirm", method = POST)
    @ResponseBody
    public void confirmWithdrawRequest(
            InvoiceConfirmData invoiceConfirmData,
            Locale locale) {
        refillService.confirmRefillRequest(invoiceConfirmData, locale);
    }

    @RequestMapping(value = "/refill/banks", method = GET)
    @ResponseBody
    public List<InvoiceBank> getBankListForCurrency(
            @RequestParam Integer currencyId) {
        List<InvoiceBank> banks = refillService.findBanksForCurrency(currencyId);
        banks.forEach(bank -> {
            if (bank.getBankDetails() != null) {
                bank.setBankDetails(bank.getBankDetails().replaceAll("\n", "<br/>"));
            }
        });
        return banks;
    }

    @RequestMapping(value = "/refill/commission", method = GET)
    @ResponseBody
    public Map<String, String> getCommissions(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            @RequestParam("merchant") Integer merchantId,
            Principal principal,
            Locale locale) {
        Integer userId = userService.getIdByEmail(principal.getName());
        return refillService.correctAmountAndCalculateCommission(userId, amount, currencyId, merchantId, locale);
    }

    @RequestMapping(value = "/refill/unconfirmed", method = GET)
    @ResponseBody
    public PaginationWrapper<List<MyInputOutputHistoryDto>> findMyUnconfirmedRefillRequests(@RequestParam("currency") String currencyName,
                                                                                            @RequestParam("limit") Integer limit,
                                                                                            @RequestParam("offset") Integer offset,
                                                                                            Principal principal, Locale locale) {
        return inputOutputService.findUnconfirmedInvoices(principal.getName(), currencyName, limit, offset, locale);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/refill/take", method = POST)
    @ResponseBody
    public void takeToWork(
            @RequestParam Integer id,
            Principal principal) {
        Integer requesterAdminId = userService.getIdByEmail(principal.getName());
        refillService.takeInWorkRefillRequest(id, requesterAdminId);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/refill/return", method = POST)
    @ResponseBody
    public void returnFromWork(
            @RequestParam Integer id,
            Principal principal) {
        Integer requesterAdminId = userService.getIdByEmail(principal.getName());
        refillService.returnFromWorkRefillRequest(id, requesterAdminId);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/refill/decline", method = POST)
    @ResponseBody
    public void decline(
            @RequestParam Integer id,
            @RequestParam String comment,
            Principal principal, HttpServletRequest request) {
        Integer requesterAdminId = userService.getIdByEmail(principal.getName());
        refillService.declineRefillRequest(id, requesterAdminId, comment);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/refill/accept", method = POST)
    @ResponseBody
    public void accept(
            @RequestBody RefillRequestAcceptEntryParamsDto acceptDto,
            Principal principal) {
        Integer requesterAdminId = userService.getIdByEmail(principal.getName());
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(acceptDto.getRequestId())
                .amount(acceptDto.getAmount())
                .requesterAdminId(requesterAdminId)
                .remark(acceptDto.getRemark())
                .merchantTransactionId(acceptDto.getMerchantTxId())
                .build();
        refillService.acceptRefillRequest(requestAcceptDto);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseBody
    public ErrorInfo NotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            InvoiceActionIsProhibitedForCurrencyPermissionOperationException.class,
            InvoiceActionIsProhibitedForNotHolderException.class
    })
    @ResponseBody
    public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({
            NotEnoughUserWalletMoneyException.class,
    })
    @ResponseBody
    public ErrorInfo NotAcceptableExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            InvalidAccountException.class,
    })
    @ResponseBody
    public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, InvalidAccountException exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception, exception.getReason());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        log.error(ExceptionUtils.getStackTrace(exception));
        if (exception instanceof MerchantException) {
            return new ErrorInfo(req.getRequestURL(), exception,
                    messageSource.getMessage(((MerchantException) (exception)).getReason(), null, localeResolver.resolveLocale(req)));
        }
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}
