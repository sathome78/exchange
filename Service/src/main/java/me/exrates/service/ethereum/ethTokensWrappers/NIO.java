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
 * <p>Generated with web3j version 3.3.1.
 */
public class NIO extends Contract implements ethTokenERC20 {
    private static final String BINARY = "60606040526000600555600260115534156200001a57600080fd5b604051620019c2380380620019c2833981016040528080518201919060200180518201919060200180519190602001805191906020018051919060200180518201919060200180518201919060200180519190602001805191906020018051919060200180519150505b60098b8051620000999291602001906200035e565b50600a8a8051620000af9291602001906200035e565b50600b899055600160a060020a03331660009081526020819052604090208990556006868051620000e59291602001906200035e565b506007858051620000fb9291602001906200035e565b50600c805460ff19168815151790556012805461010060a860020a03191661010033600160a060020a031602179055600d849055600e839055600f82905581156200021957426010555b62000166601054620002626401000000000262000ba7176401000000009004565b60ff166015141515620001845760108054610e0f1901905562000145565b5b620001a66010546200028a6401000000000262000c6f176401000000009004565b60ff166005141515620001c557601080546201517f1901905562000184565b620001e6601054620002ab6401000000000262000d7d176401000000009004565b60105462000202906401000000006200142c620002bf82021704565b603c020160ff166010600082825403925050819055505b6012805460ff1916821515179081905562000250906101009004600160a060020a0316640100000000620002df8102620014531704565b5b505050505050505050505062000408565b60006018603c80845b048115156200027657fe5b048115156200028157fe5b0690505b919050565b6000600762015180835b046004018115156200028157fe5b0690505b919050565b6000603c8262000281565b0690505b919050565b6000603c808362000276565b048115156200028157fe5b0690505b919050565b600160a060020a03811660009081526002602052604090205460ff1615156200035a5760058054600160a060020a038316600081815260036020908152604080832085905560018086019096559382528481528382208054600160a060020a03191684179055918152600290915220805460ff191690911790555b5b50565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620003a157805160ff1916838001178555620003d1565b82800160010185558215620003d1579182015b82811115620003d1578251825591602001919060010190620003b4565b5b50620003e0929150620003e4565b5090565b6200040591905b80821115620003e05760008155600101620003eb565b5090565b90565b6115aa80620004186000396000f300606060405236156101b45763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146101b9578063095ea7b31461024457806318160ddd1461027a5780631f6eeb681461029f57806323b872dd146102c657806334686b73146102f75780633e239e1a1461031c57806342966c68146103485780634ac1ad78146103605780634e9833ac1461038c57806354be50a5146103a45780635672f548146103c957806370a08231146104545780637423d96e1461048557806381a28c28146104aa5780638aa001fc146104d15780638da5cb5b146104fd578063935c1fb11461052c57806395d89b411461055d578063a0712d68146105e8578063a502522214610600578063a610fe9b14610633578063a9059cbb14610648578063b58c2d3114610673578063b686d8b414610694578063b780a659146106b9578063c121be4d14610744578063c25fe1a414610765578063ce1f561c1461077d578063dd62ed3e146107a2578063e7f6edbd146107d9578063eaf214ea146107fe578063edf26d9b14610816578063fa93f88314610848578063faff660e14610874575b600080fd5b34156101c457600080fd5b6101cc61089b565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156102095780820151818401525b6020016101f0565b50505050905090810190601f1680156102365780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561024f57600080fd5b610266600160a060020a0360043516602435610939565b604051901515815260200160405180910390f35b341561028557600080fd5b61028d6109a6565b60405190815260200160405180910390f35b34156102aa57600080fd5b6102666109ac565b604051901515815260200160405180910390f35b610266600160a060020a03600435811690602435166044356109b5565b604051901515815260200160405180910390f35b341561030257600080fd5b61028d610ba1565b60405190815260200160405180910390f35b341561032757600080fd5b610332600435610ba7565b60405160ff909116815260200160405180910390f35b341561035357600080fd5b61035e600435610bcd565b005b341561036b57600080fd5b610332600435610c6f565b60405160ff909116815260200160405180910390f35b341561039757600080fd5b61035e600435610c8f565b005b34156103af57600080fd5b61028d610cb8565b60405190815260200160405180910390f35b34156103d457600080fd5b6101cc610cbe565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156102095780820151818401525b6020016101f0565b50505050905090810190601f1680156102365780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561045f57600080fd5b61028d600160a060020a0360043516610d5c565b60405190815260200160405180910390f35b341561049057600080fd5b61028d610d6e565b60405190815260200160405180910390f35b34156104b557600080fd5b610266610d74565b604051901515815260200160405180910390f35b34156104dc57600080fd5b610332600435610d7d565b60405160ff909116815260200160405180910390f35b341561050857600080fd5b610510610d90565b604051600160a060020a03909116815260200160405180910390f35b341561053757600080fd5b61028d600160a060020a0360043516610da4565b60405190815260200160405180910390f35b341561056857600080fd5b6101cc610db6565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156102095780820151818401525b6020016101f0565b50505050905090810190601f1680156102365780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156105f357600080fd5b61035e600435610e54565b005b341561060b57600080fd5b610266600160a060020a0360043516610ef2565b604051901515815260200160405180910390f35b341561063e57600080fd5b61035e610f07565b005b610266600160a060020a03600435166024356110ac565b604051901515815260200160405180910390f35b341561067e57600080fd5b61035e600160a060020a0360043516611260565b005b341561069f57600080fd5b61028d6112a1565b60405190815260200160405180910390f35b34156106c457600080fd5b6101cc6112a7565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156102095780820151818401525b6020016101f0565b50505050905090810190601f1680156102365780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561074f57600080fd5b61035e600160a060020a0360043516611345565b005b341561077057600080fd5b61035e600435611386565b005b341561078857600080fd5b61028d6113af565b60405190815260200160405180910390f35b34156107ad57600080fd5b61028d600160a060020a03600435811690602435166113b5565b60405190815260200160405180910390f35b34156107e457600080fd5b61028d6113e2565b60405190815260200160405180910390f35b341561080957600080fd5b61035e6004356113e8565b005b341561082157600080fd5b610510600435611411565b604051600160a060020a03909116815260200160405180910390f35b341561085357600080fd5b61033260043561142c565b60405160ff909116815260200160405180910390f35b341561087f57600080fd5b61026661144a565b604051901515815260200160405180910390f35b60098054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109315780601f1061090657610100808354040283529160200191610931565b820191906000526020600020905b81548152906001019060200180831161091457829003601f168201915b505050505081565b600160a060020a03338116600081815260046020908152604080832094871680845294909152808220859055909291907f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a35060015b92915050565b600b5481565b600c5460ff1681565b601254600090600160a060020a0385811661010090920416148015906109ee5750601254600160a060020a038481166101009092041614155b80156109fb5750600d5415155b15610a4057601254600d54610100909104600160a060020a0316906108fc81150290604051600060405180830381858888f193505050501515610a4057506000610b99565b5b60125460ff168015610a665750601254600160a060020a038581166101009092041614155b8015610a8b5750600160a060020a03831660009081526002602052604090205460ff16155b15610a9857506000610b99565b600160a060020a038416600090815260208190526040902054829010801590610ae85750600160a060020a0380851660009081526004602090815260408083203390941683529290522054829010155b8015610af45750600082115b8015610b195750600160a060020a038316600090815260208190526040902054828101115b15610b9557600160a060020a038085166000818152602081815260408083208054889003905560048252808320338616845282528083208054889003905593871680835290829052908390208054860190559160008051602061155f8339815191529085905190815260200160405180910390a3506001610b99565b5060005b5b9392505050565b60055481565b60006018603c80845b04811515610bba57fe5b04811515610bc457fe5b0690505b919050565b600c5460ff168015610bf2575060125433600160a060020a0390811661010090920416145b15610c6b57600160a060020a03331660009081526020819052604090205481901015610c1d57600080fd5b600160a060020a03331660008181526020819052604080822080548590039055600b8054859003905590919060008051602061155f8339815191529084905190815260200160405180910390a35b5b50565b6000600762015180835b04600401811515610bc457fe5b0690505b919050565b60125433600160a060020a039081166101009092041614610caf57600080fd5b600f8190555b50565b60105481565b60068054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109315780601f1061090657610100808354040283529160200191610931565b820191906000526020600020905b81548152906001019060200180831161091457829003601f168201915b505050505081565b60006020819052908152604090205481565b600d5481565b60085460ff1681565b6000603c82610bc4565b0690505b919050565b6012546101009004600160a060020a031681565b60036020526000908152604090205481565b600a8054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109315780601f1061090657610100808354040283529160200191610931565b820191906000526020600020905b81548152906001019060200180831161091457829003601f168201915b505050505081565b600c5460ff168015610e79575060125433600160a060020a0390811661010090920416145b15610c6b57600160a060020a0333166000908152602081905260409020548181011015610ea557600080fd5b600160a060020a033316600081815260208190526040808220805485019055600b80548501905560008051602061155f8339815191529084905190815260200160405180910390a35b5b50565b60026020526000908152604090205460ff1681565b600080600080600f5460001415156110a45760105442039350600092505b600f5462093a8002841061101b5760019250600f5462093a800284039350600091505b60055482101561101657601254600083815260016020526040902054600160a060020a03908116610100909204161461100a57601154600e54600084815260016020908152604080832054600160a060020a0316835290829052902054600a9290920a91829102811515610fb857fe5b04811515610fc257fe5b600084815260016020908152604080832054600160a060020a039081168452918390528083208054959094049485900390935560125461010090041681522080548201905590505b5b600190910190610f48565b610f25565b82156110a457426010555b611031601054610ba7565b60ff16601514151561104d5760108054610e0f19019055611026565b5b611059601054610c6f565b60ff16600514151561107657601080546201517f1901905561104d565b611081601054610d7d565b60105461108d9061142c565b603c020160ff166010600082825403925050819055505b5b5b50505050565b60006110b6610f07565b600160a060020a033316600090815260208190526040902054829010156110df575060006109a0565b600160a060020a0383166000908152602081905260409020548281011015611109575060006109a0565b60125433600160a060020a0390811661010090920416148015906111405750601254600160a060020a038481166101009092041614155b801561114d5750600d5415155b1561119257601254600d54610100909104600160a060020a0316906108fc81150290604051600060405180830381858888f193505050501515611192575060006109a0565b5b60125460ff1680156111b9575060125433600160a060020a039081166101009092041614155b80156111de5750600160a060020a03831660009081526002602052604090205460ff16155b156111eb575060006109a0565b600160a060020a033381166000908152602081905260408082208054869003905591851681522080548301905561122183611453565b82600160a060020a031633600160a060020a031660008051602061155f8339815191528460405190815260200160405180910390a35060015b92915050565b60125433600160a060020a0390811661010090920416148015611285575060125460ff165b156101b45761129381611453565b610c6b565b600080fd5b5b50565b600f5481565b60078054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109315780601f1061090657610100808354040283529160200191610931565b820191906000526020600020905b81548152906001019060200180831161091457829003601f168201915b505050505081565b60125433600160a060020a039081166101009092041614801561136a575060125460ff165b156101b457611293816114de565b610c6b565b600080fd5b5b50565b60125433600160a060020a0390811661010090920416146113a657600080fd5b600e8190555b50565b60115481565b600160a060020a038083166000908152600460209081526040808320938516835292905220545b92915050565b600e5481565b60125433600160a060020a03908116610100909204161461140857600080fd5b600d8190555b50565b600160205260009081526040902054600160a060020a031681565b6000603c8083610bba565b04811515610bc457fe5b0690505b919050565b60125460ff1681565b600160a060020a03811660009081526002602052604090205460ff161515610c6b5760058054600160a060020a03831660008181526003602090815260408083208590556001808601909655938252848152838220805473ffffffffffffffffffffffffffffffffffffffff191684179055918152600290915220805460ff191690911790555b5b50565b600160a060020a03811660009081526002602052604090205460ff1615610c6b5760058054600019019055600160a060020a038116600081815260036020908152604080832054835260018252808320805473ffffffffffffffffffffffffffffffffffffffff191690559282526002905220805460ff191690555b5b505600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a72305820c4019bbd1bd0d99f08b808affd3d15109ef0fcf84a9d98c0c3e8e813991ef4ec0029000000000000000000000000000000000000000000000000000000000000016000000000000000000000000000000000000000000000000000000000000001a00000000000000000000000000000000000000000000000000000000005f5e1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000001e000000000000000000000000000000000000000000000000000000000000002200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000a55534420546574686572000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004555344540000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000025b5d00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000025b5d000000000000000000000000000000000000000000000000000000000000";

