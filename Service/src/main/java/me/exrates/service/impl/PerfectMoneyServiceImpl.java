package me.exrates.service.impl;

import com.squareup.okhttp.*;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.PerfectMoneyService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.InvalidPayeeWalletException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.NotImplimentedMethod;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/merchants/perfectmoney.properties")
public class PerfectMoneyServiceImpl implements PerfectMoneyService {

    private @Value("${accountId}") String accountId;
    private @Value("${accountPass}") String accountPass;
    private @Value("${payeeName}") String payeeName;
    private @Value("${paymentSuccess}") String paymentSuccess;
    private @Value("${paymentFailure}") String paymentFailure;
    private @Value("${paymentStatus}") String paymentStatus;
    private @Value("${USDAccount}") String usdCompanyAccount;
    private @Value("${EURAccount}") String eurCompanyAccount;
    private @Value("${alternatePassphrase}") String alternatePassphrase;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PerfectMoneyServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;

    @Override
    public Map<String, String> getPerfectMoneyParams(Transaction transaction) {
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final String currency = transaction.getCurrency().getName();
        final Number amountToPay;
        switch (currency) {
            case "GOLD":
                amountToPay = sum.toBigInteger();
                break;
            default:
                amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return new HashMap<String,String>(){
            {
                put("PAYEE_ACCOUNT", currency.equals("USD") ? usdCompanyAccount : eurCompanyAccount);
                put("PAYEE_NAME",payeeName);
                put("PAYMENT_AMOUNT", String.valueOf(amountToPay));
                put("PAYMENT_UNITS",currency);
                put("PAYMENT_ID", String.valueOf(transaction.getId()));
                put("PAYMENT_URL",paymentSuccess);
                put("NOPAYMENT_URL",paymentFailure);
                put("STATUS_URL",paymentStatus);
                put("FORCED_PAYMENT_METHOD","account");
            }
        };
    }

    @Override
    @Transactional
    public void provideOutputPayment(Payment payment, CreditsOperation creditsOperation) {
        final Transaction transaction = preparePaymentTransactionRequest(creditsOperation);
        provideTransaction(transaction.getId());
        final String response = provideOutputPayment(payment.getDestination(), transaction);
        switch (response) {
            case "OK":
                return;
            case "INVALID_USER_ACCOUNT" :
                throw new InvalidPayeeWalletException();
            case "INVALID_AMOUNT" :
                throw new InvalidAmountException();
            default:
                throw new MerchantInternalException("Exception while Output");
        }
    }

    private String provideOutputPayment(String to,Transaction transaction) {
        OkHttpClient okHttpClient = new OkHttpClient();
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final String currency = transaction.getCurrency().getName();
        final Number amountToPay;
        switch (currency) {
            case "GOLD":
                amountToPay = sum.toBigInteger();
                break;
            default:
                amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("AccountID", accountId)
                .addFormDataPart("PassPhrase", accountPass)
                .addFormDataPart("Payer_Account", currency.equals("USD") ? usdCompanyAccount : eurCompanyAccount)
                .addFormDataPart("Payee_Account", to)
                .addFormDataPart("Amount", String.valueOf(amountToPay))
                .addFormDataPart("PAYMENT_ID", String.valueOf(transaction.getId()))
                .build();
        Request request = new Request.Builder()
                .url("https://perfectmoney.is/acct/confirm.asp")
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();
            if (responseString.contains("ERROR")) {
                if (responseString.contains("Invalid Payee_Account")) {
                    return "INVALID_USER_ACCOUNT";
                } else if (responseString.contains("Invalid Amount")) {
                    return "INVALID_AMOUNT";
                }
                return "INTERNAL_ERROR";
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
        return "OK";
    }

    @Override
    @Transactional
    public Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation) {
        return transactionService.createTransactionRequest(creditsOperation);
    }

    @Override
    @Transactional
    public boolean provideTransaction(int transactionId) {
        Transaction transaction = transactionService.findById(transactionId);
        if (transaction.isProvided()){
            return true;
        }
        try{
            transactionService.provideTransaction(transaction);
        }catch (EmptyResultDataAccessException e){
            logger.error(e);
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void invalidateTransaction(Transaction transaction) {
        transactionService.invalidateTransaction(transaction);
    }

    @Override
    public String computePaymentHash(Map<String, String> params) {
        final String passpphraseHash = algorithmService.computeMD5Hash(alternatePassphrase).toUpperCase();
        final String hashParams = params.get("PAYMENT_ID") +
                ":"+params.get("PAYEE_ACCOUNT") +
                ":"+params.get("PAYMENT_AMOUNT") +
                ":"+params.get("PAYMENT_UNITS") +
                ":"+params.get("PAYMENT_BATCH_NUM") +
                ":"+params.get("PAYER_ACCOUNT") +
                ":"+passpphraseHash +
                ":"+params.get("TIMESTAMPGMT");
        return algorithmService.computeMD5Hash(hashParams).toUpperCase();
    }

    @Override
    public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new NotImplimentedMethod("for "+withdrawMerchantOperationDto);
    }
}