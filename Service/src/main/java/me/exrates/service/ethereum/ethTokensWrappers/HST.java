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
public class HST extends Contract implements ethTokenERC20{
    public static final String FUNC_MINTINGFINISHED = "mintingFinished";
    public static final String FUNC_NAME = "name";
    public static final String FUNC_APPROVE = "approve";
    public static final String FUNC_TOTALSUPPLY = "totalSupply";
    public static final String FUNC_TRANSFERFROM = "transferFrom";
    public static final String FUNC_DECIMALS = "decimals";
    public static final String FUNC_TRIGGERTIME = "triggerTime";
    public static final String FUNC_MINT = "mint";
    public static final String FUNC_CLAIMOWNERSHIP = "claimOwnership";
    public static final String FUNC_VERSION = "version";
    public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_FINISHMINTING = "finishMinting";
    public static final String FUNC_OWNER = "owner";
    public static final String FUNC_SYMBOL = "symbol";
    public static final String FUNC_TRANSFER = "transfer";
    public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";
    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_PENDINGOWNER = "pendingOwner";
    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";
    public static final Event MINT_EVENT = new Event("Mint",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    public static final Event MINTFINISHED_EVENT = new Event("MintFinished",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;
    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    private static final String BINARY = "60606040526003805460a060020a60ff02191690556000600555341561002457600080fd5b60038054600160a060020a033316600160a060020a03199182168117909116179055610c89806100556000396000f3006060604052361561010f5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305d2035b811461011457806306fdde031461013b578063095ea7b3146101c557806318160ddd146101e757806323b872dd1461020c578063313ce56714610234578063370fb47b1461025d57806340c10f19146102705780634e71e0c81461029257806354fd4d50146102a757806366188463146102ba57806370a08231146102dc5780637d64bcb4146102fb5780638da5cb5b1461030e57806395d89b411461033d578063a9059cbb14610350578063d73dd62314610372578063dd62ed3e14610394578063e30c3978146103b9578063f2fde38b146103cc575b600080fd5b341561011f57600080fd5b6101276103eb565b604051901515815260200160405180910390f35b341561014657600080fd5b61014e61040c565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561018a578082015183820152602001610172565b50505050905090810190601f1680156101b75780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101d057600080fd5b610127600160a060020a0360043516602435610443565b34156101f257600080fd5b6101fa6104e9565b60405190815260200160405180910390f35b341561021757600080fd5b610127600160a060020a03600435811690602435166044356104ef565b341561023f57600080fd5b610247610514565b60405160ff909116815260200160405180910390f35b341561026857600080fd5b6101fa610519565b341561027b57600080fd5b610127600160a060020a036004351660243561051f565b341561029d57600080fd5b6102a561063d565b005b34156102b257600080fd5b61014e61068c565b34156102c557600080fd5b610127600160a060020a03600435166024356106c3565b34156102e757600080fd5b6101fa600160a060020a03600435166107bd565b341561030657600080fd5b6101276107d8565b341561031957600080fd5b610321610826565b604051600160a060020a03909116815260200160405180910390f35b341561034857600080fd5b61014e610835565b341561035b57600080fd5b610127600160a060020a036004351660243561086c565b341561037d57600080fd5b610127600160a060020a036004351660243561088f565b341561039f57600080fd5b6101fa600160a060020a0360043581169060243516610933565b34156103c457600080fd5b61032161095e565b34156103d757600080fd5b6102a5600160a060020a036004351661096d565b60035474010000000000000000000000000000000000000000900460ff1681565b60408051908101604052600e81527f4465636973696f6e20546f6b656e000000000000000000000000000000000000602082015281565b60008115806104755750600160a060020a03338116600090815260026020908152604080832093871683529290522054155b151561048057600080fd5b600160a060020a03338116600081815260026020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b60005481565b60055460009042101561050157600080fd5b61050c8484846109b7565b949350505050565b601281565b60055481565b60035460009033600160a060020a0390811691161461053d57600080fd5b60035474010000000000000000000000000000000000000000900460ff161561056557600080fd5b600054610578908363ffffffff610ae116565b6000908155600160a060020a0384168152600160205260409020546105a3908363ffffffff610ae116565b600160a060020a0384166000818152600160205260409081902092909255907f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968859084905190815260200160405180910390a282600160a060020a031660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8460405190815260200160405180910390a350600192915050565b60045433600160a060020a0390811691161461065857600080fd5b600480546003805473ffffffffffffffffffffffffffffffffffffffff19908116600160a060020a03841617909155169055565b60408051908101604052600381527f312e300000000000000000000000000000000000000000000000000000000000602082015281565b600160a060020a0333811660009081526002602090815260408083209386168352929052908120548083111561072057600160a060020a033381166000908152600260209081526040808320938816835292905290812055610757565b610730818463ffffffff610af016565b600160a060020a033381166000908152600260209081526040808320938916835292905220555b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020547f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925915190815260200160405180910390a35060019392505050565b600160a060020a031660009081526001602052604090205490565b60035460009033600160a060020a039081169116146107f657600080fd5b6005541561080357600080fd5b61081642620d2f0063ffffffff610ae116565b600555610821610b02565b905090565b600354600160a060020a031681565b60408051908101604052600381527f4853540000000000000000000000000000000000000000000000000000000000602082015281565b60055460009042101561087e57600080fd5b6108888383610b87565b9392505050565b600160a060020a0333811660009081526002602090815260408083209386168352929052908120546108c7908363ffffffff610ae116565b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020849055919290917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591905190815260200160405180910390a350600192915050565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b600454600160a060020a031681565b60035433600160a060020a0390811691161461098857600080fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600080600160a060020a03841615156109cf57600080fd5b50600160a060020a03808516600081815260026020908152604080832033909516835293815283822054928252600190529190912054610a15908463ffffffff610af016565b600160a060020a038087166000908152600160205260408082209390935590861681522054610a4a908463ffffffff610ae116565b600160a060020a038516600090815260016020526040902055610a73818463ffffffff610af016565b600160a060020a03808716600081815260026020908152604080832033861684529091529081902093909355908616917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9086905190815260200160405180910390a3506001949350505050565b60008282018381101561088857fe5b600082821115610afc57fe5b50900390565b60035460009033600160a060020a03908116911614610b2057600080fd5b6003805474ff00000000000000000000000000000000000000001916740100000000000000000000000000000000000000001790557fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0860405160405180910390a150600190565b6000600160a060020a0383161515610b9e57600080fd5b600160a060020a033316600090815260016020526040902054610bc7908363ffffffff610af016565b600160a060020a033381166000908152600160205260408082209390935590851681522054610bfc908363ffffffff610ae116565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a3506001929150505600a165627a7a72305820e8208f6c68c613e42c61b843ac4b86405c55875765838591fff54279e7eff6080029\n"
            + "\n";
    ;

    protected HST(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected HST(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RemoteCall<HST> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(HST.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<HST> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(HST.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static HST load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new HST(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static HST load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new HST(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<Boolean> mintingFinished() {
        final Function function = new Function(FUNC_MINTINGFINISHED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from),
                new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> triggerTime() {
        final Function function = new Function(FUNC_TRIGGERTIME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> mint(String _to, BigInteger _amount) {
        final Function function = new Function(
                FUNC_MINT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> claimOwnership() {
        final Function function = new Function(
                FUNC_CLAIMOWNERSHIP,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> version() {
        final Function function = new Function(FUNC_VERSION,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> decreaseApproval(String _spender, BigInteger _subtractedValue) {
        final Function function = new Function(
                FUNC_DECREASEAPPROVAL,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender),
                new org.web3j.abi.datatypes.generated.Uint256(_subtractedValue)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> finishMinting() {
        final Function function = new Function(
                FUNC_FINISHMINTING,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> increaseApproval(String _spender, BigInteger _addedValue) {
        final Function function = new Function(
                FUNC_INCREASEAPPROVAL,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender),
                new org.web3j.abi.datatypes.generated.Uint256(_addedValue)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner),
                new org.web3j.abi.datatypes.Address(_spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> pendingOwner() {
        final Function function = new Function(FUNC_PENDINGOWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINT_EVENT, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintEventResponse> mintEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintEventResponse>() {
            @Override
            public MintEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINT_EVENT, log);
                MintEventResponse typedResponse = new MintEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MintEventResponse> mintEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINT_EVENT));
        return mintEventObservable(filter);
    }

    public List<MintFinishedEventResponse> getMintFinishedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINTFINISHED_EVENT, transactionReceipt);
        ArrayList<MintFinishedEventResponse> responses = new ArrayList<MintFinishedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintFinishedEventResponse>() {
            @Override
            public MintFinishedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINTFINISHED_EVENT, log);
                MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTFINISHED_EVENT));
        return mintFinishedEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
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

    public Observable<ApprovalEventResponse> approvalEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
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

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
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

    public static class MintEventResponse {
        public Log log;

        public String to;

        public BigInteger amount;
    }

    public static class MintFinishedEventResponse {
        public Log log;
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
