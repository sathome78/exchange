package me.exrates.controller.merchants;

import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.userOperation.UserOperationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.*;
import static me.exrates.model.enums.UserCommentTopicEnum.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class CommonMerchantsController {

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MerchantService merchantService;

  @Autowired
  private WalletService walletService;

  @Autowired
  private UserService userService;

  @Autowired
  private UserOperationService userOperationService;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  WithdrawService withdrawService;

  @Autowired
  RefillService refillService;

  @Autowired
  TransferService transferService;
  @Autowired
  private MessageSource messageSource;

  private static final Logger LOG = LogManager.getLogger("merchant");

  @RequestMapping(value = "/merchants/input", method = GET)
  public ModelAndView inputCredits(
      @RequestParam("currency") String currencyName,
      Principal principal) {
    try {
      OperationType operationType = INPUT;
      ModelAndView modelAndView = new ModelAndView("globalPages/merchantsInput");
      Currency currency = currencyService.findByName(currencyName);
      modelAndView.addObject("currency", currency);
      Payment payment = new Payment();
      payment.setOperationType(operationType);
      modelAndView.addObject("payment", payment);
      BigDecimal minRefillSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
      modelAndView.addObject("minRefillSum", minRefillSum);
      Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForRefill();
      modelAndView.addObject("scaleForCurrency", scaleForCurrency);
      List<Integer> currenciesId = Collections.singletonList(currency.getId());
      List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
      refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(merchantCurrencyData, principal.getName());
      modelAndView.addObject("merchantCurrencyData", merchantCurrencyData);
      boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(principal.getName()), UserOperationAuthority.INPUT);
      modelAndView.addObject("accessToOperationForUser", accessToOperationForUser);
      List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), REFILL_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      modelAndView.addObject("isAmountInputNeeded", merchantCurrencyData.size() > 0
              && !merchantCurrencyData.get(0).getProcessType().equals("CRYPTO"));
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName() + ": " + e.getMessage());
      return modelAndView;
    }
  }

  @RequestMapping(value = "/merchants/output", method = GET)
  public ModelAndView outputCredits(
      @RequestParam("currency") String currencyName,
      Principal principal) {
    try {
      OperationType operationType = OUTPUT;
      ModelAndView modelAndView = new ModelAndView("globalPages/merchantsOutput");
      Currency currency = currencyService.findByName(currencyName);
      modelAndView.addObject("currency", currency);
      Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(principal.getName()), currency);
      modelAndView.addObject("wallet", wallet);
      modelAndView.addObject("balance", BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
      Payment payment = new Payment();
      payment.setOperationType(operationType);
      modelAndView.addObject("payment", payment);
      BigDecimal minWithdrawSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
      modelAndView.addObject("minWithdrawSum", minWithdrawSum);
      Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
      modelAndView.addObject("scaleForCurrency", scaleForCurrency);
      List<Integer> currenciesId = Collections.singletonList(currency.getId());
      List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
      withdrawService.retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies(merchantCurrencyData);
      modelAndView.addObject("merchantCurrencyData", merchantCurrencyData);
      boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(principal.getName()), UserOperationAuthority.OUTPUT);
      modelAndView.addObject("accessToOperationForUser", accessToOperationForUser);
      List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), WITHDRAW_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      boolean checkingZeroBalance = wallet.getActiveBalance().signum()==0;
      modelAndView.addObject("checkingZeroBalance", checkingZeroBalance);
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName() + ": " + e.getMessage());
      return modelAndView;
    }
  }

  @RequestMapping(value = "/merchants/transfer", method = GET)
  public ModelAndView transfer(
      @RequestParam("currency") String currencyName,
      Principal principal) {
    try {
      OperationType operationType = USER_TRANSFER;
      ModelAndView modelAndView = new ModelAndView("globalPages/transfer");
      Currency currency = currencyService.findByName(currencyName);
      modelAndView.addObject("currency", currency);
      Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(principal.getName()), currency);
      modelAndView.addObject("wallet", wallet);
      modelAndView.addObject("balance", BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
      Payment payment = new Payment();
      payment.setOperationType(operationType);
      modelAndView.addObject("payment", payment);
      BigDecimal minTransferSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
      modelAndView.addObject("minTransferSum", minTransferSum);
      Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
      modelAndView.addObject("scaleForCurrency", scaleForCurrency);
      List<Integer> currenciesId = Collections.singletonList(currency.getId());
      List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
      transferService.retrieveAdditionalParamsForWithdrawForMerchantCurrencies(merchantCurrencyData);
      modelAndView.addObject("merchantCurrencyData", merchantCurrencyData);
      boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(principal.getName()), UserOperationAuthority.TRANSFER);
      modelAndView.addObject("accessToOperationForUser", accessToOperationForUser);
     /* List<String> initialWarningCodeList = currencyService.getWarningForCurrency(currency.getId(), INITIAL_TRANSFER_CURRENCY_WARNING);
      modelAndView.addObject("initialWarningCodeList", initialWarningCodeList);*/
      List<String> warningCodeList = currencyService.getWarningsByTopic(TRANSFER_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      boolean checkingZeroBalance = wallet.getActiveBalance().signum()==0;
      modelAndView.addObject("checkingZeroBalance", checkingZeroBalance);
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName() + ": " + e.getMessage());
      return modelAndView;
    }
  }

  @RequestMapping(value = "/merchants/data", method = GET)
  public
  @ResponseBody
  List<MerchantCurrency> getMerchantsData() {
    List<Integer> currenciesId = currencyService
        .getAllCurrencies()
        .stream()
        .mapToInt(Currency::getId)
        .boxed()
        .collect(Collectors.toList());
    return merchantService
        .getAllUnblockedForOperationTypeByCurrencies(currenciesId, OperationType.INPUT);
  }

  @RequestMapping(value = "/merchants/commission", method = GET)
  @ResponseBody
  public Map<String,String> getCommissions(final @RequestParam("type") OperationType type,
                                           final @RequestParam("amount") BigDecimal amount,
                                           final @RequestParam("currency") String currency,
                                           final @RequestParam("merchant") String merchant,
                                           Locale locale)
  {
    try {
      return merchantService.computeCommissionAndMapAllToString(amount, type, currency, merchant);
    } catch (InvalidAmountException e) {
      throw new InvalidAmountException(messageSource.getMessage(e.getMessage(), null, locale));
    }
  }

  @RequestMapping(value = "/merchants/warnings", method = GET)
  @ResponseBody
  public List<String> getMerchantWarnings( @RequestParam("type") OperationType type,
                                           @RequestParam("merchant") Integer merchantId,
                                           Locale locale) {
    return merchantService.getWarningsForMerchant(type, merchantId, locale);
  }


}
