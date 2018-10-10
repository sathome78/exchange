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
 * <p>Generated with web3j version 3.3.1.
 */
public class ENGT extends Contract implements ethTokenERC20 {
    private static final String BINARY = "60606040526003805460a060020a60ff0219169055341561001f57600080fd5b60038054600160a060020a03191633600160a060020a0316179055341561004557600080fd5b6b033b2e3c9fd0803ce80000006000818155600160a060020a0333168082526001602052604080832084905590927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef915190815260200160405180910390a36110b8806100b36000396000f30060606040526004361061013d5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305d2035b811461014a57806306fdde0314610171578063095ea7b3146101fb57806318160ddd1461021d5780631e89d5451461024257806323b872dd146102d15780632ff2e9dc146102f9578063313ce5671461030c57806342966c6814610335578063661884631461034b57806370a082311461036d5780637d64bcb41461038c5780638da5cb5b1461039f5780638fda183a146103ce57806395d89b41146103ed5780639f727c2714610400578063a0712d6814610413578063a9059cbb14610429578063d73dd6231461044b578063dd62ed3e1461046d578063e5b4003b14610492578063eb4c5593146104b1578063f2fde38b146104c4578063fa404e34146104e3575b341561014857600080fd5b005b341561015557600080fd5b61015d610502565b604051901515815260200160405180910390f35b341561017c57600080fd5b610184610523565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101c05780820151838201526020016101a8565b50505050905090810190601f1680156101ed5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561020657600080fd5b61015d600160a060020a036004351660243561055a565b341561022857600080fd5b610230610600565b60405190815260200160405180910390f35b341561024d57600080fd5b61014860046024813581810190830135806020818102016040519081016040528093929190818152602001838360200280828437820191505050505050919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284375094965061060695505050505050565b34156102dc57600080fd5b61015d600160a060020a0360043581169060243516604435610665565b341561030457600080fd5b6102306106c4565b341561031757600080fd5b61031f6106d4565b60405160ff909116815260200160405180910390f35b341561034057600080fd5b6101486004356106d9565b341561035657600080fd5b61015d600160a060020a03600435166024356107bf565b341561037857600080fd5b610230600160a060020a03600435166108b9565b341561039757600080fd5b6101486108d4565b34156103aa57600080fd5b6103b261097a565b604051600160a060020a03909116815260200160405180910390f35b34156103d957600080fd5b61015d600160a060020a0360043516610989565b34156103f857600080fd5b6101846109a7565b341561040b57600080fd5b6101486109de565b341561041e57600080fd5b610148600435610a31565b341561043457600080fd5b61015d600160a060020a0360043516602435610b46565b341561045657600080fd5b61015d600160a060020a0360043516602435610ba3565b341561047857600080fd5b610230600160a060020a0360043581169060243516610c47565b341561049d57600080fd5b610148600160a060020a0360043516610c72565b34156104bc57600080fd5b610230610ce0565b34156104cf57600080fd5b610148600160a060020a0360043516610ce8565b34156104ee57600080fd5b610148600160a060020a0360043516610d83565b60035474010000000000000000000000000000000000000000900460ff1681565b60408051908101604052600f81527f456e676167656d656e74546f6b656e0000000000000000000000000000000000602082015281565b600081158061058c5750600160a060020a03338116600090815260026020908152604080832093871683529290522054155b151561059757600080fd5b600160a060020a03338116600081815260026020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b60005481565b6000815183511461061657600080fd5b5060005b82518110156106605761065783828151811061063257fe5b9060200190602002015183838151811061064857fe5b90602001906020020151610b46565b5060010161061a565b505050565b6000635b23017f42101580610688575060035433600160a060020a039081169116145b80610697575061069783610989565b806106a657506106a633610989565b15156106b157600080fd5b6106bc848484610dee565b949350505050565b6b033b2e3c9fd0803ce800000081565b601281565b60035433600160a060020a039081169116146106f457600080fd5b6000811161070157600080fd5b600160a060020a03331660009081526001602052604090205481111561072657600080fd5b600160a060020a03331660009081526001602052604090205461074f908263ffffffff610f7016565b600160a060020a0333166000908152600160205260408120919091555461077c908263ffffffff610f7016565b600055600160a060020a0333167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca58260405190815260200160405180910390a250565b600160a060020a0333811660009081526002602090815260408083209386168352929052908120548083111561081c57600160a060020a033381166000908152600260209081526040808320938816835292905290812055610853565b61082c818463ffffffff610f7016565b600160a060020a033381166000908152600260209081526040808320938916835292905220555b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020547f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925915190815260200160405180910390a35060019392505050565b600160a060020a031660009081526001602052604090205490565b60035433600160a060020a039081169116146108ef57600080fd5b60035474010000000000000000000000000000000000000000900460ff161561091757600080fd5b6003805474ff00000000000000000000000000000000000000001916740100000000000000000000000000000000000000001790557fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0860405160405180910390a1565b600354600160a060020a031681565b600160a060020a031660009081526004602052604090205460ff1690565b60408051908101604052600381527f4547540000000000000000000000000000000000000000000000000000000000602082015281565b60035433600160a060020a039081169116146109f957600080fd5b600354600160a060020a039081169030163180156108fc0290604051600060405180830381858888f193505050501515610a2f57fe5b565b60035433600160a060020a03908116911614610a4c57600080fd5b60035474010000000000000000000000000000000000000000900460ff1615610a7457600080fd5b600054610a87908263ffffffff610f8216565b6000908155600160a060020a033316815260016020526040902054610ab2908263ffffffff610f8216565b600160a060020a0333166000818152600160205260409081902092909255907f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968859083905190815260200160405180910390a2600160a060020a03331660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8360405190815260200160405180910390a350565b6000635b23017f42101580610b69575060035433600160a060020a039081169116145b80610b785750610b7883610989565b80610b875750610b8733610989565b1515610b9257600080fd5b610b9c8383610f91565b9392505050565b600160a060020a033381166000908152600260209081526040808320938616835292905290812054610bdb908363ffffffff610f8216565b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020849055919290917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591905190815260200160405180910390a350600192915050565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b60035433600160a060020a03908116911614610c8d57600080fd5b600160a060020a03811660008181526004602052604090819020805460ff191660011790557f1349d3eaa89eb9aa16dc3c06eb90a383f863cc1483d6a9e4b8be2e7ab5c6cf7d905160405180910390a250565b635b23017f81565b60035433600160a060020a03908116911614610d0357600080fd5b600160a060020a0381161515610d1857600080fd5b600354600160a060020a0380831691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60035433600160a060020a03908116911614610d9e57600080fd5b600160a060020a03811660008181526004602052604090819020805460ff191690557f15400faee0685fef808caec11b71577450b8d918f81d7c99f2159d7b4b2d5a10905160405180910390a250565b6000600160a060020a0383161515610e0557600080fd5b600160a060020a038416600090815260016020526040902054821115610e2a57600080fd5b600160a060020a0380851660009081526002602090815260408083203390941683529290522054821115610e5d57600080fd5b600160a060020a038416600090815260016020526040902054610e86908363ffffffff610f7016565b600160a060020a038086166000908152600160205260408082209390935590851681522054610ebb908363ffffffff610f8216565b600160a060020a03808516600090815260016020908152604080832094909455878316825260028152838220339093168252919091522054610f03908363ffffffff610f7016565b600160a060020a03808616600081815260026020908152604080832033861684529091529081902093909355908516917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060019392505050565b600082821115610f7c57fe5b50900390565b600082820183811015610b9c57fe5b6000600160a060020a0383161515610fa857600080fd5b600160a060020a033316600090815260016020526040902054821115610fcd57600080fd5b600160a060020a033316600090815260016020526040902054610ff6908363ffffffff610f7016565b600160a060020a03338116600090815260016020526040808220939093559085168152205461102b908363ffffffff610f8216565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a3506001929150505600a165627a7a72305820802c6c132876f80a8ef157de777d6fad51a2e601110d4ee5148d13395e5d0d730029";

