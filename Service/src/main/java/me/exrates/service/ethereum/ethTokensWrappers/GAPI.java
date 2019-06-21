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
public class GAPI extends Contract implements ethTokenNotERC20{
    private static final String BINARY = "0x6080604052600436106100e55763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166302fb0c5e81146100ea57806306fdde0314610113578063078837031461019d578063095ea7b3146101c757806318160ddd146101eb57806323b872dd14610200578063313ce5671461022a57806342966c681461023f57806370a08231146102575780638395c9481461027857806395d89b411461028f578063a8d84fc3146102a4578063a9059cbb146102b9578063c1eb8c89146102dd578063dd62ed3e146102f2578063f2fde38b14610319575b600080fd5b3480156100f657600080fd5b506100ff61033a565b604080519115158252519081900360200190f35b34801561011f57600080fd5b50610128610343565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561016257818101518382015260200161014a565b50505050905090810190601f16801561018f5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101a957600080fd5b506101b56004356103d1565b60408051918252519081900360200190f35b3480156101d357600080fd5b506100ff600160a060020a0360043516602435610448565b3480156101f757600080fd5b506101b56104ae565b34801561020c57600080fd5b506100ff600160a060020a03600435811690602435166044356104b4565b34801561023657600080fd5b506101b5610667565b34801561024b57600080fd5b506101b560043561066d565b34801561026357600080fd5b506101b5600160a060020a03600435166106d8565b34801561028457600080fd5b5061028d6106f3565b005b34801561029b57600080fd5b50610128610718565b3480156102b057600080fd5b5061028d610773565b3480156102c557600080fd5b506100ff600160a060020a0360043516602435610796565b3480156102e957600080fd5b506100ff610897565b3480156102fe57600080fd5b506101b5600160a060020a03600435811690602435166108f1565b34801561032557600080fd5b5061028d600160a060020a036004351661091c565b60015460ff1681565b6005805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156103c95780601f1061039e576101008083540402835291602001916103c9565b820191906000526020600020905b8154815290600101906020018083116103ac57829003601f168201915b505050505081565b600454600090600160a060020a031633146103eb57600080fd5b600082111561044357600454600160a060020a03166000908152600260205260409020546007546104219190600a0a8402610974565b600454600160a060020a03166000908152600260205260408120919091555490505b919050565b336000818152600360209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b60005481565b6001805460009160ff9091161515146104cc57600080fd5b82600160a060020a03811615156104e257600080fd5b600083116104ef57600080fd5b600160a060020a03851660009081526002602052604090205483111561051457600080fd5b600160a060020a038416600090815260026020526040902054838101101561053b57600080fd5b600160a060020a038516600090815260036020908152604080832033845290915290205483111561056b57600080fd5b600160a060020a03851660009081526002602052604090205461058e9084610998565b600160a060020a0380871660009081526002602052604080822093909355908616815220546105bd9084610974565b600160a060020a0380861660009081526002602090815260408083209490945591881681526003825282812033825290915220546105fb9084610998565b600160a060020a03808716600081815260036020908152604080832033845282529182902094909455805187815290519288169391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a3506001949350505050565b60075481565b600454600090600160a060020a0316331461068757600080fd5b6000821180156106a65750336000908152600260205260409020548211155b1561044357600454600160a060020a03166000908152600260205260409020546007546104219190600a0a8402610998565b600160a060020a031660009081526002602052604090205490565b600454600160a060020a0316331461070a57600080fd5b6001805460ff191681179055565b6006805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156103c95780601f1061039e576101008083540402835291602001916103c9565b600454600160a060020a0316331461078a57600080fd5b6001805460ff19169055565b6001805460009160ff9091161515146107ae57600080fd5b82600160a060020a03811615156107c457600080fd5b336000908152600260205260409020548311156107e057600080fd5b33600160a060020a03851614610890573360009081526002602052604090205461080a9084610998565b3360009081526002602052604080822092909255600160a060020a038616815220546108369084610974565b600160a060020a0385166000818152600260209081526040918290209390935580518681529051919233927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a3600191505b5092915050565b600454600090600160a060020a031633146108b157600080fd5b6000303111156108ee576040513390303180156108fc02916000818181858888f193505050501580156108e8573d6000803e3d6000fd5b50600190505b90565b600160a060020a03918216600090815260036020908152604080832093909416825291909152205490565b600454600160a060020a0316331461093357600080fd5b600454600160a060020a03828116911614610971576004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b50565b60008282018381108015906109895750828110155b151561099157fe5b9392505050565b6000828211156109a457fe5b509003905600a165627a7a72305820b86ec5a72349d81ac192c2f088e52ba153f0d729ddcf441168d9ab0215b9f2520029";

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

    protected GAPI(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected GAPI(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public static RemoteCall<GAPI> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(GAPI.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<GAPI> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(GAPI.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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

    public static GAPI load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new GAPI(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static GAPI load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new GAPI(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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
