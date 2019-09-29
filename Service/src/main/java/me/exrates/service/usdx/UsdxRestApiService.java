package me.exrates.service.usdx;

import me.exrates.service.usdx.model.UsdxAccountBalance;
import me.exrates.service.usdx.model.UsdxTransaction;

import java.util.List;

public interface UsdxRestApiService {

    UsdxTransaction transferAssetsToUserAccount(UsdxTransaction usdxTransaction);

    List<UsdxTransaction> getAllTransactions();

    List<UsdxTransaction> getTransactionsHistory(String fromId, int limit);

    UsdxAccountBalance getAccountBalance();

    UsdxTransaction getTransactionStatus(String transferId);


    String getAccountName();

    String getSecurityHeaderName();

    String generateSecurityHeaderValue(String timestamp, String body);

    String getStringJsonUsdxTransaction(UsdxTransaction usdxTransaction);
}
