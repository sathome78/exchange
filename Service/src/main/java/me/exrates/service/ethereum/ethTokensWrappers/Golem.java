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
public class Golem extends Contract implements ethTokenNotERC20 {
    private static final String BINARY = "606060408190526002805460ff19166001179055608080610f99833960e06040529051905160a05160c051600160a060020a03841615156100e857610002565b83604051610542806101158339018082600160a060020a03168152602001915050604051809103906000f0801561000257600480546c01000000000000000000000000928302839004600160a060020a03199182161790915560038054868402849004921691909117905560028054868302929092046101000261010060a860020a03199092169190911790556000829055600181905550505050610942806106576000396000f35b600160a060020a03831615156100fd57610002565b43821161010957610002565b81811161003f5761000256606060408190526000600355602080610542833950608060405251600180546c0100000000000000000000000033810204600160a060020a031990911617905562ed4e004201600255600160a060020a0381166000908152602081905260408120614e2090556109c47f2f5182ca3c71fba9448b5797d92da7b954eeaeb24b669d38048e225561035564556102da7f45df8c30765988748331aa685f931bde8d58d486ac2309077e90fd6baaf79e038190557f7ea8a6eced6159c474e51a36ed2fdbeefcbc828cf6c05d46fdd1fad880f548ea8190557fd80aeb97d8ade363914f049101e13bc4c79be147033a8a1fa0e91f954320cfd18190557fd1a1c717e5b5609c72c4991362e166a35ee5521d47496bc9108fc3a6602538008190557fc61bf6c63255306aa3f1daf108b6b962c862b30a9ef9b3111a55cf9a1be9a8d6556102767fe4359dc5a3244eba9cfcf67269b2f0a0a57c214e4cb82f9b6912def299f047528190557fd933b2425e5345f54b48182cee8f889a1054dce093c93ee4a944524ad0af59c48190557ffbefcf091d9562d672e0fdc71891d2d3803db16d6743ea8a23fcfef88796a09d8190557fe5b2b0da35bec8da153886157cd88779fbd6472c27b66913d5c69e1445aa806f556101367f085c12f836c258aa880654d07c75055519a771d8386caee004632ed5afcbe9d155608a7f3a722b2d2783e2bd2f725b60f80f56ff256a418ebecca715894d57d5feb689715560877fd1c40006b594ba5b504908c67a40189432057d2394f1773ab7774bd3123bca545560647f0eef056c83f1149e261cf3b61d9e20a9e0c5f64ada6748701e1b27d1de158ce38190557f7070adbdb8f2d4019cc852cc26afe358144bc7fbe990d77bc637f5c7e722c41c8190557f581488c6d9a945dc9b0fd448f74525266c0b506653a1d4c8fbb199bb557cf58e8190557f943f08fbd86d9c14937cccb5045806305960ee93608df8764c86f5364a3102335560467fb6bd8179f2fe2847ab9692c0c2e093d2f25ee7f1808ba3b0e7d52c98fa7d9f558190557fb63e17ea19e0eaf429d63bb96f3600c9aca74fbce87db89dc7bc4577fc7683b78190557f502063b8af6f4e9d3f416f8166f86c7f3c6c0abe8692397a61182ba8dca7c1208190557fe7e3a4e3c5fd8cb5848bc5c4fe5e5566299670c2bf4adffd95984be3e33a61df55602a7f664cea02e91f34521fafc42bfb9f9365ac4cd7ca869351e22c8d1ed75d54157f5573d0af9f75ea618163944585bf56aca98204d0ab66905260197f0d038efd35c79f794bca2eb26019390c0b2b1975cb3389c23e540ef5ea1670275550610177806103cb6000396000f3606060405260e060020a6000350463a69df4b5811461001e575b610002565b346100025761003c6000600060026000505442101561004257610002565b005b5050565b60035415156100c357600160009054906101000a9004600160a060020a0316600160a060020a03166370a08231306000604051602001526040518260e060020a0281526004018082600160a060020a03168152602001915050602060405180830381600087803b156100025760325a03f11561000257505060405151600355505b600160a060020a03331660009081526020819052604081208054919055600354909250617530908302049050600160009054906101000a9004600160a060020a0316600160a060020a031663a9059cbb33836000604051602001526040518360e060020a0281526004018083600160a060020a0316815260200182815260200192505050602060405180830381600087803b156100025760325a03f115610002575050604051511515905061003e5761000256606060405236156101065760e060020a600035046306fdde03811461010b578063162229501461014a57806318160ddd1461016757806326316e5814610182578063313ce567146101a8578063454b0608146101b55780634bb278f3146101d0578063590e1ae3146101ee578063676d2e621461020c5780636f7920fd1461022357806370a082311461023b57806375e2ff65146102605780638328dbcd1461027b57806391b43d131461029257806395a0f5eb146102a057806395d89b41146102ae578063a9059cbb146102ed578063c039daf614610310578063cb4c86b714610327578063cf8d652c14610338578063d648a64714610346578063efc81a8c14610354575b610002565b346100025761036b60408051808201909152601381527f476f6c656d204e6574776f726b20546f6b656e00000000000000000000000000602082015281565b34610002576103d9600254600160a060020a036101009091041681565b34610002576005545b60408051918252519081900360200190f35b34610002576103f560043560035433600160a060020a0390811691161461042157610002565b34610002576103f7601281565b34610002576103f560043560025460ff161561046b57610002565b34610002576103f5600254600090819060ff16151561059457610002565b34610002576103f5600254600090819060ff16151561069557610002565b34610002576103d9600354600160a060020a031681565b34610002576101706b02a649c112686927b400000081565b3461000257600160a060020a0360043516600090815260066020526040902054610170565b34610002576103f560043560025460ff161561077e57610002565b34610002576103d9600754600160a060020a031681565b346100025761017060015481565b346100025761017060085481565b346100025761036b60408051808201909152600381527f474e540000000000000000000000000000000000000000000000000000000000602082015281565b346100025761040d600435602435600254600090819060ff16156107f057610002565b34610002576101706a7c13bc4b2c133c5600000081565b346100025761040d60025460ff1681565b34610002576101706103e881565b346100025761017060005481565b6103f560025460009060ff16151561089057610002565b60405180806020018281038252838181518152602001915080519060200190808383829060006004602084601f0104600302600f01f150905090810190601f1680156103cb5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b60408051600160a060020a039092168252519081900360200190f35b005b6040805160ff9092168252519081900360200190f35b604080519115158252519081900360200190f35b600160a060020a038116151561043657610002565b600380546c010000000000000000000000008084020473ffffffffffffffffffffffffffffffffffffffff1990911617905550565b600754600160a060020a0316151561048257610002565b80151561048e57610002565b600160a060020a0333166000908152600660205260409020548111156104b357610002565b600160a060020a0333811660008181526006602052604080822080548690039055600580548690039055600880548601905560075481517f7a3130e30000000000000000000000000000000000000000000000000000000081526004810194909452602484018690529051931692637a3130e392604480820193929182900301818387803b156100025760325a03f115610002575050600754604080518481529051600160a060020a03928316935033909216917f18df02dcc52b9c494f391df09661519c0069bd8540141946280399408205ca1a9181900360200190a350565b600154431115806105b257506005546a7c13bc4b2c133c5600000090105b80156105cc57506005546b02a649c112686927b400000090105b156105d657610002565b50506002805460ff1916905560058054605260128083029190910491820190925560048054600160a060020a03908116600090815260066020908152604080832080548701905593548451868152945193169391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a36040516002546101009004600160a060020a0390811691309091163180156108fc02916000818181858888f19350505050151561069157610002565b5050565b60015443116106a357610002565b6005546a7c13bc4b2c133c5600000090106106bd57610002565b600160a060020a03331660009081526006602052604090205491508115156106e457610002565b600160a060020a0333166000908152600660205260408120556005805483900390556103e88204905033600160a060020a03167fbb28353e4598c3b9199101a66e0989549b659a59a54d2c27fbb183f1932c8e6d826040518082815260200191505060405180910390a2604051600160a060020a0333169082156108fc029083906000818181858888f19350505050151561069157610002565b600754600160a060020a03161561079457610002565b60035433600160a060020a039081169116146107af57610002565b600780546c010000000000000000000000008084020473ffffffffffffffffffffffffffffffffffffffff1990911617905550565b600091505b5092915050565b50600160a060020a03331660009081526006602052604090205482811080159061081a5750600083115b156107e457600160a060020a0333811660008181526006602090815260408083209588900395869055938816808352918490208054880190558351878152935191937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a3600191506107e9565b60005443101561089f57610002565b6001544311156108ae57610002565b3415156108ba57610002565b6005546103e8906b02a649c112686927b400000003043411156108dc57610002565b5060058054346103e802908101909155600160a060020a0333166000818152600660209081526040808320805486019055805185815290517fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a35056";

