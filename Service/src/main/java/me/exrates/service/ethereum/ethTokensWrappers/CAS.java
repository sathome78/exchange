package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
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
public class CAS extends Contract implements ethTokenERC20 {
    private static final String BINARY = "60606040526009805460ff1916905534156200001a57600080fd5b6040516200165238038062001652833981016040528080519190602001805182019190602001805182019190602001805191906020018051919060200180519150505b855b60038054600160a060020a031916600160a060020a0383161790555b50600685805162000091929160200190620000dc565b506007848051620000a7929160200190620000dc565b5060008381556008839055600160a060020a0387168152600160205260409020839055600a8190555b50505050505062000186565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200011f57805160ff19168380011785556200014f565b828001600101855582156200014f579182015b828111156200014f57825182559160200191906001019062000132565b5b506200015e92915062000162565b5090565b6200018391905b808211156200015e576000815560010162000169565b5090565b90565b6114bc80620001966000396000f300606060405236156101515763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde038114610156578063095ea7b3146101e157806318160ddd1461021757806323b872dd1461023c578063313ce5671461027857806342966c681461029d57806345977d03146102b55780634eee966f146102cd5780635de4ccb0146103625780635f412d4f14610391578063600440cb146103a657806366188463146103d55780636748a0c61461040b57806370a08231146104305780638444b3911461046157806395d89b411461049857806396132521146105235780639738968c1461054a578063a9059cbb14610571578063c752ff62146105a7578063d73dd623146105cc578063d7e7088a14610602578063dd62ed3e14610623578063eefa597b1461054a578063fccc281314610681578063ffeb7d75146106b0575b600080fd5b341561016157600080fd5b6101696106d1565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101a65780820151818401525b60200161018d565b50505050905090810190601f1680156101d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101ec57600080fd5b610203600160a060020a036004351660243561076f565b604051901515815260200160405180910390f35b341561022257600080fd5b61022a6107dc565b60405190815260200160405180910390f35b341561024757600080fd5b610203600160a060020a03600435811690602435166044356107e2565b604051901515815260200160405180910390f35b341561028357600080fd5b61022a61090e565b60405190815260200160405180910390f35b34156102a857600080fd5b6102b3600435610914565b005b34156102c057600080fd5b6102b36004356109f0565b005b34156102d857600080fd5b6102b360046024813581810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f016020809104026020016040519081016040528181529291906020840183838082843750949650610b7595505050505050565b005b341561036d57600080fd5b610375610d29565b604051600160a060020a03909116815260200160405180910390f35b341561039c57600080fd5b6102b3610d38565b005b34156103b157600080fd5b610375610d63565b604051600160a060020a03909116815260200160405180910390f35b34156103e057600080fd5b610203600160a060020a0360043516602435610d72565b604051901515815260200160405180910390f35b341561041657600080fd5b61022a610e6e565b60405190815260200160405180910390f35b341561043b57600080fd5b61022a600160a060020a0360043516610e74565b60405190815260200160405180910390f35b341561046c57600080fd5b610474610e93565b6040518082600481111561048457fe5b60ff16815260200191505060405180910390f35b34156104a357600080fd5b610169610ee0565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101a65780820151818401525b60200161018d565b50505050905090810190601f1680156101d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561052e57600080fd5b610203610f7e565b604051901515815260200160405180910390f35b341561055557600080fd5b610203610f87565b604051901515815260200160405180910390f35b341561057c57600080fd5b610203600160a060020a0360043516602435610f8d565b604051901515815260200160405180910390f35b34156105b257600080fd5b61022a610fbe565b60405190815260200160405180910390f35b34156105d757600080fd5b610203600160a060020a0360043516602435610fc4565b604051901515815260200160405180910390f35b341561060d57600080fd5b6102b3600160a060020a0360043516611069565b005b341561062e57600080fd5b61022a600160a060020a0360043581169060243516611254565b60405190815260200160405180910390f35b341561055557600080fd5b610203610f87565b604051901515815260200160405180910390f35b341561068c57600080fd5b610375611287565b604051600160a060020a03909116815260200160405180910390f35b34156106bb57600080fd5b6102b3600160a060020a036004351661128c565b005b60068054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107675780601f1061073c57610100808354040283529160200191610767565b820191906000526020600020905b81548152906001019060200180831161074a57829003601f168201915b505050505081565b600160a060020a03338116600081815260026020908152604080832094871680845294909152808220859055909291907f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a35060015b92915050565b60005481565b600080600160a060020a03841615156107fa57600080fd5b50600160a060020a03808516600081815260026020908152604080832033909516835293815283822054928252600190529190912054610840908463ffffffff6112e816565b600160a060020a038087166000908152600160205260408082209390935590861681522054610875908463ffffffff6112ff16565b600160a060020a03851660009081526001602052604090205561089e818463ffffffff6112e816565b600160a060020a03808716600081815260026020908152604080832033861684529091529081902093909355908616917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9086905190815260200160405180910390a3600191505b509392505050565b60085481565b33600160a060020a03811660009081526001602052604090205461093890836112e8565b600160a060020a03821660009081526001602052604081209190915554610965908363ffffffff6112e816565b6000557f696de425f79f4a40bc6d2122ca50507f0efbeabbff86a84871b7196ab8ea8df78183604051600160a060020a03909216825260208201526040908101905180910390a16000600160a060020a0382167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8460405190815260200160405180910390a35b5050565b60006109fa610e93565b905060035b816004811115610a0b57fe5b1480610a23575060045b816004811115610a2157fe5b145b1515610a2e57600080fd5b811515610a3a57600080fd5b600160a060020a033316600090815260016020526040902054610a63908363ffffffff6112e816565b600160a060020a03331660009081526001602052604081209190915554610a90908363ffffffff6112e816565b600055600554610aa6908363ffffffff6112ff16565b600555600454600160a060020a031663753e88e533846040517c010000000000000000000000000000000000000000000000000000000063ffffffff8516028152600160a060020a0390921660048301526024820152604401600060405180830381600087803b1515610b1857600080fd5b6102c65a03f11515610b2957600080fd5b5050600454600160a060020a03908116915033167f7e5c344a8141a805725cb476f76c6953b842222b967edd1f78ddb6e8b3f397ac8460405190815260200160405180910390a35b5050565b60035433600160a060020a03908116911614610b9057600080fd5b600060068054600181600116156101000203166002900490501180610bcb575060006007805460018160011615610100020316600290049050115b15610bd557600080fd5b6006828051610be89291602001906113f0565b506007818051610bfc9291602001906113f0565b507fd131ab1e6f279deea74e13a18477e13e2107deb6dc8ae955648948be5841fb4660066007604051604080825283546002600019610100600184161502019091160490820181905281906020820190606083019086908015610ca05780601f10610c7557610100808354040283529160200191610ca0565b820191906000526020600020905b815481529060010190602001808311610c8357829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015610d145780601f10610ce957610100808354040283529160200191610d14565b820191906000526020600020905b815481529060010190602001808311610cf757829003601f168201915b505094505050505060405180910390a15b5050565b600454600160a060020a031681565b60035433600160a060020a03908116911614610d5357600080fd5b6009805460ff191660011790555b565b600354600160a060020a031681565b600160a060020a03338116600090815260026020908152604080832093861683529290529081205480831115610dcf57600160a060020a033381166000908152600260209081526040808320938816835292905290812055610e06565b610ddf818463ffffffff6112e816565b600160a060020a033381166000908152600260209081526040808320938916835292905220555b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020547f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925915190815260200160405180910390a3600191505b5092915050565b600a5481565b600160a060020a0381166000908152600160205260409020545b919050565b6000610e9d610f87565b1515610eab57506001610eda565b600454600160a060020a03161515610ec557506002610eda565b6005541515610ed657506003610eda565b5060045b5b5b5b90565b60078054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107675780601f1061073c57610100808354040283529160200191610767565b820191906000526020600020905b81548152906001019060200180831161074a57829003601f168201915b505050505081565b60095460ff1681565b60015b90565b6000600a54421115610faa5760095460ff161515610faa57600080fd5b5b610fb58383611319565b90505b92915050565b60055481565b600160a060020a033381166000908152600260209081526040808320938616835292905290812054610ffc908363ffffffff6112ff16565b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020849055919290917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591905190815260200160405180910390a35060015b92915050565b611071610f87565b151561107c57600080fd5b600160a060020a038116151561109157600080fd5b60035433600160a060020a039081169116146110ac57600080fd5b60045b6110b7610e93565b60048111156110c257fe5b14156110cd57600080fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038381169190911791829055166361d3d7a66000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b151561115157600080fd5b6102c65a03f1151561116257600080fd5b50505060405180519050151561117757600080fd5b600080546004549091600160a060020a0390911690634b2ba0dd90604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b15156111e057600080fd5b6102c65a03f115156111f157600080fd5b5050506040518051905014151561120757600080fd5b6004547f7845d5aa74cc410e35571258d954f23b82276e160fe8c188fa80566580f279cc90600160a060020a0316604051600160a060020a03909116815260200160405180910390a15b50565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b60015b90565b600081565b600160a060020a03811615156112a157600080fd5b60035433600160a060020a039081169116146112bc57600080fd5b6003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b50565b6000828211156112f457fe5b508082035b92915050565b60008282018381101561130e57fe5b8091505b5092915050565b6000600160a060020a038316151561133057600080fd5b600160a060020a033316600090815260016020526040902054611359908363ffffffff6112e816565b600160a060020a03338116600090815260016020526040808220939093559085168152205461138e908363ffffffff6112ff16565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060015b92915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061143157805160ff191683800117855561145e565b8280016001018555821561145e579182015b8281111561145e578251825591602001919060010190611443565b5b5061146b92915061146f565b5090565b610eda91905b8082111561146b5760008155600101611475565b5090565b905600a165627a7a723058204efbd132c7fdb212c5c4d7e19c27bd7cc02e84b17e951816e2b32c05d61ef9e700290000000000000000000000006efd5665ab4b345a7ebe63c679b651f375dddb7e00000000000000000000000000000000000000000000000000000000000000c000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000033b2e3c9fd0803ce80000000000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000005b117b900000000000000000000000000000000000000000000000000000000000000006436173686161000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000034341530000000000000000000000000000000000000000000000000000000000";

