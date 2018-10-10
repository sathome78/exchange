package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
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
 * <p>Generated with web3j version 3.3.1.
 */
public class EOS extends Contract implements ethTokenERC20{
    private static final String BINARY = "606060405260126006556000600790600019169055341561001c57fe5b604051602080611c5d833981016040528080519060200190919050505b5b60005b80600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550806000819055505b5033600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055503373ffffffffffffffffffffffffffffffffffffffff167fce241d7ca1f669fee44b6fc00b8eba2df3bb514eed0f6f668f8f89096e81ed9460405180905060405180910390a25b80600581600019169055505b505b611b2e8061012f6000396000f3006060604052361561011b576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde031461011d57806307da68f51461014b578063095ea7b31461015d57806313af4035146101b457806318160ddd146101ea57806323b872dd14610210578063313ce567146102865780633452f51d146102ac5780635ac801fe1461031557806369d3e20e1461033957806370a082311461036b57806375f12b21146103b55780637a9e5e4b146103df5780638402181f146104155780638da5cb5b1461047e57806390bc1693146104d057806395d89b4114610502578063a9059cbb14610530578063be9a655514610587578063bf7e214f14610599578063dd62ed3e146105eb575bfe5b341561012557fe5b61012d610654565b60405180826000191660001916815260200191505060405180910390f35b341561015357fe5b61015b61065a565b005b341561016557fe5b61019a600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803590602001909190505061075e565b604051808215151515815260200191505060405180910390f35b34156101bc57fe5b6101e8600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190505061083c565b005b34156101f257fe5b6101fa610920565b6040518082815260200191505060405180910390f35b341561021857fe5b61026c600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803590602001909190505061092b565b604051808215151515815260200191505060405180910390f35b341561028e57fe5b610296610a0b565b6040518082815260200191505060405180910390f35b34156102b457fe5b6102fb600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080356fffffffffffffffffffffffffffffffff16906020019091905050610a11565b604051808215151515815260200191505060405180910390f35b341561031d57fe5b610337600480803560001916906020019091905050610a38565b005b341561034157fe5b61036960048080356fffffffffffffffffffffffffffffffff16906020019091905050610a7e565b005b341561037357fe5b61039f600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610c44565b6040518082815260200191505060405180910390f35b34156103bd57fe5b6103c5610c8e565b604051808215151515815260200191505060405180910390f35b34156103e757fe5b610413600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610ca1565b005b341561041d57fe5b610464600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080356fffffffffffffffffffffffffffffffff16906020019091905050610d85565b604051808215151515815260200191505060405180910390f35b341561048657fe5b61048e610dad565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34156104d857fe5b61050060048080356fffffffffffffffffffffffffffffffff16906020019091905050610dd3565b005b341561050a57fe5b610512610f99565b60405180826000191660001916815260200191505060405180910390f35b341561053857fe5b61056d600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610f9f565b604051808215151515815260200191505060405180910390f35b341561058f57fe5b61059761107d565b005b34156105a157fe5b6105a9611181565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34156105f357fe5b61063e600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506111a7565b6040518082815260200191505060405180910390f35b60075481565b61069061068b336000357fffffffff000000000000000000000000000000000000000000000000000000001661122f565b611491565b6000600060043591506024359050806000191682600019163373ffffffffffffffffffffffffffffffffffffffff166000357fffffffff00000000000000000000000000000000000000000000000000000000167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19163460003660405180848152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a46001600460146101000a81548160ff0219169083151502179055505b5b50505b565b6000610779600460149054906101000a900460ff1615611491565b6000600060043591506024359050806000191682600019163373ffffffffffffffffffffffffffffffffffffffff166000357fffffffff00000000000000000000000000000000000000000000000000000000167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19163460003660405180848152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a461082f85856114a2565b92505b5b50505b92915050565b61087261086d336000357fffffffff000000000000000000000000000000000000000000000000000000001661122f565b611491565b80600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167fce241d7ca1f669fee44b6fc00b8eba2df3bb514eed0f6f668f8f89096e81ed9460405180905060405180910390a25b5b50565b600060005490505b90565b6000610946600460149054906101000a900460ff1615611491565b6000600060043591506024359050806000191682600019163373ffffffffffffffffffffffffffffffffffffffff166000357fffffffff00000000000000000000000000000000000000000000000000000000167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19163460003660405180848152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a46109fd868686611595565b92505b5b50505b9392505050565b60065481565b6000610a2f83836fffffffffffffffffffffffffffffffff16610f9f565b90505b92915050565b610a6e610a69336000357fffffffff000000000000000000000000000000000000000000000000000000001661122f565b611491565b80600781600019169055505b5b50565b610ab4610aaf336000357fffffffff000000000000000000000000000000000000000000000000000000001661122f565b611491565b610acd600460149054906101000a900460ff1615611491565b6000600060043591506024359050806000191682600019163373ffffffffffffffffffffffffffffffffffffffff166000357fffffffff00000000000000000000000000000000000000000000000000000000167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19163460003660405180848152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a4610bd4600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054846fffffffffffffffffffffffffffffffff166118f9565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610c35600054846fffffffffffffffffffffffffffffffff166118f9565b6000819055505b5b50505b5b50565b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490505b919050565b600460149054906101000a900460ff1681565b610cd7610cd2336000357fffffffff000000000000000000000000000000000000000000000000000000001661122f565b611491565b80600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f1abebea81bfa2637f28358c371278fb15ede7ea8dd28d2e03b112ff6d936ada460405180905060405180910390a25b5b50565b6000610da48333846fffffffffffffffffffffffffffffffff1661092b565b90505b92915050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b610e09610e04336000357fffffffff000000000000000000000000000000000000000000000000000000001661122f565b611491565b610e22600460149054906101000a900460ff1615611491565b6000600060043591506024359050806000191682600019163373ffffffffffffffffffffffffffffffffffffffff166000357fffffffff00000000000000000000000000000000000000000000000000000000167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19163460003660405180848152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a4610f29600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054846fffffffffffffffffffffffffffffffff16611913565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610f8a600054846fffffffffffffffffffffffffffffffff16611913565b6000819055505b5b50505b5b50565b60055481565b6000610fba600460149054906101000a900460ff1615611491565b6000600060043591506024359050806000191682600019163373ffffffffffffffffffffffffffffffffffffffff166000357fffffffff00000000000000000000000000000000000000000000000000000000167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19163460003660405180848152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a4611070858561192d565b92505b5b50505b92915050565b6110b36110ae336000357fffffffff000000000000000000000000000000000000000000000000000000001661122f565b611491565b6000600060043591506024359050806000191682600019163373ffffffffffffffffffffffffffffffffffffffff166000357fffffffff00000000000000000000000000000000000000000000000000000000167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19163460003660405180848152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a46000600460146101000a81548160ff0219169083151502179055505b5b50505b565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490505b92915050565b60003073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16141561126e576001905061148b565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614156112cd576001905061148b565b600073ffffffffffffffffffffffffffffffffffffffff16600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141561132d576000905061148b565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663b70096138430856000604051602001526040518463ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191681526020019350505050602060405180830381600087803b151561146957fe5b6102c65a03f1151561147757fe5b50505060405180519050905061148b565b5b5b5b92915050565b80151561149e5760006000fd5b5b50565b600081600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a3600190505b92915050565b600081600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054101515156115e257fe5b81600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541015151561166a57fe5b6116f0600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205483611913565b600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506117b9600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205483611913565b600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550611845600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054836118f9565b600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190505b9392505050565b6000828284019150811015151561190c57fe5b5b92915050565b6000828284039150811115151561192657fe5b5b92915050565b600081600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541015151561197a57fe5b6119c3600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205483611913565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550611a4f600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054836118f9565b600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190505b929150505600a165627a7a723058204432a84cfe06a995bd95935559d84003b39a006720c2eabd96115f376347f9b80029454f530000000000000000000000000000000000000000000000000000000000";

