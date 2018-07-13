package me.exrates.service.decred;

import me.exrates.service.decred.rpc.Api;

import java.util.Iterator;

public interface DecredGrpcService {
    Api.NextAddressResponse getNewAddress();

    Iterator<Api.GetTransactionsResponse> getTransactions(int lastBlock);

    Api.BestBlockResponse getBlockInfo();
}
