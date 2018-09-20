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
 * <p>Generated with web3j version 3.4.0.
 */
public class TAVITT extends Contract implements ethTokenERC20 {
    private static final String BINARY = "60c0604052600a60808190527f546176697474636f696e0000000000000000000000000000000000000000000060a0908152620000409160069190620001e6565b506007805460ff191660089081179091556040805180820190915260068082527f5441564954540000000000000000000000000000000000000000000000000000602090920191825262000096929190620001e6565b506040805180820190915260018082527f31000000000000000000000000000000000000000000000000000000000000006020909201918252620000dd91600991620001e6565b50348015620000eb57600080fd5b5060008054600160a060020a03191673e57f73f0d380e1698f59dc7270352724c1cc8306179081905560408051600160a060020a03929092168252517fa2b0867ddc9434a6620c27aae2ee7b19d3db53f3e7f059bdbecc3f2091e24f779181900360200190a160075460ff16600a0a6305f5e10002600281905573e57f73f0d380e1698f59dc7270352724c1cc83066000818152600360209081527f030fd48267a509220448bcf16d0b58c48d0846ede55e94013caa3e41f3d0ca658490556040805194855251929391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a36200028b565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200022957805160ff191683800117855562000259565b8280016001018555821562000259579182015b82811115620002595782518255916020019190600101906200023c565b50620002679291506200026b565b5090565b6200028891905b8082111562000267576000815560010162000272565b90565b610e3f806200029b6000396000f30060806040526004361061011c5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde03811461012e578063095ea7b3146101b857806318160ddd146101f05780631a9aea0a146102175780631b8fc2f01461022c57806323b872dd1461024f578063313ce567146102795780634cf78170146102a457806354fd4d50146102d55780635be7cc16146102ea57806370a082311461030b57806379c650681461032c57806381eaf99b1461035057806395d89b4114610365578063a9059cbb1461037a578063ac869cd81461039e578063bff35618146103c4578063d1df306c146103de578063dd62ed3e14610402578063f851a44014610429578063fa51a2bf1461043e575b34801561012857600080fd5b50600080fd5b34801561013a57600080fd5b50610143610458565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561017d578181015183820152602001610165565b50505050905090810190601f1680156101aa5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101c457600080fd5b506101dc600160a060020a03600435166024356104e6565b604080519115158252519081900360200190f35b3480156101fc57600080fd5b50610205610588565b60408051918252519081900360200190f35b34801561022357600080fd5b506101dc61058e565b34801561023857600080fd5b5061024d600160a060020a03600435166105b0565b005b34801561025b57600080fd5b506101dc600160a060020a0360043581169060243516604435610628565b34801561028557600080fd5b5061028e61079d565b6040805160ff9092168252519081900360200190f35b3480156102b057600080fd5b506102b96107a6565b60408051600160a060020a039092168252519081900360200190f35b3480156102e157600080fd5b506101436107b5565b3480156102f657600080fd5b5061024d600160a060020a0360043516610810565b34801561031757600080fd5b50610205600160a060020a03600435166108a3565b34801561033857600080fd5b5061024d600160a060020a03600435166024356108be565b34801561035c57600080fd5b506101dc6109a1565b34801561037157600080fd5b506101436109c2565b34801561038657600080fd5b506101dc600160a060020a0360043516602435610a1d565b3480156103aa57600080fd5b5061024d600160a060020a03600435166024351515610b2e565b3480156103d057600080fd5b5061024d6004351515610ba9565b3480156103ea57600080fd5b5061024d600160a060020a0360043516602435610c37565b34801561040e57600080fd5b50610205600160a060020a0360043581169060243516610d05565b34801561043557600080fd5b506102b9610d30565b34801561044a57600080fd5b5061024d6004351515610d3f565b6006805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104de5780601f106104b3576101008083540402835291602001916104de565b820191906000526020600020905b8154815290600101906020018083116104c157829003601f168201915b505050505081565b60008115806105165750336000908152600460209081526040808320600160a060020a0387168452909152902054155b151561052157600080fd5b336000818152600460209081526040808320600160a060020a03881680855290835292819020869055805186815290519293927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929181900390910190a350600192915050565b60025481565b6000547501000000000000000000000000000000000000000000900460ff1681565b600054600160a060020a031633146105c757600080fd5b60018054600160a060020a03831673ffffffffffffffffffffffffffffffffffffffff19909116811790915560408051918252517f742e2ebd0014f6b28dbbce00d10b8f4f4a46f5b69d9a6224c87d0e733a8d99779181900360200190a150565b600080547501000000000000000000000000000000000000000000900460ff16158061065e5750600154600160a060020a031633145b151561066957600080fd5b600160a060020a038316151561067e57600080fd5b600160a060020a03841660009081526005602052604090205460ff16156106a457600080fd5b600160a060020a03841660009081526004602090815260408083203384529091529020546106d8908363ffffffff610dcb16565b600160a060020a038516600081815260046020908152604080832033845282528083209490945591815260039091522054610719908363ffffffff610dcb16565b600160a060020a03808616600090815260036020526040808220939093559085168152205461074e908363ffffffff610ddd16565b600160a060020a038085166000818152600360209081526040918290209490945580518681529051919392881692600080516020610df483398151915292918290030190a35060019392505050565b60075460ff1681565b600154600160a060020a031681565b6009805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104de5780601f106104b3576101008083540402835291602001916104de565b600054600160a060020a0316331461082757600080fd5b600160a060020a038116151561083c57600080fd5b6000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03838116919091179182905560408051929091168252517f4f2723059e5730f1d4ffa943789d401722067ca1121b828944c6965dbd303e08916020908290030190a150565b600160a060020a031660009081526003602052604090205490565b600054600160a060020a031633146108d557600080fd5b60005474010000000000000000000000000000000000000000900460ff16156108fd57600080fd5b600160a060020a0382166000908152600360205260409020546109209082610ddd565b600160a060020a0383166000908152600360205260409020556002546109469082610ddd565b6002556040805182815290513091600091600080516020610df48339815191529181900360200190a3604080518281529051600160a060020a038416913091600080516020610df48339815191529181900360200190a35050565b60005474010000000000000000000000000000000000000000900460ff1681565b6008805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104de5780601f106104b3576101008083540402835291602001916104de565b600080547501000000000000000000000000000000000000000000900460ff161580610a535750600154600160a060020a031633145b1515610a5e57600080fd5b600160a060020a0383161515610a7357600080fd5b3360009081526005602052604090205460ff1615610a9057600080fd5b33600090815260036020526040902054610ab0908363ffffffff610dcb16565b3360009081526003602052604080822092909255600160a060020a03851681522054610ae2908363ffffffff610ddd16565b600160a060020a038416600081815260036020908152604091829020939093558051858152905191923392600080516020610df48339815191529281900390910190a350600192915050565b600054600160a060020a03163314610b4557600080fd5b600160a060020a038216600081815260056020908152604091829020805460ff191685151590811790915582519384529083015280517f0adeb3125cc5db4bbcd04a6ad07b095f8c5f7db710ea08e9a35481d7a4bcc4719281900390910190a15050565b600054600160a060020a03163314610bc057600080fd5b600080548215157501000000000000000000000000000000000000000000810275ff000000000000000000000000000000000000000000199092169190911790915560408051918252517ff33f8ef436f631648b30f6761d8d417b93dc359444a28c3d5c5bdb05c10edc169181900360200190a150565b600054600160a060020a03163314610c4e57600080fd5b60005474010000000000000000000000000000000000000000900460ff1615610c7657600080fd5b600160a060020a038216600090815260036020526040902054610c999082610dcb565b600160a060020a038316600090815260036020526040902055600254610cbf9082610dcb565b600255604080518281529051600160a060020a038416917f696de425f79f4a40bc6d2122ca50507f0efbeabbff86a84871b7196ab8ea8df7919081900360200190a25050565b600160a060020a03918216600090815260046020908152604080832093909416825291909152205490565b600054600160a060020a031681565b600054600160a060020a03163314610d5657600080fd5b6000805482151574010000000000000000000000000000000000000000810274ff0000000000000000000000000000000000000000199092169190911790915560408051918252517fc66e378b596f3b01004d4ee4ade9faff42014dae2242d63966a6d66821500e6a9181900360200190a150565b600082821115610dd757fe5b50900390565b600082820183811015610dec57fe5b93925050505600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a72305820767f390c5e41add3cb5c4d2bb51ced636c5fef75b5c018cb839d2c44d4d7a2f50029";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_LOCKTRANSFER = "lockTransfer";

