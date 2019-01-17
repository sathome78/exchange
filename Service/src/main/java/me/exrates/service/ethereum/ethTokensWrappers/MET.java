package me.exrates.service.ethereum.ethTokensWrappers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
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
public class MET extends Contract implements ethTokenERC20 {
    private static final String BINARY = "606060405260008054600160a060020a033316600160a060020a03199091161790556120bf806100306000396000f3006060604052600436106101d45763ffffffff60e060020a60003504166306fdde0381146101d95780630754617214610263578063079cf76e14610292578063095ea7b3146102c357806318160ddd146102f9578063195629de1461030c5780631987b8871461033957806323b872dd146103585780632af4c31e14610380578063313ce5671461039f57806334fec467146103c85780633bf11a6c146103db57806340c10f191461040657806346be2310146104285780634892f0af146104b85780634cef0ff6146104cb5780634ee4d731146104ed57806350b48c5e146105005780635b75dd8d146105135780636ffbff9c1461055c57806370a08231146107195780637240eccf1461073857806379ba50971461075a5780638da5cb5b1461076d57806393b212bc1461078057806393d81d581461079f57806395d89b41146107be578063a24835d1146107d1578063a4546876146107f3578063a9059cbb14610812578063aa4925d714610834578063b33fcc7a14610859578063bae1cc74146108a8578063c3f51fca146108f7578063cd755b4114610922578063d2d85cf214610947578063d4ee1d901461096c578063dab5f3401461097f578063dd62ed3e14610995578063e6f47613146109ba575b600080fd5b34156101e457600080fd5b6101ec610a49565b60405160208082528190810183818151815260200191508051906020019080838360005b83811015610228578082015183820152602001610210565b50505050905090810190601f1680156102555780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561026e57600080fd5b610276610a80565b604051600160a060020a03909116815260200160405180910390f35b341561029d57600080fd5b6102b1600160a060020a0360043516610a8f565b60405190815260200160405180910390f35b34156102ce57600080fd5b6102e5600160a060020a0360043516602435610aaa565b604051901515815260200160405180910390f35b341561030457600080fd5b6102b1610b36565b341561031757600080fd5b610337600160a060020a0360043581169060243516604435606435610b3c565b005b341561034457600080fd5b6102e5600160a060020a0360043516610b69565b341561036357600080fd5b6102e5600160a060020a0360043581169060243516604435610bcc565b341561038b57600080fd5b6102e5600160a060020a0360043516610bf3565b34156103aa57600080fd5b6103b2610c5a565b60405160ff909116815260200160405180910390f35b34156103d357600080fd5b6102e5610c5f565b34156103e657600080fd5b610337600160a060020a0360043581169060243516604435606435610c68565b341561041157600080fd5b6102e5600160a060020a0360043516602435610c8f565b341561043357600080fd5b6102e56004803577ffffffffffffffffffffffffffffffffffffffffffffffff19169060248035600160a060020a039081169260443590911691606435916084359160c49060a43590810190830135806020601f82018190048102016040519081016040528181529291906020840183838082843750949650610d9395505050505050565b34156104c357600080fd5b610276610ed8565b34156104d657600080fd5b6102e5600160a060020a0360043516602435610ee7565b34156104f857600080fd5b6102e5610f90565b341561050b57600080fd5b610276611017565b341561051e57600080fd5b610538600160a060020a0360043581169060243516611026565b60405180848152602001838152602001828152602001935050505060405180910390f35b341561056757600080fd5b6102e577ffffffffffffffffffffffffffffffffffffffffffffffff1960048035821691602480359091169190606490604435908101908301358060208181020160405190810160405280939291908181526020018383602002808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284375094965061105e95505050505050565b341561072457600080fd5b6102b1600160a060020a0360043516611345565b341561074357600080fd5b6102e5600160a060020a0360043516602435611360565b341561076557600080fd5b6102e5611399565b341561077857600080fd5b61027661142a565b341561078b57600080fd5b6102e5600160a060020a0360043516611439565b34156107aa57600080fd5b6102e5600160a060020a036004351661146a565b34156107c957600080fd5b6101ec61153e565b34156107dc57600080fd5b6102e5600160a060020a0360043516602435611575565b34156107fe57600080fd5b6102b1600160a060020a0360043516611679565b341561081d57600080fd5b6102e5600160a060020a036004351660243561168b565b341561083f57600080fd5b6102e5600435602435600160a060020a03604435166116b0565b341561086457600080fd5b6102e5600460248135818101908301358060208181020160405190810160405280939291908181526020018383602002808284375094965061178295505050505050565b34156108b357600080fd5b6102b160046024813581810190830135806020818102016040519081016040528093929190818152602001838360200280828437509496506117a595505050505050565b341561090257600080fd5b610337600160a060020a03600435811690602435166044356064356117ed565b341561092d57600080fd5b610538600160a060020a03600435811690602435166118d4565b341561095257600080fd5b6102e5600160a060020a0360043581169060243516611900565b341561097757600080fd5b610276611927565b341561098a57600080fd5b610337600435611936565b34156109a057600080fd5b6102b1600160a060020a0360043581169060243516611951565b34156109c557600080fd5b6102b160046024813581810190830135806020818102016040519081016040528093929190818152602001838360200280828437820191505050505050919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284375094965061197c95505050505050565b60408051908101604052600981527f4d6574726f6e6f6d650000000000000000000000000000000000000000000000602082015281565b600554600160a060020a031681565b600160a060020a031660009081526009602052604090205490565b600030600160a060020a031683600160a060020a031614151515610acd57600080fd5b600160a060020a03338116600081815260076020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b60025490565b60005433600160a060020a03908116911614610b5757600080fd5b610b6384848484610c68565b50505050565b6000805433600160a060020a03908116911614610b8557600080fd5b600160a060020a0382161515610b9a57600080fd5b5060068054600160a060020a03831673ffffffffffffffffffffffffffffffffffffffff199091161790556001919050565b60085460009060ff161515610be057600080fd5b610beb8484846119ef565b949350505050565b6000805433600160a060020a03908116911614610c0f57600080fd5b600054600160a060020a0383811691161415610c2a57600080fd5b5060018054600160a060020a03831673ffffffffffffffffffffffffffffffffffffffff19909116178155919050565b601281565b60085460ff1681565b60005433600160a060020a03908116911614610c8357600080fd5b610b63848484846117ed565b60055460009033600160a060020a0390811691161480610cbd575060065433600160a060020a039081169116145b1515610cc857600080fd5b600160a060020a038316600090815260036020526040902054610cf1908363ffffffff611c6516565b600160a060020a038416600090815260036020526040902055600254610d1d908363ffffffff611c6516565b600255600160a060020a0383167f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968858360405190815260200160405180910390a282600160a060020a031660006000805160206120748339815191528460405190815260200160405180910390a350600192915050565b600654600090600160a060020a03161515610dad57600080fd5b600654600160a060020a031663f29b20403389898989898960405160e060020a63ffffffff8a16028152600160a060020a038089166004830190815277ffffffffffffffffffffffffffffffffffffffffffffffff1989166024840152878216604484015290861660648301526084820185905260a4820184905260e060c48301908152909160e40183818151815260200191508051906020019080838360005b83811015610e66578082015183820152602001610e4e565b50505050905090810190601f168015610e935780820380516001836020036101000a031916815260200191505b5098505050505050505050602060405180830381600087803b1515610eb757600080fd5b5af11515610ec457600080fd5b505050604051805198975050505050505050565b600654600160a060020a031681565b600160a060020a03338116600090815260076020908152604080832093861683529290529081205481610f20828563ffffffff611c6516565b600160a060020a033381166000818152600760209081526040808320948b16808452949091529081902084905592935090917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259084905190815260200160405180910390a3506001949350505050565b60085460009060ff16158015610ff85750600554600160a060020a0316631d38bebd6040518163ffffffff1660e060020a028152600401602060405180830381600087803b1515610fe057600080fd5b5af11515610fed57600080fd5b505050604051805190505b151561100357600080fd5b506008805460ff1916600190811790915590565b600454600160a060020a031681565b600160a060020a039182166000908152600a602090815260408083209390941682529190915220805460018201546002909201549092565b600654600090600160a060020a0316151561107857600080fd5b600654600160a060020a0316636ffbff9c8a8a8a8a8a8a8a8a6040518963ffffffff1660e060020a028152600401808977ffffffffffffffffffffffffffffffffffffffffffffffff191677ffffffffffffffffffffffffffffffffffffffffffffffff191681526020018877ffffffffffffffffffffffffffffffffffffffffffffffff191677ffffffffffffffffffffffffffffffffffffffffffffffff1916815260200180602001806020018060200180602001806020018060200187810387528d818151815260200191508051906020019060200280838360005b8381101561116f578082015183820152602001611157565b5050505090500187810386528c818151815260200191508051906020019080838360005b838110156111ab578082015183820152602001611193565b50505050905090810190601f1680156111d85780820380516001836020036101000a031916815260200191505b5087810385528b818151815260200191508051906020019060200280838360005b838110156112115780820151838201526020016111f9565b5050505090500187810384528a818151815260200191508051906020019060200280838360005b83811015611250578082015183820152602001611238565b50505050905001878103835289818151815260200191508051906020019060200280838360005b8381101561128f578082015183820152602001611277565b50505050905001878103825288818151815260200191508051906020019080838360005b838110156112cb5780820151838201526020016112b3565b50505050905090810190601f1680156112f85780820380516001836020036101000a031916815260200191505b509e505050505050505050505050505050602060405180830381600087803b151561132257600080fd5b5af1151561132f57600080fd5b50505060405180519a9950505050505050505050565b600160a060020a031660009081526003602052604090205490565b600160a060020a03338116600090815260076020908152604080832093861683529290529081205481610f20828563ffffffff611c7416565b60015460009033600160a060020a039081169116146113b757600080fd5b600154600054600160a060020a0391821691167f0384899bd253d83b23daa4d29aaa2efe0563d1132b43101e9ad667235aeb951b60405160405180910390a350600180546000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0390921691909117905590565b600054600160a060020a031681565b60085460009060ff16151561144d57600080fd5b6114578233611c86565b151561146257600080fd5b506001919050565b600160a060020a033381166000908152600a60209081526040808320938516835292905290812054151561149d57600080fd5b600160a060020a033381166000908152600a602090815260408083209386168352929052206001015415156114d157600080fd5b600160a060020a033381166000818152600a602090815260408083209487168084529490915280822082815560018101839055600201919091557f4bc04e574b4f2b061a663b3328263978d3adeab9e4ea9526441c159cdee4eca8905160405180910390a3506001919050565b60408051908101604052600381527f4d45540000000000000000000000000000000000000000000000000000000000602082015281565b60045460009033600160a060020a03908116911614806115a3575060065433600160a060020a039081169116145b15156115ae57600080fd5b600160a060020a0383166000908152600360205260409020546115d7908363ffffffff611c7416565b600160a060020a038416600090815260036020526040902055600254611603908363ffffffff611c7416565b600255600160a060020a0383167f81325e2a6c442af9d36e4ee9697f38d5f4bf0837ade0f6c411c6a40af7c057ee8360405190815260200160405180910390a2600083600160a060020a03166000805160206120748339815191528460405190815260200160405180910390a350600192915050565b60096020526000908152604090205481565b60085460009060ff16151561169f57600080fd5b6116a98383611e24565b9392505050565b6000428410156116bf57600080fd5b8215156116cb57600080fd5b600160a060020a03821615156116e057600080fd5b606060405190810160409081528582526020808301869052818301879052600160a060020a033381166000908152600a8352838120918716815291522081518155602082015181600101556040820151816002015590505081600160a060020a031633600160a060020a03167f5c632ccb5c42002836e0c49ef1f6d67e925c5e77c1048ddbb47a9bf6a0f2d01260405160405180910390a35060019392505050565b60085460009060ff16151561179657600080fd5b61179f82611fb1565b92915050565b600080805b83518110156117e6576117d28482815181106117c257fe5b9060200190602002015133611c86565b156117de576001909101905b6001016117aa565b5092915050565b60005433600160a060020a0390811691161461180857600080fd5b600454600160a060020a03161580156118295750600160a060020a03841615155b151561183457600080fd5b600554600160a060020a03161580156118555750600160a060020a03831615155b151561186057600080fd5b60048054600160a060020a0380871673ffffffffffffffffffffffffffffffffffffffff199283161790925560058054928616929091169190911790556118ad828263ffffffff61203116565b6002819055600160a060020a03909416600090815260036020526040902093909355505050565b600a60209081526000928352604080842090915290825290208054600182015460029092015490919083565b600160a060020a039081166000908152600960205260408082205493909216815220541490565b600154600160a060020a031681565b600160a060020a033316600090815260096020526040902055565b600160a060020a03918216600090815260076020908152604080832093909416825291909152205490565b6000806000835185511461198f57600080fd5b5060009050805b84518110156119e7576119d38582815181106119ae57fe5b906020019060200201518583815181106119c457fe5b90602001906020020151611c86565b156119df576001909101905b600101611996565b509392505050565b600080600160a060020a0384161515611a0757600080fd5b600554600160a060020a03858116911614801590611a335750600554600160a060020a03868116911614155b1515611a3e57600080fd5b30600160a060020a031684600160a060020a031614158015611a72575030600160a060020a031685600160a060020a031614155b1515611a7d57600080fd5b600554600160a060020a03166355b5ec646040518163ffffffff1660e060020a028152600401602060405180830381600087803b1515611abc57600080fd5b5af11515611ac957600080fd5b50505060405180519050905080600160a060020a031684600160a060020a031614158015611b09575080600160a060020a031685600160a060020a031614155b1515611b1457600080fd5b600454600160a060020a0386811691161415611b2f57600080fd5b600160a060020a038086166000908152600760209081526040808320339094168352929052205483901015611b6357600080fd5b600160a060020a038516600090815260036020526040902054611b8c908463ffffffff611c7416565b600160a060020a038087166000908152600360205260408082209390935590861681522054611bc1908463ffffffff611c6516565b600160a060020a03808616600090815260036020908152604080832094909455888316825260078152838220339093168252919091522054611c09908463ffffffff611c7416565b600160a060020a03808716600081815260076020908152604080832033861684529091529081902093909355908616916000805160206120748339815191529086905190815260200160405180910390a3506001949350505050565b6000828201838110156116a957fe5b600082821115611c8057fe5b50900390565b600160a060020a038083166000908152600a6020908152604080832093851683529290529081208054829081908190118015611cc3575082544290105b8015611cd3575060008360010154115b15611e1657611d0262093a80611cf6856002015442611c7490919063ffffffff16565b9063ffffffff61205c16565b9150611d1b83600101548361203190919063ffffffff16565b9050600082118015611d465750600160a060020a038616600090815260036020526040902054819010155b15611e1657600160a060020a038087166000818152600a60209081526040808320948a1683529381528382204260029091015591815260039091522054611d93908263ffffffff611c7416565b600160a060020a038088166000908152600360205260408082209390935590871681522054611dc8908263ffffffff611c6516565b600160a060020a03808716600081815260036020526040908190209390935591908816906000805160206120748339815191529084905190815260200160405180910390a360019350611e1b565b600093505b50505092915050565b600080600160a060020a0384161515611e3c57600080fd5b600554600160a060020a0385811691161415611e5757600080fd5b30600160a060020a031684600160a060020a031614151515611e7857600080fd5b600454600160a060020a0385811691161415611e9357600080fd5b600554600160a060020a03166355b5ec646040518163ffffffff1660e060020a028152600401602060405180830381600087803b1515611ed257600080fd5b5af11515611edf57600080fd5b5050506040518051915050600160a060020a038481169082161415611f0357600080fd5b600160a060020a033316600090815260036020526040902054611f2c908463ffffffff611c7416565b600160a060020a033381166000908152600360205260408082209390935590861681522054611f61908463ffffffff611c6516565b600160a060020a0380861660008181526003602052604090819020939093559133909116906000805160206120748339815191529086905190815260200160405180910390a35060019392505050565b60008080805b8451831015612026576060858481518110611fce57fe5b906020019060200201519060020a90049150848381518110611fec57fe5b906020019060200201516bffffffffffffffffffffffff169050612010828261168b565b151561201b57600080fd5b600190920191611fb7565b506001949350505050565b60008083151561204457600091506117e6565b5082820282848281151561205457fe5b04146116a957fe5b600080828481151561206a57fe5b049493505050505600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a7230582062ffa3d2c04263654dbffc8876e9fb51bf446bbfa2c43a5d01b62f8b0a66f1bb0029\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_MINTER = "minter";

