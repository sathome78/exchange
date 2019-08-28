package me.exrates.service.coinpay;

import me.exrates.model.dto.merchants.coinpay.CoinPayCreateWithdrawDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayResponseDepositDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayWithdrawRequestDto;

public interface CoinpayApi {

    String authorizeUser();

    CoinpayApiImpl.BalanceResponse getBalancesAndWallets(String token);

    CoinPayWithdrawRequestDto createWithdrawRequest(String token, CoinPayCreateWithdrawDto request);

    String checkOrderById(String token, String orderId);

    CoinPayResponseDepositDto createDeposit(String token, String amount, String currency, String callbackUrl);
}