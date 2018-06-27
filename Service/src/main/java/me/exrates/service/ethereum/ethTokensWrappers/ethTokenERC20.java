package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface ethTokenERC20 {

    RemoteCall<BigInteger> balanceOf(String param0);

    RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value);

    RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value);

    RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value);

    RemoteCall<BigInteger> allowance(String param0, String param1);
}
