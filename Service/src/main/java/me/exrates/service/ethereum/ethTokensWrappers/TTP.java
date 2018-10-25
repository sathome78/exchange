package me.exrates.service.ethereum.ethTokensWrappers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
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
public class TTP extends Contract implements ethTokenNotERC20{
    private static final String BINARY = "606060405260408051908101604052601881527f61746f7368696d616b4070726f746f6e6d61696c2e636f6d0000000000000000602082015260039080516200004d9291602001906200009f565b506009805460ff19908116600190811790925560048054600160a060020a033316600160a060020a031990911617905560008054909116600f179081905560ff16600a0a633b9aca0002905562000144565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620000e257805160ff191683800117855562000112565b8280016001018555821562000112579182015b8281111562000112578251825591602001919060010190620000f5565b506200012092915062000124565b5090565b6200014191905b808211156200012057600081556001016200012b565b90565b6112db80620001546000396000f3006060604052600436106100fb5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde038114610100578063095ea7b31461018a57806318160ddd146101c057806323b872dd146101e5578063313ce5671461020d57806340c10f191461023657806342966c6814610258578063661884631461027057806370a0823114610292578063820e93f5146102b15780638d2a3739146102c45780638f770ad01461036257806395d89b4114610375578063a9059cbb14610388578063ad1b3909146103aa578063beb0a416146103d9578063d73dd623146103ec578063dd62ed3e1461040e575b600080fd5b341561010b57600080fd5b610113610433565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561014f578082015183820152602001610137565b50505050905090810190601f16801561017c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561019557600080fd5b6101ac600160a060020a036004351660243561046a565b604051901515815260200160405180910390f35b34156101cb57600080fd5b6101d36104d6565b60405190815260200160405180910390f35b34156101f057600080fd5b6101ac600160a060020a03600435811690602435166044356104dc565b341561021857600080fd5b61022061065e565b60405160ff909116815260200160405180910390f35b341561024157600080fd5b6101ac600160a060020a0360043516602435610667565b341561026357600080fd5b61026e6004356107ab565b005b341561027b57600080fd5b6101ac600160a060020a0360043516602435610828565b341561029d57600080fd5b6101d3600160a060020a0360043516610922565b34156102bc57600080fd5b61011361093d565b34156102cf57600080fd5b61026e60046024813581810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284375094965050509235600160a060020a031692506109db915050565b341561036d57600080fd5b6101d3610e4d565b341561038057600080fd5b610113610e53565b341561039357600080fd5b6101ac600160a060020a0360043516602435610e8a565b34156103b557600080fd5b6103bd610f85565b604051600160a060020a03909116815260200160405180910390f35b34156103e457600080fd5b610113610f94565b34156103f757600080fd5b6101ac600160a060020a0360043516602435610fff565b341561041957600080fd5b6101d3600160a060020a03600435811690602435166110a3565b60408051908101604052600581527f5472656e74000000000000000000000000000000000000000000000000000000602082015281565b600160a060020a03338116600081815260086020908152604080832094871680845294909152808220859055909291907f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b60065481565b6000600160a060020a03831615156104f357600080fd5b600160a060020a03841660009081526007602052604090205482111561051857600080fd5b600160a060020a038085166000908152600860209081526040808320339094168352929052205482111561054b57600080fd5b600160a060020a038416600090815260076020526040902054610574908363ffffffff6110ce16565b600160a060020a0380861660009081526007602052604080822093909355908516815220546105a9908363ffffffff6110e016565b600160a060020a038085166000908152600760209081526040808320949094558783168252600881528382203390931682529190915220546105f1908363ffffffff6110ce16565b600160a060020a03808616600081815260086020908152604080832033861684529091529081902093909355908516917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060019392505050565b60005460ff1681565b60045460009033600160a060020a0390811691161480610695575060055433600160a060020a039081169116145b15156106a057600080fd5b60095460ff1615156106b157600080fd5b6001546006546106c7908463ffffffff6110e016565b11156106d257600080fd5b6006546106e5908363ffffffff6110e016565b600655600160a060020a038316600090815260076020526040902054610711908363ffffffff6110e016565b600160a060020a0384166000818152600760205260409081902092909255907f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968859084905190815260200160405180910390a2600160a060020a03831660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8460405190815260200160405180910390a350600192915050565b600160a060020a0333166000908152600760205260408120548211156107d057600080fd5b5033600160a060020a0381166000908152600760205260409020546107f590836110ce565b600160a060020a038216600090815260076020526040902055600654610821908363ffffffff6110ce16565b6006555050565b600160a060020a0333811660009081526008602090815260408083209386168352929052908120548083111561088557600160a060020a0333811660009081526008602090815260408083209388168352929052908120556108bc565b610895818463ffffffff6110ce16565b600160a060020a033381166000908152600860209081526040808320938916835292905220555b600160a060020a0333811660008181526008602090815260408083209489168084529490915290819020547f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925915190815260200160405180910390a35060019392505050565b600160a060020a031660009081526007602052604090205490565b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109d35780601f106109a8576101008083540402835291602001916109d3565b820191906000526020600020905b8154815290600101906020018083116109b657829003601f168201915b505050505081565b60045433600160a060020a039081169116146109f657600080fd5b6040517f77656200000000000000000000000000000000000000000000000000000000008152600301604051908190039020836040518082805190602001908083835b60208310610a585780518252601f199092019160209182019101610a39565b6001836020036101000a03801982511681845116179092525050509190910192506040915050519081900390201415610a9457610a94826110f6565b6040517f656d61696c0000000000000000000000000000000000000000000000000000008152600501604051908190039020836040518082805190602001908083835b60208310610af65780518252601f199092019160209182019101610ad7565b6001836020036101000a03801982511681845116179092525050509190910192506040915050519081900390201415610b3257610b3282611128565b6040517f636f6e74726163740000000000000000000000000000000000000000000000008152600801604051908190039020836040518082805190602001908083835b60208310610b945780518252601f199092019160209182019101610b75565b6001836020036101000a03801982511681845116179092525050509190910192506040915050519081900390201415610bd057610bd081611156565b6040517f6f776e00000000000000000000000000000000000000000000000000000000008152600301604051908190039020836040518082805190602001908083835b60208310610c325780518252601f199092019160209182019101610c13565b6001836020036101000a03801982511681845116179092525050509190910192506040915050519081900390201415610c6e57610c6e816111b5565b6040517f64696500000000000000000000000000000000000000000000000000000000008152600301604051908190039020836040518082805190602001908083835b60208310610cd05780518252601f199092019160209182019101610cb1565b6001836020036101000a03801982511681845116179092525050509190910192506040915050519081900390201415610d1157600454600160a060020a0316ff5b6040517f6d696e74000000000000000000000000000000000000000000000000000000008152600401604051908190039020836040518082805190602001908083835b60208310610d735780518252601f199092019160209182019101610d54565b6001836020036101000a03801982511681845116179092525050509190910192506040915050519081900390201415610e48576040517f74727565000000000000000000000000000000000000000000000000000000008152600401604051908190039020826040518082805190602001908083835b60208310610e085780518252601f199092019160209182019101610de9565b6001836020036101000a03801982511681845116179092525050509190910192506040915050519081900390206009805460ff1916929091149190911790555b505050565b60015481565b60408051908101604052600381527f5454500000000000000000000000000000000000000000000000000000000000602082015281565b6000600160a060020a0383161515610ea157600080fd5b600160a060020a033316600090815260076020526040902054821115610ec657600080fd5b600160a060020a033316600090815260076020526040902054610eef908363ffffffff6110ce16565b600160a060020a033381166000908152600760205260408082209390935590851681522054610f24908363ffffffff6110e016565b600160a060020a0380851660008181526007602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a350600192915050565b600554600160a060020a031681565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109d35780601f106109a8576101008083540402835291602001916109d3565b600160a060020a033381166000908152600860209081526040808320938616835292905290812054611037908363ffffffff6110e016565b600160a060020a0333811660008181526008602090815260408083209489168084529490915290819020849055919290917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591905190815260200160405180910390a350600192915050565b600160a060020a03918216600090815260086020908152604080832093909416825291909152205490565b6000828211156110da57fe5b50900390565b6000828201838110156110ef57fe5b9392505050565b60045433600160a060020a0390811691161461111157600080fd5b6002818051611124929160200190611214565b5050565b60045433600160a060020a0390811691161461114357600080fd5b6003818051611124929160200190611214565b60045433600160a060020a0390811691161461117157600080fd5b600160a060020a038116151561118657600080fd5b6005805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60045433600160a060020a039081169116146111d057600080fd5b600160a060020a03811615156111e557600080fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061125557805160ff1916838001178555611282565b82800160010185558215611282579182015b82811115611282578251825591602001919060010190611267565b5061128e929150611292565b5090565b6112ac91905b8082111561128e5760008155600101611298565b905600a165627a7a72305820cb8e33400d50a0b3eb3227fc243e8c02fa4784cdd34bbb669199768e3c82cf840029\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_EMAIL = "email";

    public static final String FUNC_ATOSHIMA = "atoshima";

    public static final String FUNC_SUPPLYCAP = "supplyCap";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_COINAGE = "coinage";

    public static final String FUNC_WEBSITE = "website";

    public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event MINT_EVENT = new Event("Mint", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected TTP(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TTP(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<TransactionReceipt> mint(String _to, BigInteger _amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteCall<String> email() {
        final Function function = new Function(FUNC_EMAIL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> atoshima(String b, String t, String c) {
        final Function function = new Function(
                FUNC_ATOSHIMA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(b), 
                new org.web3j.abi.datatypes.Utf8String(t), 
                new org.web3j.abi.datatypes.Address(c)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> supplyCap() {
        final Function function = new Function(FUNC_SUPPLYCAP, 
                Arrays.<Type>asList(), 
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

    public RemoteCall<String> coinage() {
        final Function function = new Function(FUNC_COINAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> website() {
        final Function function = new Function(FUNC_WEBSITE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public static RemoteCall<TTP> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TTP.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<TTP> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TTP.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static TTP load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TTP(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TTP load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TTP(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger value;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class MintEventResponse {
        public Log log;

        public String to;

        public BigInteger amount;
    }
}