    public static final String FUNC_GETROOT = "getRoot";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_INITMETTOKEN = "initMETToken";

    public static final String FUNC_SETTOKENPORTER = "setTokenPorter";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_CHANGEOWNERSHIP = "changeOwnership";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_TRANSFERALLOWED = "transferAllowed";

    public static final String FUNC_INITTOKEN = "initToken";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_EXPORT = "export";

    public static final String FUNC_TOKENPORTER = "tokenPorter";

    public static final String FUNC_APPROVEMORE = "approveMore";

    public static final String FUNC_ENABLEMETTRANSFERS = "enableMETTransfers";

    public static final String FUNC_AUTONOMOUSCONVERTER = "autonomousConverter";

    public static final String FUNC_GETSUBSCRIPTION = "getSubscription";

    public static final String FUNC_IMPORTMET = "importMET";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_APPROVELESS = "approveLess";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SUBWITHDRAW = "subWithdraw";

    public static final String FUNC_CANCELSUBSCRIPTION = "cancelSubscription";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_DESTROY = "destroy";

    public static final String FUNC_ROOTS = "roots";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_SUBSCRIBE = "subscribe";

    public static final String FUNC_MULTITRANSFER = "multiTransfer";

    public static final String FUNC_MULTISUBWITHDRAW = "multiSubWithdraw";

