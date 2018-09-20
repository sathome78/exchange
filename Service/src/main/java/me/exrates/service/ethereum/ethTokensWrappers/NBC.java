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
public class NBC extends Contract implements ethTokenERC20{
    private static final String BINARY = "60606040526005805460a060020a60ff02191690556007805460ff1916905534156200002a57600080fd5b60405162001e0a38038062001e0a8339810160405280805182019190602001805182019190602001805191906020018051919060200180519150505b335b5b60038054600160a060020a03191633600160a060020a03161790555b60098054600160a060020a031916600160a060020a0383161790555b5060038054600160a060020a03191633600160a060020a0316179055600c858051620000d2929160200190620001a4565b50600d848051620000e8929160200190620001a4565b506000838155600e805460ff191660ff8516179055600354600160a060020a0316815260016020526040812084905583111562000173576003546000547f30385c845b448a36257a6a1716e6ad2e1bc2cbe333cde1e69fe849ad6511adfe91600160a060020a031690604051600160a060020a03909216825260208201526040908101905180910390a15b80151562000197576007805460ff1916600117905560005415156200019757600080fd5b5b5b50505050506200024e565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620001e757805160ff191683800117855562000217565b8280016001018555821562000217579182015b8281111562000217578251825591602001919060010190620001fa565b5b50620002269291506200022a565b5090565b6200024b91905b8082111562000226576000815560010162000231565b5090565b90565b611bac806200025e6000396000f300606060405236156101c75763ffffffff60e060020a60003504166302f652a381146101cc57806305d2035b146101f257806306fdde0314610219578063095ea7b3146102a457806318160ddd146102da57806323b872dd146102ff57806329ff4f531461033b578063313ce5671461035c5780633c58795d1461038557806340c10f19146103b857806342c1867b146103dc578063432146751461040f57806345977d03146104355780634eee966f1461044d5780635de4ccb0146104e25780635f412d4f14610511578063600440cb1461052657806370a082311461055557806379ba5097146105865780637aa86e2f1461059b5780638444b391146105c2578063867c2857146105f95780638da5cb5b1461062c57806395d89b411461065b57806396132521146106e65780639738968c1461070d578063a293d1e814610734578063a9059cbb1461075f578063b9599f3a14610795578063c752ff62146107aa578063d05c78da146107cf578063d1f276d3146107fa578063d4ee1d9014610829578063d7e7088a14610858578063dd62ed3e14610879578063e4a2c6d6146108b0578063e6cb9013146108ba578063f2fde38b146108e5578063ffeb7d7514610906575b600080fd5b34156101d757600080fd5b6101f0600160a060020a03600435166024351515610927565b005b34156101fd57600080fd5b610205610988565b604051901515815260200160405180910390f35b341561022457600080fd5b61022c610991565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156102695780820151818401525b602001610250565b50505050905090810190601f1680156102965780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156102af57600080fd5b610205600160a060020a0360043516602435610a2f565b604051901515815260200160405180910390f35b34156102e557600080fd5b6102ed610ad8565b60405190815260200160405180910390f35b341561030a57600080fd5b610205600160a060020a0360043581169060243516604435610ade565b604051901515815260200160405180910390f35b341561034657600080fd5b6101f0600160a060020a0360043516610b35565b005b341561036757600080fd5b61036f610b99565b60405160ff909116815260200160405180910390f35b341561039057600080fd5b610205600160a060020a0360043516610ba2565b604051901515815260200160405180910390f35b34156103c357600080fd5b6101f0600160a060020a0360043516602435610bb7565b005b34156103e757600080fd5b610205600160a060020a0360043516610c78565b604051901515815260200160405180910390f35b341561041a57600080fd5b6101f0600160a060020a03600435166024351515610c8d565b005b341561044057600080fd5b6101f0600435610d2c565b005b341561045857600080fd5b6101f060046024813581810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f016020809104026020016040519081016040528181529291906020840183838082843750949650610e8695505050505050565b005b34156104ed57600080fd5b6104f5610ff6565b604051600160a060020a03909116815260200160405180910390f35b341561051c57600080fd5b6101f0611005565b005b341561053157600080fd5b6104f5611039565b604051600160a060020a03909116815260200160405180910390f35b341561056057600080fd5b6102ed600160a060020a0360043516611048565b60405190815260200160405180910390f35b341561059157600080fd5b6101f0611067565b005b34156105a657600080fd5b6102056110f3565b604051901515815260200160405180910390f35b34156105cd57600080fd5b6105d5611248565b604051808260048111156105e557fe5b60ff16815260200191505060405180910390f35b341561060457600080fd5b610205600160a060020a0360043516611295565b604051901515815260200160405180910390f35b341561063757600080fd5b6104f56112aa565b604051600160a060020a03909116815260200160405180910390f35b341561066657600080fd5b61022c6112b9565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156102695780820151818401525b602001610250565b50505050905090810190601f1680156102965780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156106f157600080fd5b610205611357565b604051901515815260200160405180910390f35b341561071857600080fd5b610205611367565b604051901515815260200160405180910390f35b341561073f57600080fd5b6102ed60043560243561138d565b60405190815260200160405180910390f35b341561076a57600080fd5b610205600160a060020a03600435166024356113a4565b604051901515815260200160405180910390f35b34156107a057600080fd5b6101f06113f9565b005b34156107b557600080fd5b6102ed61146f565b60405190815260200160405180910390f35b34156107da57600080fd5b6102ed600435602435611475565b60405190815260200160405180910390f35b341561080557600080fd5b6104f56114a4565b604051600160a060020a03909116815260200160405180910390f35b341561083457600080fd5b6104f56114b3565b604051600160a060020a03909116815260200160405180910390f35b341561086357600080fd5b6101f0600160a060020a03600435166114c2565b005b341561088457600080fd5b6102ed600160a060020a036004358116906024351661167b565b60405190815260200160405180910390f35b6101f06116a8565b005b34156108c557600080fd5b6102ed600435602435611758565b60405190815260200160405180910390f35b34156108f057600080fd5b6101f0600160a060020a0360043516611772565b005b341561091157600080fd5b6101f0600160a060020a03600435166117ba565b005b60035433600160a060020a0390811691161461094257600080fd5b60055460009060a060020a900460ff161561095c57600080fd5b600160a060020a0383166000908152600660205260409020805460ff19168315151790555b5b505b5050565b60075460ff1681565b600c8054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a275780601f106109fc57610100808354040283529160200191610a27565b820191906000526020600020905b815481529060010190602001808311610a0a57829003601f168201915b505050505081565b60008115801590610a645750600160a060020a0333811660009081526002602090815260408083209387168352929052205415155b15610a6e57600080fd5b600160a060020a03338116600081815260026020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a35060015b92915050565b60005481565b600554600090849060a060020a900460ff161515610b1d57600160a060020a03811660009081526006602052604090205460ff161515610b1d57600080fd5b5b610b29858585611816565b91505b5b509392505050565b60035433600160a060020a03908116911614610b5057600080fd5b60055460009060a060020a900460ff1615610b6a57600080fd5b6005805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0384161790555b5b505b50565b600e5460ff1681565b60136020526000908152604090205460ff1681565b600160a060020a03331660009081526008602052604090205460ff161515610bde57600080fd5b60075460ff1615610bee57600080fd5b610bfa60005482611758565b6000908155600160a060020a038316815260016020526040902054610c1f9082611758565b600160a060020a0383166000818152600160205260408082209390935590917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9084905190815260200160405180910390a35b5b5b5050565b60086020526000908152604090205460ff1681565b60035433600160a060020a03908116911614610ca857600080fd5b60075460ff1615610cb857600080fd5b600160a060020a03821660009081526008602052604090819020805460ff19168315151790557f4b0adf6c802794c7dde28a08a4e07131abcff3bf9603cd71f14f90bec7865efa908390839051600160a060020a039092168252151560208201526040908101905180910390a15b5b5b5050565b6000610d36611248565b905060035b816004811115610d4757fe5b1480610d5f575060045b816004811115610d5d57fe5b145b1515610d6a57600080fd5b811515610d7657600080fd5b600160a060020a033316600090815260016020526040902054610d99908361138d565b600160a060020a03331660009081526001602052604081209190915554610dc0908361138d565b600055600b54610dd09083611758565b600b55600a54600160a060020a031663753e88e5338460405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401600060405180830381600087803b1515610e2957600080fd5b6102c65a03f11515610e3a57600080fd5b5050600a54600160a060020a03908116915033167f7e5c344a8141a805725cb476f76c6953b842222b967edd1f78ddb6e8b3f397ac8460405190815260200160405180910390a35b5050565b60035433600160a060020a03908116911614610ea157600080fd5b600c828051610eb4929160200190611ae0565b50600d818051610ec8929160200190611ae0565b507fd131ab1e6f279deea74e13a18477e13e2107deb6dc8ae955648948be5841fb46600c600d604051604080825283546002600019610100600184161502019091160490820181905281906020820190606083019086908015610f6c5780601f10610f4157610100808354040283529160200191610f6c565b820191906000526020600020905b815481529060010190602001808311610f4f57829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015610fe05780601f10610fb557610100808354040283529160200191610fe0565b820191906000526020600020905b815481529060010190602001808311610fc357829003601f168201915b505094505050505060405180910390a15b5b5050565b600a54600160a060020a031681565b60055433600160a060020a0390811691161461102057600080fd5b6007805460ff19166001179055611035611983565b5b5b565b600954600160a060020a031681565b600160a060020a0381166000908152600160205260409020545b919050565b60045433600160a060020a0390811691161461108257600080fd5b600454600354600160a060020a0391821691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36004546003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911790555b565b6010546000908190819060ff16151561110b57600080fd5b600160a060020a03331660009081526013602052604090205460ff161561113157600080fd5b61113a33611048565b915081151561114857600080fd5b600054611157600f5484611475565b81151561116057fe5b0490505a81101561117057600080fd5b61117c6012548261138d565b601255600160a060020a0333166000908152601360205260409020805460ff191660011790556011546111af9082611758565b601155601254600090116111cd576010805460ff191690556000600f555b600160a060020a03331681156108fc0282604051600060405180830381858888f1935050505015156111fe57600080fd5b7f63f0ae2ed588612fe02f984568cec25398c29f3830fadedffdf78ea0bfa733c73382604051600160a060020a03909216825260208201526040908101905180910390a15b505090565b6000611252611367565b15156112605750600161128f565b600a54600160a060020a0316151561127a5750600261128f565b600b54151561128b5750600361128f565b5060045b5b5b5b90565b60066020526000908152604090205460ff1681565b600354600160a060020a031681565b600d8054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a275780601f106109fc57610100808354040283529160200191610a27565b820191906000526020600020905b815481529060010190602001808311610a0a57829003601f168201915b505050505081565b60055460a060020a900460ff1681565b60055460009060a060020a900460ff16801561138657506113866119c6565b5b90505b90565b60008282111561139957fe5b508082035b92915050565b600554600090339060a060020a900460ff1615156113e357600160a060020a03811660009081526006602052604090205460ff1615156113e357600080fd5b5b6113ee84846119cc565b91505b5b5092915050565b60035433600160a060020a0390811691161461141457600080fd5b601254151561142257600080fd5b33600160a060020a03166108fc6012549081150290604051600060405180830381858888f19350505050151561145757600080fd5b600060128190556010805460ff19169055600f555b5b565b600b5481565b6000828202831580611491575082848281151561148e57fe5b04145b151561149957fe5b8091505b5092915050565b600554600160a060020a031681565b600454600160a060020a031681565b6114ca611367565b15156114d557600080fd5b600160a060020a03811615156114ea57600080fd5b60095433600160a060020a0390811691161461150557600080fd5b60045b611510611248565b600481111561151b57fe5b141561152657600080fd5b600a805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038381169190911791829055166361d3d7a66000604051602001526040518163ffffffff1660e060020a028152600401602060405180830381600087803b151561159157600080fd5b6102c65a03f115156115a257600080fd5b5050506040518051905015156115b757600080fd5b60008054600a549091600160a060020a0390911690634b2ba0dd90604051602001526040518163ffffffff1660e060020a028152600401602060405180830381600087803b151561160757600080fd5b6102c65a03f1151561161857600080fd5b5050506040518051905014151561162e57600080fd5b600a547f7845d5aa74cc410e35571258d954f23b82276e160fe8c188fa80566580f279cc90600160a060020a0316604051600160a060020a03909116815260200160405180910390a15b50565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b60035433600160a060020a039081169116146116c357600080fd5b60055460a060020a900460ff1615156116db57600080fd5b60105460ff16156116eb57600080fd5b3415156116f757600080fd5b34600f81905560128190556010805460ff191660011790557f9a9ed624dc1e4945e50d877ee48b1c2adf04bc62ec524795ec96add90b646f9c903390604051600160a060020a03909216825260208201526040908101905180910390a15b5b565b60008282018381101561149957fe5b8091505b5092915050565b60035433600160a060020a0390811691161461178d57600080fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b50565b600160a060020a03811615156117cf57600080fd5b60095433600160a060020a039081169116146117ea57600080fd5b6009805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b50565b600160a060020a03808416600081815260026020908152604080832033909516835293815283822054928252600190529182205483901080159061185a5750828110155b80156118665750600083115b801561188b5750600160a060020a038416600090815260016020526040902054838101115b1561197157600160a060020a0384166000908152600160205260409020546118b39084611758565b600160a060020a0380861660009081526001602052604080822093909355908716815220546118e2908461138d565b600160a060020a038616600090815260016020526040902055611905818461138d565b600160a060020a03808716600081815260026020908152604080832033861684529091529081902093909355908616917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9086905190815260200160405180910390a360019150610b2c565b60009150610b2c565b5b509392505050565b60055433600160a060020a0390811691161461199e57600080fd5b6005805474ff0000000000000000000000000000000000000000191660a060020a1790555b5b565b60015b90565b600160a060020a0333166000908152600160205260408120548290108015906119f55750600082115b8015611a1a5750600160a060020a038316600090815260016020526040902054828101115b15611ad157600160a060020a033316600090815260016020526040902054611a42908361138d565b600160a060020a033381166000908152600160205260408082209390935590851681522054611a719083611758565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a3506001610ad2565b506000610ad2565b5b92915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10611b2157805160ff1916838001178555611b4e565b82800160010185558215611b4e579182015b82811115611b4e578251825591602001919060010190611b33565b5b50611b5b929150611b5f565b5090565b61128f91905b80821115611b5b5760008155600101611b65565b5090565b905600a165627a7a72305820d49152ba4f0477c7324980892837ee417059897d257ce9c80731c7558da084a3002900000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000074e696f6269756d0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000034e42430000000000000000000000000000000000000000000000000000000000";

