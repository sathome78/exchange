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
public class LEDU extends Contract implements ethTokenERC20{
    public static final String FUNC_NAME = "name";
    public static final String FUNC_APPROVE = "approve";
    public static final String FUNC_TOTALSUPPLY = "totalSupply";
    public static final String FUNC_TRANSFERFROM = "transferFrom";
    public static final String FUNC_DECIMALS = "decimals";
    public static final String FUNC_UNPAUSE = "unpause";
    public static final String FUNC_BURN = "burn";
    public static final String FUNC_FINALIZE = "finalize";
    public static final String FUNC_MOTD = "motd";
    public static final String FUNC_PAUSED = "paused";
    public static final String FUNC_SETMOTD = "setMotd";
    public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";
    public static final String FUNC_CLAIMTOKENS = "claimTokens";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";
    public static final String FUNC_PAUSE = "pause";
    public static final String FUNC_OWNER = "owner";
    public static final String FUNC_CONTROLLERAPPROVE = "controllerApprove";
    public static final String FUNC_SETCONTROLLER = "setController";
    public static final String FUNC_SYMBOL = "symbol";
    public static final String FUNC_CONTROLLERTRANSFER = "controllerTransfer";
    public static final String FUNC_CHANGEOWNER = "changeOwner";
    public static final String FUNC_TRANSFER = "transfer";
    public static final String FUNC_FINALIZED = "finalized";
    public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";
    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_CONTROLLER = "controller";
    public static final Event MOTD_EVENT = new Event("Motd",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    private static final String BINARY = "60606040525b60008054600160a060020a03191633600160a060020a03161790555b5b6115cf806100316000396000f300606060405236156101435763ffffffff60e060020a60003504166306fdde038114610148578063095ea7b3146101d357806318160ddd1461020957806323b872dd1461022e578063313ce5671461026a5780633f4ba83a1461029357806342966c68146102a85780634bb278f3146102c05780635aab4ac8146102d55780635c975abb146103605780635fe59b9d1461038757806366188463146103da57806369ffa08a1461041057806370a082311461044957806379ba50971461047a5780638456cb591461048f5780638da5cb5b146104a45780638e339b66146104d357806392eefe9b146104fd57806395d89b411461051e5780639b504387146105a9578063a6f9dae1146105d3578063a9059cbb146105f4578063b3f05b971461062a578063d73dd62314610651578063dd62ed3e14610687578063f77c4791146106be575b600080fd5b341561015357600080fd5b61015b6106ed565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101985780820151818401525b60200161017f565b50505050905090810190601f1680156101c55780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101de57600080fd5b6101f5600160a060020a0360043516602435610724565b604051901515815260200160405180910390f35b341561021457600080fd5b61021c610822565b60405190815260200160405180910390f35b341561023957600080fd5b6101f5600160a060020a036004358116906024351660443561088c565b604051901515815260200160405180910390f35b341561027557600080fd5b61027d610992565b60405160ff909116815260200160405180910390f35b341561029e57600080fd5b6102a6610997565b005b34156102b357600080fd5b6102a66004356109d5565b005b34156102cb57600080fd5b6102a6610a88565b005b34156102e057600080fd5b61015b610adc565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101985780820151818401525b60200161017f565b50505050905090810190601f1680156101c55780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561036b57600080fd5b6101f5610b7a565b604051901515815260200160405180910390f35b341561039257600080fd5b6102a660046024813581810190830135806020601f82018190048102016040519081016040528181529291906020840183838082843750949650610b8a95505050505050565b005b34156103e557600080fd5b6101f5600160a060020a0360043516602435610c57565b604051901515815260200160405180910390f35b341561041b57600080fd5b6101f5600160a060020a0360043581169060243516610dd5565b604051901515815260200160405180910390f35b341561045457600080fd5b61021c600160a060020a0360043516610edf565b60405190815260200160405180910390f35b341561048557600080fd5b6102a6610f5c565b005b341561049a57600080fd5b6102a6610fa6565b005b34156104af57600080fd5b6104b7610fea565b604051600160a060020a03909116815260200160405180910390f35b34156104de57600080fd5b6102a6600160a060020a0360043581169060243516604435610ff9565b005b341561050857600080fd5b6102a6600160a060020a036004351661104d565b005b341561052957600080fd5b61015b6110be565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101985780820151818401525b60200161017f565b50505050905090810190601f1680156101c55780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156105b457600080fd5b6102a6600160a060020a03600435811690602435166044356110f5565b005b34156105de57600080fd5b6102a6600160a060020a0360043516611149565b005b34156105ff57600080fd5b6101f5600160a060020a0360043516602435611191565b604051901515815260200160405180910390f35b341561063557600080fd5b6101f561128f565b604051901515815260200160405180910390f35b341561065c57600080fd5b6101f5600160a060020a03600435166024356112b0565b604051901515815260200160405180910390f35b341561069257600080fd5b61021c600160a060020a036004358116906024351661142e565b60405190815260200160405180910390f35b34156106c957600080fd5b6104b76114b4565b604051600160a060020a03909116815260200160405180910390f35b60408051908101604052600981527f456475636174696f6e0000000000000000000000000000000000000000000000602082015281565b60006002604436101561073357fe5b60015460a860020a900460ff161561074a57600080fd5b600254600160a060020a031663e1f21c6733868660006040516020015260405160e060020a63ffffffff8616028152600160a060020a0393841660048201529190921660248201526044810191909152606401602060405180830381600087803b15156107b657600080fd5b6102c65a03f115156107c757600080fd5b50505060405180519050156108145783600160a060020a031633600160a060020a03166000805160206115848339815191528560405190815260200160405180910390a360019150610819565b600091505b5b5b5092915050565b600254600090600160a060020a03166318160ddd82604051602001526040518163ffffffff1660e060020a028152600401602060405180830381600087803b151561086c57600080fd5b6102c65a03f1151561087d57600080fd5b50505060405180519150505b90565b60006003606436101561089b57fe5b60015460a860020a900460ff16156108b257600080fd5b600254600160a060020a03166315dacbea3387878760006040516020015260405160e060020a63ffffffff8716028152600160a060020a0394851660048201529284166024840152921660448201526064810191909152608401602060405180830381600087803b151561092557600080fd5b6102c65a03f1151561093657600080fd5b50505060405180519050156109835783600160a060020a031685600160a060020a03166000805160206115648339815191528560405190815260200160405180910390a360019150610988565b600091505b5b5b509392505050565b600881565b60005433600160a060020a039081169116146109b257600080fd5b6001805475ff000000000000000000000000000000000000000000191690555b5b565b60015460a860020a900460ff16156109ec57600080fd5b600254600160a060020a0316639dc29fac338360405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401600060405180830381600087803b1515610a4257600080fd5b6102c65a03f11515610a5357600080fd5b505050600033600160a060020a03166000805160206115648339815191528360405190815260200160405180910390a35b5b50565b60005433600160a060020a03908116911614610aa357600080fd5b6001805474ff00000000000000000000000000000000000000001916740100000000000000000000000000000000000000001790555b5b565b60038054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610b725780601f10610b4757610100808354040283529160200191610b72565b820191906000526020600020905b815481529060010190602001808311610b5557829003601f168201915b505050505081565b60015460a860020a900460ff1681565b60005433600160a060020a03908116911614610ba557600080fd5b6003818051610bb89291602001906114c3565b507f6e7666d68b6b7c619b2fe5a2c3dd0564bf3e02b0508b217d7a28ce5805583eab8160405160208082528190810183818151815260200191508051906020019080838360005b83811015610c185780820151818401525b602001610bff565b50505050905090810190601f168015610c455780820380516001836020036101000a031916815260200191505b509250505060405180910390a15b5b50565b60008060026044361015610c6757fe5b60015460a860020a900460ff1615610c7e57600080fd5b600254600160a060020a031663f019c26733878760006040516020015260405160e060020a63ffffffff8616028152600160a060020a0393841660048201529190921660248201526044810191909152606401602060405180830381600087803b1515610cea57600080fd5b6102c65a03f11515610cfb57600080fd5b5050506040518051905015610dc657600254600160a060020a031663dd62ed3e338760006040516020015260405160e060020a63ffffffff8516028152600160a060020a03928316600482015291166024820152604401602060405180830381600087803b1515610d6b57600080fd5b6102c65a03f11515610d7c57600080fd5b50505060405180519050915084600160a060020a031633600160a060020a03166000805160206115848339815191528460405190815260200160405180910390a360019250610dcb565b600092505b5b5b505092915050565b60008054819033600160a060020a03908116911614610df357600080fd5b5082600160a060020a03811663a9059cbb84826370a082313060006040516020015260405160e060020a63ffffffff8416028152600160a060020a039091166004820152602401602060405180830381600087803b1515610e5357600080fd5b6102c65a03f11515610e6457600080fd5b5050506040518051905060006040516020015260405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401602060405180830381600087803b1515610eba57600080fd5b6102c65a03f11515610ecb57600080fd5b50505060405180519250505b5b5092915050565b600254600090600160a060020a03166370a0823183836040516020015260405160e060020a63ffffffff8416028152600160a060020a039091166004820152602401602060405180830381600087803b1515610f3a57600080fd5b6102c65a03f11515610f4b57600080fd5b50505060405180519150505b919050565b60015433600160a060020a03908116911614156109d2576001546000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911790555b5b565b60005433600160a060020a03908116911614610fc157600080fd5b6001805475ff000000000000000000000000000000000000000000191660a860020a1790555b5b565b600054600160a060020a031681565b60025433600160a060020a0390811691161461101157fe5b81600160a060020a031683600160a060020a03166000805160206115848339815191528360405190815260200160405180910390a35b5b505050565b60005433600160a060020a0390811691161461106857600080fd5b60015474010000000000000000000000000000000000000000900460ff161561109057600080fd5b6002805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b5b50565b60408051908101604052600381527f4544550000000000000000000000000000000000000000000000000000000000602082015281565b60025433600160a060020a0390811691161461110d57fe5b81600160a060020a031683600160a060020a03166000805160206115648339815191528360405190815260200160405180910390a35b5b505050565b60005433600160a060020a0390811691161461116457600080fd5b6001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b50565b6000600260443610156111a057fe5b60015460a860020a900460ff16156111b757600080fd5b600254600160a060020a031663beabacc833868660006040516020015260405160e060020a63ffffffff8616028152600160a060020a0393841660048201529190921660248201526044810191909152606401602060405180830381600087803b151561122357600080fd5b6102c65a03f1151561123457600080fd5b50505060405180519050156108145783600160a060020a031633600160a060020a03166000805160206115648339815191528560405190815260200160405180910390a360019150610819565b600091505b5b5b5092915050565b60015474010000000000000000000000000000000000000000900460ff1681565b600080600260443610156112c057fe5b60015460a860020a900460ff16156112d757600080fd5b600254600160a060020a031663bcdd612133878760006040516020015260405160e060020a63ffffffff8616028152600160a060020a0393841660048201529190921660248201526044810191909152606401602060405180830381600087803b1515610cea57600080fd5b6102c65a03f11515610cfb57600080fd5b5050506040518051905015610dc657600254600160a060020a031663dd62ed3e338760006040516020015260405160e060020a63ffffffff8516028152600160a060020a03928316600482015291166024820152604401602060405180830381600087803b1515610d6b57600080fd5b6102c65a03f11515610d7c57600080fd5b50505060405180519050915084600160a060020a031633600160a060020a03166000805160206115848339815191528460405190815260200160405180910390a360019250610dcb565b600092505b5b5b505092915050565b600254600090600160a060020a031663dd62ed3e8484846040516020015260405160e060020a63ffffffff8516028152600160a060020a03928316600482015291166024820152604401602060405180830381600087803b151561149157600080fd5b6102c65a03f115156114a257600080fd5b50505060405180519150505b92915050565b600254600160a060020a031681565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061150457805160ff1916838001178555611531565b82800160010185558215611531579182015b82811115611531578251825591602001919060010190611516565b5b5061153e929150611542565b5090565b61088991905b8082111561153e5760008155600101611548565b5090565b905600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925a165627a7a72305820a9fcb3e51723cc1d95c02fe2cbb16a9458ab670aa1853441666c3f8c8a79b5290029\n"
            + "\n";
    ;

    protected LEDU(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected LEDU(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RemoteCall<LEDU> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LEDU.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<LEDU> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LEDU.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static LEDU load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new LEDU(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static LEDU load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new LEDU(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> unpause() {
        final Function function = new Function(
                FUNC_UNPAUSE,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger _amount) {
        final Function function = new Function(
                FUNC_BURN,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doFinalize() {
        final Function function = new Function(
                FUNC_FINALIZE,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> motd() {
        final Function function = new Function(FUNC_MOTD,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> paused() {
        final Function function = new Function(FUNC_PAUSED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> setMotd(String _m) {
        final Function function = new Function(
                FUNC_SETMOTD,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_m)),
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

    public RemoteCall<TransactionReceipt> claimTokens(String _token, String _to) {
        final Function function = new Function(
                FUNC_CLAIMTOKENS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_token),
                new org.web3j.abi.datatypes.Address(_to)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String a) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(a)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                FUNC_ACCEPTOWNERSHIP,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> pause() {
        final Function function = new Function(
                FUNC_PAUSE,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> controllerApprove(String _owner, String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_CONTROLLERAPPROVE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner),
                new org.web3j.abi.datatypes.Address(_spender),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setController(String _c) {
        final Function function = new Function(
                FUNC_SETCONTROLLER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_c)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> controllerTransfer(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_CONTROLLERTRANSFER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from),
                new org.web3j.abi.datatypes.Address(_to),
                new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeOwner(String _newOwner) {
        final Function function = new Function(
                FUNC_CHANGEOWNER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)),
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

    public RemoteCall<Boolean> finalized() {
        final Function function = new Function(FUNC_FINALIZED,
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

    public RemoteCall<String> controller() {
        final Function function = new Function(FUNC_CONTROLLER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public List<MotdEventResponse> getMotdEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MOTD_EVENT, transactionReceipt);
        ArrayList<MotdEventResponse> responses = new ArrayList<MotdEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MotdEventResponse typedResponse = new MotdEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.message = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MotdEventResponse> motdEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MotdEventResponse>() {
            @Override
            public MotdEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MOTD_EVENT, log);
                MotdEventResponse typedResponse = new MotdEventResponse();
                typedResponse.log = log;
                typedResponse.message = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MotdEventResponse> motdEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MOTD_EVENT));
        return motdEventObservable(filter);
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

    public static class MotdEventResponse {
        public Log log;

        public String message;
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
