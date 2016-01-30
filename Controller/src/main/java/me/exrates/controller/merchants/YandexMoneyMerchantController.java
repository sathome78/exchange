package me.exrates.controller.merchants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Authorization;
import com.yandex.money.api.net.OAuth2Session;
import com.yandex.money.api.typeadapters.TokenTypeAdapter;
import com.yandex.money.api.utils.HttpHeaders;
import com.yandex.money.api.utils.Strings;
import me.exrates.YandexMoneyProperties;
import me.exrates.model.Payment;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
//// TODO: 1/26/16 Handle transactions
//// TODO: 1/26/16 Refund
//// TODO: 1/26/16 Logging
@Controller
@org.springframework.context.annotation.Scope("session")
@RequestMapping("/merchants/yandexmoney")
public class YandexMoneyMerchantController {

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private CompanyAccountService companyAccountService;

    @Autowired
    private YandexMoneyProperties yandexMoneyProperties;

    private static final String merchantErrorPage = "redirect:merchants/yandexmoney/error";

    private static final BigDecimal COMMISSION = BigDecimal.valueOf(.03);

    @RequestMapping(value = "/token/authorization",method =  RequestMethod.GET)
    public ModelAndView yandexMoneyTemporaryAuthorizationCodeRequest(ModelAndView modelAndView) {
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.clientId(),true);
        OAuth2Session session = new OAuth2Session(apiClient);
        OAuth2Authorization oAuth2Authorization = session.createOAuth2Authorization();
        com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        session.setDebugLogging(true);
        byte[] params = oAuth2Authorization.getAuthorizeParams()
                .addScope(Scope.ACCOUNT_INFO)
                .addScope(Scope.PAYMENT_P2P)
                .setRedirectUri(yandexMoneyProperties.redirectURI())
                .setResponseType(yandexMoneyProperties.responseType())
                .build();
        Request request = new Request.Builder()
                .url(oAuth2Authorization.getAuthorizeUrl())
                .post(RequestBody.create(yandexMoneyProperties.mediaType(),params))
                .build();
        Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            return new ModelAndView(merchantErrorPage,new ModelMap().addAttribute("error","An error has been occurred. Please try again later"));
        }
        modelAndView.setViewName("redirect:"+response.header(HttpHeaders.LOCATION));
        return modelAndView;
    }

    @RequestMapping(value = "/token/access")
    public ModelAndView yandexMoneyAccessTokenRequest(@RequestParam(value = "code",required = false) String code, Principal principal) {
        ModelAndView errorModelAndView = new ModelAndView(merchantErrorPage, new ModelMap().addAttribute("error", "Internal server error. Please try again later"));
        if (Strings.isNullOrEmpty(code)) {
            return errorModelAndView;
        }
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.clientId(),true);
        com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        Request request = new Request.Builder()
                .url(apiClient.getHostsProvider().getMoney()+"/oauth/token")
                .post(RequestBody.create(yandexMoneyProperties.mediaType(),"code="+code
                +"&client_id="+yandexMoneyProperties.clientId()+"&grant_type=authorization_code&redirect_uri="+
                yandexMoneyProperties.redirectURI()))
                .build();
        Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            return errorModelAndView;
        }
        Token token;
        try {
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(response.body().string(), JsonElement.class);
            token = TokenTypeAdapter.getInstance().deserialize(jsonElement, Token.class, null);
        } catch (IOException e) {
            return errorModelAndView;
        }
        if (yandexMoneyService.addToken(token.accessToken, principal.getName())) {
            return new ModelAndView("redirect:merchants/yandexmoney/payment/prepare");
        }
        return errorModelAndView;
    }

    @RequestMapping(value = "/payment/prepare",method = RequestMethod.POST)
    public ModelAndView preparePayment(@ModelAttribute @Valid  Payment payment, BindingResult result, Principal principal, HttpSession httpSession) {
        System.out.println(payment.getUserId());
        System.out.println(payment.getSum());
        System.out.println(result.hasErrors());
        //commissionService.getCommissionByType(); // TODO: 1/26/16 receive commission here from database
        System.out.println(yandexMoneyProperties.yandexMoneyP2PCommission());
        final ModelAndView modelAndView = new ModelAndView("assertpayment");
        final int userId = userService.getIdByEmail(principal.getName());
        final BigDecimal paymentSum= BigDecimal.valueOf(payment.getSum());
        final BigDecimal amountToBeCredited =
                (paymentSum.subtract(paymentSum).multiply(COMMISSION)).setScale(9,BigDecimal.ROUND_CEILING);
        ModelMap modelMap = new ModelMap()
                .addAttribute("userId",userId)
                .addAttribute("currency",payment.getCurrency())
                .addAttribute("amount",amountToBeCredited)
                .addAttribute("commission",paymentSum.subtract(amountToBeCredited).setScale(9,BigDecimal.ROUND_CEILING))
                .addAttribute("sumToPay",paymentSum.add(paymentSum.subtract(amountToBeCredited)));
        httpSession.setAttribute("paymentPrepareData",modelMap);
        modelAndView.addAllObjects(modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "/payment/process",method = RequestMethod.POST)
    public ModelAndView processPayment(Principal principal, HttpSession httpSession) throws InvalidTokenException, InsufficientScopeException, InvalidRequestException, IOException {
        String email = principal.getName();
        String accessToken = yandexMoneyService.getTokenByUserEmail(email);
        if (Strings.isNullOrEmpty(accessToken)) {
            return new ModelAndView("redirect:/merchants/yandexmoney/token/authorization");
        }
        ModelMap paymentData;
        paymentData = (ModelMap) httpSession.getAttribute("paymentPrepareData");
        httpSession.removeAttribute("paymentPrepareData");
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.clientId(),true);
        OAuth2Session oAuth2Session = new OAuth2Session(apiClient);
        oAuth2Session.setAccessToken(accessToken);
        String payInfo = "Purchase "+ paymentData.get("amount") + paymentData.get("currency")
                + " from " + principal.getName() + ". Total transferred amount: " + paymentData.get("sumToPay")
                + ", Commission: "+paymentData.get("commission") + ", Amount to be credited to user wallet: " + paymentData.get("amount");
        System.out.println(payInfo); // Yandex won`t receive this string(answers with error - illegal_param_label). Perhaps data too long, but nothing in documentation about this case
        P2pTransferParams p2pTransferParams = new P2pTransferParams.Builder(yandexMoneyProperties.companyYandexMoneyWalletId())
                .setAmount(BigDecimal.valueOf((Double)paymentData.get("sumToPay")))
                .setComment("Purchase "+ paymentData.get("amount")+ paymentData.get("currency")+" at the S.E. Birzha")
                .create();
        RequestPayment.Request request = RequestPayment.Request.newInstance(p2pTransferParams);
        RequestPayment execute;
        ModelAndView redirectToMerchantError = new ModelAndView(merchantErrorPage);
        try {
            execute = oAuth2Session.execute(request);
            BaseRequestPayment.Status responseStatus = execute.status;
            if (responseStatus.equals(BaseRequestPayment.Status.REFUSED)) {
                switch (execute.error) {
                    case NOT_ENOUGH_FUNDS:
                        return redirectToMerchantError.addObject("error","Transaction failed. Not enough money");
                    case AUTHORIZATION_REJECT:
                        return redirectToMerchantError.addObject("error","Transaction failed. Authorization rejected");
                    case LIMIT_EXCEEDED:
                        return redirectToMerchantError.addObject("error","Limit exceeded. Please, try again later");
                    case ACCOUNT_BLOCKED:
                        return new ModelAndView("redirect:"+execute.accountUnblockUri);
                    case EXT_ACTION_REQUIRED:
                        return new ModelAndView("redirect:"+execute.extActionUri);
                    default:
                        //// TODO: 1/26/16 LOG error
                        return redirectToMerchantError.addObject("Transaction failed. Internal server error.");
                }
            }
        } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
            //// TODO: 1/26/16 LOG error
            return redirectToMerchantError.addObject("error","Transaction failed. Internal server error.");
        }
        ProcessPayment processPayment = oAuth2Session.execute(new ProcessPayment.Request(execute.requestId));
        if (processPayment.status.equals(ProcessPayment.Status.SUCCESS)) {
            final int idByEmail = userService.getIdByEmail(principal.getName());
            final int walletId = walletService.getWalletId(idByEmail, (Integer) paymentData.get("currency"));
            walletService.setWalletABalance(walletId,(double)paymentData.get("amount"));
            return new ModelAndView("redirect:merchants");
        } else if (processPayment.status.equals(ProcessPayment.Status.REFUSED)){
            switch (processPayment.error) {
                case NOT_ENOUGH_FUNDS:
                    return redirectToMerchantError.addObject("error","Transaction failed. Not enough money");
                case ACCOUNT_BLOCKED:
                    return new ModelAndView("redirect:"+execute.accountUnblockUri);
            }
        }
        return redirectToMerchantError.addObject("error","Transaction failed. Internal server error.");
    }
}