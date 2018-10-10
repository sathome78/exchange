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
public class JET extends Contract implements ethTokenERC20 {
    private static final String BINARY = "606060409081526003805460a060020a60ff02191690558051908101604052600781527f4a6574636f696e000000000000000000000000000000000000000000000000006020820152600490805161005b92916020019061019d565b5060408051908101604052600381527f4a45540000000000000000000000000000000000000000000000000000000000602082015260059080516100a392916020019061019d565b506006805460ff1916601217905560408051908101604052600381527f322e300000000000000000000000000000000000000000000000000000000000602082015260079080516100f892916020019061019d565b5060088054600160a060020a03191673c1e6c6c681b286fb503b36a9dd6c1dbff85e73cf179055341561012a57600080fd5b5b5b60038054600160a060020a03191633600160a060020a03161790555b6a422ca8b0a00a4250000000600081815573e955c7616dc449fd0cbeeca277cc078f9510bc04905260016020527fae6e08ff71f16122039d3187ed51ef4ead2a18fdc6833965a88e9ef54f23c3a0555b61023d565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106101de57805160ff191683800117855561020b565b8280016001018555821561020b579182015b8281111561020b5782518255916020019190600101906101f0565b5b5061021892915061021c565b5090565b61023a91905b808211156102185760008155600101610222565b5090565b90565b610c1f8061024c6000396000f300606060405236156100e35763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100e8578063095ea7b31461017357806318160ddd1461019757806323b872dd146101bc578063313ce567146101e65780633f4ba83a1461020f57806354fd4d50146102365780635c975abb146102c15780636678887f146102e857806370a08231146103175780638456cb59146103485780638da5cb5b1461036f57806395d89b411461039e578063a9059cbb14610429578063dd62ed3e1461044d578063f2fde38b14610484575b600080fd5b34156100f357600080fd5b6100fb6104a5565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101385780820151818401525b60200161011f565b50505050905090810190601f1680156101655780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561017e57600080fd5b610195600160a060020a0360043516602435610543565b005b34156101a257600080fd5b6101aa61056a565b60405190815260200160405180910390f35b34156101c757600080fd5b610195600160a060020a0360043581169060243516604435610570565b005b34156101f157600080fd5b6101f96105ba565b60405160ff909116815260200160405180910390f35b341561021a57600080fd5b6102226105c3565b604051901515815260200160405180910390f35b341561024157600080fd5b6100fb61064c565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101385780820151818401525b60200161011f565b50505050905090810190601f1680156101655780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156102cc57600080fd5b6102226106ea565b604051901515815260200160405180910390f35b34156102f357600080fd5b6102fb6106fa565b604051600160a060020a03909116815260200160405180910390f35b341561032257600080fd5b6101aa600160a060020a0360043516610709565b60405190815260200160405180910390f35b341561035357600080fd5b610222610728565b604051901515815260200160405180910390f35b341561037a57600080fd5b6102fb6107b6565b604051600160a060020a03909116815260200160405180910390f35b34156103a957600080fd5b6100fb6107c5565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101385780820151818401525b60200161011f565b50505050905090810190601f1680156101655780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561043457600080fd5b610195600160a060020a0360043516602435610863565b005b341561045857600080fd5b6101aa600160a060020a03600435811690602435166108ab565b60405190815260200160405180910390f35b341561048f57600080fd5b610195600160a060020a03600435166108d8565b005b60048054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561053b5780601f106105105761010080835404028352916020019161053b565b820191906000526020600020905b81548152906001019060200180831161051e57829003601f168201915b505050505081565b60035460a060020a900460ff161561055a57600080fd5b6105648282610930565b5b5b5050565b60005481565b60035460a060020a900460ff161561058757600080fd5b30600160a060020a031682600160a060020a0316141515156105a857600080fd5b6105b38383836109d2565b5b5b505050565b60065460ff1681565b60035460009033600160a060020a039081169116146105e157600080fd5b60035460a060020a900460ff1615156105f957600080fd5b6003805474ff0000000000000000000000000000000000000000191690557f7805862f689e2f13df9f062ff482ad3ad112aca9e0847911ed832e158c525b3360405160405180910390a15060015b5b5b90565b60078054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561053b5780601f106105105761010080835404028352916020019161053b565b820191906000526020600020905b81548152906001019060200180831161051e57829003601f168201915b505050505081565b60035460a060020a900460ff1681565b600854600160a060020a031681565b600160a060020a0381166000908152600160205260409020545b919050565b60035460009033600160a060020a0390811691161461074657600080fd5b60035460a060020a900460ff161561075d57600080fd5b6003805474ff0000000000000000000000000000000000000000191660a060020a1790557f6985a02210a168e66602d3235cb6db0e70f92b3ba4d376a33c0f3d9434bff62560405160405180910390a15060015b5b5b90565b600354600160a060020a031681565b60058054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561053b5780601f106105105761010080835404028352916020019161053b565b820191906000526020600020905b81548152906001019060200180831161051e57829003601f168201915b505050505081565b60035460a060020a900460ff161561087a57600080fd5b30600160a060020a031682600160a060020a03161415151561089b57600080fd5b6105648282610af5565b5b5b5050565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b60035433600160a060020a039081169116146108f357600080fd5b600160a060020a0381161561092b576003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b5b50565b80158015906109635750600160a060020a0333811660009081526002602090815260408083209386168352929052205415155b1561096d57600080fd5b600160a060020a03338116600081815260026020908152604080832094871680845294909152908190208490557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259084905190815260200160405180910390a35b5050565b6000606060643610156109e457600080fd5b600160a060020a038086166000908152600260209081526040808320338516845282528083205493881683526001909152902054909250610a2b908463ffffffff610bc216565b600160a060020a038086166000908152600160205260408082209390935590871681522054610a60908463ffffffff610bdc16565b600160a060020a038616600090815260016020526040902055610a89828463ffffffff610bdc16565b600160a060020a03808716600081815260026020908152604080832033861684529091529081902093909355908616917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9086905190815260200160405180910390a35b5b5050505050565b60406044361015610b0557600080fd5b600160a060020a033316600090815260016020526040902054610b2e908363ffffffff610bdc16565b600160a060020a033381166000908152600160205260408082209390935590851681522054610b63908363ffffffff610bc216565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35b5b505050565b600082820183811015610bd157fe5b8091505b5092915050565b600082821115610be857fe5b508082035b929150505600a165627a7a72305820e6e7ef4ebd80d214f3d4beba112420e3e2450c5d8be0dffa0858d8b870a7f3a10029\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_UNPAUSE = "unpause";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_PAUSED = "paused";

