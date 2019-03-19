package me.exrates.controller.mobile;

import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.util.RateLimitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by OLEG on 02.09.2016.
 */

/**
 * ALL controleers oommented for security reasons
 */
@RestController
@RequestMapping("/api/payments")
public class MobileInputOutputController {

    private static final Logger LOGGER = LogManager.getLogger("mobileAPI");


    @Autowired
    private UserService userService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private InputOutputService inputOutputService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    WithdrawService withdrawService;

    @Autowired
    RefillService refillService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private CurrencyService currencyService;


    /**
     * @apiDefine InvalidAmountError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Invalid Payment Amount:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "INVALID_PAYMENT_AMOUNT",
     *           "url": "http://127.0.0.1:8080/api/payments/preparePayment",
     *           "cause": "InvalidAmountException",
     *           "detail": null
     *      }
     *
     * */

    /**
     * @apiDefine NotEnoughMoneyForWithdrawError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "INSUFFICIENT_FUNDS",
     *           "url": "http://127.0.0.1:8080/api/payments/withdraw",
     *           "cause": "NotEnoughUserWalletMoneyException",
     *           "detail": "Not enough money to withdraw on user wallet Wallet{id=4277, currencyId=1, userId=golvazin@gmail.com,
     *           activeBalance=-10.000000000, reservedBalance=0E-9, name='RUB'}"
     *      }
     *
     * */

    /**
     * @apiDefine IllegalInvoiceStatusException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Illegal Invoice Status:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "BAD_INVOICE_STATUS",
     *           "url": "http://localhost:8080/api/payments/invoice/confirm",
     *           "cause": "UnsupportedInvoiceStatusForActionException",
     *           "detail": "current state: EXPIRED action: CONFIRM_USER"
     *      }
     *
     * */

    /**
     * @apiDefine InvoiceNotFoundException
     * @apiError (404) {String} errorCode error code
     * @apiError (404) {String} url request URL
     * @apiError (404) {String} cause name of root exception
     * @apiError (404) {String} details detail of root exception
     * @apiErrorExample {json} Invoice Not Found:
     * HTTP/1.1 404 Not Found
     *      {
     *           "errorCode": "INVOICE_NOT_FOUND",
     *           "url": "http://localhost:8080/api/payments/invoice/confirm",
     *           "cause": "InvoiceNotFoundException",
     *           "detail": "invoice id: 23423"
     *      }
     *
     * */

    /**
     * @apiDefine VoucherNotFoundException
     * @apiError (404) {String} errorCode error code
     * @apiError (404) {String} url request URL
     * @apiError (404) {String} cause name of root exception
     * @apiError (404) {String} details detail of root exception
     * @apiErrorExample {json} Voucher Not Found:
     * HTTP/1.1 404 Not Found
     *      {
     *            "errorCode": "VOUCHER_NOT_FOUND",
     *            "url": "http://localhost:8080/api/payments/transfer/accept",
     *            "cause": "VoucherNotFoundException",
     *            "detail": "Ваучер не найден!"
     *      }
     *
     * */


    /**
     * @apiDefine MerchantCurrencyBlockedException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Merchant and Currency Blocked:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "BLOCKED_CURRENCY_FOR_MERCHANT",
     *           "url": "http://127.0.0.1:8080/api/payments/preparePayment",
     *           "cause": "MerchantCurrencyBlockedException",
     *           "detail": "Operation INPUT is blocked for this currency! "
     *      }
     *
     * */

    /**
     * @apiDefine InputRequestLimitExceededException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "INPUT_REQUEST_LIMIT_EXCEEDED",
     *           "url": "http://127.0.0.1:8080/api/payments/preparePayment",
     *           "cause": "InputRequestLimitExceededException",
     *           "detail": "Ваш дневной лимит ввода для этой валюты превышен. Пожалуйста, попробуйте завтра."
     *      }
     *
     * */

    /**
     * @apiDefine OutputRequestLimitExceededException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "OUTPUT_REQUEST_LIMIT_EXCEEDED",
     *           "url": "http://127.0.0.1:8080/api/payments/withdraw",
     *           "cause": "RequestLimitExceededException",
     *           "detail": "Ваш дневной лимит вывода для этой валюты превышен. Пожалуйста, попробуйте завтра."
     *      }
     *
     * */

