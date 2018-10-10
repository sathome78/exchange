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
 * <p>Generated with web3j version 3.4.0.
 */
public class TGAME extends Contract implements ethTokenERC20{
    public static final String FUNC_CROWDSALEADDRESS = "crowdSaleAddress";
    public static final String FUNC_NAME = "name";
    public static final String FUNC_APPROVE = "approve";
    public static final String FUNC_TOTALSUPPLY = "totalSupply";
    public static final String FUNC_TRANSFERFROM = "transferFrom";
    public static final String FUNC_RETURNTOKENS = "returnTokens";
    public static final String FUNC_DECIMALS = "decimals";
    public static final String FUNC_KILL = "kill";
    public static final String FUNC_VERSION = "version";
    public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_OWNER = "owner";
    public static final String FUNC_SYMBOL = "symbol";
    public static final String FUNC_BURN = "burn";
    public static final String FUNC_UNLOCK = "unlock";
    public static final String FUNC_TRANSFER = "transfer";
    public static final String FUNC_LOCKED = "locked";
    public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";
    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_UPDATECROWDSALEADDRESS = "updateCrowdsaleAddress";
    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";
    public static final String FUNC_LOCK = "lock";
    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    private static final String BINARY = "60606040526040805190810160405280600481526020017f76302e3100000000000000000000000000000000000000000000000000000000815250600590805190602001906200005192919062000251565b5034156200005e57600080fd5b604051602080620022498339810160405280805190602001909190505033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600760006101000a81548160ff0219169083151502179055506af8277896582678ac0000006006819055506040805190810160405280600581526020017f5447414d45000000000000000000000000000000000000000000000000000000815250600290805190602001906200013692919062000251565b506040805190810160405280600581526020017f5447414d45000000000000000000000000000000000000000000000000000000815250600390805190602001906200018492919062000251565b506012600460006101000a81548160ff021916908360ff16021790555080600760016101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060065460086000600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505062000300565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200029457805160ff1916838001178555620002c5565b82800160010185558215620002c5579182015b82811115620002c4578251825591602001919060010190620002a7565b5b509050620002d49190620002d8565b5090565b620002fd91905b80821115620002f9576000816000905550600101620002df565b5090565b90565b611f3980620003106000396000f30060606040523615610126576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630184fc351461012b57806306fdde0314610180578063095ea7b31461020e57806318160ddd1461026857806323b872dd1461029157806326fc4c7f1461030a578063313ce5671461036457806341c0e1b51461039357806354fd4d50146103a8578063661884631461043657806370a08231146104905780638da5cb5b146104dd57806395d89b41146105325780639dc29fac146105c0578063a69df4b51461061a578063a9059cbb1461062f578063cf30901214610689578063d73dd623146106b6578063dd62ed3e14610710578063ea112b6c1461077c578063f2fde38b146107b5578063f83d08ba146107ee575b600080fd5b341561013657600080fd5b61013e610803565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561018b57600080fd5b610193610829565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101d35780820151818401526020810190506101b8565b50505050905090810190601f1680156102005780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561021957600080fd5b61024e600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506108c7565b604051808215151515815260200191505060405180910390f35b341561027357600080fd5b61027b6109b9565b6040518082815260200191505060405180910390f35b341561029c57600080fd5b6102f0600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506109bf565b604051808215151515815260200191505060405180910390f35b341561031557600080fd5b61034a600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610db7565b604051808215151515815260200191505060405180910390f35b341561036f57600080fd5b61037761106e565b604051808260ff1660ff16815260200191505060405180910390f35b341561039e57600080fd5b6103a6611081565b005b34156103b357600080fd5b6103bb611114565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156103fb5780820151818401526020810190506103e0565b50505050905090810190601f1680156104285780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561044157600080fd5b610476600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506111b2565b604051808215151515815260200191505060405180910390f35b341561049b57600080fd5b6104c7600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611443565b6040518082815260200191505060405180910390f35b34156104e857600080fd5b6104f061148c565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561053d57600080fd5b6105456114b2565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561058557808201518184015260208101905061056a565b50505050905090810190601f1680156105b25780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156105cb57600080fd5b610600600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050611550565b604051808215151515815260200191505060405180910390f35b341561062557600080fd5b61062d611712565b005b341561063a57600080fd5b61066f600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506117e5565b604051808215151515815260200191505060405180910390f35b341561069457600080fd5b61069c6119f4565b604051808215151515815260200191505060405180910390f35b34156106c157600080fd5b6106f6600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050611a07565b604051808215151515815260200191505060405180910390f35b341561071b57600080fd5b610766600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611c03565b6040518082815260200191505060405180910390f35b341561078757600080fd5b6107b3600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611c8a565b005b34156107c057600080fd5b6107ec600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611d25565b005b34156107f957600080fd5b610801611df7565b005b600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108bf5780601f10610894576101008083540402835291602001916108bf565b820191906000526020600020905b8154815290600101906020018083116108a257829003601f168201915b505050505081565b600081600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b60065481565b6000600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614158015610a2b5750600760009054906101000a900460ff165b15610a3557600080fd5b81600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410151515610a8357600080fd5b600960008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020548211151515610b0e57600080fd5b610b6082600860008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611eca90919063ffffffff16565b600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610bf582600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611ee390919063ffffffff16565b600860008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610cc782600960008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611eca90919063ffffffff16565b600960008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190509392505050565b6000600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614158015610e655750600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15610e6f57600080fd5b610ec182600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611eca90919063ffffffff16565b600860008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610f788260086000600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611ee390919063ffffffff16565b60086000600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a36001905092915050565b600460009054906101000a900460ff1681565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141561111257600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16ff5b565b60058054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156111aa5780601f1061117f576101008083540402835291602001916111aa565b820191906000526020600020905b81548152906001019060200180831161118d57829003601f168201915b505050505081565b600080600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050808311156112c3576000600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550611357565b6112d68382611eca90919063ffffffff16565b600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b8373ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a3600191505092915050565b6000600860008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156115485780601f1061151d57610100808354040283529160200191611548565b820191906000526020600020905b81548152906001019060200180831161152b57829003601f168201915b505050505081565b6000600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141580156115fe5750600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b1561160857600080fd5b61165a82600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611eca90919063ffffffff16565b600860008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506116b282600654611eca90919063ffffffff16565b60068190555060008373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a36001905092915050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141580156117be5750600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b156117c857600080fd5b6000600760006101000a81548160ff021916908315150217905550565b6000600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141580156118515750600760009054906101000a900460ff165b1561185b57600080fd5b6118ad82600860003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611eca90919063ffffffff16565b600860003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555061194282600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611ee390919063ffffffff16565b600860008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a36001905092915050565b600760009054906101000a900460ff1681565b6000611a9882600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054611ee390919063ffffffff16565b600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a36001905092915050565b6000600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415611d225780600760016101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b50565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415611df457600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16141515611df35780600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b50565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614158015611ea35750600760019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15611ead57600080fd5b6001600760006101000a81548160ff021916908315150217905550565b6000828211151515611ed857fe5b818303905092915050565b6000808284019050838110158015611efb5750828110155b1515611f0357fe5b80915050929150505600a165627a7a7230582068678782213615a8ec0d161550552a29aaafd89c8bf61fcce531f9918963d3d50029000000000000000000000000a7776e2595053414f21f903e5270973d3292b659\n";
    ;

    protected TGAME(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TGAME(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RemoteCall<TGAME> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _crowdSaleAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_crowdSaleAddress)));
        return deployRemoteCall(TGAME.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<TGAME> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _crowdSaleAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_crowdSaleAddress)));
        return deployRemoteCall(TGAME.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static TGAME load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TGAME(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TGAME load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TGAME(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> crowdSaleAddress() {
        final Function function = new Function(FUNC_CROWDSALEADDRESS,
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

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from),
                new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> returnTokens(String _member, BigInteger _value) {
        final Function function = new Function(
                FUNC_RETURNTOKENS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_member),
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

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL,
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

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER,
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

    public RemoteCall<TransactionReceipt> burn(String _member, BigInteger _value) {
        final Function function = new Function(
                FUNC_BURN,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_member),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> unlock() {
        final Function function = new Function(
                FUNC_UNLOCK,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> locked() {
        final Function function = new Function(FUNC_LOCKED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteCall<TransactionReceipt> updateCrowdsaleAddress(String _crowdSaleAddress) {
        final Function function = new Function(
                FUNC_UPDATECROWDSALEADDRESS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_crowdSaleAddress)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> lock() {
        final Function function = new Function(
                FUNC_LOCK,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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
