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
public class MGX extends Contract implements ethTokenERC20{
    private static final String BINARY = "60606040526005805460a060020a60ff02191690556007805460ff1916905534156200002a57600080fd5b60405162001ad538038062001ad58339810160405280805182019190602001805182019190602001805191906020018051919060200180519150505b335b5b60038054600160a060020a03191633600160a060020a03161790555b60098054600160a060020a031916600160a060020a0383161790555b5060038054600160a060020a03191633600160a060020a0316179055600c858051620000d2929160200190620001a4565b50600d848051620000e8929160200190620001a4565b506000838155600e805460ff191660ff8516179055600354600160a060020a0316815260016020526040812084905583111562000173576003546000547f30385c845b448a36257a6a1716e6ad2e1bc2cbe333cde1e69fe849ad6511adfe91600160a060020a031690604051600160a060020a03909216825260208201526040908101905180910390a15b80151562000197576007805460ff1916600117905560005415156200019757600080fd5b5b5b50505050506200024e565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620001e757805160ff191683800117855562000217565b8280016001018555821562000217579182015b8281111562000217578251825591602001919060010190620001fa565b5b50620002269291506200022a565b5090565b6200024b91905b8082111562000226576000815560010162000231565b5090565b90565b611877806200025e6000396000f3006060604052361561019b5763ffffffff60e060020a60003504166302f652a381146101a057806305d2035b146101c657806306fdde03146101ed578063095ea7b31461027857806318160ddd146102ae57806323b872dd146102d357806329ff4f531461030f578063313ce5671461033057806340c10f191461035957806342c1867b1461037d57806343214675146103b057806345977d03146103d65780634eee966f146103ee5780635de4ccb0146104835780635f412d4f146104b2578063600440cb146104c757806370a08231146104f657806379ba5097146105275780638444b3911461053c578063867c2857146105735780638da5cb5b146105a657806395d89b41146105d557806396132521146106605780639738968c14610687578063a293d1e8146106ae578063a9059cbb146106d9578063c752ff621461070f578063d05c78da14610734578063d1f276d31461075f578063d4ee1d901461078e578063d7e7088a146107bd578063dd62ed3e146107de578063e6cb901314610815578063f2fde38b14610840578063ffeb7d7514610861575b600080fd5b34156101ab57600080fd5b6101c4600160a060020a03600435166024351515610882565b005b34156101d157600080fd5b6101d96108e3565b604051901515815260200160405180910390f35b34156101f857600080fd5b6102006108ec565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561023d5780820151818401525b602001610224565b50505050905090810190601f16801561026a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561028357600080fd5b6101d9600160a060020a036004351660243561098a565b604051901515815260200160405180910390f35b34156102b957600080fd5b6102c1610a33565b60405190815260200160405180910390f35b34156102de57600080fd5b6101d9600160a060020a0360043581169060243516604435610a39565b604051901515815260200160405180910390f35b341561031a57600080fd5b6101c4600160a060020a0360043516610a90565b005b341561033b57600080fd5b610343610af4565b60405160ff909116815260200160405180910390f35b341561036457600080fd5b6101c4600160a060020a0360043516602435610afd565b005b341561038857600080fd5b6101d9600160a060020a0360043516610bbe565b604051901515815260200160405180910390f35b34156103bb57600080fd5b6101c4600160a060020a03600435166024351515610bd3565b005b34156103e157600080fd5b6101c4600435610c72565b005b34156103f957600080fd5b6101c460046024813581810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f016020809104026020016040519081016040528181529291906020840183838082843750949650610dcc95505050505050565b005b341561048e57600080fd5b610496610f3c565b604051600160a060020a03909116815260200160405180910390f35b34156104bd57600080fd5b6101c4610f4b565b005b34156104d257600080fd5b610496610f7f565b604051600160a060020a03909116815260200160405180910390f35b341561050157600080fd5b6102c1600160a060020a0360043516610f8e565b60405190815260200160405180910390f35b341561053257600080fd5b6101c4610fad565b005b341561054757600080fd5b61054f611039565b6040518082600481111561055f57fe5b60ff16815260200191505060405180910390f35b341561057e57600080fd5b6101d9600160a060020a0360043516611086565b604051901515815260200160405180910390f35b34156105b157600080fd5b61049661109b565b604051600160a060020a03909116815260200160405180910390f35b34156105e057600080fd5b6102006110aa565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561023d5780820151818401525b602001610224565b50505050905090810190601f16801561026a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561066b57600080fd5b6101d9611148565b604051901515815260200160405180910390f35b341561069257600080fd5b6101d9611158565b604051901515815260200160405180910390f35b34156106b957600080fd5b6102c160043560243561117e565b60405190815260200160405180910390f35b34156106e457600080fd5b6101d9600160a060020a0360043516602435611195565b604051901515815260200160405180910390f35b341561071a57600080fd5b6102c16111ea565b60405190815260200160405180910390f35b341561073f57600080fd5b6102c16004356024356111f0565b60405190815260200160405180910390f35b341561076a57600080fd5b61049661121f565b604051600160a060020a03909116815260200160405180910390f35b341561079957600080fd5b61049661122e565b604051600160a060020a03909116815260200160405180910390f35b34156107c857600080fd5b6101c4600160a060020a036004351661123d565b005b34156107e957600080fd5b6102c1600160a060020a03600435811690602435166113f6565b60405190815260200160405180910390f35b341561082057600080fd5b6102c1600435602435611423565b60405190815260200160405180910390f35b341561084b57600080fd5b6101c4600160a060020a036004351661143d565b005b341561086c57600080fd5b6101c4600160a060020a0360043516611485565b005b60035433600160a060020a0390811691161461089d57600080fd5b60055460009060a060020a900460ff16156108b757600080fd5b600160a060020a0383166000908152600660205260409020805460ff19168315151790555b5b505b5050565b60075460ff1681565b600c8054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109825780601f1061095757610100808354040283529160200191610982565b820191906000526020600020905b81548152906001019060200180831161096557829003601f168201915b505050505081565b600081158015906109bf5750600160a060020a0333811660009081526002602090815260408083209387168352929052205415155b156109c957600080fd5b600160a060020a03338116600081815260026020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a35060015b92915050565b60005481565b600554600090849060a060020a900460ff161515610a7857600160a060020a03811660009081526006602052604090205460ff161515610a7857600080fd5b5b610a848585856114e1565b91505b5b509392505050565b60035433600160a060020a03908116911614610aab57600080fd5b60055460009060a060020a900460ff1615610ac557600080fd5b6005805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0384161790555b5b505b50565b600e5460ff1681565b600160a060020a03331660009081526008602052604090205460ff161515610b2457600080fd5b60075460ff1615610b3457600080fd5b610b4060005482611423565b6000908155600160a060020a038316815260016020526040902054610b659082611423565b600160a060020a0383166000818152600160205260408082209390935590917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9084905190815260200160405180910390a35b5b5b5050565b60086020526000908152604090205460ff1681565b60035433600160a060020a03908116911614610bee57600080fd5b60075460ff1615610bfe57600080fd5b600160a060020a03821660009081526008602052604090819020805460ff19168315151790557f4b0adf6c802794c7dde28a08a4e07131abcff3bf9603cd71f14f90bec7865efa908390839051600160a060020a039092168252151560208201526040908101905180910390a15b5b5b5050565b6000610c7c611039565b905060035b816004811115610c8d57fe5b1480610ca5575060045b816004811115610ca357fe5b145b1515610cb057600080fd5b811515610cbc57600080fd5b600160a060020a033316600090815260016020526040902054610cdf908361117e565b600160a060020a03331660009081526001602052604081209190915554610d06908361117e565b600055600b54610d169083611423565b600b55600a54600160a060020a031663753e88e5338460405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401600060405180830381600087803b1515610d6f57600080fd5b6102c65a03f11515610d8057600080fd5b5050600a54600160a060020a03908116915033167f7e5c344a8141a805725cb476f76c6953b842222b967edd1f78ddb6e8b3f397ac8460405190815260200160405180910390a35b5050565b60035433600160a060020a03908116911614610de757600080fd5b600c828051610dfa9291602001906117ab565b50600d818051610e0e9291602001906117ab565b507fd131ab1e6f279deea74e13a18477e13e2107deb6dc8ae955648948be5841fb46600c600d604051604080825283546002600019610100600184161502019091160490820181905281906020820190606083019086908015610eb25780601f10610e8757610100808354040283529160200191610eb2565b820191906000526020600020905b815481529060010190602001808311610e9557829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015610f265780601f10610efb57610100808354040283529160200191610f26565b820191906000526020600020905b815481529060010190602001808311610f0957829003601f168201915b505094505050505060405180910390a15b5b5050565b600a54600160a060020a031681565b60055433600160a060020a03908116911614610f6657600080fd5b6007805460ff19166001179055610f7b61164e565b5b5b565b600954600160a060020a031681565b600160a060020a0381166000908152600160205260409020545b919050565b60045433600160a060020a03908116911614610fc857600080fd5b600454600354600160a060020a0391821691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36004546003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911790555b565b6000611043611158565b151561105157506001611080565b600a54600160a060020a0316151561106b57506002611080565b600b54151561107c57506003611080565b5060045b5b5b5b90565b60066020526000908152604090205460ff1681565b600354600160a060020a031681565b600d8054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109825780601f1061095757610100808354040283529160200191610982565b820191906000526020600020905b81548152906001019060200180831161096557829003601f168201915b505050505081565b60055460a060020a900460ff1681565b60055460009060a060020a900460ff1680156111775750611177611691565b5b90505b90565b60008282111561118a57fe5b508082035b92915050565b600554600090339060a060020a900460ff1615156111d457600160a060020a03811660009081526006602052604090205460ff1615156111d457600080fd5b5b6111df8484611697565b91505b5b5092915050565b600b5481565b600082820283158061120c575082848281151561120957fe5b04145b151561121457fe5b8091505b5092915050565b600554600160a060020a031681565b600454600160a060020a031681565b611245611158565b151561125057600080fd5b600160a060020a038116151561126557600080fd5b60095433600160a060020a0390811691161461128057600080fd5b60045b61128b611039565b600481111561129657fe5b14156112a157600080fd5b600a805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038381169190911791829055166361d3d7a66000604051602001526040518163ffffffff1660e060020a028152600401602060405180830381600087803b151561130c57600080fd5b6102c65a03f1151561131d57600080fd5b50505060405180519050151561133257600080fd5b60008054600a549091600160a060020a0390911690634b2ba0dd90604051602001526040518163ffffffff1660e060020a028152600401602060405180830381600087803b151561138257600080fd5b6102c65a03f1151561139357600080fd5b505050604051805190501415156113a957600080fd5b600a547f7845d5aa74cc410e35571258d954f23b82276e160fe8c188fa80566580f279cc90600160a060020a0316604051600160a060020a03909116815260200160405180910390a15b50565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b60008282018381101561121457fe5b8091505b5092915050565b60035433600160a060020a0390811691161461145857600080fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b50565b600160a060020a038116151561149a57600080fd5b60095433600160a060020a039081169116146114b557600080fd5b6009805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b50565b600160a060020a0380841660008181526002602090815260408083203390951683529381528382205492825260019052918220548390108015906115255750828110155b80156115315750600083115b80156115565750600160a060020a038416600090815260016020526040902054838101115b1561163c57600160a060020a03841660009081526001602052604090205461157e9084611423565b600160a060020a0380861660009081526001602052604080822093909355908716815220546115ad908461117e565b600160a060020a0386166000908152600160205260409020556115d0818461117e565b600160a060020a03808716600081815260026020908152604080832033861684529091529081902093909355908616917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9086905190815260200160405180910390a360019150610a87565b60009150610a87565b5b509392505050565b60055433600160a060020a0390811691161461166957600080fd5b6005805474ff0000000000000000000000000000000000000000191660a060020a1790555b5b565b60015b90565b600160a060020a0333166000908152600160205260408120548290108015906116c05750600082115b80156116e55750600160a060020a038316600090815260016020526040902054828101115b1561179c57600160a060020a03331660009081526001602052604090205461170d908361117e565b600160a060020a03338116600090815260016020526040808220939093559085168152205461173c9083611423565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a3506001610a2d565b506000610a2d565b5b92915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106117ec57805160ff1916838001178555611819565b82800160010185558215611819579182015b828111156118195782518255916020019190600101906117fe565b5b5061182692915061182a565b5090565b61108091905b808211156118265760008155600101611830565b5090565b905600a165627a7a723058202f6383f3b2c00dfd0a82b322dc980cb203c112d35d90c50534eb499575725777002900000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000064d4547412058000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000034d47580000000000000000000000000000000000000000000000000000000000\n";

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

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_GETUPGRADESTATE = "getUpgradeState";

