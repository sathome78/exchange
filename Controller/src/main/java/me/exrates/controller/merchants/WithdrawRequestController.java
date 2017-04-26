package me.exrates.controller.merchants;

import me.exrates.controller.annotation.FinPassCheck;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.ClientBank;
import me.exrates.service.RequestLimitExceededException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestParamsDto;
import me.exrates.model.dto.WithdrawRequestsAdminTableDto;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.service.*;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OperationType.OUTPUT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * created by ValkSam
 */
@Controller
public class WithdrawRequestController {

  private static final Logger log = LogManager.getLogger("withdraw");

  @Autowired
  private MessageSource messageSource;

  @Autowired
  WithdrawService withdrawService;

  @Autowired
  UserService userService;

  @Autowired
  MerchantService merchantService;

  @Autowired
  private InputOutputService inputOutputService;

  @Autowired
  private CommissionService commissionService;

  @FinPassCheck(throwCheckPassException = true)
  @RequestMapping(value = "/withdraw/request/create", method = POST)
  @ResponseBody
  public Map<String, String> createWithdrawalRequest(
      @RequestBody WithdrawRequestParamsDto requestParamsDto,
      Principal principal,
      Locale locale) throws UnsupportedEncodingException {
    if (!withdrawService.checkOutputRequestsLimit(requestParamsDto.getCurrency(), principal.getName())) {
      throw new RequestLimitExceededException(messageSource.getMessage("merchants.OutputRequestsLimit", null, locale));
    }
    WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();
    Payment payment = new Payment(OUTPUT);
    payment.setCurrency(requestParamsDto.getCurrency());
    payment.setMerchant(requestParamsDto.getMerchant());
    payment.setSum(requestParamsDto.getSum().doubleValue());
    payment.setDestination(requestParamsDto.getDestination());
    CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, principal.getName())
        .orElseThrow(InvalidAmountException::new);
    WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
    return withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, locale);
  }

  @RequestMapping(value = "/withdraw/request/revoke", method = POST)
  @ResponseBody
  public void revokeWithdrawRequest(
      @RequestParam Integer id) {
    withdrawService.revokeWithdrawalRequest(id);
  }

  @RequestMapping(value = "/withdraw/banks", method = GET)
  @ResponseBody
  public List<ClientBank> getBankListForCurrency(
      @RequestParam Integer currencyId) {
    return withdrawService.findClientBanksForCurrency(currencyId);
  }

  @RequestMapping(value = "/withdraw/commission", method = GET)
  @ResponseBody
  public Map<String, String> getCommissions(
      @RequestParam("amount") BigDecimal amount,
      @RequestParam("currency") Integer currencyId,
      @RequestParam("merchant") Integer merchantId,
      Principal principal,
      Locale locale) {
    Integer userId = userService.getIdByEmail(principal.getName());
    return withdrawService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId, merchantId, locale);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/take", method = POST)
  @ResponseBody
  public void takeToWork(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.takeInWorkWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/return", method = POST)
  @ResponseBody
  public void returnFromWork(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.returnFromWorkWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/decline", method = POST)
  @ResponseBody
  public void decline(
      @RequestParam Integer id,
      @RequestParam String comment,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.declineWithdrawalRequest(id, requesterAdminId, comment);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/confirm", method = POST)
  @ResponseBody
  public void confirm(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.confirmWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/post", method = POST)
  @ResponseBody
  public void postHolded(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.postWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/info", method = GET)
  @ResponseBody
  public WithdrawRequestsAdminTableDto getInfo(
      @RequestParam Integer id,
      Principal principal) {
    String requesterAdmin = principal.getName();
    return withdrawService.getWithdrawRequestById(id, requesterAdmin);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
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
      NotEnoughUserWalletMoneyException.class, RequestLimitExceededException.class
  })
  @ResponseBody
  public ErrorInfo NotAcceptableExceptionHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    return new ErrorInfo(req.getRequestURL(), exception);
  }

}
