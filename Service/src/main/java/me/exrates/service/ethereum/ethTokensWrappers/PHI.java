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
public class PHI extends Contract implements ethTokenERC20 {
    private static final String BINARY = "60606040526a13fb9de239dbf9d64400006002556003805460ff1916905534156200002957600080fd5b604051608080620013958339810160405280805191906020018051919060200180519190602001805191506000905080600160a060020a03861615156200006f57600080fd5b600160a060020a03851615156200008557600080fd5b600160a060020a03841615156200009b57600080fd5b84600160a060020a031686600160a060020a031614158015620000d0575085600160a060020a031684600160a060020a031614155b1515620000dc57600080fd5b60035460ff1615620000ed57600080fd5b428311620000fa57600080fd5b600683905560058054600160a060020a031916600160a060020a038616179055600254670de0b6b3a764000090116200013257600080fd5b600091506200015b866a0684e87dc281feb430000064010000000062000c40620005cc82021704565b60048054600160a060020a031916600160a060020a0389161790559190910190620001a0856a02ea5bc198996c82480000640100000000620005cc810262000c401704565b6003805461010060a860020a031916610100600160a060020a038916021790559190910190620001ea846a07a1fde14627221d840000640100000000620005cc810262000c401704565b90910190506000620002297372b16dc0e5f85aa4bbfce81687ccc9d6871c29656930c94de6515c0fec000064010000000062000c40620005cc82021704565b0162000262737270cc02d88ea63fc26384f5d08e14ee87e75154691bfc843137b6b7c8000064010000000062000c40620005cc82021704565b016200029b7325f92f21222969bb0b1f14f19fba770d30ff678f691bfc843137b6b7c8000064010000000062000c40620005cc82021704565b01620002d473ac99c59d3353a34531fae217ba77139bbe4edbb3695de12f2e13cca358000064010000000062000c40620005cc82021704565b016200030d73be41d37eb2d2859143b9f1d29c7bc6d7e59174da69cd949c2a4ac1769a000064010000000062000c40620005cc82021704565b01620003467363e9fa0e43fcc7c702ed5997afb8e215c5bee3c969cd949c2a4ac1769a000064010000000062000c40620005cc82021704565b016200037f7395c67812c5c41733419ac3b1916d2f282e7a15a46953f58c93a7242758000064010000000062000c40620005cc82021704565b01620003b8731f5d30bb328498ff6e09b717ec22a9046c41c257690444022858838cc0000064010000000062000c40620005cc82021704565b01620003f1730a1ac564e95daedf8d454a3593b75ccdd474fc426904322c5d8ba16d3c000064010000000062000c40620005cc82021704565b016200042a730c5448d5bc4c40b4d2b2c1d7e58e0541698d3e6e6904322c5d8ba16d3c000064010000000062000c40620005cc82021704565b016200046373fae11d521538f067ce0b13b6f8c929cdea934d07690ff0e1fd06c3a6dc000064010000000062000c40620005cc82021704565b016200049c73ee51304603887fff15c6d12165c6d96ff0f0c85b6909bae678d3c3a7d4000064010000000062000c40620005cc82021704565b01620004d573d7bab04c944faafa232d6ebfe4f60ff8c4e9815f69014c2534b5c3425c000064010000000062000c40620005cc82021704565b016200050e73603f39c81560019c8360f33ba45bc1e4caecb33e6909bae678d3c3a7d4000064010000000062000c40620005cc82021704565b016200054773bb5128f1093d1aa85f6d7d0cc20b8415e0104edd69033e4832b45aaad0000064010000000062000c40620005cc82021704565b6003805460ff191660011790556002549101907fb94ae47ec9f4248692e2ecf9740b67ab493f3dcc8452bedc7d9cd911c28d1ca560405160405180910390a26a02ea5bc198996c8248000081146200059b57fe5b620005b5828264010000000062000c186200066182021704565b60025414620005c057fe5b50505050505062000678565b6000600160a060020a0383161515620005e457600080fd5b60035460ff1615620005f557600080fd5b600160a060020a0383166000818152602081905260408082208590557fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35050600160a060020a031660009081526020819052604090205490565b6000828201838110156200067157fe5b9392505050565b610d0d80620006886000396000f3006060604052600436106100da5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100df578063095ea7b3146101695780630d6680871461019f57806318160ddd146101c457806323b872dd146101d7578063313ce567146101ff57806342966c6814610228578063441697521461024057806354e0b4641461026f57806370a0823114610282578063799b7bb8146102a157806395d89b41146102b4578063a9059cbb146102c7578063be45fd62146102e9578063dd62ed3e1461034e575b600080fd5b34156100ea57600080fd5b6100f2610373565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561012e578082015183820152602001610116565b50505050905090810190601f16801561015b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561017457600080fd5b61018b600160a060020a03600435166024356103aa565b604051901515815260200160405180910390f35b34156101aa57600080fd5b6101b26104ab565b60405190815260200160405180910390f35b34156101cf57600080fd5b6101b26104b1565b34156101e257600080fd5b61018b600160a060020a03600435811690602435166044356104b7565b341561020a57600080fd5b6102126106d2565b60405160ff909116815260200160405180910390f35b341561023357600080fd5b61023e6004356106d7565b005b341561024b57600080fd5b610253610858565b604051600160a060020a03909116815260200160405180910390f35b341561027a57600080fd5b610253610867565b341561028d57600080fd5b6101b2600160a060020a0360043516610876565b34156102ac57600080fd5b610253610891565b34156102bf57600080fd5b6100f26108a5565b34156102d257600080fd5b61018b600160a060020a03600435166024356108dc565b34156102f457600080fd5b61018b60048035600160a060020a03169060248035919060649060443590810190830135806020601f82018190048102016040519081016040528181529291906020840183838082843750949650610a7895505050505050565b341561035957600080fd5b6101b2600160a060020a0360043581169060243516610bed565b60408051908101604052600981527f50484920546f6b656e0000000000000000000000000000000000000000000000602082015281565b60006006544211806103e7575060035433600160a060020a039081166101009092041614806103e7575060045433600160a060020a039081169116145b15156103f257600080fd5b600160a060020a038316151561040757600080fd5b8115806104375750600160a060020a03338116600090815260016020908152604080832093871683529290522054155b151561044257600080fd5b600160a060020a03338116600081815260016020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b60065481565b60025481565b60006006544211806104f4575060035433600160a060020a039081166101009092041614806104f4575060045433600160a060020a039081169116145b15156104ff57600080fd5b600160a060020a038416151561051457600080fd5b600160a060020a038316151561052957600080fd5b30600160a060020a031683600160a060020a03161415151561054a57600080fd5b600160a060020a0384166000908152602081905260409020548290101561057057600080fd5b600160a060020a0380851660009081526001602090815260408083203390941683529290522054829010156105a457600080fd5b600160a060020a0383166000908152602081905260409020546105cd818463ffffffff610c1816565b10156105d857600080fd5b600160a060020a038316600090815260208190526040902054610601908363ffffffff610c1816565b600160a060020a038085166000908152602081905260408082209390935590861681522054610636908363ffffffff610c2e16565b600160a060020a038086166000908152602081815260408083209490945560018152838220339093168252919091522054610677908363ffffffff610c2e16565b600160a060020a0380861660008181526001602090815260408083203386168452909152908190209390935590851691600080516020610cc28339815191529085905190815260200160405180910390a35060019392505050565b601281565b600080600654421180610715575060035433600160a060020a03908116610100909204161480610715575060045433600160a060020a039081169116145b151561072057600080fd5b6000831161072d57600080fd5b600160a060020a0333166000908152602081905260409020548390101561075357600080fd5b6002548390101561076357600080fd5b505033600160a060020a0381166000908152602081905260409020549061078a8284610c2e565b600160a060020a0382166000908152602081905260409020556002546107b6908463ffffffff610c2e16565b600255600160a060020a0381167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca58460405190815260200160405180910390a2600081600160a060020a0316600080516020610cc28339815191528560405190815260200160405180910390a3610833828463ffffffff610c2e16565b600160a060020a0382166000908152602081905260409020541461085357fe5b505050565b600554600160a060020a031681565b600454600160a060020a031681565b600160a060020a031660009081526020819052604090205490565b6003546101009004600160a060020a031681565b60408051908101604052600381527f5048490000000000000000000000000000000000000000000000000000000000602082015281565b6000600654421180610919575060035433600160a060020a03908116610100909204161480610919575060045433600160a060020a039081169116145b151561092457600080fd5b600160a060020a038316151561093957600080fd5b30600160a060020a031683600160a060020a03161415151561095a57600080fd5b600160a060020a0333166000908152602081905260409020548290101561098057600080fd5b600160a060020a0383166000908152602081905260409020546109a9818463ffffffff610c1816565b10156109b457600080fd5b600160a060020a0333166000908152602081905260409020546109dd908363ffffffff610c2e16565b600160a060020a033381166000908152602081905260408082209390935590851681522054610a12908363ffffffff610c1816565b60008085600160a060020a0316600160a060020a031681526020019081526020016000208190555082600160a060020a031633600160a060020a0316600080516020610cc28339815191528460405190815260200160405180910390a350600192915050565b6000806000600654421180610ab8575060035433600160a060020a03908116610100909204161480610ab8575060045433600160a060020a039081169116145b1515610ac357600080fd5b610acd86866108dc565b1515610ad857600080fd5b853b91506000821115610be1575084600160a060020a03811663c0ee0b8a3387876040518463ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018084600160a060020a0316600160a060020a0316815260200183815260200180602001828103825283818151815260200191508051906020019080838360005b83811015610b7f578082015183820152602001610b67565b50505050905090810190601f168015610bac5780820380516001836020036101000a031916815260200191505b50945050505050600060405180830381600087803b1515610bcc57600080fd5b6102c65a03f11515610bdd57600080fd5b5050505b50600195945050505050565b600160a060020a03918216600090815260016020908152604080832093909416825291909152205490565b600082820183811015610c2757fe5b9392505050565b600082821115610c3a57fe5b50900390565b6000600160a060020a0383161515610c5757600080fd5b60035460ff1615610c6757600080fd5b600160a060020a038316600081815260208190526040808220859055600080516020610cc28339815191529085905190815260200160405180910390a35050600160a060020a0316600090815260208190526040902054905600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a723058206762d56fc8f0ccd3db3270c48ca0481f5105a61f0ce21bdfda6c6d43914425490029000000000000000000000000ac65447589555ec19cbafc5cec717318ec811874000000000000000000000000a17f2bc6b11b4dd60ab38c94e7784634646749c90000000000000000000000008e36256fa656adcf951bb458ba6e5439432780f0000000000000000000000000000000000000000000000000000000005ac60fc0\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_LOCKTIME = "lockTime";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_WALLET_ADDR = "WALLET_ADDR";

