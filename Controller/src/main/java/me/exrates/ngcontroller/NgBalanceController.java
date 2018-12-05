package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j;
import me.exrates.controller.annotation.CheckActiveUserStatus;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestParamsDto;
import me.exrates.model.dto.WalletTotalUsdDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.ngcontroller.exception.NgDashboardException;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.service.SecureService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.UserNotFoundException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.CharUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OperationType.USER_TRANSFER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/info/private/v2/balances",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgBalanceController {

    private final BalanceService balanceService;
    private final ExchangeRatesHolder exchangeRatesHolder;
    private final LocaleResolver localeResolver;
    private final WalletService walletService;
    private final UserService userService;
    private final TransferService transferService;
    private final MerchantService merchantService;
    private final UserOperationService userOperationService;
    private final InputOutputService inputOutputService;
    private final MessageSource messageSource;
    private final SecureService secureService;
    private final G2faService g2faService;

    @Autowired
    public NgBalanceController(BalanceService balanceService,
                               ExchangeRatesHolder exchangeRatesHolder,
                               LocaleResolver localeResolver,
                               WalletService walletService,
                               UserService userService,
                               TransferService transferService,
                               MerchantService merchantService,
                               UserOperationService userOperationService,
                               InputOutputService inputOutputService,
                               MessageSource messageSource,
                               SecureService secureService,
                               G2faService g2faService) {
        this.balanceService = balanceService;
        this.exchangeRatesHolder = exchangeRatesHolder;
        this.localeResolver = localeResolver;
        this.walletService = walletService;
        this.userService = userService;
        this.transferService = transferService;
        this.merchantService = merchantService;
        this.userOperationService = userOperationService;
        this.inputOutputService = inputOutputService;
        this.messageSource = messageSource;
        this.secureService = secureService;
        this.g2faService = g2faService;
    }

    // apiUrl/info/private/v2/balances?limit=20&offset=0&excludeZero=false
    @GetMapping
    public ResponseEntity<PagedResult<MyWalletsDetailedDto>> getBalances(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "false") Boolean excludeZero,
            @RequestParam(required = false) CurrencyType currencyType) {
        String email = getPrincipalEmail();
        try {
            PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(offset, limit, email, excludeZero, currencyType);
            return ResponseEntity.ok(pagedResult);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // apiUrl/info/private/v2/balances/pendingRequests?limit=20&offset=0
    @GetMapping("/pendingRequests")
    public ResponseEntity<PagedResult<RefillPendingRequestDto>> getPendingRequests(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        String email = getPrincipalEmail();
        try {
            PagedResult<RefillPendingRequestDto> pendingRequests = balanceService.getPendingRequests(offset, limit, email);
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // apiUrl/info/private/v2/balances/totalBalance
    @SuppressWarnings("Duplicates")
    @GetMapping("/totalBalance")
    @ResponseBody
    public Map<String, Object> getUserTotalBalance(HttpServletRequest request) {
        List<MyWalletsStatisticsDto> resultWallet = walletService.getAllWalletsForUserReduced(null, getPrincipalEmail(),
                localeResolver.resolveLocale(request), CurrencyPairType.MAIN);
        HashMap<String, Object> map = new HashMap<>();
        map.put("mapWallets", resultWallet);

        if (resultWallet.size() > 1) {
            List<ExOrderStatisticsShortByPairsDto> resultOrders = exchangeRatesHolder.getAllRates();

            final HashMap<String, BigDecimal> ratesBTC_ETH = new HashMap<>();
            resultOrders
                    .stream()
                    .filter(p -> p.getCurrencyPairName().contains("BTC/USD") || p.getCurrencyPairName().contains("ETH/USD"))
                    .forEach(p -> ratesBTC_ETH.put(p.getCurrencyPairName(), new BigDecimal(p.getLastOrderRate())));

            final List<WalletTotalUsdDto> walletTotalUsdDtoList = new ArrayList<>();
            for (MyWalletsStatisticsDto myWalletsStatisticsDto : resultWallet) {
                WalletTotalUsdDto walletTotalUsdDto = new WalletTotalUsdDto(myWalletsStatisticsDto.getCurrencyName());
                Map<String, BigDecimal> mapWalletTotalUsdDto = new HashMap<>();
                if (myWalletsStatisticsDto.getCurrencyName().equals("USD")) {
                    walletTotalUsdDto.setSumUSD(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
                resultOrders.stream()
                        .filter(o -> o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/USD"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/BTC"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/ETH"))
                        )
                        .forEach(o -> {
                            mapWalletTotalUsdDto.put(o.getCurrencyPairName(), new BigDecimal(o.getLastOrderRate()));
                        });
                if (!mapWalletTotalUsdDto.isEmpty()) {
                    walletTotalUsdDto.setTotalBalance(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
            }

            walletTotalUsdDtoList.stream().forEach(wallet -> {
                if (wallet.getRates().containsKey(wallet.getCurrency().concat("/USD"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/USD")).multiply(wallet.getTotalBalance()));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/BTC"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/BTC"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("BTC/USD")));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/ETH"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/ETH"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("ETH/USD")));
                }
            });

            map.put("sumTotalUSD", walletTotalUsdDtoList.stream().mapToDouble(w -> w.getSumUSD().doubleValue()).sum());
        }

        return map;
    }

    //  apiUrl/info/private/v2/balances/inputOutputData?limit=20&offset=0&currencyId=0&dateFrom=2018-11-21&dateTo=2018-11-26
    @GetMapping("/inputOutputData")
    public ResponseEntity<PagedResult<MyInputOutputHistoryDto>> getMyInputOutputData(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "0") Integer currencyId,
            @RequestParam(required = false, name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletRequest request) {
        String email = getPrincipalEmail();
        Locale locale = localeResolver.resolveLocale(request);
        try {
            PagedResult<MyInputOutputHistoryDto> page =
                    balanceService.getUserInputOutputHistory(email, limit, offset, currencyId, dateFrom, dateTo, locale);
            return ResponseEntity.ok(page);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
        amount: "0.00165000"
        companyCommissionAmount: "0"
        companyCommissionRate: "(0,2%)"
        merchantCommissionAmount: "0.00000330"
        merchantCommissionRate: "(0,2%, но не менее 0 BTC)"
        resultAmount: "0.0016467"
        totalCommissionAmount: "0.0000033"
     */
    @CheckActiveUserStatus
    @RequestMapping(value = "/voucher/inner/commission", method = GET)
    @ResponseBody
    public Map<String, String> getCommissionsForInnerVoucher(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            Locale locale) {

        MerchantCurrency merchant = merchantService.findMerchantForInnerTransferByCurrencyId(currencyId);
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        return transferService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId,
                merchant.getMerchantId(), locale);
    }

    @CheckActiveUserStatus
    @RequestMapping(value = "/voucher/request/create", method = POST)
    @ResponseBody
    public Map<String, Object> createTransferRequest(@RequestBody TransferRequestParamsDto requestParamsDto,
                                                     HttpServletRequest servletRequest) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        Locale locale = localeResolver.resolveLocale(servletRequest);
        if (requestParamsDto.getOperationType() != USER_TRANSFER) {
            throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
        }
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRANSFER);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        if (requestParamsDto.getRecipient() != null && CharUtils.isCyrillic(requestParamsDto.getRecipient())) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "message.only.latin.symblos", null, locale));
        }
        MerchantCurrency merchant = merchantService.findMerchantForInnerTransferByCurrencyId(requestParamsDto.getCurrency());
        TransferStatusEnum beginStatus = (TransferStatusEnum) TransferStatusEnum.getBeginState();
        Payment payment = new Payment(requestParamsDto.getOperationType());
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(merchant.getMerchantId());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        payment.setRecipient(requestParamsDto.getRecipient());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, getPrincipalEmail(), locale)
                .orElseThrow(InvalidAmountException::new);
        TransferRequestCreateDto transferRequest = new TransferRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);

        if (!g2faService.checkGoogle2faVerifyCode(requestParamsDto.getPin(), userId)) {
            throw new IncorrectPinException("Incorrect google auth code");
        }

        return transferService.createTransferRequest(transferRequest);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class, UserNotFoundException.class,
            UserOperationAccessException.class, IllegalArgumentException.class, IncorrectPinException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}