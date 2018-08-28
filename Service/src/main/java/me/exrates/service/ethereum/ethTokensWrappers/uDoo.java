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
public class uDoo extends Contract implements ethTokenNotERC20{
    private static final String BINARY = "0x60606040526004361061019d5763ffffffff60e060020a60003504166306fdde0381146101a2578063095ea7b31461022c578063151eeb5514610262578063176345141461028157806318160ddd146102a65780631b8fc2f0146102b9578063211e28b6146102da57806323b872dd146102f25780633092afd51461031a578063313ce5671461033957806336a23dbf1461036257806340c10f191461037a5780634cf781701461039c5780635a3b7e42146103cb5780635d452201146103de57806366188463146103f157806370a082311461041357806379ba5097146104325780637e5cd5c1146104455780638da5cb5b1461045857806395d89b411461046b578063983b2d561461047e578063a9059cbb1461049d578063ade4637a146104bf578063ae32afe3146104d2578063b6f50c29146104e5578063c516358f14610504578063c5f956af14610517578063cf3090121461052a578063d4ee1d901461053d578063d5abeb0114610550578063d73dd62314610563578063dd62ed3e14610585578063f2fde38b146105aa578063f46eccc4146105c9575b600080fd5b34156101ad57600080fd5b6101b56105e8565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101f15780820151838201526020016101d9565b50505050905090810190601f16801561021e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561023757600080fd5b61024e600160a060020a0360043516602435610686565b604051901515815260200160405180910390f35b341561026d57600080fd5b61024e600160a060020a03600435166106af565b341561028c57600080fd5b6102946106f6565b60405190815260200160405180910390f35b34156102b157600080fd5b6102946106fc565b34156102c457600080fd5b6102d8600160a060020a0360043516610702565b005b34156102e557600080fd5b6102d86004351515610761565b34156102fd57600080fd5b61024e600160a060020a036004358116906024351660443561078f565b341561032557600080fd5b6102d8600160a060020a03600435166107ba565b341561034457600080fd5b61034c6107f6565b60405160ff909116815260200160405180910390f35b341561036d57600080fd5b6102d860043515156107ff565b341561038557600080fd5b610294600160a060020a03600435166024356108df565b34156103a757600080fd5b6103af6109e4565b604051600160a060020a03909116815260200160405180910390f35b34156103d657600080fd5b6101b56109f3565b34156103e957600080fd5b6103af610a5e565b34156103fc57600080fd5b61024e600160a060020a0360043516602435610a72565b341561041e57600080fd5b610294600160a060020a0360043516610a92565b341561043d57600080fd5b6102d8610aad565b341561045057600080fd5b61024e610af6565b341561046357600080fd5b6103af610aff565b341561047657600080fd5b6101b5610b0e565b341561048957600080fd5b6102d8600160a060020a0360043516610b79565b34156104a857600080fd5b61024e600160a060020a0360043516602435610bb8565b34156104ca57600080fd5b61024e610bd8565b34156104dd57600080fd5b6103af610bf9565b34156104f057600080fd5b6102d8600160a060020a0360043516610c08565b341561050f57600080fd5b6103af610c6d565b341561052257600080fd5b6103af610c7c565b341561053557600080fd5b61024e610c8b565b341561054857600080fd5b6103af610c94565b341561055b57600080fd5b610294610ca3565b341561056e57600080fd5b61024e600160a060020a0360043516602435610ca9565b341561059057600080fd5b610294600160a060020a0360043581169060243516610cc9565b34156105b557600080fd5b6102d8600160a060020a0360043516610cf4565b34156105d457600080fd5b61024e600160a060020a0360043516610d53565b60078054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561067e5780601f106106535761010080835404028352916020019161067e565b820191906000526020600020905b81548152906001019060200180831161066157829003601f168201915b505050505081565b600a5460009060ff161561069c575060006106a9565b6106a68383610d68565b90505b92915050565b600f54600090600160a060020a03838116911614156106d0575060016106f1565b5060115474010000000000000000000000000000000000000000900460ff16155b919050565b60055481565b60005481565b60035433600160a060020a0390811691161461071d57600080fd5b600160a060020a038116151561073257600080fd5b6011805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60035433600160a060020a0390811691161461077c57600080fd5b600a805460ff1916911515919091179055565b600061079a846106af565b15156107a557600080fd5b6107b0848484610dd4565b90505b9392505050565b60035433600160a060020a039081169116146107d557600080fd5b600160a060020a03166000908152600b60205260409020805460ff19169055565b60065460ff1681565b60035433600160a060020a0390811691161461081a57600080fd5b600d546101009004600160a060020a03161580159061089d5750600d546101009004600160a060020a031663e031d6f06000604051602001526040518163ffffffff1660e060020a028152600401602060405180830381600087803b151561088157600080fd5b6102c65a03f1151561089257600080fd5b505050604051805190505b156108dc576011805474ff0000000000000000000000000000000000000000191674010000000000000000000000000000000000000000831515021790555b50565b600160a060020a0333166000908152600b602052604081205460ff16151560011461090957600080fd5b60035433600160a060020a03908116911614156109da57600d546101009004600160a060020a0316151561093c57600080fd5b600d546101009004600160a060020a031663e031d6f06000604051602001526040518163ffffffff1660e060020a028152600401602060405180830381600087803b151561098957600080fd5b6102c65a03f1151561099a57600080fd5b50505060405180519050806109bc5750601154600160a060020a038481169116145b156109d2576109cb8383610df5565b90506106a9565b5060006106a9565b6106a68383610df5565b601154600160a060020a031681565b60098054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561067e5780601f106106535761010080835404028352916020019161067e565b600d546101009004600160a060020a031681565b600a5460009060ff1615610a88575060006106a9565b6106a68383610f00565b600160a060020a031660009081526001602052604090205490565b60045433600160a060020a0390811691161415610af4576004546003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911790555b565b600d5460ff1681565b600354600160a060020a031681565b60088054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561067e5780601f106106535761010080835404028352916020019161067e565b60035433600160a060020a03908116911614610b9457600080fd5b600160a060020a03166000908152600b60205260409020805460ff19166001179055565b6000610bc3336106af565b1515610bce57600080fd5b6106a68383610ffc565b60115474010000000000000000000000000000000000000000900460ff1681565b601054600160a060020a031681565b60035433600160a060020a03908116911614610c2357600080fd5b600160a060020a0381161515610c3857600080fd5b600d8054600160a060020a039092166101000274ffffffffffffffffffffffffffffffffffffffff0019909216919091179055565b600f54600160a060020a031681565b600e54600160a060020a031681565b600a5460ff1681565b600454600160a060020a031681565b600c5481565b600a5460009060ff1615610cbf575060006106a9565b6106a68383611019565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b60035433600160a060020a03908116911614610d0f57600080fd5b600160a060020a0381161515610d2457600080fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600b6020526000908152604090205460ff1681565b600160a060020a03338116600081815260026020908152604080832094871680845294909152808220859055909291907f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b600a5460009060ff1615610dea575060006107b3565b6107b08484846110bd565b600160a060020a0333166000908152600b602052604081205460ff161515600114610e1f57600080fd5b600a5460ff16151560011415610e37575060006106a9565b811515610e46575060006106a9565b600c54600054610e5c908463ffffffff61123f16565b1115610e6a575060006106a9565b600054610e7d908363ffffffff61123f16565b6000908155600160a060020a038416815260016020526040902054610ea8908363ffffffff61123f16565b600160a060020a0384166000818152600160205260408082209390935590917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a350919050565b600160a060020a03338116600090815260026020908152604080832093861683529290529081205480831115610f5d57600160a060020a033381166000908152600260209081526040808320938816835292905290812055610f94565b610f6d818463ffffffff61124e16565b600160a060020a033381166000908152600260209081526040808320938916835292905220555b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020547f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925915190815260200160405180910390a3600191505b5092915050565b600a5460009060ff161561100f57600080fd5b6106a68383611260565b600160a060020a033381166000908152600260209081526040808320938616835292905290812054611051908363ffffffff61123f16565b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020849055919290917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591905190815260200160405180910390a350600192915050565b6000600160a060020a03831615156110d457600080fd5b600160a060020a0384166000908152600160205260409020548211156110f957600080fd5b600160a060020a038085166000908152600260209081526040808320339094168352929052205482111561112c57600080fd5b600160a060020a038416600090815260016020526040902054611155908363ffffffff61124e16565b600160a060020a03808616600090815260016020526040808220939093559085168152205461118a908363ffffffff61123f16565b600160a060020a038085166000908152600160209081526040808320949094558783168252600281528382203390931682529190915220546111d2908363ffffffff61124e16565b600160a060020a03808616600081815260026020908152604080832033861684529091529081902093909355908516917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060019392505050565b6000828201838110156107b357fe5b60008282111561125a57fe5b50900390565b6000600160a060020a038316151561127757600080fd5b600160a060020a03331660009081526001602052604090205482111561129c57600080fd5b600160a060020a0333166000908152600160205260409020546112c5908363ffffffff61124e16565b600160a060020a0333811660009081526001602052604080822093909355908516815220546112fa908363ffffffff61123f16565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a350600192915050565b60008083151561136e5760009150610ff5565b5082820282848281151561137e57fe5b04146107b357fe5b600080600160a060020a038516158015906113a95750600160a060020a03841615155b80156113bd5750600160a060020a03831615155b15156113c857600080fd5b600e8054600160a060020a0380881673ffffffffffffffffffffffffffffffffffffffff1992831617909255600f80548684169083161790556010805492871692909116919091179055600654611438906404731c69b49060ff9081166001190116600a0a63ffffffff61135b16565b9150816114458584610df5565b149050801561148a5760065461146b90629896809060ff16600a0a63ffffffff61135b16565b600f54909250829061148690600160a060020a031682610df5565b1490505b80156114d4576006546114b5906369f6bc709060001960ff9182160116600a0a63ffffffff61135b16565b600e5490925082906114d090600160a060020a031682610df5565b1490505b6001811515146114e357600080fd5b50505050505600a165627a7a72305820ff6fcf7ec0121d45967c5ef93093ebd0df8c220c9a104fdb5e1f8ced4942d1a50029\n";

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

    protected uDoo(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected uDoo(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public static RemoteCall<uDoo> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(uDoo.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<uDoo> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(uDoo.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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

    public static uDoo load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new uDoo(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static uDoo load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new uDoo(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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