    /**
     * @apiDefine CommissionExceedingAmountException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "COMMISSION_EXCEEDS_AMOUNT",
     *          "url": "http://localhost:8080/api/payments/dynamicCommission",
     *          "cause": "CommissionExceedingAmountException",
     *          "detail": "Commission 6.001 exceeds amount 5"
     *      }
     *
     * */


    /**
     * @api {get} /api/payments/merchants Merchants info
     * @apiName findNonTransferMerchantCurrencies
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of all non-transfer merchants (if no currencyId) or by currency
     * @apiParam {Integer} currencyId - currency id (OPTIONAL)
     * @apiParamExample Request example
     * /api/payments/merchants?currencyId=7
     * @apiSuccess {Array} merchants List of available merchants
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.merchantId merchant id
     * @apiSuccess {Integer} data.currencyId currency id
     * @apiSuccess {String} data.name merchant name
     * @apiSuccess {String} merchant.processType merchant process type (CRYPTO, MERCHANT, INVOICE)
     * @apiSuccess {Number} data.minInputSum minimal sum of input payment
     * @apiSuccess {Number} data.minOutputSum minimal sum of output payment
     * @apiSuccess {Number} data.inputCommission commission rate for refill operations
     * @apiSuccess {Number} data.outputCommission commission rate for withdraw operations
     * @apiSuccess {Number} data.minFixedCommission minimal commission amount
     * @apiSuccess {Boolean} data.additionalTagForWithdrawAddressIsUsed if additional tag is needed for output
     * @apiSuccess {String} data.additionalFieldName name of additional tag
     * @apiSuccess {Boolean} data.generateAdditionalRefillAddressAvailable for cryptos - if it is possible to generate new address
     * @apiSuccess {Boolean} data.withdrawCommissionDependsOnDestinationTag - if withdraw commission is computed dynamically against dst tag value
     * @apiSuccess {Array} data.merchantImageList List of merchant images
     * @apiSuccess {Object} merchantImage Merchant image
     * @apiSuccess {String} data.merchantImage.merchantId merchant id
     * @apiSuccess {String} data.merchantImage.currencyId currency id
     * @apiSuccess {String} data.merchantImage.image_name image name
     * @apiSuccess {String} data.merchantImage.image_path - path for image on server
     * @apiSuccess {String} data.merchantImage.id - merchant image id
     * @apiSuccess {Boolean} data.withdrawBlocked if refill is blocked for merchant
     * @apiSuccess {Boolean} data.refillBlocked if withdraw is blocked for merchant
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "merchantId": 20,
     * "currencyId": 19,
     * "name": "Ripple",
     * "processType": "CRYPTO",
     * "minInputSum": 0.01,
     * "minOutputSum": 5,
     * "inputCommission": 0,
     * "outputCommission": 0,
     * "minFixedCommission": 0,
     * "additionalTagForWithdrawAddressIsUsed": true,
     * "additionalFieldName": "Destination Tag",
     * "generateAdditionalRefillAddressAvailable": false,
     * "withdrawCommissionDependsOnDestinationTag": false,
     * "listMerchantImage": [],
     * "withdrawBlocked": false,
     * "refillBlocked": false
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/merchants", method = GET)
    public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(@RequestParam(required = false) Integer currencyId) {
        return merchantService.findNonTransferMerchantCurrencies(currencyId);
    }*/


    /**
     * @api {get} /api/payments/transferMerchants Transfer merchants info
     * @apiName findNonTransferMerchantCurrencies
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of transfer merchants
     * @apiParam {Integer} currencyId - currency id (OPTIONAL)
     * @apiParamExample Request example
     * /api/payments/transferMerchants
     * @apiSuccess {Array} merchants List of available merchants
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.merchantId merchant id
     * @apiSuccess {String} data.name merchant name
     * @apiSuccess {Boolean} data.isVoucher defines if transfer is by voucher
     * @apiSuccess {Boolean} data.recipientUserIsNeeded defines if transfer is for a certain user
     * @apiSuccess {Array} data.blockedForCurrencies IDs of currencies where the transfer is blocked
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "merchantId": 31,
     * "name": "VoucherTransfer",
     * "isVoucher": true,
     * "recipientUserIsNeeded": true,
     * "blockedForCurrencies": [
     * 9,
     * 10
     * ],
     * },
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/transferMerchants", method = GET)
    public List<TransferMerchantApiDto> findAllTransferMerchantCurrencies() {
        return merchantService.findTransferMerchants();
    }*/


