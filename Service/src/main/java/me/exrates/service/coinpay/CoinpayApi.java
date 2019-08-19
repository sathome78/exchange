package me.exrates.service.coinpay;

public interface CoinpayApi {

    boolean createUser(String email, String password, String username, String referralId);

    String authorizeUser(String email, String password);

    String refreshToken(String oldToken);

    CoinpayApiImpl.BalanceResponse getBalancesAndWallets(String token);
}