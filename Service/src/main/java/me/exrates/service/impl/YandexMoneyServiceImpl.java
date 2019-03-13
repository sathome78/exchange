package me.exrates.service.impl;

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
import com.yandex.money.api.utils.Strings;
import me.exrates.dao.YandexMoneyMerchantDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.squareup.okhttp.MediaType.parse;
import static me.exrates.model.enums.OperationType.INPUT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service("yandexMoneyService")
@PropertySource("classpath:/merchants/yandexmoney.properties")
@Conditional(MonolitConditional.class)
public class YandexMoneyServiceImpl implements YandexMoneyService {

    private @Value("${yandexmoney.clientId}") String clientId;
    private @Value("${yandexmoney.token}") String token;
    private @Value("${yandexmoney.redirectURI}") String redirectURI;
    private @Value("${yandexmoney.companyWalletId}") String companyWalletId;
    private @Value("${yandexmoney.responseType}") String responseType;

    private static final Logger logger = LogManager.getLogger(YandexMoneyServiceImpl.class);

    @Autowired
    private YandexMoneyMerchantDao yandexMoneyMerchantDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WithdrawUtils withdrawUtils;

    @Override
    public List<String> getAllTokens() {
        return yandexMoneyMerchantDao.getAllTokens();
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {
        return yandexMoneyMerchantDao.getTokenByUserEmail(userEmail);
    }

    @Override
    public boolean addToken(String token, String email) {
        final int id = userService.getIdByEmail(email);
        return yandexMoneyMerchantDao.createToken(token, id);
    }

    @Override
    public boolean updateTokenByUserEmail(String newToken, String email) {
        return yandexMoneyMerchantDao.updateTokenByUserEmail(email, newToken);
    }

    @Override
    public boolean deleteTokenByUserEmail(String email) {
        return yandexMoneyMerchantDao.deleteTokenByUserEmail(email);
    }

    @Override
    public String getTemporaryAuthCode(String redirectURI) {
        final DefaultApiClient apiClient = new DefaultApiClient(clientId, true);
        final OAuth2Session session = new OAuth2Session(apiClient);
        final OAuth2Authorization oAuth2Authorization = session.createOAuth2Authorization();
        final com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        final byte[] params = oAuth2Authorization.getAuthorizeParams()
                .addScope(Scope.ACCOUNT_INFO)
                .addScope(Scope.PAYMENT_P2P)
                .setRedirectUri(redirectURI)
                .setResponseType(responseType)
                .build();
        final Request request = new Request.Builder()
                .url(oAuth2Authorization.getAuthorizeUrl())
                .post(RequestBody.create(parse(APPLICATION_FORM_URLENCODED.toString()), params))
                .build();
        final Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            logger.fatal(e);
            throw new MerchantInternalException("YandexMoneyServiceInput");
        }
        return response.header(HttpHeaders.LOCATION);
    }

    @Override
    public String getTemporaryAuthCode() {
        return getTemporaryAuthCode(redirectURI);
    }

    @Override
    public Optional<String> getAccessToken(String code) {
        final Token.Request request = new Token.Request(code, clientId, redirectURI);
        final OAuth2Session session = new OAuth2Session(new DefaultApiClient(clientId));
        final Token token;
        try {
            token = session.execute(request);
        } catch (IOException e) {
            logger.fatal(e);
            throw new MerchantInternalException("YandexMoneyServiceInput");
        } catch (InvalidRequestException | InvalidTokenException | InsufficientScopeException e) {
            logger.error(e);
            return Optional.empty();
        }
        return Optional.of(token.accessToken);
    }

    @Override
    @Transactional
    public Optional<RequestPayment> requestPayment(String token, CreditsOperation creditsOperation) {
        if (Strings.isNullOrEmpty(token)) {
            token = this.token;
        }
        final DefaultApiClient apiClient = new DefaultApiClient(clientId, true);
        final OAuth2Session oAuth2Session = new OAuth2Session(apiClient);
        oAuth2Session.setAccessToken(token);
        final String destination = creditsOperation
                .getDestination()
                .orElse(companyWalletId);
        final BigDecimal amount = creditsOperation.getOperationType() == INPUT ?
                creditsOperation.getAmount().add(creditsOperation.getCommissionAmount()) :
                creditsOperation.getAmount().subtract(creditsOperation.getCommissionAmount());
        final P2pTransferParams p2pTransferParams = new P2pTransferParams.Builder(destination)
                .setAmount(amount.setScale(2,BigDecimal.ROUND_HALF_UP))
                .create();
        final RequestPayment.Request request = RequestPayment.Request.newInstance(p2pTransferParams);
        try {
            final RequestPayment execute = oAuth2Session.execute(request);
            if (execute.status.equals(BaseRequestPayment.Status.REFUSED)) {
                return Optional.of(execute);
            }
            executePayment(execute.requestId,oAuth2Session,creditsOperation);
        } catch (IOException e) {
            logger.fatal(e);
            final String message = "YandexMoneyService".concat(destination.equals(companyWalletId) ? "Input" : "Output");
            throw new MerchantInternalException(message);
        } catch (InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected void executePayment(String requestId, OAuth2Session oAuth2Session,
                                  CreditsOperation creditsOperation) {
        final ProcessPayment processPayment;
        final Transaction transaction;
        try {
            transaction = transactionService.createTransactionRequest(creditsOperation);
            transactionService.provideTransaction(transaction);
            processPayment = oAuth2Session.execute(new ProcessPayment.Request(requestId));
        } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
            logger.fatal(e.getMessage());
            throw new MerchantInternalException(creditsOperation.getOperationType().name());
        }
        if (processPayment.status.equals(ProcessPayment.Status.SUCCESS)) {
            logger.info(transaction.toString());
            return;
        }
        if (processPayment.status.equals(ProcessPayment.Status.REFUSED)) {
            switch (processPayment.error) {
                case NOT_ENOUGH_FUNDS:
                    throw new NotEnoughUserWalletMoneyException("Not enough money on yandex wallet");
//                case ACCOUNT_BLOCKED:
//                    return new ModelAndView("redirect:" + execute.accountUnblockUri);
            }
        }
    }

    @Override
    public int saveInputPayment(Payment payment) {
        return yandexMoneyMerchantDao.savePayment(payment.getCurrency(), BigDecimal.valueOf(payment.getSum()));
    }
    @Override
    public Optional<Payment> getPaymentById(Integer id) {
        return yandexMoneyMerchantDao.getPaymentById(id);
    }
    @Override
    public void deletePayment(Integer id) {
        yandexMoneyMerchantDao.deletePayment(id);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new NotImplimentedMethod("for "+withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request){
        throw new NotImplimentedMethod("for "+request);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplimentedMethod("for "+params);
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }

}