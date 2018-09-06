package me.exrates.service.ethereum.ethTokensWrappers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
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
public class DOR extends Contract implements ethTokenERC20{
    private static final String BINARY = "606060405260e06040519081016040908152690ed2b525841adfc000008252691da56a4b0835bf8000006020830152692c781f708c509f40000090820152693c59e1867548d82000006060820152694c3ba39c5e41110000006080820152695c1d65b2473949e0000060a0820152696bff27c8303182c0000060c08201526200008d90600490600762000110565b5060e0604051908101604090815260218252601e6020830152601b908201526016606082015260116080820152600c60a0820152600760c08201819052620000d891600b916200015e565b50600c805460ff191690553415620000ef57600080fd5b60038054600160a060020a03191633600160a060020a031617905562000235565b82600781019282156200014c579160200282015b828111156200014c57825182906001605060020a031690559160200191906001019062000124565b506200015a929150620001f4565b5090565b600183019183908215620001e65791602002820160005b83821115620001b557835183826101000a81548160ff021916908360ff160217905550926020019260010160208160000104928301926001030262000175565b8015620001e45782816101000a81549060ff0219169055600101602081600001049283019260010302620001b5565b505b506200015a92915062000214565b6200021191905b808211156200015a5760008155600101620001fb565b90565b6200021191905b808211156200015a57805460ff191681556001016200021b565b61159f80620002456000396000f3006060604052600436106101325763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde03811461013d578063095ea7b3146101c757806318160ddd146101fd57806323b872dd1461022257806327e235e31461024a578063313ce5671461026957806341910f901461029257806343d726d6146102a5578063475a9fa9146102b857806366188463146102da57806370a08231146102fc5780637fc88fe21461031b5780638da5cb5b1461034a57806392e8438c1461035d57806395d89b41146103ec578063a035b1fe146103ff578063a9059cbb14610412578063ce55703114610434578063d73dd62314610448578063dd62ed3e1461046a578063e55a07c21461048f578063f2fde38b146104a2578063f946372c146104c1575b61013b336104d4565b005b341561014857600080fd5b6101506105a0565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561018c578082015183820152602001610174565b50505050905090810190601f1680156101b95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101d257600080fd5b6101e9600160a060020a03600435166024356105d7565b604051901515815260200160405180910390f35b341561020857600080fd5b610210610643565b60405190815260200160405180910390f35b341561022d57600080fd5b6101e9600160a060020a0360043581169060243516604435610649565b341561025557600080fd5b610210600160a060020a0360043516610670565b341561027457600080fd5b61027c610682565b60405160ff909116815260200160405180910390f35b341561029d57600080fd5b610210610687565b34156102b057600080fd5b61013b61068d565b34156102c357600080fd5b61013b600160a060020a0360043516602435610746565b34156102e557600080fd5b6101e9600160a060020a036004351660243561077b565b341561030757600080fd5b610210600160a060020a0360043516610875565b341561032657600080fd5b61032e610890565b604051600160a060020a03909116815260200160405180910390f35b341561035557600080fd5b61032e6108a4565b341561036857600080fd5b61013b6004602481358181019083013580602081810201604051908101604052809392919081815260200183836020028082843782019150505050505091908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437509496506108b395505050505050565b34156103f757600080fd5b61015061095c565b341561040a57600080fd5b610210610993565b341561041d57600080fd5b6101e9600160a060020a03600435166024356109ac565b61013b600160a060020a03600435166104d4565b341561045357600080fd5b6101e9600160a060020a03600435166024356109d1565b341561047557600080fd5b610210600160a060020a0360043581169060243516610a75565b341561049a57600080fd5b6101e9610aa0565b34156104ad57600080fd5b61013b600160a060020a0360043516610aa9565b34156104cc57600080fd5b610210610b44565b6000696bff27c8303182c000006000541080156104f45750600c5460ff16155b80156105045750635a7b22804210155b151561050f57600080fd5b662386f26fc1000034101561052357600080fd5b61052c34610b52565b9050696bff27c8303182c0000061054e82600054610c6f90919063ffffffff16565b111561055957600080fd5b6105638282610c81565b600354600160a060020a039081169030163180156108fc0290604051600060405180830381858888f19350505050151561059c57600080fd5b5050565b60408051908101604052600b81527f446f7261646f546f6b656e000000000000000000000000000000000000000000602082015281565b600160a060020a03338116600081815260026020908152604080832094871680845294909152808220859055909291907f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b60005481565b600c5460009060ff16151561065d57600080fd5b610668848484610d18565b949350505050565b60016020526000908152604090205481565b600f81565b611a0b81565b600354600090819033600160a060020a039081169116146106ad57600080fd5b600c5460ff16156106bd57600080fd5b506930b453321fc603c00000905069370ea0d47cf61a8000006106df82610e88565b6106e881610f94565b6000546106fd9083830163ffffffff610c6f16565b600055600c805460ff19166001179055600354600160a060020a039081169030163180156108fc0290604051600060405180830381858888f19350505050151561059c57600080fd5b60035433600160a060020a0390811691161461076157600080fd5b600c5460ff161561077157600080fd5b61059c8282610c81565b600160a060020a033381166000908152600260209081526040808320938616835292905290812054808311156107d857600160a060020a03338116600090815260026020908152604080832093881683529290529081205561080f565b6107e8818463ffffffff610fdf16565b600160a060020a033381166000908152600260209081526040808320938916835292905220555b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020547f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925915190815260200160405180910390a35060019392505050565b600160a060020a031660009081526001602052604090205490565b600c546101009004600160a060020a031681565b600354600160a060020a031681565b60035460009033600160a060020a039081169116146108d157600080fd5b600c5460ff16156108e157600080fd5b81518351146108ef57600080fd5b6064835111156108fe57600080fd5b5060005b81518110156109575761093f83828151811061091a57fe5b9060200190602002015183838151811061093057fe5b90602001906020020151610c81565b61095081600163ffffffff610c6f16565b9050610902565b505050565b60408051908101604052600381527f444f520000000000000000000000000000000000000000000000000000000000602082015281565b60006109a6670de0b6b3a7640000610b52565b90505b90565b600c5460009060ff1615156109c057600080fd5b6109ca8383610ff4565b9392505050565b600160a060020a033381166000908152600260209081526040808320938616835292905290812054610a09908363ffffffff610c6f16565b600160a060020a0333811660008181526002602090815260408083209489168084529490915290819020849055919290917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591905190815260200160405180910390a350600192915050565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b600c5460ff1681565b60035433600160a060020a03908116911614610ac457600080fd5b600160a060020a0381161515610ad957600080fd5b600354600160a060020a0380831691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b696bff27c8303182c0000081565b600080806509184e72a000610b6f85611a0b63ffffffff6110dd16565b811515610b7857fe5b046402540be400029150610b8a611104565b9050600b60ff821660078110610b9c57fe5b602091828204019190069054906101000a900460ff1660640360ff16610bcc6064846110dd90919063ffffffff16565b811515610bd557fe5b0492505b600460ff821660078110610be957fe5b0154600054610bff90859063ffffffff610c6f16565b118015610c0f575060068160ff16105b15610c6857600101600b60ff821660078110610c2757fe5b602091828204019190069054906101000a900460ff1660640360ff16610c576064846110dd90919063ffffffff16565b811515610c6057fe5b049250610bd9565b5050919050565b6000828201838110156109ca57600080fd5b600160a060020a0382161515610c9657600080fd5b600054610ca9908263ffffffff610c6f16565b6000908155600160a060020a038316815260016020526040902054610cd4908263ffffffff610c6f16565b600160a060020a0383166000818152600160205260408082209390935590916000805160206115548339815191529084905190815260200160405180910390a35050565b6000600160a060020a0383161515610d2f57600080fd5b600160a060020a038416600090815260016020526040902054821115610d5457600080fd5b600160a060020a0380851660009081526002602090815260408083203390941683529290522054821115610d8757600080fd5b600160a060020a038416600090815260016020526040902054610db0908363ffffffff610fdf16565b600160a060020a038086166000908152600160205260408082209390935590851681522054610de5908363ffffffff610c6f16565b600160a060020a03808516600090815260016020908152604080832094909455878316825260028152838220339093168252919091522054610e2d908363ffffffff610fdf16565b600160a060020a03808616600081815260026020908152604080832033861684529091529081902093909355908516916000805160206115548339815191529085905190815260200160405180910390a35060019392505050565b6003546000903090600160a060020a0316635fee6600610ea66111d6565b600160a060020a03938416815291909216602082015267ffffffffffffffff90911660408083019190915260609091019051809103906000f0801515610eeb57600080fd5b600c805474ffffffffffffffffffffffffffffffffffffffff001916610100600160a060020a038481168202929092179283905590910416600090815260016020526040902054909150610f3f9083610c6f565b600c8054600160a060020a036101009182900481166000908152600160205260408082209590955592549190910416916000805160206115548339815191529085905190815260200160405180910390a35050565b60038054600160a060020a039081166000908152600160205260408082208590559254909116916000805160206115548339815191529084905190815260200160405180910390a350565b600082821115610fee57600080fd5b50900390565b6000600160a060020a038316151561100b57600080fd5b600160a060020a03331660009081526001602052604090205482111561103057600080fd5b600160a060020a033316600090815260016020526040902054611059908363ffffffff610fdf16565b600160a060020a03338116600090815260016020526040808220939093559085168152205461108e908363ffffffff610c6f16565b600160a060020a0380851660008181526001602052604090819020939093559133909116906000805160206115548339815191529085905190815260200160405180910390a350600192915050565b60008282028315806110f957508284828115156110f657fe5b04145b15156109ca57600080fd5b600061110e611143565b90505b60068160ff161080156111365750600460ff82166007811061112f57fe5b0154600054115b156109a957600101611111565b6000635afc558042111561115657600080fd5b635ae9e08042111561116a575060066109a9565b635ad76b8042111561117e575060056109a9565b635ac4f680421115611192575060046109a9565b635ab281804211156111a6575060036109a9565b635aa00c804211156111ba575060026109a9565b635a8d97804211156111ce575060016109a9565b5060006109a9565b60405161036d806111e78339019056006060604052341561000f57600080fd5b60405160608061036d8339810160405280805191906020018051919060200180519150506001604060020a034281169082161161004b57600080fd5b60008054600160a060020a0319908116600160a060020a0395861617825560018054909116939094169290921760a060020a60e060020a031916740100000000000000000000000000000000000000006001604060020a039290921691909102179091556102ae9081906100bf90396000f3006060604052600436106100485763ffffffff60e060020a60003504166338af3eed811461004d57806386d1a69f1461007c578063b91d400114610091578063fc0c546a146100c1575b600080fd5b341561005857600080fd5b6100606100d4565b604051600160a060020a03909116815260200160405180910390f35b341561008757600080fd5b61008f6100e3565b005b341561009c57600080fd5b6100a46101c6565b60405167ffffffffffffffff909116815260200160405180910390f35b34156100cc57600080fd5b6100606101ee565b600154600160a060020a031681565b60015460009067ffffffffffffffff74010000000000000000000000000000000000000000909104811642909116101561011c57600080fd5b60008054600160a060020a0316906370a082319030906040516020015260405160e060020a63ffffffff8416028152600160a060020a039091166004820152602401602060405180830381600087803b151561017757600080fd5b6102c65a03f1151561018857600080fd5b5050506040518051915050600081116101a057600080fd5b6001546000546101c391600160a060020a0391821691168363ffffffff6101fd16565b50565b60015474010000000000000000000000000000000000000000900467ffffffffffffffff1681565b600054600160a060020a031681565b82600160a060020a031663a9059cbb838360006040516020015260405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401602060405180830381600087803b151561025a57600080fd5b6102c65a03f1151561026b57600080fd5b50505060405180519050151561027d57fe5b5050505600a165627a7a723058205a1fdd9d750a8f1788c842c091210356e341e5e4fba386728d5595efc4ad28680029ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a72305820b07a2e65cd57474afc38ab7b1ce5a7e7effe644697c932919f4d62ed5059b2130029\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_BALANCES = "balances";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_BASE_RATE = "BASE_RATE";

