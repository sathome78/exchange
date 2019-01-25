package me.exrates.service.omni;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.merchants.omni.OmniBalanceDto;
import me.exrates.model.dto.merchants.omni.OmniTxDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface OmniService extends IRefillable, IWithdrawable {

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
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return false;
    }

    void putOnBchExam(RefillRequestPutOnBchExamDto dto);

    RefillRequestAcceptDto createRequest(String address, String hash, BigDecimal amount);

    void frozeCoins(String address, BigDecimal amount);

    Merchant getMerchant();

    Currency getCurrency();

    String getWalletPassword();

    OmniBalanceDto getUsdtBalances();

    BigDecimal getBtcBalance();

    Integer getUsdtPropertyId();

    List<OmniTxDto> getAllTransactions();

    List<RefillRequestAddressShortDto> getBlockedAddressesOmni();

    void createRefillRequestAdmin(Map<String, String> params);
}