    /**
     * @api {get} /api/payments/dynamicCommission Dynamic withdraw commission
     * @apiName retrieveDynamicCommissionValue
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns merchant commission value for withdraw
     * @apiParam {Number} amount - amount for withdrawal
     * @apiParam {Number} merchant - merchant id
     * @apiParam {Number} currency - currency id
     * @apiParam {String} memo - additional tag for withdraw (OPTIONAL)
     * @apiParamExample Request example
     * /api/payments/dynamicCommission?amount=20&merchant=33&currency=24&memo=jsdfkaksjdfhfgagksjdfga
     * @apiSuccess {Number} commission merchant commission value for withdraw
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * <p>
     * 2
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse CommissionExceedingAmountException
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/dynamicCommission", method = GET)
    public Double retrieveDynamicCommissionValue(@RequestParam("amount") BigDecimal amount,
                                                 @RequestParam("currency") Integer currencyId,
                                                 @RequestParam("merchant") Integer merchantId,
                                                 @RequestParam(value = "memo", required = false) String memo) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);

        Integer userId = userService.getIdByEmail(userEmail);
        if (!StringUtils.isEmpty(memo)) {
            merchantService.checkDestinationTag(merchantId, memo);
        }
        try {
            Map<String, String> result = withdrawService.correctAmountAndCalculateCommissionPreliminarily(userId,
                    amount, currencyId, merchantId, userLocale, memo);
            String merchantCommissionAmountString = result.get("merchantCommissionAmount");
            BigDecimal merchantCommissionAmount = merchantCommissionAmountString == null ? BigDecimal.ZERO : new BigDecimal(merchantCommissionAmountString);
            return merchantCommissionAmount.doubleValue();
        } catch (InvalidAmountException e) {
            throw new CommissionExceedingAmountException(e.getMessage());
        }
    }*/

    /**
     * @api {post} /api/payments/transfer/accept Accept transfer
     * @apiName acceptVoucher
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Accept transfer by entering voucher code
     * @apiParam {String} code voucher code
     * @apiParamExample {json} Request Example:
     * {
     * "code": "ad4b3e017838a373c8cc4c59d1ed8269ce0e0b4bc9588cdfa879c5adbef8273e"
     * }
     * @apiSuccess {String} message success message
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * 100,20 USD успешно переведено на ваш счёт
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MessageNotReadableError
     * @apiUse VoucherNotFoundException
     * @apiUse InvalidAmountError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/transfer/accept", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> acceptVoucher(@RequestBody Map<String, String> params) {
        String code = RestApiUtils.retrieveParamFormBody(params, "code", true);
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        if (!rateLimitService.checkLimitsExceed(userEmail)) {
            throw new RequestsLimitExceedException();
        }
        InvoiceActionTypeEnum action = PRESENT_VOUCHER;
        List<InvoiceStatus> requiredStatus = TransferStatusEnum.getAvailableForActionStatusesList(action);
        if (requiredStatus.size() > 1) {
            throw new RuntimeException("voucher processing error");
        }
        Optional<TransferRequestFlatDto> dto = transferService
                .getByHashAndStatus(code, requiredStatus.get(0).getCode(), true);
        if (!dto.isPresent() || !transferService.checkRequest(dto.get(), userEmail)) {
            rateLimitService.registerRequest(userEmail);
            throw new VoucherNotFoundException(messageSource.getMessage(
                    "voucher.invoice.not.found", null, userLocale));
        }
        TransferRequestFlatDto flatDto = dto.get();
        flatDto.setInitiatorEmail(userEmail);
        transferService.performTransfer(flatDto, userLocale, action);
        return Collections.singletonMap("message", messageSource.getMessage("transfer.accept.success", new Object[]{BigDecimalProcessing.formatLocaleFixedSignificant(flatDto.getAmount(),
                userLocale, 2) + " " + currencyService.getCurrencyName(flatDto.getCurrencyId())}, userLocale));
    }*/