    public static final String FUNC_SETALLOWEDADDRESS = "setAllowedAddress";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_ALLOWEDADDRESS = "allowedAddress";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_TRANSFERADMINSHIP = "transferAdminship";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_MINTTOKEN = "mintToken";

    public static final String FUNC_LOCKSUPPLY = "lockSupply";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_SETFROZEN = "setFrozen";

    public static final String FUNC_SETTRANSFERLOCK = "setTransferLock";

    public static final String FUNC_BURNTOKEN = "burnToken";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_ADMIN = "admin";

    public static final String FUNC_SETSUPPLYLOCK = "setSupplyLock";

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event BURNED_EVENT = new Event("Burned", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event FROZENSTATUS_EVENT = new Event("FrozenStatus", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event ALLOWEDSET_EVENT = new Event("AllowedSet", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event SETSUPPLYLOCK_EVENT = new Event("SetSupplyLock", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event SETTRANSFERLOCK_EVENT = new Event("SetTransferLock", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event TRANSFERADMINSHIP_EVENT = new Event("TransferAdminship", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event ADMINED_EVENT = new Event("Admined", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    protected TAVITT(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TAVITT(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<Boolean> lockTransfer() {
        final Function function = new Function(FUNC_LOCKTRANSFER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> setAllowedAddress(String _to) {
        final Function function = new Function(
                FUNC_SETALLOWEDADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to)), 
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

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> allowedAddress() {
        final Function function = new Function(FUNC_ALLOWEDADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> version() {
        final Function function = new Function(FUNC_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _newAdmin) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newAdmin)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> mintToken(String _target, BigInteger _mintedAmount) {
        final Function function = new Function(
                FUNC_MINTTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_target), 
                new org.web3j.abi.datatypes.generated.Uint256(_mintedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> lockSupply() {
        final Function function = new Function(FUNC_LOCKSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteCall<TransactionReceipt> setFrozen(String _target, Boolean _flag) {
        final Function function = new Function(
                FUNC_SETFROZEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_target), 
                new org.web3j.abi.datatypes.Bool(_flag)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setTransferLock(Boolean _set) {
        final Function function = new Function(
                FUNC_SETTRANSFERLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Bool(_set)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burnToken(String _target, BigInteger _burnedAmount) {
        final Function function = new Function(
                FUNC_BURNTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_target), 
                new org.web3j.abi.datatypes.generated.Uint256(_burnedAmount)), 
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

    public RemoteCall<String> admin() {
        final Function function = new Function(FUNC_ADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> setSupplyLock(Boolean _set) {
        final Function function = new Function(
                FUNC_SETSUPPLYLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Bool(_set)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<TAVITT> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TAVITT.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<TAVITT> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TAVITT.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
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

    public List<BurnedEventResponse> getBurnedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURNED_EVENT, transactionReceipt);
        ArrayList<BurnedEventResponse> responses = new ArrayList<BurnedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnedEventResponse typedResponse = new BurnedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._target = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnedEventResponse> burnedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnedEventResponse>() {
            @Override
            public BurnedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURNED_EVENT, log);
                BurnedEventResponse typedResponse = new BurnedEventResponse();
                typedResponse.log = log;
                typedResponse._target = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<BurnedEventResponse> burnedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURNED_EVENT));
        return burnedEventObservable(filter);
    }

    public List<FrozenStatusEventResponse> getFrozenStatusEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FROZENSTATUS_EVENT, transactionReceipt);
        ArrayList<FrozenStatusEventResponse> responses = new ArrayList<FrozenStatusEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FrozenStatusEventResponse typedResponse = new FrozenStatusEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._target = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._flag = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<FrozenStatusEventResponse> frozenStatusEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, FrozenStatusEventResponse>() {
            @Override
            public FrozenStatusEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FROZENSTATUS_EVENT, log);
                FrozenStatusEventResponse typedResponse = new FrozenStatusEventResponse();
                typedResponse.log = log;
                typedResponse._target = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._flag = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<FrozenStatusEventResponse> frozenStatusEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FROZENSTATUS_EVENT));
        return frozenStatusEventObservable(filter);
    }

    public List<AllowedSetEventResponse> getAllowedSetEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ALLOWEDSET_EVENT, transactionReceipt);
        ArrayList<AllowedSetEventResponse> responses = new ArrayList<AllowedSetEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AllowedSetEventResponse typedResponse = new AllowedSetEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AllowedSetEventResponse> allowedSetEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, AllowedSetEventResponse>() {
            @Override
            public AllowedSetEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ALLOWEDSET_EVENT, log);
                AllowedSetEventResponse typedResponse = new AllowedSetEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<AllowedSetEventResponse> allowedSetEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ALLOWEDSET_EVENT));
        return allowedSetEventObservable(filter);
    }

    public List<SetSupplyLockEventResponse> getSetSupplyLockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SETSUPPLYLOCK_EVENT, transactionReceipt);
        ArrayList<SetSupplyLockEventResponse> responses = new ArrayList<SetSupplyLockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetSupplyLockEventResponse typedResponse = new SetSupplyLockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._set = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SetSupplyLockEventResponse> setSupplyLockEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, SetSupplyLockEventResponse>() {
            @Override
            public SetSupplyLockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SETSUPPLYLOCK_EVENT, log);
                SetSupplyLockEventResponse typedResponse = new SetSupplyLockEventResponse();
                typedResponse.log = log;
                typedResponse._set = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<SetSupplyLockEventResponse> setSupplyLockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETSUPPLYLOCK_EVENT));
        return setSupplyLockEventObservable(filter);
    }

    public List<SetTransferLockEventResponse> getSetTransferLockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SETTRANSFERLOCK_EVENT, transactionReceipt);
        ArrayList<SetTransferLockEventResponse> responses = new ArrayList<SetTransferLockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetTransferLockEventResponse typedResponse = new SetTransferLockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._set = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SetTransferLockEventResponse> setTransferLockEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, SetTransferLockEventResponse>() {
            @Override
            public SetTransferLockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SETTRANSFERLOCK_EVENT, log);
                SetTransferLockEventResponse typedResponse = new SetTransferLockEventResponse();
                typedResponse.log = log;
                typedResponse._set = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<SetTransferLockEventResponse> setTransferLockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETTRANSFERLOCK_EVENT));
        return setTransferLockEventObservable(filter);
    }

    public List<TransferAdminshipEventResponse> getTransferAdminshipEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERADMINSHIP_EVENT, transactionReceipt);
        ArrayList<TransferAdminshipEventResponse> responses = new ArrayList<TransferAdminshipEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferAdminshipEventResponse typedResponse = new TransferAdminshipEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.newAdminister = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferAdminshipEventResponse> transferAdminshipEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferAdminshipEventResponse>() {
            @Override
            public TransferAdminshipEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERADMINSHIP_EVENT, log);
                TransferAdminshipEventResponse typedResponse = new TransferAdminshipEventResponse();
                typedResponse.log = log;
                typedResponse.newAdminister = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferAdminshipEventResponse> transferAdminshipEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERADMINSHIP_EVENT));
        return transferAdminshipEventObservable(filter);
    }

