package me.exrates.service.ethereum;


import me.exrates.service.ethereum.EthTokenServiceImpl;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Maks on 19.09.2017.
 */
public interface EthTokenService {

    List<String> getContractAddress();

    void tokenTransaction(Transaction transaction);

    void checkTransaction(BigInteger txBlock);

    EthTokenServiceImpl.TransferEventResponse extractData(List<String> topics, String data);

    boolean isERC20();

    ExConvert.Unit getUnit();

    default Integer currencyId(){
        return null;
    }
}
