package me.exrates.controller.merchants;

import com.google.common.base.Preconditions;
import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestManualDto;
import me.exrates.model.dto.RefillRequestsAdminTableDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.RefillRequestTableViewTypeEnum;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.RequestLimitExceededException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;


    @RequestMapping(value = "/2a8fy7b07dxe44/refill")
    public ModelAndView refillRequests(Principal principal) {
        final Map<String, Object> params = new HashMap<>();
        List<UserCurrencyOperationPermissionDto> permittedCurrencies = currencyService.getCurrencyOperationPermittedForRefill(principal.getName())
                .stream().filter(dto -> dto.getInvoiceOperationPermission() != InvoiceOperationPermission.NONE)
                .sorted(Comparator.comparing(UserCurrencyOperationPermissionDto::getCurrencyName))
                .collect(Collectors.toList());
        params.put("currencies", permittedCurrencies);
        if (!permittedCurrencies.isEmpty()) {
            List<Integer> currencyList = permittedCurrencies.stream()
                    .map(UserCurrencyOperationPermissionDto::getCurrencyId)
                    .collect(Collectors.toList());
            List<Merchant> merchants = merchantService.getAllUnblockedForOperationTypeByCurrencies(currencyList, OperationType.INPUT)
                    .stream()
                    .map(item -> new Merchant(item.getMerchantId(), item.getName(), item.getDescription()))
                    .distinct().sorted(Comparator.comparing(Merchant::getName))
                    .collect(Collectors.toList());
            params.put("merchants", merchants);
        }
        List<Integer> ids = merchantService.getIdsByProcessType(Collections.singletonList("CRYPTO"));
        params.put("cryptoCurrencies", permittedCurrencies.stream()
                .filter(p -> ids.contains(p.getCurrencyId()) && p.getInvoiceOperationPermission().equals(InvoiceOperationPermission.ACCEPT_DECLINE))
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
    @RequestMapping(value = "/2a8fy7b07dxe44/refill/crypto_create", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String creteRefillRequestForCrypto(
            @Valid @RequestBody RefillRequestManualDto refillDto, Principal principal, HttpServletRequest servletRequest) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException {
        log.debug("refiil dto {}", refillDto);
        Locale locale = localeResolver.resolveLocale(servletRequest);
        List<UserCurrencyOperationPermissionDto> permittedCurrencies = currencyService.getCurrencyOperationPermittedForRefill(principal.getName())
                .stream()
                .filter(dto -> dto.getInvoiceOperationPermission() == InvoiceOperationPermission.ACCEPT_DECLINE)
                .collect(Collectors.toList());
        Preconditions.checkArgument(
                permittedCurrencies.stream().anyMatch(p -> p.getCurrencyId().equals(refillDto.getCurrency())),
                "Access decline");
        Integer id = refillService.manualCreateRefillRequestCrypto(refillDto, locale);
        return new JSONObject().put("message", messageSource.getMessage("message.refill.manual.created",
                new String[]{id.toString()}, locale)).toString();
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