    public static final String FUNC_CLOSE = "close";

    public static final String FUNC_ISSUETOKENS = "issueTokens";

    public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_TIMELOCKCONTRACTADDRESS = "timelockContractAddress";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_ISSUETOKENSMULTI = "issueTokensMulti";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_PRICE = "price";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_PURCHASETOKENS = "purchaseTokens";

    public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_TOKENSALECLOSED = "tokenSaleClosed";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_TOKENS_SALE_HARD_CAP = "TOKENS_SALE_HARD_CAP";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected DOR(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DOR(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balances(String param0) {
        final Function function = new Function(FUNC_BALANCES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> BASE_RATE() {
        final Function function = new Function(FUNC_BASE_RATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> close() {
        final Function function = new Function(
                FUNC_CLOSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> issueTokens(String _beneficiary, BigInteger _tokens) {
        final Function function = new Function(
                FUNC_ISSUETOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_beneficiary), 
                new org.web3j.abi.datatypes.generated.Uint256(_tokens)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> decreaseApproval(String _spender, BigInteger _subtractedValue) {
        final Function function = new Function(
                FUNC_DECREASEAPPROVAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_subtractedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> timelockContractAddress() {
        final Function function = new Function(FUNC_TIMELOCKCONTRACTADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> issueTokensMulti(List<String> _addresses, List<BigInteger> _tokens) {
        final Function function = new Function(
                FUNC_ISSUETOKENSMULTI, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_tokens, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> price() {
        final Function function = new Function(FUNC_PRICE, 
                Arrays.<Type>asList(), 
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

    public RemoteCall<TransactionReceipt> purchaseTokens(String _beneficiary, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_PURCHASETOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_beneficiary)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> increaseApproval(String _spender, BigInteger _addedValue) {
        final Function function = new Function(
                FUNC_INCREASEAPPROVAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_addedValue)), 
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

    public RemoteCall<Boolean> tokenSaleClosed() {
        final Function function = new Function(FUNC_TOKENSALECLOSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> TOKENS_SALE_HARD_CAP() {
        final Function function = new Function(FUNC_TOKENS_SALE_HARD_CAP, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<DOR> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DOR.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DOR> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DOR.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
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

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
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

    public Observable<ApprovalEventResponse> approvalEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
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

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public static DOR load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DOR(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DOR load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DOR(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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