    public static final String FUNC_REPLACESOLDCONTRACT = "replacesOldContract";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_PAUSE = "pause";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event PAUSE_EVENT = new Event("Pause", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event UNPAUSE_EVENT = new Event("Unpause", 
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

    protected JET(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected JET(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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

    public RemoteCall<TransactionReceipt> unpause() {
        final Function function = new Function(
                FUNC_UNPAUSE, 
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

    public RemoteCall<Boolean> paused() {
        final Function function = new Function(FUNC_PAUSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> replacesOldContract() {
        final Function function = new Function(FUNC_REPLACESOLDCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> pause() {
        final Function function = new Function(
                FUNC_PAUSE, 
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

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<JET> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(JET.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<JET> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(JET.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<PauseEventResponse> getPauseEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PAUSE_EVENT, transactionReceipt);
        ArrayList<PauseEventResponse> responses = new ArrayList<PauseEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PauseEventResponse typedResponse = new PauseEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PauseEventResponse> pauseEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, PauseEventResponse>() {
            @Override
            public PauseEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PAUSE_EVENT, log);
                PauseEventResponse typedResponse = new PauseEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<PauseEventResponse> pauseEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PAUSE_EVENT));
        return pauseEventObservable(filter);
    }

    public List<UnpauseEventResponse> getUnpauseEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UNPAUSE_EVENT, transactionReceipt);
        ArrayList<UnpauseEventResponse> responses = new ArrayList<UnpauseEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnpauseEventResponse typedResponse = new UnpauseEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UnpauseEventResponse> unpauseEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UnpauseEventResponse>() {
            @Override
            public UnpauseEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UNPAUSE_EVENT, log);
                UnpauseEventResponse typedResponse = new UnpauseEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<UnpauseEventResponse> unpauseEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNPAUSE_EVENT));
        return unpauseEventObservable(filter);
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

    public static JET load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new JET(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static JET load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new JET(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class PauseEventResponse {
        public Log log;
    }

    public static class UnpauseEventResponse {
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