    protected ENGT(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ENGT(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<PoolRoleGrantedEventResponse> getPoolRoleGrantedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PoolRoleGranted", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<PoolRoleGrantedEventResponse> responses = new ArrayList<PoolRoleGrantedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PoolRoleGrantedEventResponse typedResponse = new PoolRoleGrantedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PoolRoleGrantedEventResponse> poolRoleGrantedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PoolRoleGranted", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PoolRoleGrantedEventResponse>() {
            @Override
            public PoolRoleGrantedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                PoolRoleGrantedEventResponse typedResponse = new PoolRoleGrantedEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<PoolRoleRevokedEventResponse> getPoolRoleRevokedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PoolRoleRevoked", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<PoolRoleRevokedEventResponse> responses = new ArrayList<PoolRoleRevokedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PoolRoleRevokedEventResponse typedResponse = new PoolRoleRevokedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PoolRoleRevokedEventResponse> poolRoleRevokedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PoolRoleRevoked", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PoolRoleRevokedEventResponse>() {
            @Override
            public PoolRoleRevokedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                PoolRoleRevokedEventResponse typedResponse = new PoolRoleRevokedEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Mint", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<MintEventResponse> mintEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Mint", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintEventResponse>() {
            @Override
            public MintEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                MintEventResponse typedResponse = new MintEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<MintFinishedEventResponse> getMintFinishedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("MintFinished", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<MintFinishedEventResponse> responses = new ArrayList<MintFinishedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("MintFinished", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintFinishedEventResponse>() {
            @Override
            public MintFinishedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Burn", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.burner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Burn", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse.burner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("OwnershipTransferred", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("OwnershipTransferred", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
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

    public RemoteCall<Boolean> mintingFinished() {
        final Function function = new Function("mintingFinished", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> name() {
        final Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                "approve", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> multiTransfer(List<String> recipients, List<BigInteger> amounts) {
        final Function function = new Function(
                "multiTransfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(recipients, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(amounts, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> INITIAL_SUPPLY() {
        final Function function = new Function("INITIAL_SUPPLY", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        final Function function = new Function(
                "burn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> decreaseApproval(String _spender, BigInteger _subtractedValue) {
        final Function function = new Function(
                "decreaseApproval", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_subtractedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> finishMinting() {
        final Function function = new Function(
                "finishMinting", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> hasPoolRole(String addr) {
        final Function function = new Function("hasPoolRole", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> reclaimEther() {
        final Function function = new Function(
                "reclaimEther", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mint(BigInteger _amount) {
        final Function function = new Function(
                "mint", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> increaseApproval(String _spender, BigInteger _addedValue) {
        final Function function = new Function(
                "increaseApproval", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_addedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function("allowance", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> grantPoolRole(String addr) {
        final Function function = new Function(
                "grantPoolRole", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> FREEZE_END() {
        final Function function = new Function("FREEZE_END", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                "transferOwnership", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> revokePoolRole(String addr) {
        final Function function = new Function(
                "revokePoolRole", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<ENGT> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ENGT.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<ENGT> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ENGT.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static ENGT load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ENGT(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ENGT load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ENGT(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class PoolRoleGrantedEventResponse {
        public Log log;

        public String addr;
    }

    public static class PoolRoleRevokedEventResponse {
        public Log log;

        public String addr;
    }

    public static class MintEventResponse {
        public Log log;

        public String to;

        public BigInteger amount;
    }

    public static class MintFinishedEventResponse {
        public Log log;
    }

    public static class BurnEventResponse {
        public Log log;

        public String burner;

        public BigInteger value;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String previousOwner;

        public String newOwner;
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