    /**
     * @api {post} /api/payments/withdraw Withdraw
     * @apiName withdraw
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Submit request for costs withdrawal
     * @apiParam {Integer} currency currency id
     * @apiParam {Integer} merchant merchant id
     * @apiParam {Number} sum amount of payment
     * @apiParam {String} destination wallet to send money
     * @apiParam {String} destinationTag additional tag to send money (see additionalTagForWithdrawAddressIsUsed in /merchants response)
     * @apiParam {Integer} merchantImage merchant image id (OPTIONAL)
     * @apiParamExample {json} Request Example:
     * {
     * "currency": 2,
     * "merchant": 10,
     * "sum": 10.0,
     * "destination": "11111111111",
     * "merchantImage": 34
     * }
     * @apiSuccess {String} balance User's current balance for respective currency
     * @apiSuccess {String} success success notification
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "balance": "USD 17801.04",
     * "success": "Your withdrawal request #126,556 through system Interkassa has been accepted and it will be processed within 48 hours."
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse NotEnoughMoneyForWithdrawError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse InvalidParamError
     * @apiUse InternalServerError
     */
    // TODO temporary disable
    // @RequestMapping(value="/withdraw", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  /*  public ResponseEntity<Map<String, String>> withdraw(@RequestBody @Valid WithdrawRequestParamsDto requestParamsDto) {