    protected NBC(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NBC(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public List<ProfitDeliveredEventResponse> getProfitDeliveredEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ProfitDelivered", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ProfitDeliveredEventResponse> responses = new ArrayList<ProfitDeliveredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ProfitDeliveredEventResponse typedResponse = new ProfitDeliveredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.fetcher = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ProfitDeliveredEventResponse> profitDeliveredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ProfitDelivered", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ProfitDeliveredEventResponse>() {
            @Override
            public ProfitDeliveredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ProfitDeliveredEventResponse typedResponse = new ProfitDeliveredEventResponse();
                typedResponse.log = log;
                typedResponse.fetcher = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<ProfitLoadedEventResponse> getProfitLoadedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ProfitLoaded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ProfitLoadedEventResponse> responses = new ArrayList<ProfitLoadedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ProfitLoadedEventResponse typedResponse = new ProfitLoadedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ProfitLoadedEventResponse> profitLoadedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ProfitLoaded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ProfitLoadedEventResponse>() {
            @Override
            public ProfitLoadedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ProfitLoadedEventResponse typedResponse = new ProfitLoadedEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
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

    public List<MintingAgentChangedEventResponse> getMintingAgentChangedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("MintingAgentChanged", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<MintingAgentChangedEventResponse> responses = new ArrayList<MintingAgentChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintingAgentChangedEventResponse typedResponse = new MintingAgentChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.state = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintingAgentChangedEventResponse> mintingAgentChangedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("MintingAgentChanged", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintingAgentChangedEventResponse>() {
            @Override
            public MintingAgentChangedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                MintingAgentChangedEventResponse typedResponse = new MintingAgentChangedEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.state = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
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
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
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
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<MintedEventResponse> getMintedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Minted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<MintedEventResponse> responses = new ArrayList<MintedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintedEventResponse typedResponse = new MintedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.receiver = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintedEventResponse> mintedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Minted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintedEventResponse>() {
            @Override
            public MintedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                MintedEventResponse typedResponse = new MintedEventResponse();
                typedResponse.log = log;
                typedResponse.receiver = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
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

    public RemoteCall<TransactionReceipt> setTransferAgent(String addr, Boolean state) {
        final Function function = new Function(
                "setTransferAgent", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr), 
                new org.web3j.abi.datatypes.Bool(state)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setReleaseAgent(String addr) {
        final Function function = new Function(
                "setReleaseAgent", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> hasFetchedProfit(String param0) {
        final Function function = new Function("hasFetchedProfit", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> mint(String receiver, BigInteger amount) {
        final Function function = new Function(
                "mint", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(receiver), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> mintAgents(String param0) {
        final Function function = new Function("mintAgents", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> setMintAgent(String addr, Boolean state) {
        final Function function = new Function(
                "setMintAgent", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr), 
                new org.web3j.abi.datatypes.Bool(state)), 
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

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                "acceptOwnership", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> fetchProfit() {
        final Function function = new Function(
                "fetchProfit", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getUpgradeState() {
        final Function function = new Function("getUpgradeState", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> transferAgents(String param0) {
        final Function function = new Function("transferAgents", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteCall<TransactionReceipt> safeSub(BigInteger a, BigInteger b) {
        final Function function = new Function(
                "safeSub", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
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

    public RemoteCall<TransactionReceipt> fetchUndistributedProfit() {
        final Function function = new Function(
                "fetchUndistributedProfit", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalUpgraded() {
        final Function function = new Function("totalUpgraded", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> safeMul(BigInteger a, BigInteger b) {
        final Function function = new Function(
                "safeMul", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> releaseAgent() {
        final Function function = new Function("releaseAgent", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> newOwner() {
        final Function function = new Function("newOwner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteCall<TransactionReceipt> loadProfit(BigInteger weiValue) {
        final Function function = new Function(
                "loadProfit", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> safeAdd(BigInteger a, BigInteger b) {
        final Function function = new Function(
                "safeAdd", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        final Function function = new Function(
                "transferOwnership", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setUpgradeMaster(String master) {
        final Function function = new Function(
                "setUpgradeMaster", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(master)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<NBC> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable)));
        return deployRemoteCall(NBC.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<NBC> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable)));
        return deployRemoteCall(NBC.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static NBC load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NBC(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static NBC load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NBC(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class UpdatedTokenInformationEventResponse {
        public Log log;

        public String newName;

        public String newSymbol;
    }

    public static class ProfitDeliveredEventResponse {
        public Log log;

        public String fetcher;

        public BigInteger profit;
    }

    public static class ProfitLoadedEventResponse {
        public Log log;

        public String owner;

        public BigInteger profit;
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

    public static class MintingAgentChangedEventResponse {
        public Log log;

        public String addr;

        public Boolean state;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String _from;

        public String _to;
    }

    public static class MintedEventResponse {
        public Log log;

        public String receiver;

        public BigInteger amount;
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