    protected NIO(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NIO(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> name() {
        final Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _amount) {
        final Function function = new Function(
                "approve", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> canMintBurn() {
        final Function function = new Function("canMintBurn", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _amount, BigInteger weiValue) {
        final Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _amount) {
        final Function function = new Function(
                "transferFrom",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from),
                        new org.web3j.abi.datatypes.Address(_to),
                        new org.web3j.abi.datatypes.generated.Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }


    public RemoteCall<BigInteger> numberOfAddress() {
        final Function function = new Function("numberOfAddress", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> getHour(BigInteger timestamp) {
        final Function function = new Function(
                "getHour", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        final Function function = new Function(
                "burn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> getWeekday(BigInteger timestamp) {
        final Function function = new Function(
                "getWeekday", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeHoldingTaxInterval(BigInteger _newValue) {
        final Function function = new Function(
                "changeHoldingTaxInterval", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_newValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> lastHoldingTax() {
        final Function function = new Function("lastHoldingTax", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> physicalString() {
        final Function function = new Function("physicalString", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> balanceOf(String param0) {
        final Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> txnTax() {
        final Function function = new Function("txnTax", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> isSecured() {
        final Function function = new Function("isSecured", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> getSecond(BigInteger timestamp) {
        final Function function = new Function(
                "getSecond", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> addressIndex(String param0) {
        final Function function = new Function("addressIndex", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> mint(BigInteger _value) {
        final Function function = new Function(
                "mint", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> addressExists(String param0) {
        final Function function = new Function("addressExists", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> chargeHoldingTax() {
        final Function function = new Function(
                "chargeHoldingTax", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value, BigInteger weiValue) {
        final Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                "transfer",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to),
                        new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addAddressManual(String addr) {
        final Function function = new Function(
                "addAddressManual", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> holdingTaxInterval() {
        final Function function = new Function("holdingTaxInterval", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> cryptoString() {
        final Function function = new Function("cryptoString", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> removeAddressManual(String addr) {
        final Function function = new Function(
                "removeAddressManual", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeHoldingTax(BigInteger _newValue) {
        final Function function = new Function(
                "changeHoldingTax", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_newValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> holdingTaxDecimals() {
        final Function function = new Function("holdingTaxDecimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function("allowance", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> holdingTax() {
        final Function function = new Function("holdingTax", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> changeTxnTax(BigInteger _newValue) {
        final Function function = new Function(
                "changeTxnTax", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_newValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> addresses(BigInteger param0) {
        final Function function = new Function("addresses", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> getMinute(BigInteger timestamp) {
        final Function function = new Function(
                "getMinute", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isPrivate() {
        final Function function = new Function("isPrivate", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public static RemoteCall<NIO> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String n, String a, BigInteger totalSupplyToUse, Boolean isSecured, Boolean cMB, String physical, String crypto, BigInteger txnTaxToUse, BigInteger holdingTaxToUse, BigInteger holdingTaxIntervalToUse, Boolean isPrivateToUse) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(n), 
                new org.web3j.abi.datatypes.Utf8String(a), 
                new org.web3j.abi.datatypes.generated.Uint256(totalSupplyToUse), 
                new org.web3j.abi.datatypes.Bool(isSecured), 
                new org.web3j.abi.datatypes.Bool(cMB), 
                new org.web3j.abi.datatypes.Utf8String(physical), 
                new org.web3j.abi.datatypes.Utf8String(crypto), 
                new org.web3j.abi.datatypes.generated.Uint256(txnTaxToUse), 
                new org.web3j.abi.datatypes.generated.Uint256(holdingTaxToUse), 
                new org.web3j.abi.datatypes.generated.Uint256(holdingTaxIntervalToUse), 
                new org.web3j.abi.datatypes.Bool(isPrivateToUse)));
        return deployRemoteCall(NIO.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<NIO> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String n, String a, BigInteger totalSupplyToUse, Boolean isSecured, Boolean cMB, String physical, String crypto, BigInteger txnTaxToUse, BigInteger holdingTaxToUse, BigInteger holdingTaxIntervalToUse, Boolean isPrivateToUse) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(n), 
                new org.web3j.abi.datatypes.Utf8String(a), 
                new org.web3j.abi.datatypes.generated.Uint256(totalSupplyToUse), 
                new org.web3j.abi.datatypes.Bool(isSecured), 
                new org.web3j.abi.datatypes.Bool(cMB), 
                new org.web3j.abi.datatypes.Utf8String(physical), 
                new org.web3j.abi.datatypes.Utf8String(crypto), 
                new org.web3j.abi.datatypes.generated.Uint256(txnTaxToUse), 
                new org.web3j.abi.datatypes.generated.Uint256(holdingTaxToUse), 
                new org.web3j.abi.datatypes.generated.Uint256(holdingTaxIntervalToUse), 
                new org.web3j.abi.datatypes.Bool(isPrivateToUse)));
        return deployRemoteCall(NIO.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static NIO load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NIO(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static NIO load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NIO(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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
