package me.exrates.service.stellar;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.enums.StellarNetworkModeEnum;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.TransactionResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maks on 11.06.2017.
 */
@Log4j2(topic = "stellar_log")
@Service
@PropertySource("classpath:/merchants/stellar.properties")
@Conditional(MonolitConditional.class)
public class StellarTransactionServiceImpl implements StellarTransactionService {

    private static final BigDecimal XLM_MIN_BALANCE = new BigDecimal(21);
    private @Value("${stellar.mode}") String MODE;
    private @Value("${stellar.horizon.url}")String SEVER_URL;
    private Server server;

    @PostConstruct
    public void init() {
        server = new Server(SEVER_URL);
    }

    @Override
    public TransactionResponse getTxByHash(String txId) throws IOException {
        return server.transactions().transaction(txId);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto, String serverUrl, String accountSecret)  {
        Server server = new Server(serverUrl);
        KeyPair source = KeyPair.fromSecretSeed(accountSecret);
        KeyPair destination = KeyPair.fromAccountId(withdrawMerchantOperationDto.getAccountTo());
    // First, check to make sure that the destination account exists.
    // You could skip this, but if the account does not exist, you will be charged
    // the transaction fee when the transaction fails.
    // It will throw HttpResponseException if account does not exist or there was another error.
        try {
            server.accounts().account(destination.getAccountId());
        } catch (IOException e) {
            throw new InvalidAccountException("Destination XLM account not found");
        }
    // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
            String balance = Arrays.stream(sourceAccount.getBalances())
                    .filter(p -> p.getAsset().equals(new AssetTypeNative())).findFirst().get().getBalance();
            if (new BigDecimal(balance).compareTo(XLM_MIN_BALANCE) <= 0 ) {
                throw new InsufficientCostsInWalletException("XLM BALANCE LOW");
            }
        } catch (IOException | NullPointerException e) {
           throw new RuntimeException("System account not found");
        }
    // Start building the transaction.
        Transaction transaction = new Transaction.Builder(sourceAccount, getNetworkMode())
                .addOperation(new PaymentOperation.Builder(destination.getAccountId(),
                        new AssetTypeNative(), withdrawMerchantOperationDto.getAmount()).build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(StringUtils
                        .isEmpty(withdrawMerchantOperationDto.getDestinationTag()) ? Memo.text("")
                        : Memo.id(Long.valueOf(withdrawMerchantOperationDto.getDestinationTag())))
                .build();

    // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
    // And finally, send it off to Stellar!
        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            log.debug("response is success {}", response.isSuccess());
            if (response.isSuccess()) {
                return new HashMap<String, String>() {{
                    put("hash", response.getHash());
                }};
            } else {
                String result = response.getExtras().getResultCodes().getTransactionResultCode();
                log.debug("error result {}", result);
                throw new MerchantException(result);
            }
        } catch (Exception e) {
            log.debug("xlm_error", e);
            throw new RuntimeException(e.toString());
        }
    }


    private Network getNetworkMode() {
        switch (StellarNetworkModeEnum.valueOf(MODE)) {
            case PUBLIC : {
                return Network.PUBLIC;
            }
            case TEST :
            default: {
                return Network.TESTNET;
            }
        }
    }
}
