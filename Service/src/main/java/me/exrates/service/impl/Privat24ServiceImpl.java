package me.exrates.service.impl;

import com.squareup.okhttp.OkHttpClient;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.Privat24Service;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@PropertySource("classpath:/merchants/privat24.properties")
public class Privat24ServiceImpl implements Privat24Service {

    private @Value("${privat24.url}") String url;
    private @Value("${privat24.merchant}") String merchant;
    private @Value("${privat24.details}") String details;
    private @Value("${privat24.ext_details}") String ext_details;
    private @Value("${privat24.pay_way}") String pay_way;
    private @Value("${privat24.return_url}") String return_url;
    private @Value("${privat24.server_url}") String server_url;
    private @Value("${privat24.password}") String password;

    private final OkHttpClient client = new OkHttpClient();

    private static final Logger LOG = LogManager.getLogger("merchant");

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private WithdrawUtils withdrawUtils;

    @Override
    @Transactional
    public Map<String, String> preparePayment(CreditsOperation creditsOperation, String email) {

        Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);

        Map<String, String> properties = new HashMap<>();

        properties.put("amt", String.valueOf(amountToPay));
        properties.put("ccy", creditsOperation.getCurrency().getName());
        properties.put("merchant", merchant);
        properties.put("order", String.valueOf(transaction.getId()));
        properties.put("details", details);
        properties.put("ext_details", ext_details + transaction.getId());
        properties.put("pay_way", pay_way);
        properties.put("return_url", return_url);
        properties.put("server_url", server_url);

        return properties;
    }

    @Override
    @Transactional
    public boolean confirmPayment(Map<String,String> params, String signature, String payment) {

        Transaction transaction;
        try{
            transaction = transactionService.findById(Integer.parseInt(params.get("order")));
            if (transaction.isProvided()){
                return true;
            }
        }catch (EmptyResultDataAccessException e){
            LOG.error(e);
            return false;
        }
        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

        String checkSignature = algorithmService.sha1(algorithmService.computeMD5Hash(payment + password));
        if (checkSignature.equals(signature)
                && Double.parseDouble(params.get("amt"))==transactionSum){
            transactionService.provideTransaction(transaction);
            return true;
        }

        return false;
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