    protected Golem(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Golem(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public List<MigrateEventResponse> getMigrateEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Migrate", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<MigrateEventResponse> responses = new ArrayList<MigrateEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MigrateEventResponse typedResponse = new MigrateEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MigrateEventResponse> migrateEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Migrate", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, MigrateEventResponse>() {
            @Override
            public MigrateEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                MigrateEventResponse typedResponse = new MigrateEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<RefundEventResponse> getRefundEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Refund", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<RefundEventResponse> responses = new ArrayList<RefundEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RefundEventResponse typedResponse = new RefundEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RefundEventResponse> refundEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Refund", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RefundEventResponse>() {
            @Override
            public RefundEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                RefundEventResponse typedResponse = new RefundEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
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

    public RemoteCall<String> golemFactory() {
        final Function function = new Function("golemFactory", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setMigrationMaster(String _master) {
        final Function function = new Function(
                "setMigrationMaster", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_master)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> migrate(BigInteger _value) {
        final Function function = new Function(
                "migrate", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

//    public RemoteCall<TransactionReceipt> finalize() {
//        final Function function = new Function(
//                "finalize",
//                Arrays.<Type>asList(),
//                Collections.<TypeReference<?>>emptyList());
//        return executeRemoteCallTransaction(function);
//    }

    public RemoteCall<TransactionReceipt> refund() {
        final Function function = new Function(
                "refund", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> migrationMaster() {
        final Function function = new Function("migrationMaster", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> tokenCreationCap() {
        final Function function = new Function("tokenCreationCap", 
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

    public RemoteCall<TransactionReceipt> setMigrationAgent(String _agent) {
        final Function function = new Function(
                "setMigrationAgent", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_agent)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> migrationAgent() {
        final Function function = new Function("migrationAgent", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> fundingEndBlock() {
        final Function function = new Function("fundingEndBlock", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> totalMigrated() {
        final Function function = new Function("totalMigrated", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> tokenCreationMin() {
        final Function function = new Function("tokenCreationMin", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> funding() {
        final Function function = new Function("funding", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> tokenCreationRate() {
        final Function function = new Function("tokenCreationRate", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> fundingStartBlock() {
        final Function function = new Function("fundingStartBlock", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> create(BigInteger weiValue) {
        final Function function = new Function(
                "create", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public static RemoteCall<Golem> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _golemFactory, String _migrationMaster, BigInteger _fundingStartBlock, BigInteger _fundingEndBlock) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_golemFactory), 
                new org.web3j.abi.datatypes.Address(_migrationMaster), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingStartBlock), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingEndBlock)));
        return deployRemoteCall(Golem.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Golem> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _golemFactory, String _migrationMaster, BigInteger _fundingStartBlock, BigInteger _fundingEndBlock) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_golemFactory), 
                new org.web3j.abi.datatypes.Address(_migrationMaster), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingStartBlock), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingEndBlock)));
        return deployRemoteCall(Golem.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static Golem load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Golem(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Golem load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Golem(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class MigrateEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class RefundEventResponse {
        public Log log;

        public String _from;

        public BigInteger _value;
    }
}