    public static final String FUNC_INITMINTABLE = "initMintable";

    public static final String FUNC_SUBS = "subs";

    public static final String FUNC_ROOTSMATCH = "rootsMatch";

    public static final String FUNC_NEWOWNER = "newOwner";

    public static final String FUNC_SETROOT = "setRoot";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_MULTISUBWITHDRAWFOR = "multiSubWithdrawFor";

    public static final Event LOGSUBSCRIPTION_EVENT = new Event("LogSubscription", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event LOGCANCELSUBSCRIPTION_EVENT = new Event("LogCancelSubscription", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event MINT_EVENT = new Event("Mint", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event DESTROY_EVENT = new Event("Destroy", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event OWNERSHIPCHANGED_EVENT = new Event("OwnershipChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected MET(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MET(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> minter() {
        final Function function = new Function(FUNC_MINTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<byte[]> getRoot(String addr) {
        final Function function = new Function(FUNC_GETROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
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

    public RemoteCall<TransactionReceipt> initMETToken(String _autonomousConverter, String _minter, BigInteger _initialSupply, BigInteger _decmult) {
        final Function function = new Function(
                FUNC_INITMETTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_autonomousConverter), 
                new org.web3j.abi.datatypes.Address(_minter), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decmult)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setTokenPorter(String _tokenPorter) {
        final Function function = new Function(
                FUNC_SETTOKENPORTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_tokenPorter)), 
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

    public RemoteCall<TransactionReceipt> changeOwnership(String _newOwner) {
        final Function function = new Function(
                FUNC_CHANGEOWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> transferAllowed() {
        final Function function = new Function(FUNC_TRANSFERALLOWED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> initToken(String _autonomousConverter, String _minter, BigInteger _initialSupply, BigInteger _decmult) {
        final Function function = new Function(
                FUNC_INITTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_autonomousConverter), 
                new org.web3j.abi.datatypes.Address(_minter), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decmult)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mint(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> export(byte[] _destChain, String _destMetronomeAddr, String _destRecipAddr, BigInteger _amount, BigInteger _fee, byte[] _extraData) {
        final Function function = new Function(
                FUNC_EXPORT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes8(_destChain), 
                new org.web3j.abi.datatypes.Address(_destMetronomeAddr), 
                new org.web3j.abi.datatypes.Address(_destRecipAddr), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount), 
                new org.web3j.abi.datatypes.generated.Uint256(_fee), 
                new org.web3j.abi.datatypes.DynamicBytes(_extraData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> tokenPorter() {
        final Function function = new Function(FUNC_TOKENPORTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approveMore(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVEMORE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> enableMETTransfers() {
        final Function function = new Function(
                FUNC_ENABLEMETTRANSFERS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> autonomousConverter() {
        final Function function = new Function(FUNC_AUTONOMOUSCONVERTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>> getSubscription(String _owner, String _recipient) {
        final Function function = new Function(FUNC_GETSUBSCRIPTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_recipient)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> importMET(byte[] _originChain, byte[] _destinationChain, List<String> _addresses, byte[] _extraData, List<byte[]> _burnHashes, List<BigInteger> _supplyOnAllChains, List<BigInteger> _importData, byte[] _proof) {
        final Function function = new Function(
                FUNC_IMPORTMET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes8(_originChain), 
                new org.web3j.abi.datatypes.generated.Bytes8(_destinationChain), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_extraData), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(_burnHashes, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_supplyOnAllChains, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_importData, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_proof)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> approveLess(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVELESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                FUNC_ACCEPTOWNERSHIP, 
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

    public RemoteCall<TransactionReceipt> subWithdraw(String _owner) {
        final Function function = new Function(
                FUNC_SUBWITHDRAW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> cancelSubscription(String _recipient) {
        final Function function = new Function(
                FUNC_CANCELSUBSCRIPTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> destroy(String _from, BigInteger _value) {
        final Function function = new Function(
                FUNC_DESTROY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<byte[]> roots(String param0) {
        final Function function = new Function(FUNC_ROOTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> subscribe(BigInteger _startTime, BigInteger _payPerWeek, String _recipient) {
        final Function function = new Function(
                FUNC_SUBSCRIBE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_startTime), 
                new org.web3j.abi.datatypes.generated.Uint256(_payPerWeek), 
                new org.web3j.abi.datatypes.Address(_recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> multiTransfer(List<BigInteger> bits) {
        final Function function = new Function(
                FUNC_MULTITRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(bits, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> multiSubWithdraw(List<String> _owners) {
        final Function function = new Function(
                FUNC_MULTISUBWITHDRAW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_owners, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> initMintable(String _autonomousConverter, String _minter, BigInteger _initialSupply, BigInteger _decmult) {
        final Function function = new Function(
                FUNC_INITMINTABLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_autonomousConverter), 
                new org.web3j.abi.datatypes.Address(_minter), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decmult)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>> subs(String param0, String param1) {
        final Function function = new Function(FUNC_SUBS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.Address(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> rootsMatch(String a, String b) {
        final Function function = new Function(FUNC_ROOTSMATCH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(a), 
                new org.web3j.abi.datatypes.Address(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> newOwner() {
        final Function function = new Function(FUNC_NEWOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> setRoot(byte[] data) {
        final Function function = new Function(
                FUNC_SETROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(data)), 
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

    public RemoteCall<TransactionReceipt> multiSubWithdrawFor(List<String> _owners, List<String> _recipients) {
        final Function function = new Function(
                FUNC_MULTISUBWITHDRAWFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_owners, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_recipients, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<LogSubscriptionEventResponse> getLogSubscriptionEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGSUBSCRIPTION_EVENT, transactionReceipt);
        ArrayList<LogSubscriptionEventResponse> responses = new ArrayList<LogSubscriptionEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogSubscriptionEventResponse typedResponse = new LogSubscriptionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.subscriber = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.subscribesTo = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogSubscriptionEventResponse> logSubscriptionEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogSubscriptionEventResponse>() {
            @Override
            public LogSubscriptionEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGSUBSCRIPTION_EVENT, log);
                LogSubscriptionEventResponse typedResponse = new LogSubscriptionEventResponse();
                typedResponse.log = log;
                typedResponse.subscriber = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.subscribesTo = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogSubscriptionEventResponse> logSubscriptionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGSUBSCRIPTION_EVENT));
        return logSubscriptionEventObservable(filter);
    }

    public List<LogCancelSubscriptionEventResponse> getLogCancelSubscriptionEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGCANCELSUBSCRIPTION_EVENT, transactionReceipt);
        ArrayList<LogCancelSubscriptionEventResponse> responses = new ArrayList<LogCancelSubscriptionEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogCancelSubscriptionEventResponse typedResponse = new LogCancelSubscriptionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.subscriber = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.subscribesTo = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogCancelSubscriptionEventResponse> logCancelSubscriptionEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogCancelSubscriptionEventResponse>() {
            @Override
            public LogCancelSubscriptionEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGCANCELSUBSCRIPTION_EVENT, log);
                LogCancelSubscriptionEventResponse typedResponse = new LogCancelSubscriptionEventResponse();
                typedResponse.log = log;
                typedResponse.subscriber = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.subscribesTo = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogCancelSubscriptionEventResponse> logCancelSubscriptionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGCANCELSUBSCRIPTION_EVENT));
        return logCancelSubscriptionEventObservable(filter);
    }

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINT_EVENT, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintEventResponse> mintEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintEventResponse>() {
            @Override
            public MintEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINT_EVENT, log);
                MintEventResponse typedResponse = new MintEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MintEventResponse> mintEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINT_EVENT));
        return mintEventObservable(filter);
    }

    public List<DestroyEventResponse> getDestroyEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DESTROY_EVENT, transactionReceipt);
        ArrayList<DestroyEventResponse> responses = new ArrayList<DestroyEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DestroyEventResponse typedResponse = new DestroyEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DestroyEventResponse> destroyEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, DestroyEventResponse>() {
            @Override
            public DestroyEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DESTROY_EVENT, log);
                DestroyEventResponse typedResponse = new DestroyEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<DestroyEventResponse> destroyEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DESTROY_EVENT));
        return destroyEventObservable(filter);
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

    public List<OwnershipChangedEventResponse> getOwnershipChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPCHANGED_EVENT, transactionReceipt);
        ArrayList<OwnershipChangedEventResponse> responses = new ArrayList<OwnershipChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipChangedEventResponse typedResponse = new OwnershipChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.prevOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipChangedEventResponse> ownershipChangedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipChangedEventResponse>() {
            @Override
            public OwnershipChangedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPCHANGED_EVENT, log);
                OwnershipChangedEventResponse typedResponse = new OwnershipChangedEventResponse();
                typedResponse.log = log;
                typedResponse.prevOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipChangedEventResponse> ownershipChangedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPCHANGED_EVENT));
        return ownershipChangedEventObservable(filter);
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

    public static RemoteCall<MET> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MET.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<MET> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MET.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static MET load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MET(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MET load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MET(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogSubscriptionEventResponse {
        public Log log;

        public String subscriber;

        public String subscribesTo;
    }

    public static class LogCancelSubscriptionEventResponse {
        public Log log;

        public String subscriber;

        public String subscribesTo;
    }

    public static class MintEventResponse {
        public Log log;

        public String _to;

        public BigInteger _value;
    }

    public static class DestroyEventResponse {
        public Log log;

        public String _from;

        public BigInteger _value;
    }

    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class OwnershipChangedEventResponse {
        public Log log;

        public String prevOwner;

        public String newOwner;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
