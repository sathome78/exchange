package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
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
public class ANY extends Contract implements ethTokenNotERC20{
    public static final String FUNC_NAME = "name";
    public static final String FUNC_APPROVE = "approve";
    public static final String FUNC_BURNTOKENS = "burnTokens";
    public static final String FUNC_TOTALSUPPLY = "totalSupply";
    public static final String FUNC_TRANSFERFROM = "transferFrom";
    public static final String FUNC_DECIMALS = "decimals";
    public static final String FUNC_ICO = "ico";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_SYMBOL = "symbol";
    public static final String FUNC_TRANSFER = "transfer";
    public static final String FUNC_TOKENSAREFROZEN = "tokensAreFrozen";
    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_MINTTOKENS = "mintTokens";
    public static final String FUNC_DEFROST = "defrost";
    public static final Event BURN_EVENT = new Event("Burn",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    private static final String BINARY = "0x6060604052361561011a5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305fefda7811461013057806306fdde0314610148578063095ea7b3146101d857806318160ddd1461020b57806323b872dd1461022d578063313ce567146102665780634b7503341461028c5780635a3b7e42146102ae57806370a082311461033e57806379c650681461036c5780638620410b1461038d5780638da5cb5b146103af57806395d89b41146103db578063a6f2ae3a1461046b578063a9059cbb14610475578063b414d4b614610496578063cae9ca51146104c6578063dd62ed3e1461053d578063e4849b3214610571578063e724529c14610586578063f2fde38b146105a9575b341561012257fe5b61012e5b60006000fd5b565b005b341561013857fe5b61012e6004356024356105c7565b005b341561015057fe5b6101586105f3565b60408051602080825283518183015283519192839290830191850190808383821561019e575b80518252602083111561019e57601f19909201916020918201910161017e565b505050905090810190601f1680156101ca5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101e057fe5b6101f7600160a060020a036004351660243561067e565b604080519115158252519081900360200190f35b341561021357fe5b61021b6106af565b60408051918252519081900360200190f35b341561023557fe5b6101f7600160a060020a03600435811690602435166044356106b5565b604080519115158252519081900360200190f35b341561026e57fe5b6102766107d9565b6040805160ff9092168252519081900360200190f35b341561029457fe5b61021b6107e2565b60408051918252519081900360200190f35b34156102b657fe5b6101586107e8565b60408051602080825283518183015283519192839290830191850190808383821561019e575b80518252602083111561019e57601f19909201916020918201910161017e565b505050905090810190601f1680156101ca5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561034657fe5b61021b600160a060020a0360043516610875565b60408051918252519081900360200190f35b341561037457fe5b61012e600160a060020a0360043516602435610887565b005b341561039557fe5b61021b610931565b60408051918252519081900360200190f35b34156103b757fe5b6103bf610937565b60408051600160a060020a039092168252519081900360200190f35b34156103e357fe5b610158610946565b60408051602080825283518183015283519192839290830191850190808383821561019e575b80518252602083111561019e57601f19909201916020918201910161017e565b505050905090810190601f1680156101ca5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b61012e6109d4565b005b341561047d57fe5b61012e600160a060020a0360043516602435610a6a565b005b341561049e57fe5b6101f7600160a060020a0360043516610b3a565b604080519115158252519081900360200190f35b34156104ce57fe5b604080516020600460443581810135601f81018490048402850184019095528484526101f7948235600160a060020a0316946024803595606494929391909201918190840183828082843750949650610b4f95505050505050565b604080519115158252519081900360200190f35b341561054557fe5b61021b600160a060020a0360043581169060243516610c89565b60408051918252519081900360200190f35b341561057957fe5b61012e600435610ca6565b005b341561058e57fe5b61012e600160a060020a03600435166024351515610d67565b005b34156105b157fe5b61012e600160a060020a0360043516610de9565b005b60005433600160a060020a039081169116146105e35760006000fd5b600882905560098190555b5b5050565b6002805460408051602060018416156101000260001901909316849004601f810184900484028201840190925281815292918301828280156106765780601f1061064b57610100808354040283529160200191610676565b820191906000526020600020905b81548152906001019060200180831161065957829003601f168201915b505050505081565b600160a060020a03338116600090815260076020908152604080832093861683529290522081905560015b92915050565b60055481565b600160a060020a0383166000908152600a602052604081205460ff16156106dc5760006000fd5b600160a060020a038416600090815260066020526040902054829010156107035760006000fd5b600160a060020a038316600090815260066020526040902054828101101561072b5760006000fd5b600160a060020a038085166000908152600760209081526040808320339094168352929052205482111561075f5760006000fd5b600160a060020a0380851660008181526006602090815260408083208054889003905587851680845281842080548901905584845260078352818420339096168452948252918290208054879003905581518681529151600080516020610e338339815191529281900390910190a35060015b9392505050565b60045460ff1681565b60085481565b60018054604080516020600284861615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156106765780601f1061064b57610100808354040283529160200191610676565b820191906000526020600020905b81548152906001019060200180831161065957829003601f168201915b505050505081565b60066020526000908152604090205481565b60005433600160a060020a039081169116146108a35760006000fd5b600160a060020a0380831660009081526006602090815260408083208054860190556005805486019055805185815290513090941693600080516020610e33833981519152929181900390910190a381600160a060020a031630600160a060020a0316600080516020610e33833981519152836040518082815260200191505060405180910390a35b5b5050565b60095481565b600054600160a060020a031681565b6003805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156106765780601f1061064b57610100808354040283529160200191610676565b820191906000526020600020905b81548152906001019060200180831161065957829003601f168201915b505050505081565b6000600954348115156109e357fe5b600160a060020a033016600090815260066020526040902054919004915081901015610a0f5760006000fd5b600160a060020a0333811660008181526006602090815260408083208054870190553090941680835291849020805486900390558351858152935192939192600080516020610e338339815191529281900390910190a35b50565b600160a060020a03331660009081526006602052604090205481901015610a915760006000fd5b600160a060020a0382166000908152600660205260409020548181011015610ab95760006000fd5b600160a060020a0333166000908152600a602052604090205460ff1615610ae05760006000fd5b600160a060020a0333811660008181526006602090815260408083208054879003905593861680835291849020805486019055835185815293519193600080516020610e33833981519152929081900390910190a35b5050565b600a6020526000908152604090205460ff1681565b600083610b5c818561067e565b15610c805780600160a060020a0316638f4ffcb1338630876040518563ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018085600160a060020a0316600160a060020a0316815260200184815260200183600160a060020a0316600160a060020a0316815260200180602001828103825283818151815260200191508051906020019080838360008314610c20575b805182526020831115610c2057601f199092019160209182019101610c00565b505050905090810190601f168015610c4c5780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b1515610c6a57fe5b6102c65a03f11515610c7857fe5b505050600191505b5b509392505050565b600760209081526000928352604080842090915290825290205481565b600160a060020a03331660009081526006602052604090205481901015610ccd5760006000fd5b600160a060020a03308116600090815260066020526040808220805485019055339092168082528282208054859003905560085492519092840280156108fc0292909190818181858888f193505050501515610d295760006000fd5b30600160a060020a031633600160a060020a0316600080516020610e33833981519152836040518082815260200191505060405180910390a35b5b50565b60005433600160a060020a03908116911614610d835760006000fd5b600160a060020a0382166000818152600a6020908152604091829020805460ff191685151590811790915582519384529083015280517f48335238b4855f35377ed80f164e8c6f3c366e54ac00b96a6402d4a9814a03a59281900390910190a15b5b5050565b60005433600160a060020a03908116911614610e055760006000fd5b6000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b505600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a7230582063a24a6191387a45dcbb4a9553ba2f4e6261dedea25c400f5c393f86c40563aa0029\n"
            + "\n";
    ;

    protected ANY(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ANY(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RemoteCall<ANY> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _ico) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_ico)));
        return deployRemoteCall(ANY.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<ANY> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _ico) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_ico)));
        return deployRemoteCall(ANY.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static ANY load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ANY(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ANY load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ANY(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _amount) {
        final Function function = new Function(
                FUNC_APPROVE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender),
                new org.web3j.abi.datatypes.generated.Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burnTokens(String _holder, BigInteger _value) {
        final Function function = new Function(
                FUNC_BURNTOKENS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_holder),
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

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _amount) {
        final Function function = new Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from),
                new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> ico() {
        final Function function = new Function(FUNC_ICO,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _holder) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_holder)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _amount) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> tokensAreFrozen() {
        final Function function = new Function(FUNC_TOKENSAREFROZEN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner),
                new org.web3j.abi.datatypes.Address(_spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> mintTokens(String _holder, BigInteger _value) {
        final Function function = new Function(
                FUNC_MINTTOKENS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_holder),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> defrost() {
        final Function function = new Function(
                FUNC_DEFROST,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
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

    public static class BurnEventResponse {
        public Log log;

        public String from;

        public BigInteger value;
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
