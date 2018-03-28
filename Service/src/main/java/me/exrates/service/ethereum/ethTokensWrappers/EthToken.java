package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

/**
 * Created by Maks on 28.03.2018.
 */
public interface EthToken {

    RemoteCall<BigInteger> balanceOf(String param0);

    RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value);
}
