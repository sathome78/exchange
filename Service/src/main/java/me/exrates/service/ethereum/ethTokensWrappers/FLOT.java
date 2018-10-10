package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.3.1.
 */
public class FLOT extends Contract implements ethTokenERC20 {
    private static final String BINARY = "                                                                                                                                                       60606040526040805190810160405280600a81526020017f46697265204c6f74746f000000000000000000000000000000000000000000008152506003908051906020019061004f92919061010f565b506040805190810160405280600481526020017f464c4f54000000000000000000000000000000000000000000000000000000008152506004908051906020019061009b92919061010f565b5060126005556a52b7d2dcc80cd2e400000060065534156100bb57600080fd5b600654600081905550600654600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506101b4565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061015057805160ff191683800117855561017e565b8280016001018555821561017e579182015b8281111561017d578251825591602001919060010190610162565b5b50905061018b919061018f565b5090565b6101b191905b808211156101ad576000816000905550600101610195565b5090565b90565b610c79806101c36000396000f3006060604052600436106100a4576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde03146100a9578063095ea7b31461013757806318160ddd1461019157806323b872dd146101ba5780632ff2e9dc14610233578063313ce5671461025c57806370a082311461028557806395d89b41146102d2578063a9059cbb14610360578063dd62ed3e146103ba575b600080fd5b34156100b457600080fd5b6100bc610426565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100fc5780820151818401526020810190506100e1565b50505050905090810190601f1680156101295780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561014257600080fd5b610177600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506104c4565b604051808215151515815260200191505060405180910390f35b341561019c57600080fd5b6101a461064b565b6040518082815260200191505060405180910390f35b34156101c557600080fd5b610219600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610651565b604051808215151515815260200191505060405180910390f35b341561023e57600080fd5b610246610901565b6040518082815260200191505060405180910390f35b341561026757600080fd5b61026f610907565b6040518082815260200191505060405180910390f35b341561029057600080fd5b6102bc600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190505061090d565b6040518082815260200191505060405180910390f35b34156102dd57600080fd5b6102e5610956565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561032557808201518184015260208101905061030a565b50505050905090810190601f1680156103525780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561036b57600080fd5b6103a0600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506109f4565b604051808215151515815260200191505060405180910390f35b34156103c557600080fd5b610410600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610b8f565b6040518082815260200191505060405180910390f35b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104bc5780601f10610491576101008083540402835291602001916104bc565b820191906000526020600020905b81548152906001019060200180831161049f57829003601f168201915b505050505081565b60008082148061055057506000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054145b151561055b57600080fd5b81600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b60005481565b600080600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905061072583600160008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610c1690919063ffffffff16565b600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506107ba83600160008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610c3490919063ffffffff16565b600160008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506108108382610c3490919063ffffffff16565b600260008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508373ffffffffffffffffffffffffffffffffffffffff168573ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef856040518082815260200191505060405180910390a360019150509392505050565b60065481565b60055481565b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b60048054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109ec5780601f106109c1576101008083540402835291602001916109ec565b820191906000526020600020905b8154815290600101906020018083116109cf57829003601f168201915b505050505081565b6000610a4882600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610c3490919063ffffffff16565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610add82600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610c1690919063ffffffff16565b600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a36001905092915050565b6000600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b6000808284019050838110151515610c2a57fe5b8091505092915050565b6000828211151515610c4257fe5b8183039050929150505600a165627a7a72305820ea0b6e72d8acda7ea8eab80a092b7668f68f041f4b0420894c5b74ab54c0f7a30029";

    protected FLOT(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FLOT(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> name() {
        final Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                "approve", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> INITIAL_SUPPLY() {
        final Function function = new Function("INITIAL_SUPPLY", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function("allowance", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<FLOT> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FLOT.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<FLOT> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FLOT.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static FLOT load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FLOT(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static FLOT load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FLOT(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger value;
    }
}
