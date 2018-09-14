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
 * <p>Generated with web3j version 3.4.0.
 */
public class MTL extends Contract implements ethTokenERC20{
    public static final String FUNC_NAME = "name";
    public static final String FUNC_APPROVE = "approve";
    public static final String FUNC_TOTALSUPPLY = "totalSupply";
    public static final String FUNC_TRANSFERFROM = "transferFrom";
    public static final String FUNC_INITIAL_SUPPLY = "INITIAL_SUPPLY";
    public static final String FUNC_DECIMALS = "decimals";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_SYMBOL = "symbol";
    public static final String FUNC_TRANSFER = "transfer";
    public static final String FUNC_ALLOWANCE = "allowance";
    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    private static final String BINARY = "60a0604052600560608190527f4d6574616c000000000000000000000000000000000000000000000000000000608090815261003e91600391906100c1565b506040805180820190915260038082527f4d544c00000000000000000000000000000000000000000000000000000000006020909201918252610083916004916100c1565b5060086005556617a8395d85d800600655341561009c57fe5b5b6006546000818155600160a060020a0333168152600160205260409020555b610161565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061010257805160ff191683800117855561012f565b8280016001018555821561012f579182015b8281111561012f578251825591602001919060010190610114565b5b5061013c929150610140565b5090565b61015e91905b8082111561013c5760008155600101610146565b5090565b90565b610772806101706000396000f300606060405236156100a15763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100a3578063095ea7b31461013357806318160ddd1461015457806323b872dd146101765780632ff2e9dc1461019d578063313ce567146101bf57806370a08231146101e157806395d89b411461020f578063a9059cbb1461029f578063dd62ed3e146102c0575bfe5b34156100ab57fe5b6100b36102f4565b6040805160208082528351818301528351919283929083019185019080838382156100f9575b8051825260208311156100f957601f1990920191602091820191016100d9565b505050905090810190601f1680156101255780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561013b57fe5b610152600160a060020a0360043516602435610382565b005b341561015c57fe5b610164610422565b60408051918252519081900360200190f35b341561017e57fe5b610152600160a060020a0360043581169060243516604435610428565b005b34156101a557fe5b61016461054c565b60408051918252519081900360200190f35b34156101c757fe5b610164610552565b60408051918252519081900360200190f35b34156101e957fe5b610164600160a060020a0360043516610558565b60408051918252519081900360200190f35b341561021757fe5b6100b3610577565b6040805160208082528351818301528351919283929083019185019080838382156100f9575b8051825260208311156100f957601f1990920191602091820191016100d9565b505050905090810190601f1680156101255780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156102a757fe5b610152600160a060020a0360043516602435610605565b005b34156102c857fe5b610164600160a060020a03600435811690602435166106d3565b60408051918252519081900360200190f35b6003805460408051602060026001851615610100026000190190941693909304601f8101849004840282018401909252818152929183018282801561037a5780601f1061034f5761010080835404028352916020019161037a565b820191906000526020600020905b81548152906001019060200180831161035d57829003601f168201915b505050505081565b80158015906103b55750600160a060020a0333811660009081526002602090815260408083209386168352929052205415155b156103c05760006000fd5b600160a060020a03338116600081815260026020908152604080832094871680845294825291829020859055815185815291517f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259281900390910190a35b5050565b60005481565b60006060606436101561043b5760006000fd5b600160a060020a038086166000908152600260209081526040808320338516845282528083205493881683526001909152902054909250610482908463ffffffff61070016565b600160a060020a0380861660009081526001602052604080822093909355908716815220546104b7908463ffffffff61071c16565b600160a060020a0386166000908152600160205260409020556104e0828463ffffffff61071c16565b600160a060020a038087166000818152600260209081526040808320338616845282529182902094909455805187815290519288169391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a35b5b5050505050565b60065481565b60055481565b600160a060020a0381166000908152600160205260409020545b919050565b6004805460408051602060026001851615610100026000190190941693909304601f8101849004840282018401909252818152929183018282801561037a5780601f1061034f5761010080835404028352916020019161037a565b820191906000526020600020905b81548152906001019060200180831161035d57829003601f168201915b505050505081565b604060443610156106165760006000fd5b600160a060020a03331660009081526001602052604090205461063f908363ffffffff61071c16565b600160a060020a033381166000908152600160205260408082209390935590851681522054610674908363ffffffff61070016565b600160a060020a038085166000818152600160209081526040918290209490945580518681529051919333909316927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a35b5b505050565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b600082820161071184821015610735565b8091505b5092915050565b600061072a83831115610735565b508082035b92915050565b8015156107425760006000fd5b5b505600a165627a7a7230582092516707e9aa16eec89985eb257944dfd7e5f4503f9f5c40744ce12ffebd731b0029\n"
            + "\n";
    ;

    protected MTL(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MTL(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RemoteCall<MTL> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MTL.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<MTL> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MTL.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static MTL load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MTL(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MTL load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MTL(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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

    public RemoteCall<BigInteger> INITIAL_SUPPLY() {
        final Function function = new Function(FUNC_INITIAL_SUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
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

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner),
                new org.web3j.abi.datatypes.Address(_spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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
