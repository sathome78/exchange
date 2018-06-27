package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface ethTokenNotERC20 {

    RemoteCall<BigInteger> balanceOf(String param0);

    RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value);
}