    public static final String FUNC_TRANSFERAGENTS = "transferAgents";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_RELEASED = "released";

    public static final String FUNC_CANUPGRADE = "canUpgrade";

    public static final String FUNC_SAFESUB = "safeSub";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TOTALUPGRADED = "totalUpgraded";

    public static final String FUNC_SAFEMUL = "safeMul";

    public static final String FUNC_RELEASEAGENT = "releaseAgent";

    public static final String FUNC_NEWOWNER = "newOwner";

    public static final String FUNC_SETUPGRADEAGENT = "setUpgradeAgent";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_SAFEADD = "safeAdd";

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

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event MINTED_EVENT = new Event("Minted", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected MGX(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MGX(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
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

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                FUNC_ACCEPTOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteCall<TransactionReceipt> safeSub(BigInteger a, BigInteger b) {
        final Function function = new Function(
                FUNC_SAFESUB, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
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

    public RemoteCall<BigInteger> totalUpgraded() {
        final Function function = new Function(FUNC_TOTALUPGRADED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> safeMul(BigInteger a, BigInteger b) {
        final Function function = new Function(
                FUNC_SAFEMUL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> releaseAgent() {
        final Function function = new Function(FUNC_RELEASEAGENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> newOwner() {
        final Function function = new Function(FUNC_NEWOWNER, 
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

    public RemoteCall<TransactionReceipt> safeAdd(BigInteger a, BigInteger b) {
        final Function function = new Function(
                FUNC_SAFEADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)), 
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

    public static RemoteCall<MGX> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable)));
        return deployRemoteCall(MGX.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<MGX> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable)));
        return deployRemoteCall(MGX.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
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

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventObservable(filter);
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

    public static MGX load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MGX(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MGX load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MGX(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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
