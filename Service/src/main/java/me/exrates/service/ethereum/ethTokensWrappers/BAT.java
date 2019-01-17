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
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class BAT extends Contract implements ethTokenERC20 {
    private static final String BINARY = "60a0604052600360608190527f312e300000000000000000000000000000000000000000000000000000000000608090815261003c919081610114565b50341561004557fe5b604051608080610e9783398101604090815281516020830151918301516060909301519092905b6005805460048054600160a060020a031916600160a060020a0388811691909117909155600160a860020a031990911685821617808355600685905560078490556b019d971e4fe8401e74000000600081815591831682526001602090815260409283902082905593548251918252915191909216927fb33527d2e0d30b7aece2c5e82927985866c1b75173d671c14f4457bf67aa6910928290030190a25b505050506101b4565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061015557805160ff1916838001178555610182565b82800160010185558215610182579182015b82811115610182578251825591602001919060010190610167565b5b5061018f929150610193565b5090565b6101b191905b8082111561018f5760008155600101610199565b5090565b90565b610cd4806101c36000396000f3006060604052361561010c5763ffffffff60e060020a60003504166301a7a8c0811461010e57806306fdde031461013a578063095ea7b3146101ca57806318160ddd146101fd578063229a49781461021f57806323b872dd14610241578063313ce5671461027a5780634172d0801461029c5780634bb278f3146102be57806354fd4d50146102d0578063590e1ae3146103605780636f7920fd1461037257806370a08231146103945780638d4e4083146103c257806391b43d13146103e657806395d89b4114610408578063a81c3bdf14610498578063a9059cbb146104c4578063b4427263146104f7578063c039daf614610501578063d648a64714610523578063dd62ed3e14610545575bfe5b341561011657fe5b61011e610579565b60408051600160a060020a039092168252519081900360200190f35b341561014257fe5b61014a610588565b604080516020808252835181830152835191928392908301918501908083838215610190575b80518252602083111561019057601f199092019160209182019101610170565b505050905090810190601f1680156101bc5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101d257fe5b6101e9600160a060020a03600435166024356105bf565b604080519115158252519081900360200190f35b341561020557fe5b61020d61062a565b60408051918252519081900360200190f35b341561022757fe5b61020d610630565b60408051918252519081900360200190f35b341561024957fe5b6101e9600160a060020a0360043581169060243516604435610640565b604080519115158252519081900360200190f35b341561028257fe5b61020d610736565b60408051918252519081900360200190f35b34156102a457fe5b61020d61073b565b60408051918252519081900360200190f35b34156102c657fe5b6102ce610741565b005b34156102d857fe5b61014a61081a565b604080516020808252835181830152835191928392908301918501908083838215610190575b80518252602083111561019057601f199092019160209182019101610170565b505050905090810190601f1680156101bc5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561036857fe5b6102ce6108a8565b005b341561037a57fe5b61020d6109dd565b60408051918252519081900360200190f35b341561039c57fe5b61020d600160a060020a03600435166109ed565b60408051918252519081900360200190f35b34156103ca57fe5b6101e9610a0c565b604080519115158252519081900360200190f35b34156103ee57fe5b61020d610a1c565b60408051918252519081900360200190f35b341561041057fe5b61014a610a22565b604080516020808252835181830152835191928392908301918501908083838215610190575b80518252602083111561019057601f199092019160209182019101610170565b505050905090810190601f1680156101bc5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156104a057fe5b61011e610a59565b60408051600160a060020a039092168252519081900360200190f35b34156104cc57fe5b6101e9600160a060020a0360043516602435610a68565b604080519115158252519081900360200190f35b6102ce610b14565b005b341561050957fe5b61020d610bf3565b60408051918252519081900360200190f35b341561052b57fe5b61020d610c03565b60408051918252519081900360200190f35b341561054d57fe5b61020d600160a060020a0360043581169060243516610c09565b60408051918252519081900360200190f35b600554600160a060020a031681565b60408051808201909152601581527f426173696320417474656e74696f6e20546f6b656e0000000000000000000000602082015281565b600160a060020a03338116600081815260026020908152604080832094871680845294825280832086905580518681529051929493927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929181900390910190a35060015b92915050565b60005481565b6b019d971e4fe8401e7400000081565b600160a060020a0383166000908152600160205260408120548290108015906106905750600160a060020a0380851660009081526002602090815260408083203390941683529290522054829010155b801561069c5750600082115b1561072a57600160a060020a03808416600081815260016020908152604080832080548801905588851680845281842080548990039055600283528184203390961684529482529182902080548790039055815186815291519293927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a350600161072e565b5060005b5b9392505050565b601281565b61190081565b60055460a060020a900460ff16156107595760006000fd5b60045433600160a060020a039081169116146107755760006000fd5b6000546b022e58cf5246568f830000009010156107925760006000fd5b60075443111580156107b257506000546b04d8c55aefb8c05b5c00000014155b156107bd5760006000fd5b6005805474ff0000000000000000000000000000000000000000191660a060020a179055600454604051600160a060020a039182169130163180156108fc02916000818181858888f1935050505015156108175760006000fd5b5b565b6003805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156108a05780601f10610875576101008083540402835291602001916108a0565b820191906000526020600020905b81548152906001019060200180831161088357829003601f168201915b505050505081565b600554600090819060a060020a900460ff16156108c55760006000fd5b60075443116108d45760006000fd5b6000546b022e58cf5246568f8300000090106108f05760006000fd5b60055433600160a060020a039081169116141561090d5760006000fd5b600160a060020a03331660009081526001602052604090205491508115156109355760006000fd5b600160a060020a03331660009081526001602052604081208190555461095b9083610c36565b600055611900825b04905033600160a060020a03167fb6c0eca8138e097d71e2dd31e19a1266487f0553f170b7260ffe68bcbe9ff8a7826040518082815260200191505060405180910390a2604051600160a060020a0333169082156108fc029083906000818181858888f1935050505015156109d85760006000fd5b5b5050565b6b04d8c55aefb8c05b5c00000081565b600160a060020a0381166000908152600160205260409020545b919050565b60055460a060020a900460ff1681565b60075481565b60408051808201909152600381527f4241540000000000000000000000000000000000000000000000000000000000602082015281565b600454600160a060020a031681565b600160a060020a033316600090815260016020526040812054829010801590610a915750600082115b15610b0557600160a060020a03338116600081815260016020908152604080832080548890039055938716808352918490208054870190558351868152935191937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a3506001610624565b506000610624565b5b92915050565b600554600090819060a060020a900460ff1615610b315760006000fd5b600654431015610b415760006000fd5b600754431115610b515760006000fd5b341515610b5e5760006000fd5b610b6a34611900610c51565b9150610b7860005483610c80565b90506b04d8c55aefb8c05b5c00000081901015610b955760006000fd5b6000818155600160a060020a033316808252600160209081526040928390208054860190558251858152925191927fb33527d2e0d30b7aece2c5e82927985866c1b75173d671c14f4457bf67aa6910929081900390910190a25b5050565b6b022e58cf5246568f8300000081565b60065481565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b60008082841015610c4357fe5b5050808203805b5092915050565b6000828202831580610c6d5750828482811515610c6a57fe5b04145b1515610c7557fe5b8091505b5092915050565b6000828201838110801590610c6d5750828110155b1515610c7557fe5b8091505b50929150505600a165627a7a72305820e7d550d22cfb06e8da1efe14627217183e846b5986b9bac6199503c4780b03d70029000000000000000000000000ac2fa512db158f44f5ee2fa5766ea7c282763cdb00000000000000000000000088e2efac3d2ef957fcd82ec201a506871ad06204000000000000000000000000000000000000000000000000000000000039f67000000000000000000000000000000000000000000000000000000000003c7a58\n";

    public static final String FUNC_BATFUNDDEPOSIT = "batFundDeposit";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_BATFUND = "batFund";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_TOKENEXCHANGERATE = "tokenExchangeRate";

    public static final String FUNC_FINALIZE = "finalize";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_REFUND = "refund";

    public static final String FUNC_TOKENCREATIONCAP = "tokenCreationCap";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_ISFINALIZED = "isFinalized";

    public static final String FUNC_FUNDINGENDBLOCK = "fundingEndBlock";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_ETHFUNDDEPOSIT = "ethFundDeposit";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_CREATETOKENS = "createTokens";

    public static final String FUNC_TOKENCREATIONMIN = "tokenCreationMin";

    public static final String FUNC_FUNDINGSTARTBLOCK = "fundingStartBlock";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final Event LOGREFUND_EVENT = new Event("LogRefund", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event CREATEBAT_EVENT = new Event("CreateBAT", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected BAT(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected BAT(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> batFundDeposit() {
        final Function function = new Function(FUNC_BATFUNDDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteCall<BigInteger> batFund() {
        final Function function = new Function(FUNC_BATFUND, 
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
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> tokenExchangeRate() {
        final Function function = new Function(FUNC_TOKENEXCHANGERATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> finalize() {
        final Function function = new Function(
                FUNC_FINALIZE, 
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

    public RemoteCall<TransactionReceipt> refund() {
        final Function function = new Function(
                FUNC_REFUND, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> tokenCreationCap() {
        final Function function = new Function(FUNC_TOKENCREATIONCAP, 
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

    public RemoteCall<Boolean> isFinalized() {
        final Function function = new Function(FUNC_ISFINALIZED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> fundingEndBlock() {
        final Function function = new Function(FUNC_FUNDINGENDBLOCK, 
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

    public RemoteCall<String> ethFundDeposit() {
        final Function function = new Function(FUNC_ETHFUNDDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
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

    public RemoteCall<TransactionReceipt> createTokens(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CREATETOKENS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<BigInteger> tokenCreationMin() {
        final Function function = new Function(FUNC_TOKENCREATIONMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> fundingStartBlock() {
        final Function function = new Function(FUNC_FUNDINGSTARTBLOCK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<BAT> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _ethFundDeposit, String _batFundDeposit, BigInteger _fundingStartBlock, BigInteger _fundingEndBlock) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_ethFundDeposit), 
                new org.web3j.abi.datatypes.Address(_batFundDeposit), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingStartBlock), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingEndBlock)));
        return deployRemoteCall(BAT.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<BAT> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _ethFundDeposit, String _batFundDeposit, BigInteger _fundingStartBlock, BigInteger _fundingEndBlock) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_ethFundDeposit), 
                new org.web3j.abi.datatypes.Address(_batFundDeposit), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingStartBlock), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingEndBlock)));
        return deployRemoteCall(BAT.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<LogRefundEventResponse> getLogRefundEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGREFUND_EVENT, transactionReceipt);
        ArrayList<LogRefundEventResponse> responses = new ArrayList<LogRefundEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogRefundEventResponse typedResponse = new LogRefundEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogRefundEventResponse> logRefundEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogRefundEventResponse>() {
            @Override
            public LogRefundEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGREFUND_EVENT, log);
                LogRefundEventResponse typedResponse = new LogRefundEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogRefundEventResponse> logRefundEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGREFUND_EVENT));
        return logRefundEventObservable(filter);
    }

    public List<CreateBATEventResponse> getCreateBATEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CREATEBAT_EVENT, transactionReceipt);
        ArrayList<CreateBATEventResponse> responses = new ArrayList<CreateBATEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CreateBATEventResponse typedResponse = new CreateBATEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CreateBATEventResponse> createBATEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CreateBATEventResponse>() {
            @Override
            public CreateBATEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CREATEBAT_EVENT, log);
                CreateBATEventResponse typedResponse = new CreateBATEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CreateBATEventResponse> createBATEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CREATEBAT_EVENT));
        return createBATEventObservable(filter);
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

    public static BAT load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new BAT(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static BAT load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new BAT(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogRefundEventResponse {
        public Log log;

        public String _to;

        public BigInteger _value;
    }

    public static class CreateBATEventResponse {
        public Log log;

        public String _to;

        public BigInteger _value;
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
