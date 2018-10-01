package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
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
 * <p>Generated with web3j version 3.4.0.
 */
public class ARN extends Contract implements ethTokenERC20 {
    public static final String FUNC_NAME = "name";
    public static final String FUNC_APPROVE = "approve";
    public static final String FUNC_TOTALSUPPLY = "totalSupply";
    public static final String FUNC_TRANSFERFROM = "transferFrom";
    public static final String FUNC_DECIMALS = "decimals";
    public static final String FUNC_BURN = "burn";
    public static final String FUNC_UNFREEZE = "unfreeze";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_OWNER = "owner";
    public static final String FUNC_SYMBOL = "symbol";
    public static final String FUNC_TRANSFER = "transfer";
    public static final String FUNC_FREEZEOF = "freezeOf";
    public static final String FUNC_FREEZE = "freeze";
    public static final String FUNC_ALLOWANCE = "allowance";
    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    public static final Event BURN_EVENT = new Event("Burn",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    public static final Event FREEZE_EVENT = new Event("Freeze",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    public static final Event UNFREEZE_EVENT = new Event("Unfreeze",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    private static final String BINARY = "6060604052341561000f57600080fd5b5b600160a060020a03331660009081526005602052604090819020662386f26fc10000908190556003558051908101604052600581527f4165726f6e0000000000000000000000000000000000000000000000000000006020820152600090805161007e9291602001906100f7565b5060408051908101604052600381527f41524e0000000000000000000000000000000000000000000000000000000000602082015260019080516100c69291602001906100f7565b506002805460ff1916600817905560048054600160a060020a033316600160a060020a03199091161790555b610197565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061013857805160ff1916838001178555610165565b82800160010185558215610165579182015b8281111561016557825182559160200191906001019061014a565b5b50610172929150610176565b5090565b61019491905b80821115610172576000815560010161017c565b5090565b90565b610bc1806101a66000396000f300606060405236156100cd5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100e0578063095ea7b31461016b57806318160ddd146101a157806323b872dd146101c6578063313ce5671461020257806342966c681461022b5780636623fc461461025557806370a082311461027f5780638da5cb5b146102b057806395d89b41146102df578063a9059cbb1461036a578063cd4217c11461038e578063d7a78db8146103bf578063dd62ed3e146103e9575b34156100d857600080fd5b5b600080fd5b005b34156100eb57600080fd5b6100f3610420565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101305780820151818401525b602001610117565b50505050905090810190601f16801561015d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561017657600080fd5b61018d600160a060020a03600435166024356104be565b604051901515815260200160405180910390f35b34156101ac57600080fd5b6101b46104fe565b60405190815260200160405180910390f35b34156101d157600080fd5b61018d600160a060020a0360043581169060243516604435610504565b604051901515815260200160405180910390f35b341561020d57600080fd5b6102156106aa565b60405160ff909116815260200160405180910390f35b341561023657600080fd5b61018d6004356106b3565b604051901515815260200160405180910390f35b341561026057600080fd5b61018d600435610778565b604051901515815260200160405180910390f35b341561028a57600080fd5b6101b4600160a060020a0360043516610858565b60405190815260200160405180910390f35b34156102bb57600080fd5b6102c361086a565b604051600160a060020a03909116815260200160405180910390f35b34156102ea57600080fd5b6100f3610879565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101305780820151818401525b602001610117565b50505050905090810190601f16801561015d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561037557600080fd5b6100de600160a060020a0360043516602435610917565b005b341561039957600080fd5b6101b4600160a060020a0360043516610a35565b60405190815260200160405180910390f35b34156103ca57600080fd5b61018d600435610a47565b604051901515815260200160405180910390f35b34156103f457600080fd5b6101b4600160a060020a0360043581169060243516610b27565b60405190815260200160405180910390f35b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104b65780601f1061048b576101008083540402835291602001916104b6565b820191906000526020600020905b81548152906001019060200180831161049957829003601f168201915b505050505081565b60008082116104cc57600080fd5b50600160a060020a03338116600090815260076020908152604080832093861683529290522081905560015b92915050565b60035481565b6000600160a060020a038316151561051b57600080fd5b6000821161052857600080fd5b600160a060020a0384166000908152600560205260409020548290101561054e57600080fd5b600160a060020a038316600090815260056020526040902054828101101561057557600080fd5b600160a060020a03808516600090815260076020908152604080832033909416835292905220548211156105a857600080fd5b600160a060020a0384166000908152600560205260409020546105cb9083610b44565b600160a060020a0380861660009081526005602052604080822093909355908516815220546105fa9083610b5d565b600160a060020a0380851660009081526005602090815260408083209490945587831682526007815283822033909316825291909152205461063c9083610b44565b600160a060020a03808616600081815260076020908152604080832033861684529091529081902093909355908516917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060015b9392505050565b60025460ff1681565b600160a060020a033316600090815260056020526040812054829010156106d957600080fd5b600082116106e657600080fd5b600160a060020a0333166000908152600560205260409020546107099083610b44565b600160a060020a03331660009081526005602052604090205560035461072f9083610b44565b600355600160a060020a0333167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca58360405190815260200160405180910390a25060015b919050565b600160a060020a0333166000908152600660205260408120548290101561079e57600080fd5b600082116107ab57600080fd5b600160a060020a0333166000908152600660205260409020546107ce9083610b44565b600160a060020a0333166000908152600660209081526040808320939093556005905220546107fd9083610b5d565b600160a060020a0333166000818152600560205260409081902092909255907f2cfce4af01bcb9d6cf6c84ee1b7c491100b8695368264146a94d71e10a63083f9084905190815260200160405180910390a25060015b919050565b60056020526000908152604090205481565b600454600160a060020a031681565b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104b65780601f1061048b576101008083540402835291602001916104b6565b820191906000526020600020905b81548152906001019060200180831161049957829003601f168201915b505050505081565b600160a060020a038216151561092c57600080fd5b6000811161093957600080fd5b600160a060020a0333166000908152600560205260409020548190101561095f57600080fd5b600160a060020a038216600090815260056020526040902054818101101561098657600080fd5b600160a060020a0333166000908152600560205260409020546109a99082610b44565b600160a060020a0333811660009081526005602052604080822093909355908416815220546109d89082610b5d565b600160a060020a0380841660008181526005602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9084905190815260200160405180910390a35b5050565b60066020526000908152604090205481565b600160a060020a03331660009081526005602052604081205482901015610a6d57600080fd5b60008211610a7a57600080fd5b600160a060020a033316600090815260056020526040902054610a9d9083610b44565b600160a060020a033316600090815260056020908152604080832093909355600690522054610acc9083610b5d565b600160a060020a0333166000818152600660205260409081902092909255907ff97a274face0b5517365ad396b1fdba6f68bd3135ef603e44272adba3af5a1e09084905190815260200160405180910390a25060015b919050565b600760209081526000928352604080842090915290825290205481565b6000610b5283831115610b85565b508082035b92915050565b6000828201610b7a848210801590610b755750838210155b610b85565b8091505b5092915050565b801515610b9157600080fd5b5b505600a165627a7a723058208b2d3292e6d3a014c36b3304a6ab5f1a45482ca02abe442dfda220e1069a2cb90029\n";
    ;

    protected ARN(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ARN(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RemoteCall<ARN> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ARN.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<ARN> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ARN.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static ARN load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ARN(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ARN load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ARN(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE,
                Arrays.<Type>asList(new Address(_spender),
                new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new Address(_from),
                new Address(_to),
                new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        final Function function = new Function(
                FUNC_BURN,
                Arrays.<Type>asList(new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> unfreeze(BigInteger _value) {
        final Function function = new Function(
                FUNC_UNFREEZE,
                Arrays.<Type>asList(new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String param0) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new Address(_to),
                new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> freezeOf(String param0) {
        final Function function = new Function(FUNC_FREEZEOF,
                Arrays.<Type>asList(new Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> freeze(BigInteger _value) {
        final Function function = new Function(
                FUNC_FREEZE,
                Arrays.<Type>asList(new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String param0, String param1) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new Address(param0),
                new Address(param1)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
        return burnEventObservable(filter);
    }

    public List<FreezeEventResponse> getFreezeEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(FREEZE_EVENT, transactionReceipt);
        ArrayList<FreezeEventResponse> responses = new ArrayList<FreezeEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            FreezeEventResponse typedResponse = new FreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<FreezeEventResponse> freezeEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, FreezeEventResponse>() {
            @Override
            public FreezeEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(FREEZE_EVENT, log);
                FreezeEventResponse typedResponse = new FreezeEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<FreezeEventResponse> freezeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FREEZE_EVENT));
        return freezeEventObservable(filter);
    }

    public List<UnfreezeEventResponse> getUnfreezeEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(UNFREEZE_EVENT, transactionReceipt);
        ArrayList<UnfreezeEventResponse> responses = new ArrayList<UnfreezeEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            UnfreezeEventResponse typedResponse = new UnfreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UnfreezeEventResponse> unfreezeEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UnfreezeEventResponse>() {
            @Override
            public UnfreezeEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(UNFREEZE_EVENT, log);
                UnfreezeEventResponse typedResponse = new UnfreezeEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<UnfreezeEventResponse> unfreezeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNFREEZE_EVENT));
        return unfreezeEventObservable(filter);
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger value;
    }

    public static class BurnEventResponse {
        public Log log;

        public String from;

        public BigInteger value;
    }

    public static class FreezeEventResponse {
        public Log log;

        public String from;

        public BigInteger value;
    }

    public static class UnfreezeEventResponse {
        public Log log;

        public String from;

        public BigInteger value;
    }
}
