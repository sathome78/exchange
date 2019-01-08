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
public class MCO extends Contract implements ethTokenERC20{
    private static final String BINARY = "60606040526004805460a060020a60ff02191690556006805460ff191690553462000000576040516200197f3803806200197f83398101604090815281516020830151918301516060840151608085015192850194939093019290915b335b5b60038054600160a060020a03191633600160a060020a03161790555b60088054600160a060020a031916600160a060020a0383161790555b5060038054600160a060020a03191633600160a060020a03161790558451600b8054600082905290917f0175b7a638427703f0dbe7bb9bbf987a2551717b34e79f33b5b1008d1fa01db9602060026101006001861615026000190190941693909304601f9081018490048201938a01908390106200012157805160ff191683800117855562000151565b8280016001018555821562000151579182015b828111156200015157825182559160200191906001019062000134565b5b50620001759291505b808211156200017157600081556001016200015b565b5090565b505083600c9080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620001c557805160ff1916838001178555620001f5565b82800160010185558215620001f5579182015b82811115620001f5578251825591602001919060010190620001d8565b5b50620002199291505b808211156200017157600081556001016200015b565b5090565b50506000838155600d839055600354600160a060020a03168152600160205260408120849055831115620002935760035460005460408051600160a060020a039093168352602083019190915280517f30385c845b448a36257a6a1716e6ad2e1bc2cbe333cde1e69fe849ad6511adfe9281900390910190a15b801515620002b8576006805460ff191660011790556000541515620002b85762000000565b5b5b50505050505b6116af80620002d06000396000f3006060604052361561016f5763ffffffff60e060020a60003504166302f652a3811461017457806305d2035b1461019457806306fdde03146101b5578063095ea7b31461024257806318160ddd1461027257806323b872dd1461029157806329ff4f53146102c7578063313ce567146102e257806340c10f191461030157806342c1867b1461031f578063432146751461034c57806345977d031461036c5780634eee966f1461037e5780635de4ccb0146104105780635f412d4f14610439578063600440cb1461044857806370a08231146104715780638444b3911461049c578063867c2857146104ca5780638da5cb5b146104f757806395d89b411461052057806396132521146105ad5780639738968c146105ce578063a9059cbb146105ef578063c752ff621461061f578063d1f276d31461063e578063d7e7088a14610667578063dd62ed3e14610682578063eefa597b146106b3578063f2fde38b146106d4578063ffeb7d75146106ef575b610000565b3461000057610192600160a060020a0360043516602435151561070a565b005b34610000576101a161076b565b604080519115158252519081900360200190f35b34610000576101c2610774565b604080516020808252835181830152835191928392908301918501908083838215610208575b80518252602083111561020857601f1990920191602091820191016101e8565b505050905090810190601f1680156102345780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34610000576101a1600160a060020a0360043516602435610802565b604080519115158252519081900360200190f35b346100005761027f6108a8565b60408051918252519081900360200190f35b34610000576101a1600160a060020a03600435811690602435166044356108ae565b604080519115158252519081900360200190f35b3461000057610192600160a060020a0360043516610905565b005b346100005761027f61095c565b60408051918252519081900360200190f35b3461000057610192600160a060020a0360043516602435610962565b005b34610000576101a1600160a060020a0360043516610aff565b604080519115158252519081900360200190f35b3461000057610192600160a060020a03600435166024351515610b14565b005b3461000057610192600435610ba6565b005b3461000057610192600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284375050604080516020601f89358b01803591820183900483028401830190945280835297999881019791965091820194509250829150840183828082843750949650610d0595505050505050565b005b346100005761041d610f88565b60408051600160a060020a039092168252519081900360200190f35b3461000057610192610f97565b005b346100005761041d610fcb565b60408051600160a060020a039092168252519081900360200190f35b346100005761027f600160a060020a0360043516610fda565b60408051918252519081900360200190f35b34610000576104a9610ff9565b6040518082600481116100005760ff16815260200191505060405180910390f35b34610000576101a1600160a060020a0360043516611046565b604080519115158252519081900360200190f35b346100005761041d61105b565b60408051600160a060020a039092168252519081900360200190f35b34610000576101c261106a565b604080516020808252835181830152835191928392908301918501908083838215610208575b80518252602083111561020857601f1990920191602091820191016101e8565b505050905090810190601f1680156102345780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34610000576101a16110f8565b604080519115158252519081900360200190f35b34610000576101a1611108565b604080519115158252519081900360200190f35b34610000576101a1600160a060020a036004351660243561112e565b604080519115158252519081900360200190f35b346100005761027f611183565b60408051918252519081900360200190f35b346100005761041d611189565b60408051600160a060020a039092168252519081900360200190f35b3461000057610192600160a060020a0360043516611198565b005b346100005761027f600160a060020a0360043581169060243516611350565b60408051918252519081900360200190f35b34610000576101a161137d565b604080519115158252519081900360200190f35b3461000057610192600160a060020a0360043516611383565b005b3461000057610192600160a060020a03600435166113ce565b005b60035433600160a060020a0390811691161461072557610000565b60045460009060a060020a900460ff161561073f57610000565b600160a060020a0383166000908152600560205260409020805460ff19168315151790555b5b505b5050565b60065460ff1681565b600b805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107fa5780601f106107cf576101008083540402835291602001916107fa565b820191906000526020600020905b8154815290600101906020018083116107dd57829003601f168201915b505050505081565b600081158015906108375750600160a060020a0333811660009081526002602090815260408083209387168352929052205415155b1561084157610000565b600160a060020a03338116600081815260026020908152604080832094881680845294825291829020869055815186815291517f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259281900390910190a35060015b92915050565b60005481565b600454600090849060a060020a900460ff1615156108ed57600160a060020a03811660009081526005602052604090205460ff1615156108ed57610000565b5b6108f985858561141d565b91505b5b509392505050565b60035433600160a060020a0390811691161461092057610000565b60045460009060a060020a900460ff161561093a57610000565b60048054600160a060020a031916600160a060020a0384161790555b5b505b50565b600d5481565b600160a060020a03331660009081526007602052604090205460ff16151561098957610000565b60065460ff161561099957610000565b60005473e7b724d4fe0da10760f27cbbb2bb8db386892b1a6366098d4f9091836000604051602001526040518363ffffffff1660e060020a028152600401808381526020018281526020019250505060206040518083038186803b156100005760325a03f4156100005750506040805180516000908155600160a060020a038616815260016020908152838220549281019190915282517f66098d4f000000000000000000000000000000000000000000000000000000008152600481019290925260248201859052915173e7b724d4fe0da10760f27cbbb2bb8db386892b1a93506366098d4f92604480840193919291829003018186803b156100005760325a03f415610000575050604080518051600160a060020a0386166000818152600160209081528582209390935586845293519094507fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a35b5b5b5050565b60076020526000908152604090205460ff1681565b60035433600160a060020a03908116911614610b2f57610000565b60065460ff1615610b3f57610000565b600160a060020a038216600081815260076020908152604091829020805460ff191685151590811790915582519384529083015280517f4b0adf6c802794c7dde28a08a4e07131abcff3bf9603cd71f14f90bec7865efa9281900390910190a15b5b5b5050565b6000610bb0610ff9565b905060038160048111610000571480610bd157506004816004811161000057145b1515610bdc57610000565b811515610be857610000565b600160a060020a033316600090815260016020526040902054610c0b9083611520565b600160a060020a03331660009081526001602052604081209190915554610c329083611520565b600055600a54610c429083611539565b600a55600954604080517f753e88e5000000000000000000000000000000000000000000000000000000008152600160a060020a033381166004830152602482018690529151919092169163753e88e591604480830192600092919082900301818387803b156100005760325a03f115610000575050600954604080518581529051600160a060020a03928316935033909216917f7e5c344a8141a805725cb476f76c6953b842222b967edd1f78ddb6e8b3f397ac9181900360200190a35b5050565b60035433600160a060020a03908116911614610d2057610000565b81600b9080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610d6c57805160ff1916838001178555610d99565b82800160010185558215610d99579182015b82811115610d99578251825591602001919060010190610d7e565b5b50610dba9291505b80821115610db65760008155600101610da2565b5090565b505080600c9080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610e0857805160ff1916838001178555610e35565b82800160010185558215610e35579182015b82811115610e35578251825591602001919060010190610e1a565b5b50610e569291505b80821115610db65760008155600101610da2565b5090565b505060408051818152600b8054600260001961010060018416150201909116049282018390527fd131ab1e6f279deea74e13a18477e13e2107deb6dc8ae955648948be5841fb46929091600c9181906020820190606083019086908015610efe5780601f10610ed357610100808354040283529160200191610efe565b820191906000526020600020905b815481529060010190602001808311610ee157829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015610f725780601f10610f4757610100808354040283529160200191610f72565b820191906000526020600020905b815481529060010190602001808311610f5557829003601f168201915b505094505050505060405180910390a15b5b5050565b600954600160a060020a031681565b60045433600160a060020a03908116911614610fb257610000565b6006805460ff19166001179055610fc7611561565b5b5b565b600854600160a060020a031681565b600160a060020a0381166000908152600160205260409020545b919050565b6000611003611108565b151561101157506001611040565b600954600160a060020a0316151561102b57506002611040565b600a54151561103c57506003611040565b5060045b5b5b5b90565b60056020526000908152604090205460ff1681565b600354600160a060020a031681565b600c805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107fa5780601f106107cf576101008083540402835291602001916107fa565b820191906000526020600020905b8154815290600101906020018083116107dd57829003601f168201915b505050505081565b60045460a060020a900460ff1681565b60045460009060a060020a900460ff168015611127575061112761137d565b5b90505b90565b600454600090339060a060020a900460ff16151561116d57600160a060020a03811660009081526005602052604090205460ff16151561116d57610000565b5b61117884846115aa565b91505b5b5092915050565b600a5481565b600454600160a060020a031681565b6111a0611108565b15156111ab57610000565b600160a060020a03811615156111c057610000565b60085433600160a060020a039081169116146111db57610000565b60046111e5610ff9565b600481116100005714156111f857610000565b60098054600160a060020a031916600160a060020a038381169190911791829055604080516000602091820181905282517f61d3d7a6000000000000000000000000000000000000000000000000000000008152925194909316936361d3d7a6936004808501948390030190829087803b156100005760325a03f115610000575050604051511515905061128b57610000565b6000805460095460408051602090810185905281517f4b2ba0dd00000000000000000000000000000000000000000000000000000000815291519394600160a060020a0390931693634b2ba0dd936004808501948390030190829087803b156100005760325a03f1156100005750506040515191909114905061130d57610000565b60095460408051600160a060020a039092168252517f7845d5aa74cc410e35571258d954f23b82276e160fe8c188fa80566580f279cc9181900360200190a15b50565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b60015b90565b60035433600160a060020a0390811691161461139e57610000565b600160a060020a038116156109595760038054600160a060020a031916600160a060020a0383161790555b5b5b50565b600160a060020a03811615156113e357610000565b60085433600160a060020a039081169116146113fe57610000565b60088054600160a060020a031916600160a060020a0383161790555b50565b600160a060020a03808416600090815260026020908152604080832033851684528252808320549386168352600190915281205490919061145e9084611539565b600160a060020a03808616600090815260016020526040808220939093559087168152205461148d9084611520565b600160a060020a0386166000908152600160205260409020556114b08184611520565b600160a060020a038087166000818152600260209081526040808320338616845282529182902094909455805187815290519288169391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a3600191505b509392505050565b600061152e83831115611673565b508082035b92915050565b60008282016115568482108015906115515750838210155b611673565b8091505b5092915050565b60045433600160a060020a0390811691161461157c57610000565b6004805474ff0000000000000000000000000000000000000000191660a060020a1790555b5b565b60015b90565b6000604060443610156115bc57610000565b600160a060020a0333166000908152600160205260409020546115df9084611520565b600160a060020a03338116600090815260016020526040808220939093559086168152205461160e9084611539565b600160a060020a038086166000818152600160209081526040918290209490945580518781529051919333909316927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a3600191505b5b5092915050565b80151561095957610000565b5b505600a165627a7a72305820660b1db3b12de391591db9b9d27a4c59035907a36d4b21ed3bf6bf35b2ae04b0002900000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000003585858000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000035959590000000000000000000000000000000000000000000000000000000000\n";

