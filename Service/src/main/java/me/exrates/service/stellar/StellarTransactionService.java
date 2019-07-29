package me.exrates.service.stellar;

import me.exrates.model.dto.WithdrawMerchantOperationDto;
import org.stellar.sdk.responses.TransactionResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by maks on 11.06.2017.
 */
public interface StellarTransactionService {

    TransactionResponse getTxByHash(String txId) throws IOException;

    Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto, String serverUrl, String accountSecret);

}