    public static final String FUNC_ICO_ADDR = "ICO_ADDR";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_PRE_ICO_ADDR = "PRE_ICO_ADDR";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final Event DEPLOYED_EVENT = new Event("Deployed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event BURN_EVENT = new Event("Burn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected PHI(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PHI(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<BigInteger> lockTime() {
        final Function function = new Function(FUNC_LOCKTIME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> WALLET_ADDR() {
        final Function function = new Function(FUNC_WALLET_ADDR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> ICO_ADDR() {
        final Function function = new Function(FUNC_ICO_ADDR, 
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

    public RemoteCall<String> PRE_ICO_ADDR() {
        final Function function = new Function(FUNC_PRE_ICO_ADDR, 
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

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value, byte[] _data) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value), 
                new org.web3j.abi.datatypes.DynamicBytes(_data)), 
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

    public static RemoteCall<PHI> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String ico_address, String pre_ico_address, String wallet_address, BigInteger _lockTime) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(ico_address), 
                new org.web3j.abi.datatypes.Address(pre_ico_address), 
                new org.web3j.abi.datatypes.Address(wallet_address), 
                new org.web3j.abi.datatypes.generated.Uint256(_lockTime)));
        return deployRemoteCall(PHI.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<PHI> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String ico_address, String pre_ico_address, String wallet_address, BigInteger _lockTime) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(ico_address), 
                new org.web3j.abi.datatypes.Address(pre_ico_address), 
                new org.web3j.abi.datatypes.Address(wallet_address), 
                new org.web3j.abi.datatypes.generated.Uint256(_lockTime)));
        return deployRemoteCall(PHI.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<DeployedEventResponse> getDeployedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPLOYED_EVENT, transactionReceipt);
        ArrayList<DeployedEventResponse> responses = new ArrayList<DeployedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DeployedEventResponse typedResponse = new DeployedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._total_supply = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DeployedEventResponse> deployedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, DeployedEventResponse>() {
            @Override
            public DeployedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPLOYED_EVENT, log);
                DeployedEventResponse typedResponse = new DeployedEventResponse();
                typedResponse.log = log;
                typedResponse._total_supply = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<DeployedEventResponse> deployedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPLOYED_EVENT));
        return deployedEventObservable(filter);
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

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._burner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse._burner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
        return burnEventObservable(filter);
    }

    public static PHI load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PHI(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static PHI load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PHI(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class DeployedEventResponse {
        public Log log;

        public BigInteger _total_supply;
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

    public static class BurnEventResponse {
        public Log log;

        public String _burner;

        public BigInteger _value;
    }
}
