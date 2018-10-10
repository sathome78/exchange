package me.exrates.service.aidos;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.merchants.btc.BtcPaymentResultDetailedDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.service.BitcoinLikeCurrency;
import me.exrates.service.MerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.math.BigDecimal;
import java.util.List;

public interface AdkService extends BitcoinLikeCurrency, IRefillable, IWithdrawable {


    @Override
    default Boolean createdRefillRequestRecordNeeded() {
        return false;
    }

    @Override
    default Boolean needToCreateRefillRequestRecord() {
        return false;
    }

    @Override
    default Boolean toMainAccountTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default Boolean generatingAdditionalRefillAddressAvailable() {
        return true;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return false;
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    Merchant getMerchant();

    Currency getCurrency();

    MerchantService getMerchantService();

    RefillRequestAcceptDto createRequest(String address, String hash, BigDecimal amount);

    void putOnBchExam(RefillRequestAcceptDto requestAcceptDto);

    String getBalance();
}
