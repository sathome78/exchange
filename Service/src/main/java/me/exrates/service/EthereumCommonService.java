package me.exrates.service;

import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.util.List;

/**
 * Created by ajet on
 */
public interface EthereumCommonService extends IMerchantService, IRefillable, IWithdrawable {

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

    Web3j getWeb3j();

    List<String> getAccounts();

    void saveLastBlock(String block);

    String loadLastBlock();

    String getMainAddress();

    Credentials getCredentialsMain();
}