    protected EOS(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected EOS(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<LogNoteEventResponse> getLogNoteEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogNote", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes4>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<LogNoteEventResponse> responses = new ArrayList<LogNoteEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogNoteEventResponse typedResponse = new LogNoteEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sig = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.guy = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.foo = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.bar = (byte[]) eventValues.getIndexedValues().get(3).getValue();
            typedResponse.wad = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.fax = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogNoteEventResponse> logNoteEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogNote", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes4>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogNoteEventResponse>() {
            @Override
            public LogNoteEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                LogNoteEventResponse typedResponse = new LogNoteEventResponse();
                typedResponse.log = log;
                typedResponse.sig = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.guy = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.foo = (byte[]) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.bar = (byte[]) eventValues.getIndexedValues().get(3).getValue();
                typedResponse.wad = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.fax = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<LogSetAuthorityEventResponse> getLogSetAuthorityEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogSetAuthority", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<LogSetAuthorityEventResponse> responses = new ArrayList<LogSetAuthorityEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogSetAuthorityEventResponse typedResponse = new LogSetAuthorityEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.authority = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogSetAuthorityEventResponse> logSetAuthorityEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogSetAuthority", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogSetAuthorityEventResponse>() {
            @Override
            public LogSetAuthorityEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                LogSetAuthorityEventResponse typedResponse = new LogSetAuthorityEventResponse();
                typedResponse.log = log;
                typedResponse.authority = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<LogSetOwnerEventResponse> getLogSetOwnerEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogSetOwner", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<LogSetOwnerEventResponse> responses = new ArrayList<LogSetOwnerEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogSetOwnerEventResponse typedResponse = new LogSetOwnerEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogSetOwnerEventResponse> logSetOwnerEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogSetOwner", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogSetOwnerEventResponse>() {
            @Override
            public LogSetOwnerEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                LogSetOwnerEventResponse typedResponse = new LogSetOwnerEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<byte[]> name() {
        final Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<TransactionReceipt> stop() {
        final Function function = new Function(
                "stop", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> approve(String guy, BigInteger wad) {
        final Function function = new Function(
                "approve", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(guy), 
                new org.web3j.abi.datatypes.generated.Uint256(wad)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setOwner(String owner_) {
        final Function function = new Function(
                "setOwner", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String src, String dst, BigInteger wad) {
        final Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(src), 
                new org.web3j.abi.datatypes.Address(dst), 
                new org.web3j.abi.datatypes.generated.Uint256(wad)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> push(String dst, BigInteger wad) {
        final Function function = new Function(
                "push", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(dst), 
                new org.web3j.abi.datatypes.generated.Uint128(wad)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setName(byte[] name_) {
        final Function function = new Function(
                "setName", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(name_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mint(BigInteger wad) {
        final Function function = new Function(
                "mint", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint128(wad)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String src) {
        final Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(src)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> stopped() {
        final Function function = new Function("stopped", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> setAuthority(String authority_) {
        final Function function = new Function(
                "setAuthority", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(authority_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> pull(String src, BigInteger wad) {
        final Function function = new Function(
                "pull", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(src), 
                new org.web3j.abi.datatypes.generated.Uint128(wad)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger wad) {
        final Function function = new Function(
                "burn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint128(wad)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<byte[]> symbol() {
        final Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<TransactionReceipt> transfer(String dst, BigInteger wad) {
        final Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(dst), 
                new org.web3j.abi.datatypes.generated.Uint256(wad)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> start() {
        final Function function = new Function(
                "start", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> authority() {
        final Function function = new Function("authority", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> allowance(String src, String guy) {
        final Function function = new Function("allowance", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(src), 
                new org.web3j.abi.datatypes.Address(guy)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<EOS> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, byte[] symbol_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(symbol_)));
        return deployRemoteCall(EOS.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<EOS> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, byte[] symbol_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(symbol_)));
        return deployRemoteCall(EOS.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static EOS load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EOS(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static EOS load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EOS(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogNoteEventResponse {
        public Log log;

        public byte[] sig;

        public String guy;

        public byte[] foo;

        public byte[] bar;

        public BigInteger wad;

        public byte[] fax;
    }

    public static class LogSetAuthorityEventResponse {
        public Log log;

        public String authority;
    }

    public static class LogSetOwnerEventResponse {
        public Log log;

        public String owner;
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
}