    public static final String FUNC_SETTRANSFERAGENT = "setTransferAgent";

    public static final String FUNC_MINTINGFINISHED = "mintingFinished";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_SETRELEASEAGENT = "setReleaseAgent";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_MINTAGENTS = "mintAgents";

    public static final String FUNC_SETMINTAGENT = "setMintAgent";

    public static final String FUNC_UPGRADE = "upgrade";

    public static final String FUNC_SETTOKENINFORMATION = "setTokenInformation";

    public static final String FUNC_UPGRADEAGENT = "upgradeAgent";

    public static final String FUNC_RELEASETOKENTRANSFER = "releaseTokenTransfer";

    public static final String FUNC_UPGRADEMASTER = "upgradeMaster";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_GETUPGRADESTATE = "getUpgradeState";

    public static final String FUNC_TRANSFERAGENTS = "transferAgents";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_RELEASED = "released";

    public static final String FUNC_CANUPGRADE = "canUpgrade";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TOTALUPGRADED = "totalUpgraded";

    public static final String FUNC_RELEASEAGENT = "releaseAgent";

    public static final String FUNC_SETUPGRADEAGENT = "setUpgradeAgent";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_ISTOKEN = "isToken";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_SETUPGRADEMASTER = "setUpgradeMaster";

    public static final Event UPDATEDTOKENINFORMATION_EVENT = new Event("UpdatedTokenInformation", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event UPGRADE_EVENT = new Event("Upgrade", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event UPGRADEAGENTSET_EVENT = new Event("UpgradeAgentSet", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event MINTINGAGENTCHANGED_EVENT = new Event("MintingAgentChanged", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event MINTED_EVENT = new Event("Minted", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected MCO(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MCO(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> setTransferAgent(String addr, Boolean state) {
        final Function function = new Function(
                FUNC_SETTRANSFERAGENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr), 
                new org.web3j.abi.datatypes.Bool(state)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> mintingFinished() {
        final Function function = new Function(FUNC_MINTINGFINISHED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteCall<TransactionReceipt> setReleaseAgent(String addr) {
        final Function function = new Function(
                FUNC_SETRELEASEAGENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> mint(String receiver, BigInteger amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(receiver), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> mintAgents(String param0) {
        final Function function = new Function(FUNC_MINTAGENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> setMintAgent(String addr, Boolean state) {
        final Function function = new Function(
                FUNC_SETMINTAGENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr), 
                new org.web3j.abi.datatypes.Bool(state)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> upgrade(BigInteger value) {
        final Function function = new Function(
                FUNC_UPGRADE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setTokenInformation(String _name, String _symbol) {
        final Function function = new Function(
                FUNC_SETTOKENINFORMATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> upgradeAgent() {
        final Function function = new Function(FUNC_UPGRADEAGENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> releaseTokenTransfer() {
        final Function function = new Function(
                FUNC_RELEASETOKENTRANSFER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> upgradeMaster() {
        final Function function = new Function(FUNC_UPGRADEMASTER, 
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

    public RemoteCall<BigInteger> getUpgradeState() {
        final Function function = new Function(FUNC_GETUPGRADESTATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> transferAgents(String param0) {
        final Function function = new Function(FUNC_TRANSFERAGENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteCall<Boolean> released() {
        final Function function = new Function(FUNC_RELEASED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Boolean> canUpgrade() {
        final Function function = new Function(FUNC_CANUPGRADE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalUpgraded() {
        final Function function = new Function(FUNC_TOTALUPGRADED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> releaseAgent() {
        final Function function = new Function(FUNC_RELEASEAGENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> setUpgradeAgent(String agent) {
        final Function function = new Function(
                FUNC_SETUPGRADEAGENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(agent)), 
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

    public RemoteCall<Boolean> isToken() {
        final Function function = new Function(FUNC_ISTOKEN, 
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

    public RemoteCall<TransactionReceipt> setUpgradeMaster(String master) {
        final Function function = new Function(
                FUNC_SETUPGRADEMASTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(master)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<MCO> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable)));
        return deployRemoteCall(MCO.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<MCO> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable)));
        return deployRemoteCall(MCO.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<UpdatedTokenInformationEventResponse> getUpdatedTokenInformationEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UPDATEDTOKENINFORMATION_EVENT, transactionReceipt);
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

    public Observable<UpdatedTokenInformationEventResponse> updatedTokenInformationEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpdatedTokenInformationEventResponse>() {
            @Override
            public UpdatedTokenInformationEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UPDATEDTOKENINFORMATION_EVENT, log);
                UpdatedTokenInformationEventResponse typedResponse = new UpdatedTokenInformationEventResponse();
                typedResponse.log = log;
                typedResponse.newName = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newSymbol = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<UpdatedTokenInformationEventResponse> updatedTokenInformationEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPDATEDTOKENINFORMATION_EVENT));
        return updatedTokenInformationEventObservable(filter);
    }

    public List<UpgradeEventResponse> getUpgradeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UPGRADE_EVENT, transactionReceipt);
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

    public Observable<UpgradeEventResponse> upgradeEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpgradeEventResponse>() {
            @Override
            public UpgradeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UPGRADE_EVENT, log);
                UpgradeEventResponse typedResponse = new UpgradeEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<UpgradeEventResponse> upgradeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPGRADE_EVENT));
        return upgradeEventObservable(filter);
    }

    public List<UpgradeAgentSetEventResponse> getUpgradeAgentSetEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UPGRADEAGENTSET_EVENT, transactionReceipt);
        ArrayList<UpgradeAgentSetEventResponse> responses = new ArrayList<UpgradeAgentSetEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpgradeAgentSetEventResponse typedResponse = new UpgradeAgentSetEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.agent = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UpgradeAgentSetEventResponse> upgradeAgentSetEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpgradeAgentSetEventResponse>() {
            @Override
            public UpgradeAgentSetEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UPGRADEAGENTSET_EVENT, log);
                UpgradeAgentSetEventResponse typedResponse = new UpgradeAgentSetEventResponse();
                typedResponse.log = log;
                typedResponse.agent = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<UpgradeAgentSetEventResponse> upgradeAgentSetEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPGRADEAGENTSET_EVENT));
        return upgradeAgentSetEventObservable(filter);
    }

    public List<MintingAgentChangedEventResponse> getMintingAgentChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINTINGAGENTCHANGED_EVENT, transactionReceipt);
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

    public Observable<MintingAgentChangedEventResponse> mintingAgentChangedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintingAgentChangedEventResponse>() {
            @Override
            public MintingAgentChangedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINTINGAGENTCHANGED_EVENT, log);
                MintingAgentChangedEventResponse typedResponse = new MintingAgentChangedEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.state = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MintingAgentChangedEventResponse> mintingAgentChangedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTINGAGENTCHANGED_EVENT));
        return mintingAgentChangedEventObservable(filter);
    }

    public List<MintedEventResponse> getMintedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINTED_EVENT, transactionReceipt);
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

    public Observable<MintedEventResponse> mintedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintedEventResponse>() {
            @Override
            public MintedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINTED_EVENT, log);
                MintedEventResponse typedResponse = new MintedEventResponse();
                typedResponse.log = log;
                typedResponse.receiver = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MintedEventResponse> mintedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTED_EVENT));
        return mintedEventObservable(filter);
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

    public static MCO load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MCO(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MCO load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MCO(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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

    public static class MintingAgentChangedEventResponse {
        public Log log;

        public String addr;

        public Boolean state;
    }

    public static class MintedEventResponse {
        public Log log;

        public String receiver;

        public BigInteger amount;
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
