package me.exrates.controller.merchants;

import com.google.common.base.Preconditions;
import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.RefillRequestTableViewTypeEnum;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.service.*;
import me.exrates.service.exception.IllegalOperationTypeException;
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
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * created by ValkSam
 */
@Controller
public class RefillRequestAdminController {

  private static final Logger log = LogManager.getLogger("refill");

  @Autowired
  private MessageSource messageSource;
  @Autowired
  private LocaleResolver localeResolver;
  @Autowired
  private RefillService refillService;
  @Autowired
  private UserService userService;
  @Autowired
  private MerchantService merchantService;
  @Autowired
  private CommissionService commissionService;
  @Autowired
  private CurrencyService currencyService;
  @Autowired
  private InputOutputService inputOutputService;

  @RequestMapping(value = "/2a8fy7b07dxe44/refill")
  public ModelAndView refillRequests(Principal principal) {
    final Map<String, Object> params = new HashMap<>();
    List<UserCurrencyOperationPermissionDto> permittedCurrencies = currencyService.getCurrencyOperationPermittedForRefill(principal.getName())
        .stream().filter(dto -> dto.getInvoiceOperationPermission() != InvoiceOperationPermission.NONE).collect(Collectors.toList());
    params.put("currencies", permittedCurrencies);
    if (!permittedCurrencies.isEmpty()) {
      List<Integer> currencyList = permittedCurrencies.stream()
          .map(UserCurrencyOperationPermissionDto::getCurrencyId)
          .collect(Collectors.toList());
      List<Merchant> merchants = merchantService.getAllUnblockedForOperationTypeByCurrencies(currencyList, OperationType.INPUT)
          .stream()
          .map(item -> new Merchant(item.getMerchantId(), item.getName(), item.getDescription()))
          .distinct()
          .collect(Collectors.toList());
      params.put("merchants", merchants);
    }
    List<Integer> ids = merchantService.getIdsByProcessType(Collections.singletonList("CRYPTO"));
    params.put("cryptoCurrencies", permittedCurrencies.stream()
            .filter(p-> ids.contains(p.getCurrencyId()) && p.getInvoiceOperationPermission().equals(InvoiceOperationPermission.ACCEPT_DECLINE))
            .collect(Collectors.toList()));
    return new ModelAndView("refillRequests", params);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/refillRequests", method = GET)
  @ResponseBody
  public DataTable<List<RefillRequestsAdminTableDto>> findRequestByStatus(
      @RequestParam("viewType") String viewTypeName,
      RefillFilterData refillFilterData,
      @RequestParam Map<String, String> params,
      Principal principal,
      Locale locale) {
    RefillRequestTableViewTypeEnum viewTypeEnum = RefillRequestTableViewTypeEnum.convert(viewTypeName);
    List<Integer> statusList = viewTypeEnum.getRefillStatusList().stream().map(RefillStatusEnum::getCode).collect(Collectors.toList());
    DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
    refillFilterData.initFilterItems();
    return refillService.getRefillRequestByStatusList(statusList, dataTableParams, refillFilterData, principal.getName(), locale);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/refill/info", method = GET)
  @ResponseBody
  public RefillRequestsAdminTableDto getInfo(
      @RequestParam Integer id,
      Principal principal) {
    String requesterAdmin = principal.getName();
    return refillService.getRefillRequestById(id, requesterAdmin);
  }

  @AdminLoggable
  @RequestMapping(value = "/2a8fy7b07dxe44/refill/crypto_create", method = POST)
  @ResponseBody
  public Integer creteRefillRequestForCrypto(
          @RequestBody RefillRequestManualDto refillDto, Principal principal, HttpServletRequest servletRequest) {
    Locale locale = localeResolver.resolveLocale(servletRequest);
    List<UserCurrencyOperationPermissionDto> permittedCurrencies = currencyService.getCurrencyOperationPermittedForRefill(principal.getName())
            .stream()
            .filter(dto -> dto.getInvoiceOperationPermission() == InvoiceOperationPermission.ACCEPT_DECLINE)
            .collect(Collectors.toList());
    Preconditions.checkArgument(
            permittedCurrencies.stream().anyMatch(p->p.getCurrencyId().equals(refillDto.getCurrency())),
            "Access decline");
    User user = Preconditions.checkNotNull(userService.findByEmail(refillDto.getEmail()), "user not found");
    if (!refillService.checkInputRequestsLimit(refillDto.getCurrency(), refillDto.getEmail())) {
      throw new RequestLimitExceededException(messageSource.getMessage("merchants.InputRequestsLimit", null, locale));
    }
    Integer merchantId = Preconditions.checkNotNull(refillService.getMerchantIdByAddressAndCurrencyAndUser(
            refillDto.getAddress(),
            refillDto.getCurrency(),
            user.getId()), "address not found");
    Merchant merchant = merchantService.findById(merchantId);
    Payment payment = new Payment(INPUT);
    payment.setCurrency(refillDto.getCurrency());
    payment.setMerchant(merchant.getId());
    payment.setSum(refillDto.getAmount() == null ? 0 : refillDto.getAmount().doubleValue());
    CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, refillDto.getEmail())
            .orElseThrow(InvalidAmountException::new);
    RefillRequestCreateDto request = new RefillRequestCreateDto(
            new RefillRequestParamsDto(refillDto),
            creditsOperation,
            RefillStatusEnum.ON_PENDING,
            locale);
    request.setTxHash(refillDto.getTxHash());
    request.setNeedToCreateRefillRequestRecord(true);
    Optional<Integer> id = refillService.createRefillByFact(request);
    return id.orElseThrow(()-> new RuntimeException("error"));
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