        Payment payment = new Payment();
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum().doubleValue());
        payment.setDestination(requestParamsDto.getDestination());
        payment.setDestinationTag(requestParamsDto.getDestinationTag());
        payment.setOperationType(OperationType.OUTPUT);

        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail, userLocale).orElseThrow(InvalidAmountException::new);
        WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();
        WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
        Map<String, String> response = withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, userLocale);
        return new ResponseEntity<>(response, OK);

    }*/

    /**
     * @api {post} /api/payments/preparePayment Input payment
     * @apiName preparePayment
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Submit request for costs input. Response depends on concrete merchant / payment system
     * (see <a href="https://drive.google.com/open?id=0Bx4pleRSZBP0YTdzT3h2RmZYU3c">link</a>)
     * @apiParam {Integer} currency currency id
     * @apiParam {Integer} merchant merchant id
     * @apiParam {Number} sum amount of payment
     * @apiParam {Integer} merchantImage merchant image id (OPTIONAL)
     * @apiParam {Integer} recipientBankId ID of destination bank - for invoice
     * @apiParam {String} userFullName full name of user - for invoice
     * @apiParam {String} remark additional remark - for invoice (OPTIONAL)
     * @apiParamExample {json} Request Example:
     * {
     * "currency": 2,
     * "merchant": 10,
     * "sum": 10.0,
     * "recipientBankId": 3,
     * "userFullName": John Smith,
     * "remark": qwerty qwerty
     * }
     * @apiSuccess {String} notification Notification with payment details (for cryptocurrencies and invoice)
     * @apiSuccess {String} url URL to redirect or send POST request
     * @apiSuccess {Object} properties Request params
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse InternalServerError
     */
 /*   @RequestMapping(value = "/preparePayment", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MerchantInputResponseDto preparePayment(@RequestBody @Valid RefillRequestParamsDto requestParamsDto, HttpServletRequest request) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        if (!refillService.checkInputRequestsLimit(requestParamsDto.getCurrency(), userEmail)) {
            throw new InputRequestLimitExceededException(messageSource.getMessage("merchants.InputRequestsLimit", null, userLocale));
        }
        Merchant merchant = merchantService.findById(requestParamsDto.getMerchant());
        MerchantCurrency merchantCurrency = merchantService.findByMerchantAndCurrency(merchant.getId(), requestParamsDto.getCurrency())
                .orElseThrow(MerchantInternalException::new);
        MerchantInputResponseDto responseDto = new MerchantInputResponseDto();
        if (merchant.getProcessType() == MerchantProcessType.CRYPTO || merchant.getProcessType() == MerchantProcessType.INVOICE) {
            responseDto.setType(MerchantApiResponseType.NOTIFY);
            if (requestParamsDto.getRecipientBankId() != null && requestParamsDto.getAddress() == null) {
                InvoiceBank bank = refillService.findInvoiceBankById(requestParamsDto.getRecipientBankId()).orElseThrow(InvoiceBankNotFoundException::new);
                requestParamsDto.setAddress(bank.getAccountNumber());
                requestParamsDto.setRecipientBankName(bank.getName());
            }
            RefillRequestCreateDto refillRequest = prepareRefillRequest(requestParamsDto, userEmail, userLocale);
            Map<String, Object> result = null;
            // TODO add last address retrieval method for crypto
            try {
                result = refillService.createRefillRequest(refillRequest);
                refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(Collections.singletonList(merchantCurrency), userEmail);

            } catch (RefillRequestGeneratingAdditionalAddressNotAvailableException e) {
                refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(Collections.singletonList(merchantCurrency), userEmail);
                Map<String, String> params = new HashMap<String, String>() {{
                    put("message", refillService.getPaymentMessageForTag(merchant.getServiceBeanName(), merchantCurrency.getAddress(), userLocale));
                }};
                result = new HashMap<String, Object>() {{
                    put("params", params);
                }};
            }
            Map<String, String> params = (Map<String, String>) result.get("params");
            String message = (String) result.get("message");
            if (message == null) {
                message = params.get("message");
            }
            if (message != null) {
                message = message.replaceAll("<button.*>", "").replaceAll("<.*?>", "");
            }
            responseDto.setData(message);
            if (merchantCurrency.getAdditionalTagForWithdrawAddressIsUsed()) {
                responseDto.setQr(merchantCurrency.getMainAddress());
                responseDto.setWalletNumber(merchantCurrency.getMainAddress());
                responseDto.setAdditionalTag(merchantCurrency.getAddress());
            } else {
                responseDto.setQr(params.get("qr"));
                responseDto.setWalletNumber(merchant.getProcessType() == MerchantProcessType.CRYPTO ? params.get("address") : params.get("walletNumber"));
            }

        } else {
            responseDto.setType(MerchantApiResponseType.REDIRECT);
            String rootUrl = String.join("", request.getScheme(), "://", request.getServerName(), ":",
                    String.valueOf(request.getServerPort()), "/api/payments/merchantRedirect?");
            String params = new HashMap<String, Object>() {{
                put("currencyId", requestParamsDto.getCurrency());
                put("merchantId", requestParamsDto.getMerchant());
                put("amount", requestParamsDto.getSum());
            }}.entrySet().stream().map((entry -> entry.getKey() + "=" + entry.getValue())).collect(Collectors.joining("&"));
            responseDto.setData(rootUrl + params);
        }

        return responseDto;
    }

    private RefillRequestCreateDto prepareRefillRequest(@RequestBody @Valid RefillRequestParamsDto requestParamsDto, String userEmail, Locale userLocale) {
        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
        Payment payment = new Payment(INPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail, userLocale)
                .orElseThrow(InvalidAmountException::new);
        return new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, userLocale);
    }*/


    /**
     * @api {post} /api/payments/invoice/confirm Confirm invoice
     * @apiName confirmInvoice
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Confirm invoice by ID
     * @apiParam {Integer} invoiceId invoice id
     * @apiParam {String} payerBankName name of payer bank
     * @apiParam {String} userAccount payer account number
     * @apiParam {String} userFullName full name of user
     * @apiParam {String} remark additional remark (OPTIONAL)
     * @apiParamExample {json} Request Example:
     * {
     * "invoiceId": 130720,
     * "payerBankName": "AAA BANK",
     * "userAccount": "6541325465",
     * "userFullName": "Talalai Talalaenko",
     * "remark": "alala ololo"
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/invoice/confirm", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> confirmInvoice(@Valid InvoiceConfirmData invoiceConfirmData) throws Exception {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        refillService.confirmRefillRequest(invoiceConfirmData, userLocale);
        return new ResponseEntity<>(OK);
    }*/

    /**
     * @api {post} /api/payments/invoice/revoke Revoke invoice
     * @apiName revokeInvoice
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Revoke invoice by ID
     * @apiParam {Integer} invoiceId invoice id
     * @apiParamExample {json} Request Example:
     * {
     * "invoiceId": 130720
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse IllegalInvoiceStatusException
     * @apiUse InvoiceNotFoundException
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/invoice/revoke", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> revokeInvoice(@RequestBody Map<String, String> params) throws Exception {
        String invoiceIdString = RestApiUtils.retrieveParamFormBody(params, "invoiceId", true);
        Integer invoiceId = Integer.parseInt(invoiceIdString);
        InvoiceConfirmData invoiceConfirmData = new InvoiceConfirmData();
        invoiceConfirmData.setInvoiceId(invoiceId);
        refillService.revokeRefillRequest(invoiceId);
        return new ResponseEntity<>(OK);
    }*/


    /**
     * @api {get} /api/payments/invoice/banks Banks info
     * @apiName getBanksByCurrency
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of banks by currency
     * @apiParam {Integer} currencyId - currency id
     * @apiParamExample Request example
     * /api/payments/invoice/banks?currencyId=10
     * @apiSuccess {Array} banks List of banks
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id bank id
     * @apiSuccess {Integer} data.currencyId currency id
     * @apiSuccess {String} data.name bank name
     * @apiSuccess {String} data.accountNumber bank account number
     * @apiSuccess {Number} data.recipient recipient's full name
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 1,
     * "currencyId": 10,
     * "name": "BCA",
     * "accountNumber": "3150963141",
     * "recipient": "Nanda Rizal Pahlewi"
     * },
     * {
     * "id": 2,
     * "currencyId": 10,
     * "name": "MANDIRI",
     * "accountNumber": "1440099965557",
     * "recipient": "Nanda Rizal Pahlewi"
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/invoice/banks", method = GET)
    public List<InvoiceBank> getBanksByCurrency(@RequestParam Integer currencyId) {
        *//*return refillService.findBanksForCurrency(currencyId);*//*
        return Collections.singletonList(InvoiceBank.getUnavilableInvoice(currencyId));
    }

    @RequestMapping(value = "/invoice/clientBanks", method = GET)
    public List<ClientBank> getClientBanksByCurrency(@RequestParam Integer currencyId) {
        return withdrawService.findClientBanksForCurrency(currencyId);
    }


    @RequestMapping(value = "/invoice/details", method = GET)
    public RefillRequestDetailsDto findInvoiceRequestDetails(@RequestParam Integer invoiceId, HttpServletRequest request) {
        RefillRequestFlatDto refillRequest = refillService.getFlatById(invoiceId);
        BigDecimal commissionAmount = commissionService.calculateCommissionForRefillAmount(refillRequest.getAmount(), refillRequest.getCommissionId());
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/rest";
        return new RefillRequestDetailsDto(refillRequest, commissionAmount, baseUrl);
    }

    @RequestMapping(value = "/invoice/withdraw", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> withdrawInvoice(@RequestBody @Valid WithdrawRequestParamsDto requestParamsDto) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        if (!withdrawService.checkOutputRequestsLimit(requestParamsDto.getCurrency(), userEmail)) {
            throw new RequestLimitExceededException(messageSource.getMessage("merchants.OutputRequestsLimit", null, userLocale));
        }
        Payment payment = new Payment();
        payment.setSum(requestParamsDto.getSum().doubleValue());
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(merchantService.findByName("Invoice").getId());
        payment.setOperationType(OperationType.OUTPUT);
        payment.setDestination(requestParamsDto.getWalletNumber());

        WithdrawData withdrawData = new WithdrawData();
        withdrawData.setRecipientBankName(requestParamsDto.getRecipientBankName());
        withdrawData.setRecipientBankCode(requestParamsDto.getRecipientBankCode());
        withdrawData.setUserFullName(requestParamsDto.getUserFullName());
        withdrawData.setRemark(requestParamsDto.getRemark());


        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail, userLocale).orElseThrow(InvalidAmountException::new);
        WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();
        WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
        Map<String, String> response = withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, userLocale);

        return new ResponseEntity<>(response, OK);
    }*/

    /**
     * @api {post} /api/payments/withdraw/revoke Revoke withdraw request
     * @apiName revokeWithdrawRequest
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Revoke withdraw request by ID
     * @apiParam {Integer} invoiceId invoice id
     * @apiParamExample {json} Request Example:
     * {
     * "invoiceId": 130720
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse IllegalInvoiceStatusException
     * @apiUse InvoiceNotFoundException
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/withdraw/revoke", method = POST)
    @ResponseBody
    public ResponseEntity<Void> revokeWithdrawRequest(@RequestBody Map<String, String> params) {
        Integer id = Integer.parseInt(RestApiUtils.retrieveParamFormBody(params, "invoiceId", true));
        withdrawService.revokeWithdrawalRequest(id);
        return new ResponseEntity<>(OK);
    }


    @RequestMapping(value = "/merchantRedirect", method = GET)
    public ModelAndView getMerchantRedirectPage(@RequestParam Integer currencyId, @RequestParam Integer merchantId,
                                                @RequestParam BigDecimal amount, @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("merchantApiInput");
        modelAndView.addObject("currency", currencyId);
        modelAndView.addObject("merchant", merchantId);
        modelAndView.addObject("amount", amount);
        modelAndView.addObject("authToken", token);
        modelAndView.addObject("operationType", OperationType.INPUT);
        return modelAndView;
    }

    @RequestMapping(value = "/preparePostPayment", method = POST)
    public Map<String, Object> preparePostPayment(@Valid RefillRequestParamsDto requestParamsDto) {
        LOGGER.debug(requestParamsDto);
        Payment payment = new Payment(INPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail, userLocale)
                .orElseThrow(InvalidAmountException::new);
        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
        RefillRequestCreateDto refillRequest = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, userLocale);
        final Map<String, Object> result = refillService.createRefillRequest(refillRequest);
        return result;
    }

    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
*/

    /**
     * @api {post} /api/payments/transfer/submit Submit transfer
     * @apiName submitTransfer
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Send transfer to other user
     * @apiParam {Integer} merchant merchant id
     * @apiParam {Integer} currency currency id
     * @apiParam {String} recipient nickname of receiver
     * @apiParam {Number} sum amount of transfer
     * @apiParamExample {json} Request Example:
     * {
     * "merchant": 31,
     * "currency": 2,
     * "recipient": "nickname",
     * "sum": 10.0
     * }
     * @apiSuccess {String} message Localized success message
     * @apiSuccess {String} balance Current active balance
     * @apiSuccess {String} hash For voucher - a code to be sent to recipient
     * @apiSuccess {String} recipient Recipient nickname (may be absent for free vouchers)
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "message": "Ваш перевод #130 через систему VoucherTransfer был создан",
     * "balance": "USD 99899.80",
     * "hash": "7ab09209e505eb5d44085e8568d3274b5651b1b71424b210d61760845d835501",
     * "recipient": "talalai123"
     * <p>
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse InternalServerError
     */
    // TODO temporary disable
    // @RequestMapping(value = "/transfer/submit", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  /*  public TransferResponseDto submitTransfer(@RequestBody TransferRequestParamsDto requestParamsDto) {
        requestParamsDto.setOperationType(USER_TRANSFER);
        Locale userLocale = userService.getUserLocaleForMobile(SecurityContextHolder.getContext().getAuthentication().getName());
        String userEmail = getAuthenticatedUserEmail();
        TransferStatusEnum beginStatus = (TransferStatusEnum) TransferStatusEnum.getBeginState();
        Payment payment = new Payment(requestParamsDto.getOperationType());
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        payment.setRecipient(requestParamsDto.getRecipient());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail, userLocale)
                .orElseThrow(InvalidAmountException::new);
        TransferRequestCreateDto request = new TransferRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, userLocale);
        Map<String, Object> result = transferService.createTransferRequest(request);
        TransferResponseDto responseDto = new TransferResponseDto();
        responseDto.setBalance((String) result.get("balance"));
        responseDto.setMessage((String) result.get("message"));
        responseDto.setHash((String) result.get("hash"));
        return responseDto;
    }*/

    /**
     * @api {get} /api/payments/lastAddress Get last address
     * @apiName getLastUsedAddressForMerchantAndCurrency
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns last used address for currency
     * @apiParam {Integer} currencyId
     * @apiParam {Integer} merchantId
     * @apiParamExample Request example
     * /api/payments/lastAddress?currencyId=21&merchantId=22
     * @apiSuccess {Array} merchants List of addresses for each merchant (generally there's only one item)
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.merchantId merchant id
     * @apiSuccess {String} data.mainAddress merchant name
     * @apiSuccess {String} data.address merchant detail
     * @apiSuccess {String} data.additionalFieldName name of additional tag
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "merchantId": 20,
     * "mainAddress": "rEDz1wnKCSakb8AU1ScCRdkLHFgr7XTNij",
     * "address": "495191240",
     * "additionalFieldName": "Destination Tag"
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/lastAddress", method = GET)
    public CryptoAddressDto getLastUsedAddressForMerchantAndCurrency(@RequestParam Integer currencyId, @RequestParam Integer merchantId) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(
                Collections.singletonList(currencyId), OperationType.INPUT).stream()
                .filter(item -> item.getMerchantId() == merchantId).collect(Collectors.toList());
        refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(merchantCurrencyData, userEmail);
        CryptoAddressDto result = merchantCurrencyData.stream().map(CryptoAddressDto::new).
                findFirst().orElseThrow(() -> new MerchantNotFoundException(String.valueOf(merchantId)));
        if (StringUtils.isEmpty(result.getAddress())) {
            //TODO temp
            RefillRequestParamsDto requestParamsDto = new RefillRequestParamsDto();
            requestParamsDto.setCurrency(currencyId);
            requestParamsDto.setMerchant(merchantId);
            requestParamsDto.setSum(BigDecimal.ZERO);
            RefillRequestCreateDto refillRequest = prepareRefillRequest(requestParamsDto, userEmail, userLocale);
            refillService.createRefillRequest(refillRequest);
            refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(merchantCurrencyData, userEmail);
            result = merchantCurrencyData.stream().map(CryptoAddressDto::new).
                    findFirst().orElseThrow(() -> new MerchantNotFoundException(String.valueOf(merchantId)));
        }
        return result;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ApiError httpMessageNotReadableExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(REQUEST_NOT_READABLE, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public ApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidAmountException.class)
    public ApiError invalidAmountExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_PAYMENT_AMOUNT, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler({NotEnoughMoneyException.class, NotEnoughUserWalletMoneyException.class})
    public ApiError notEnoughMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INSUFFICIENT_FUNDS, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler({IllegalInvoiceStatusException.class})
    public ApiError illegalInvoiceRequestStatusExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(BAD_INVOICE_STATUS, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public ApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({InvoiceNotFoundException.class, me.exrates.service.exception.invoice.InvoiceNotFoundException.class})
    @ResponseBody
    public ApiError invoiceNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INVOICE_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({UserNotFoundException.class, me.exrates.dao.exception.UserNotFoundException.class})
    @ResponseBody
    public ApiError userNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.USER_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(VoucherNotFoundException.class)
    @ResponseBody
    public ApiError VoucherNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.VOUCHER_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidNicknameException.class)
    @ResponseBody
    public ApiError invalidNicknameExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.SELF_TRANSFER_NOT_ALLOWED, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(MerchantCurrencyBlockedException.class)
    @ResponseBody
    public ApiError merchantCurrencyBlockedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.BLOCKED_CURRENCY_FOR_MERCHANT, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InputRequestLimitExceededException.class)
    @ResponseBody
    public ApiError inputRequestLimitExceededExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INPUT_REQUEST_LIMIT_EXCEEDED, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(RequestLimitExceededException.class)
    @ResponseBody
    public ApiError outputRequestLimitExceededExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.OUTPUT_REQUEST_LIMIT_EXCEEDED, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(CommissionExceedingAmountException.class)
    @ResponseBody
    public ApiError commissionExceedingAmountExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.COMMISSION_EXCEEDS_AMOUNT, req.getRequestURL(), exception);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }
*/

}
