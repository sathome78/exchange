package me.exrates.service.orders;

import com.google.common.base.Preconditions;

public class HzCollectionNamesUtils {

    private static final String WALLETS_COLLECTIONS_PREFIX = "wallets_";
    private static final String ORDERS_COLLECTIONS_PREFIX= "orders_";

    private static final String TRANSACTIONS_COLLECTION = "TRANSACTIONS";

    private static final String TRADES_COLLECTION = "TRADES";

    private HzCollectionNamesUtils() {
    }

    public static String getNameForOrdersCollection(Integer pairId) {
        Preconditions.checkNotNull(pairId);
        return ORDERS_COLLECTIONS_PREFIX.concat(pairId.toString());
    }

    public static String getNameForWalletsCollection(Integer currencyId) {
        Preconditions.checkNotNull(currencyId);
        return WALLETS_COLLECTIONS_PREFIX.concat(currencyId.toString());
    }

    public static String getTransactionsCollectionName() {
        return TRANSACTIONS_COLLECTION;
    }

    public static String getTradesCollectionName() {
        return TRADES_COLLECTION;
    }
}