    protected CAS(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CAS(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<UpdatedTokenInformationEventResponse> getUpdatedTokenInformationEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("UpdatedTokenInformation", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<UpdatedTokenInformationEventResponse> responses = new ArrayList<UpdatedTokenInformationEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpdatedTokenInformationEventResponse typedResponse = new UpdatedTokenInformationEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.newName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newSymbol = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UpdatedTokenInformationEventResponse> updatedTokenInformationEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("UpdatedTokenInformation", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpdatedTokenInformationEventResponse>() {
            @Override
            public UpdatedTokenInformationEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                UpdatedTokenInformationEventResponse typedResponse = new UpdatedTokenInformationEventResponse();
                typedResponse.log = log;
                typedResponse.newName = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newSymbol = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<UpgradeEventResponse> getUpgradeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Upgrade", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<UpgradeEventResponse> responses = new ArrayList<UpgradeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpgradeEventResponse typedResponse = new UpgradeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UpgradeEventResponse> upgradeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Upgrade", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpgradeEventResponse>() {
            @Override
            public UpgradeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                UpgradeEventResponse typedResponse = new UpgradeEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<UpgradeAgentSetEventResponse> getUpgradeAgentSetEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("UpgradeAgentSet", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<UpgradeAgentSetEventResponse> responses = new ArrayList<UpgradeAgentSetEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpgradeAgentSetEventResponse typedResponse = new UpgradeAgentSetEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.agent = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UpgradeAgentSetEventResponse> upgradeAgentSetEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("UpgradeAgentSet", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpgradeAgentSetEventResponse>() {
            @Override
            public UpgradeAgentSetEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                UpgradeAgentSetEventResponse typedResponse = new UpgradeAgentSetEventResponse();
                typedResponse.log = log;
                typedResponse.agent = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<BurnedEventResponse> getBurnedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Burned", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<BurnedEventResponse> responses = new ArrayList<BurnedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnedEventResponse typedResponse = new BurnedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.burner = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.burnedAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnedEventResponse> burnedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Burned", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnedEventResponse>() {
            @Override
            public BurnedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                BurnedEventResponse typedResponse = new BurnedEventResponse();
                typedResponse.log = log;
                typedResponse.burner = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.burnedAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
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

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger burnAmount) {
        final Function function = new Function(
                "burn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(burnAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> upgrade(BigInteger value) {
        final Function function = new Function(
                "upgrade", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setTokenInformation(String _name, String _symbol) {
        final Function function = new Function(
                "setTokenInformation", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> upgradeAgent() {
        final Function function = new Function("upgradeAgent", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> releaseTokenTransfer() {
        final Function function = new Function(
                "releaseTokenTransfer", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> upgradeMaster() {
        final Function function = new Function("upgradeMaster", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> decreaseApproval(String _spender, BigInteger _subtractedValue) {
        final Function function = new Function(
                "decreaseApproval", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_subtractedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> releaseFinalizationDate() {
        final Function function = new Function("releaseFinalizationDate", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getUpgradeState() {
        final Function function = new Function("getUpgradeState", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> released() {
        final Function function = new Function("released", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Boolean> canUpgrade() {
        final Function function = new Function("canUpgrade", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalUpgraded() {
        final Function function = new Function("totalUpgraded", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> increaseApproval(String _spender, BigInteger _addedValue) {
        final Function function = new Function(
                "increaseApproval", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_addedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setUpgradeAgent(String agent) {
        final Function function = new Function(
                "setUpgradeAgent", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(agent)), 
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

    public RemoteCall<Boolean> isToken() {
        final Function function = new Function("isToken", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> BURN_ADDRESS() {
        final Function function = new Function("BURN_ADDRESS", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> setUpgradeMaster(String master) {
        final Function function = new Function(
                "setUpgradeMaster", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(master)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<CAS> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _owner, String _name, String _symbol, BigInteger _totalSupply, BigInteger _decimals, BigInteger _releaseFinalizationDate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_totalSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decimals), 
                new org.web3j.abi.datatypes.generated.Uint256(_releaseFinalizationDate)));
        return deployRemoteCall(CAS.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<CAS> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _owner, String _name, String _symbol, BigInteger _totalSupply, BigInteger _decimals, BigInteger _releaseFinalizationDate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_totalSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decimals), 
                new org.web3j.abi.datatypes.generated.Uint256(_releaseFinalizationDate)));
        return deployRemoteCall(CAS.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static CAS load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CAS(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static CAS load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CAS(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class UpdatedTokenInformationEventResponse {
        public Log log;

        public String newName;

        public String newSymbol;
    }

    public static class UpgradeEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class UpgradeAgentSetEventResponse {
        public Log log;

        public String agent;
    }

    public static class BurnedEventResponse {
        public Log log;

        public String burner;

        public BigInteger burnedAmount;
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
