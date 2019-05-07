package me.exrates.service.ethereum;


import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;
import java.util.List;

public interface EthTokenService {

    List<String> getContractAddress();

    void tokenTransaction(Transaction transaction);

    void checkTransaction(BigInteger txBlock);

    EthTokenServiceImpl.TransferEventResponse extractData(List<String> topics, String data);

    default Integer currencyId(){
        return null;
    }
}
