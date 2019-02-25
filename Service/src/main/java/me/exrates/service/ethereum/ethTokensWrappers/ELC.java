package me.exrates.service.ethereum.ethTokensWrappers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class ELC extends Contract implements ethTokenNotERC20 {
    private static final String BINARY = "0x6080604052600436106101275763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305fefda7811461012c57806306fdde0314610149578063095ea7b3146101d357806318160ddd1461020b57806323b872dd14610232578063313ce5671461025c57806342966c68146102875780634b7503341461029f57806370a08231146102b457806379c65068146102d557806379cc6790146102f95780638620410b1461031d5780638da5cb5b1461033257806395d89b4114610363578063a6f2ae3a14610378578063a9059cbb14610380578063b414d4b6146103a4578063cae9ca51146103c5578063dd62ed3e1461042e578063e4849b3214610455578063e724529c1461046d578063f2fde38b14610493575b600080fd5b34801561013857600080fd5b506101476004356024356104b4565b005b34801561015557600080fd5b5061015e6104d6565b6040805160208082528351818301528351919283929083019185019080838360005b83811015610198578181015183820152602001610180565b50505050905090810190601f1680156101c55780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101df57600080fd5b506101f7600160a060020a0360043516602435610563565b604080519115158252519081900360200190f35b34801561021757600080fd5b506102206105c9565b60408051918252519081900360200190f35b34801561023e57600080fd5b506101f7600160a060020a03600435811690602435166044356105cf565b34801561026857600080fd5b5061027161063e565b6040805160ff9092168252519081900360200190f35b34801561029357600080fd5b506101f7600435610647565b3480156102ab57600080fd5b506102206106bf565b3480156102c057600080fd5b50610220600160a060020a03600435166106c5565b3480156102e157600080fd5b50610147600160a060020a03600435166024356106d7565b34801561030557600080fd5b506101f7600160a060020a036004351660243561078d565b34801561032957600080fd5b5061022061085e565b34801561033e57600080fd5b50610347610864565b60408051600160a060020a039092168252519081900360200190f35b34801561036f57600080fd5b5061015e610873565b6101476108cb565b34801561038c57600080fd5b506101f7600160a060020a03600435166024356108eb565b3480156103b057600080fd5b506101f7600160a060020a0360043516610901565b3480156103d157600080fd5b50604080516020600460443581810135601f81018490048402850184019095528484526101f7948235600160a060020a03169460248035953695946064949201919081908401838280828437509497506109169650505050505050565b34801561043a57600080fd5b50610220600160a060020a0360043581169060243516610a2f565b34801561046157600080fd5b50610147600435610a4c565b34801561047957600080fd5b50610147600160a060020a03600435166024351515610aa0565b34801561049f57600080fd5b50610147600160a060020a0360043516610b1b565b600054600160a060020a031633146104cb57600080fd5b600791909155600855565b60018054604080516020600284861615610100026000190190941693909304601f8101849004840282018401909252818152929183018282801561055b5780601f106105305761010080835404028352916020019161055b565b820191906000526020600020905b81548152906001019060200180831161053e57829003601f168201915b505050505081565b336000818152600660209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b60045481565b600160a060020a03831660009081526006602090815260408083203384529091528120548211156105ff57600080fd5b600160a060020a0384166000908152600660209081526040808320338452909152902080548390039055610634848484610b61565b5060019392505050565b60035460ff1681565b3360009081526005602052604081205482111561066357600080fd5b3360008181526005602090815260409182902080548690039055600480548690039055815185815291517fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59281900390910190a2506001919050565b60075481565b60056020526000908152604090205481565b600054600160a060020a031633146106ee57600080fd5b600160a060020a03821660009081526005602090815260408083208054850190556004805485019055805184815290513093927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef928290030190a3604080518281529051600160a060020a0384169130917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9181900360200190a35050565b600160a060020a0382166000908152600560205260408120548211156107b257600080fd5b600160a060020a03831660009081526006602090815260408083203384529091529020548211156107e257600080fd5b600160a060020a0383166000818152600560209081526040808320805487900390556006825280832033845282529182902080548690039055600480548690039055815185815291517fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59281900390910190a250600192915050565b60085481565b600054600160a060020a031681565b6002805460408051602060018416156101000260001901909316849004601f8101849004840282018401909252818152929183018282801561055b5780601f106105305761010080835404028352916020019161055b565b6000600854348115156108da57fe5b0490506108e8303383610b61565b50565b60006108f8338484610b61565b50600192915050565b60096020526000908152604090205460ff1681565b6000836109238185610563565b15610a27576040517f8f4ffcb10000000000000000000000000000000000000000000000000000000081523360048201818152602483018790523060448401819052608060648501908152875160848601528751600160a060020a03871695638f4ffcb195948b94938b939192909160a490910190602085019080838360005b838110156109bb5781810151838201526020016109a3565b50505050905090810190601f1680156109e85780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b158015610a0a57600080fd5b505af1158015610a1e573d6000803e3d6000fd5b50505050600191505b509392505050565b600660209081526000928352604080842090915290825290205481565b6007543090820281311015610a6057600080fd5b610a6b333084610b61565b6007546040513391840280156108fc02916000818181858888f19350505050158015610a9b573d6000803e3d6000fd5b505050565b600054600160a060020a03163314610ab757600080fd5b600160a060020a038216600081815260096020908152604091829020805460ff191685151590811790915582519384529083015280517f48335238b4855f35377ed80f164e8c6f3c366e54ac00b96a6402d4a9814a03a59281900390910190a15050565b600054600160a060020a03163314610b3257600080fd5b6000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600160a060020a0382161515610b7657600080fd5b600160a060020a038316600090815260056020526040902054811115610b9b57600080fd5b600160a060020a0382166000908152600560205260409020548181011015610bc257600080fd5b600160a060020a03831660009081526009602052604090205460ff1615610be857600080fd5b600160a060020a03821660009081526009602052604090205460ff1615610c0e57600080fd5b600160a060020a03808416600081815260056020908152604080832080548790039055938616808352918490208054860190558351858152935191937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a35050505600a165627a7a7230582091328bd91712bdaf163c52792921f20e1992b21d3438c039cc3f7392241ebbdd0029\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_VERSION = "version";

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected ELC(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ELC(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
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

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public static RemoteCall<ELC> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(ELC.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<ELC> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(ELC.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(String _spender, BigInteger _value, byte[] _extraData) {
        final Function function = new Function(
                FUNC_APPROVEANDCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value), 
                new org.web3j.abi.datatypes.DynamicBytes(_extraData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> version() {
        final Function function = new Function(FUNC_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static ELC load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ELC(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ELC load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ELC(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
