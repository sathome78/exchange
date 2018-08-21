package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface ethTokenERC20 extends EthToken {

    RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value);

    RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value);

    RemoteCall<BigInteger> allowance(String param0, String param1);
}