    public List<AdminedEventResponse> getAdminedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ADMINED_EVENT, transactionReceipt);
        ArrayList<AdminedEventResponse> responses = new ArrayList<AdminedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AdminedEventResponse typedResponse = new AdminedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.administer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AdminedEventResponse> adminedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, AdminedEventResponse>() {
            @Override
            public AdminedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ADMINED_EVENT, log);
                AdminedEventResponse typedResponse = new AdminedEventResponse();
                typedResponse.log = log;
                typedResponse.administer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<AdminedEventResponse> adminedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ADMINED_EVENT));
        return adminedEventObservable(filter);
    }

    public static TAVITT load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TAVITT(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TAVITT load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TAVITT(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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

    public static class BurnedEventResponse {
        public Log log;

        public String _target;

        public BigInteger _value;
    }

    public static class FrozenStatusEventResponse {
        public Log log;

        public String _target;

        public Boolean _flag;
    }

    public static class AllowedSetEventResponse {
        public Log log;

        public String _to;
    }

    public static class SetSupplyLockEventResponse {
        public Log log;

        public Boolean _set;
    }

    public static class SetTransferLockEventResponse {
        public Log log;

        public Boolean _set;
    }

    public static class TransferAdminshipEventResponse {
        public Log log;

        public String newAdminister;
    }

    public static class AdminedEventResponse {
        public Log log;

        public String administer;
    }
}
