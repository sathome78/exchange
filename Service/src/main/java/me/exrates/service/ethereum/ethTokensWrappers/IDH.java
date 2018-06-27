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
 * <p>Generated with web3j version 3.3.1.
 */
public class IDH extends Contract implements ethTokenNotERC20 {
    private static final String BINARY = "60606040526000600255620f4240610c8002600755600060085560006009556000600a556000600b55341561003357600080fd5b5b5b60008054600160a060020a03191633600160a060020a03161790555b5b60005460058054600160a060020a03909216600160a060020a03199283168117909155600680549092161790555b5b612321806100906000396000f300606060405236156102bc5763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663021ba8f881146102c8578063023bb74d146102ed578063024c6def1461031257806305502a621461033757806306fdde031461035c578063095ea7b3146103e757806318160ddd1461041d5780632121dc751461044257806323b872dd14610469578063313ce567146104a5578063347e26c0146104ce57806335082933146104ff57806336b19cd71461052057806336d95f561461054f57806340650c9114610574578063436e1e2e14610599578063465a092d146105ca5780634a387bef146105ef578063521eb273146106105780635b88349d1461063f5780635ef7943414610654578063615ef639146106795780636e99d52f1461069e57806370a08231146106c357806374b433bd146106f4578063763f40111461071957806379ba50971461073d57806381aea6681461075257806384ef0778146107775780638da5cb5b1461079c57806394d95f8f146107cb57806395cc2e8b146107f057806395d89b41146108155780639ea407be146108a0578063a05fccef146108b8578063a9059cbb146108e4578063ad9df0551461091a578063b557478a1461093b578063b63e93dc1461096e578063c0c133a814610983578063cab3ad2c146109a8578063cbc8ae5f146109cd578063cbdd69b5146109f2578063cbf9fe5f14610a17578063cce4bd5214610a4a578063ce916d8514610a5f578063cef9db6d14610a90578063d1b6dd3014610ab5578063d4ee1d9014610ae8578063d912ebb214610b17578063dc39d06d14610b3c578063dd62ed3e14610b72578063deaa59df14610ba9578063e1c1451a14610bca578063e3fe974014610bef578063f0e7cf1314610c16578063f21632e114610c36578063f2fde38b14610c5b578063fcc1cc9b14610c7c575b5b6102c5610ccd565b5b005b34156102d357600080fd5b6102db61107f565b60405190815260200160405180910390f35b34156102f857600080fd5b6102db611086565b60405190815260200160405180910390f35b341561031d57600080fd5b6102db61108e565b60405190815260200160405180910390f35b341561034257600080fd5b6102db611093565b60405190815260200160405180910390f35b341561036757600080fd5b61036f611098565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156103ac5780820151818401525b602001610393565b50505050905090810190601f1680156103d95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156103f257600080fd5b610409600160a060020a03600435166024356110cf565b604051901515815260200160405180910390f35b341561042857600080fd5b6102db61115f565b60405190815260200160405180910390f35b341561044d57600080fd5b610409611166565b604051901515815260200160405180910390f35b341561047457600080fd5b610409600160a060020a03600435811690602435166044356111a0565b604051901515815260200160405180910390f35b34156104b057600080fd5b6104b8611216565b60405160ff909116815260200160405180910390f35b34156104d957600080fd5b6102db600160a060020a036004351661121b565b60405190815260200160405180910390f35b341561050a57600080fd5b6102c5600160a060020a036004351661122d565b005b341561052b57600080fd5b6105336112ce565b604051600160a060020a03909116815260200160405180910390f35b341561055a57600080fd5b6102db6112dd565b60405190815260200160405180910390f35b341561057f57600080fd5b6102db6112e7565b60405190815260200160405180910390f35b34156105a457600080fd5b6102db600160a060020a03600435166112f3565b60405190815260200160405180910390f35b34156105d557600080fd5b6102db6113c5565b60405190815260200160405180910390f35b34156105fa57600080fd5b6102c5600160a060020a03600435166113d3565b005b341561061b57600080fd5b61053361145a565b604051600160a060020a03909116815260200160405180910390f35b341561064a57600080fd5b6102c5611469565b005b341561065f57600080fd5b6102db611475565b60405190815260200160405180910390f35b341561068457600080fd5b6102db61147d565b60405190815260200160405180910390f35b34156106a957600080fd5b6102db611483565b60405190815260200160405180910390f35b34156106ce57600080fd5b6102db600160a060020a036004351661148a565b60405190815260200160405180910390f35b34156106ff57600080fd5b6102db6114a9565b60405190815260200160405180910390f35b341561072457600080fd5b6102c5600160a060020a03600435166024356114b3565b005b341561074857600080fd5b6102c5611602565b005b341561075d57600080fd5b6102db61168e565b60405190815260200160405180910390f35b341561078257600080fd5b6102db611693565b60405190815260200160405180910390f35b34156107a757600080fd5b610533611699565b604051600160a060020a03909116815260200160405180910390f35b34156107d657600080fd5b6102db6116a8565b60405190815260200160405180910390f35b34156107fb57600080fd5b6102db6116b5565b60405190815260200160405180910390f35b341561082057600080fd5b61036f6116bd565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156103ac5780820151818401525b602001610393565b50505050905090810190601f1680156103d95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156108ab57600080fd5b6102c56004356116f4565b005b34156108c357600080fd5b6102c56024600480358281019290820135918135918201910135611763565b005b34156108ef57600080fd5b610409600160a060020a036004351660243561183e565b604051901515815260200160405180910390f35b341561092557600080fd5b6102c5600160a060020a03600435166118b2565b005b341561094657600080fd5b610409600160a060020a03600435166118da565b604051901515815260200160405180910390f35b341561097957600080fd5b6102c56118ef565b005b341561098e57600080fd5b6102db61195e565b60405190815260200160405180910390f35b34156109b357600080fd5b6102db611966565b60405190815260200160405180910390f35b34156109d857600080fd5b6102db61196b565b60405190815260200160405180910390f35b34156109fd57600080fd5b6102db611976565b60405190815260200160405180910390f35b3415610a2257600080fd5b610409600160a060020a036004351661197c565b604051901515815260200160405180910390f35b3415610a5557600080fd5b6102c5611991565b005b3415610a6a57600080fd5b6102db600160a060020a0360043516611b36565b60405190815260200160405180910390f35b3415610a9b57600080fd5b6102db611b48565b60405190815260200160405180910390f35b3415610ac057600080fd5b610409600160a060020a0360043516611b53565b604051901515815260200160405180910390f35b3415610af357600080fd5b610533611b68565b604051600160a060020a03909116815260200160405180910390f35b3415610b2257600080fd5b6102db611b77565b60405190815260200160405180910390f35b3415610b4757600080fd5b610409600160a060020a0360043516602435611b7d565b604051901515815260200160405180910390f35b3415610b7d57600080fd5b6102db600160a060020a0360043581169060243516611c3c565b60405190815260200160405180910390f35b3415610bb457600080fd5b6102c5600160a060020a0360043516611c69565b005b3415610bd557600080fd5b6102db611d0a565b60405190815260200160405180910390f35b3415610bfa57600080fd5b610409611d10565b604051901515815260200160405180910390f35b3415610c2157600080fd5b6102c56004803560248101910135611d33565b005b3415610c4157600080fd5b6102db611d90565b60405190815260200160405180910390f35b3415610c6657600080fd5b6102c5600160a060020a0360043516611d96565b005b3415610c8757600080fd5b6102c56004602481358181019083013580602081810201604051908101604052809392919081815260200183836020028082843750949650611e4a95505050505050565b005b600080600080610cdb61168e565b9350600092508291508190506706f05b59d3b20000341015610cfc57600080fd5b600160a060020a0333166000908152600c6020526040902054681043561a882930000090610d30903463ffffffff611f2d16565b1115610d3b57600080fd5b635a031bf084118015610d515750635a0c567084105b15610d5b57600192505b635a1ecb7084118015610d715750635a3a7af084105b15610d7b57600191505b8280610d845750815b1515610d8f57600080fd5b8215610dbf5760085469032d26d12e980b60000090610db4903463ffffffff611f2d16565b1115610dbf57600080fd5b5b600754670de0b6b3a764000090610ddd903463ffffffff611f4116565b811515610de657fe5b0490508215610e13576064610e0282608c63ffffffff611f4116565b811515610e0b57fe5b049050610e6f565b635a2805f0841015610e43576064610e0282607863ffffffff611f4116565b811515610e0b57fe5b049050610e6f565b635a314070841015610e6f576064610e6282606e63ffffffff611f4116565b811515610e6b57fe5b0490505b5b5b60095466012309ce54000090610e8d908363ffffffff611f2d16565b1115610e9857600080fd5b600160a060020a033316600090815260036020526040902054610ec1908263ffffffff611f2d16565b600160a060020a033316600090815260036020908152604080832093909355600d90522054610ef6908263ffffffff611f2d16565b600160a060020a0333166000908152600d6020526040902055600954610f22908263ffffffff611f2d16565b600955600254610f38908263ffffffff611f2d16565b600255600854610f4e903463ffffffff611f2d16565b600855600160a060020a0333166000908152600c6020526040902054610f7a903463ffffffff611f2d16565b600160a060020a0333166000818152600c602090815260408083209490945560109052828120805460ff1916600117905590916000805160206122d68339815191529084905190815260200160405180910390a3600160a060020a03331660008181526003602052604090819020547fdf61f51b99ff0164245d2ca4eb058f919dbe973d48c3891c93e50183b67f3e1e9184919034905180848152602001838152602001828152602001935050505060405180910390a2611039611d10565b1561107757600554600160a060020a039081169030163180156108fc0290604051600060405180830381858888f19350505050151561107757600080fd5b5b5b50505050565b6276a70081565b635a031bf081565b600a81565b602881565b60408051908101604052600d81527f696e64614861736820436f696e00000000000000000000000000000000000000602082015281565b600160a060020a033316600090815260036020526040812054829010156110f557600080fd5b600160a060020a03338116600081815260046020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a35060015b92915050565b6002545b90565b6000611170611d10565b151561117e57506000611163565b635a3d1df061118b61168e565b101561119957506000611163565b5060015b90565b60006111aa611166565b15156111b557600080fd5b600160a060020a03841660009081526010602052604090205460ff16156111db57600080fd5b600160a060020a03831660009081526010602052604090205460ff161561120157600080fd5b61120c848484611f6a565b90505b9392505050565b600681565b600c6020526000908152604090205481565b60005433600160a060020a0390811691161461124857600080fd5b600160a060020a038116151561125d57600080fd5b6006805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383811691909117918290557f65ef617d87f7e47ab080058ac1d5ebf06817bbd817e49898ab9f9532405ecd9b9116604051600160a060020a03909116815260200160405180910390a15b5b50565b600654600160a060020a031681565b65246139ca800081565b6706f05b59d3b2000081565b6000806000635a3a7af061130561168e565b10806113165750611314611d10565b155b1561132457600092506113be565b600160a060020a0384166000908152600e602052604090205460ff161561134e57600092506113be565b600160a060020a0384166000908152600d6020526040902054151561137657600092506113be565b600160a060020a0384166000908152600d60205260409020546009549092506113ac8366012309ce54000063ffffffff611f4116565b8115156113b557fe5b04905081810392505b5050919050565b69032d26d12e980b60000081565b60065433600160a060020a03908116911614806113fe575060005433600160a060020a039081169116145b151561140957600080fd5b600160a060020a03811660008181526010602052604090819020805460ff191690557f064f67e76df103eb3e142dac6110a06fcfc7a01ef2da651312b88eb6f0dd3d28905160405180910390a25b50565b600554600160a060020a031681565b611472336120c6565b5b565b635a0c567081565b600b5481565b6202a30081565b600160a060020a0381166000908152600360205260409020545b919050565b6548c27395000081565b60005433600160a060020a039081169116146114ce57600080fd5b600a546114e8906548c2739500009063ffffffff6121ea16565b8111156114f457600080fd5b600160a060020a03821660009081526003602052604090205461151d908263ffffffff611f2d16565b600160a060020a038316600090815260036020526040902055600a54611549908263ffffffff611f2d16565b600a5560025461155f908263ffffffff611f2d16565b600255600160a060020a038216600081815260106020526040808220805460ff191660011790556000805160206122d68339815191529084905190815260200160405180910390a3600160a060020a03821660008181526003602052604090819020547f2e8ac5177a616f2aec08c3048f5021e4e9743ece034e8d83ba5caf76688bb475918491905191825260208201526040908101905180910390a25b5b5050565b60015433600160a060020a0390811691161461161d57600080fd5b600154600054600160a060020a0391821691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36001546000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911790555b565b425b90565b60025481565b600054600160a060020a031681565b681043561a882930000081565b635a1ecb7081565b60408051908101604052600381527f4944480000000000000000000000000000000000000000000000000000000000602082015281565b60005433600160a060020a0390811691161461170f57600080fd5b635a031bf061171c61168e565b1061172657600080fd5b60078190557ff7729fa834bbef70b6d3257c2317a562aa88b56c81b544814f93dc5963a2c0038160405190815260200160405180910390a15b5b50565b600061176d611166565b151561177857600080fd5b600160a060020a03331660009081526010602052604090205460ff161561179e57600080fd5b8382146117aa57600080fd5b5060005b8381101561183657601060008686848181106117c657fe5b60209081029290920135600160a060020a03168352508101919091526040016000205460ff16151561182d5761182b85858381811061180157fe5b90506020020135600160a060020a0316848484818110151561181f57fe5b90506020020135612201565b505b5b6001016117ae565b5b5050505050565b6000611848611166565b151561185357600080fd5b600160a060020a03331660009081526010602052604090205460ff161561187957600080fd5b600160a060020a03831660009081526010602052604090205460ff161561189f57600080fd5b6118a98383612201565b90505b92915050565b60065433600160a060020a039081169116146118cd57600080fd5b6112ca816120c6565b5b50565b600f6020526000908152604090205460ff1681565b60005433600160a060020a0390811691161461190a57600080fd5b635ab121f061191761168e565b1161192157600080fd5b600554600160a060020a039081169030163180156108fc0290604051600060405180830381858888f19350505050151561147257600080fd5b5b5b565b635a3a7af081565b601481565b66012309ce54000081565b60075481565b60106020526000908152604090205460ff1681565b600080635a3a7af06119a161168e565b1180156119b357506119b1611d10565b155b15156119be57600080fd5b600160a060020a0333166000908152600f602052604090205460ff16156119e457600080fd5b600160a060020a0333166000908152600c602052604081205411611a0757600080fd5b5050600160a060020a0333166000908152600d6020908152604080832054600c8352818420546003909352922054611a45908363ffffffff6121ea16565b600160a060020a033316600090815260036020526040902055600254611a71908363ffffffff6121ea16565b600255600160a060020a0333166000818152600f602052604090819020805460ff1916600117905582156108fc0290839051600060405180830381858888f193505050501515611ac057600080fd5b600033600160a060020a03166000805160206122d68339815191528460405190815260200160405180910390a333600160a060020a03167f73f04af9dcc582a923ec15d3eea990fe34adabfff2879e28d44572e01a54abb6828460405191825260208201526040908101905180910390a25b5050565b600d6020526000908152604090205481565b66016bcc41e9000081565b600e6020526000908152604090205460ff1681565b600154600160a060020a031681565b60095481565b6000805433600160a060020a03908116911614611b9957600080fd5b60008054600160a060020a038086169263a9059cbb92909116908590604051602001526040517c010000000000000000000000000000000000000000000000000000000063ffffffff8516028152600160a060020a0390921660048301526024820152604401602060405180830381600087803b1515611c1857600080fd5b6102c65a03f11515611c2957600080fd5b50505060405180519150505b5b92915050565b600160a060020a038083166000908152600460209081526040808320938516835292905220545b92915050565b60005433600160a060020a03908116911614611c8457600080fd5b600160a060020a0381161515611c9957600080fd5b6005805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383811691909117918290557f4edbfac5b40fe46ac1af1fd222b224b38cfeeb9e21bd4fc6344526c245f7549b9116604051600160a060020a03909116815260200160405180910390a15b5b50565b600a5481565b60095460009065246139ca800090101561119957506000611163565b5060015b90565b60065460009033600160a060020a03908116911614611d5157600080fd5b5060005b81811015611d8a57611d81838383818110611d6c57fe5b90506020020135600160a060020a03166120c6565b5b600101611d55565b5b505050565b60085481565b60005433600160a060020a03908116911614611db157600080fd5b600054600160a060020a0382811691161415611dcc57600080fd5b600160a060020a0381161515611de157600080fd5b600054600160a060020a0380831691167ff4e75b79500ab730f8a026ed3cba6d55331bcb64c9e9f60c548e371356e5e3c060405160405180910390a36001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b50565b60065460009033600160a060020a0390811691161480611e78575060005433600160a060020a039081169116145b1515611e8357600080fd5b5060005b81518110156115fd57600060106000848481518110611ea257fe5b90602001906020020151600160a060020a031681526020810191909152604001600020805460ff1916911515919091179055818181518110611ee057fe5b90602001906020020151600160a060020a03167f064f67e76df103eb3e142dac6110a06fcfc7a01ef2da651312b88eb6f0dd3d2860405160405180910390a25b600101611e87565b5b5050565b8181018281101561115957fe5b5b92915050565b818102821580611f5b5750818382811515611f5857fe5b04145b151561115957fe5b5b92915050565b600160a060020a03831660009081526003602052604081205482901015611f9057600080fd5b600160a060020a038085166000908152600460209081526040808320339094168352929052205482901015611fc457600080fd5b600160a060020a038416600090815260036020526040902054611fed908363ffffffff6121ea16565b600160a060020a0380861660009081526003602090815260408083209490945560048152838220339093168252919091522054612030908363ffffffff6121ea16565b600160a060020a0380861660009081526004602090815260408083203385168452825280832094909455918616815260039091522054612076908363ffffffff611f2d16565b600160a060020a03808516600081815260036020526040908190209390935591908616906000805160206122d68339815191529085905190815260200160405180910390a35060015b9392505050565b60006120d1826112f3565b9050600081116120e057600080fd5b600160a060020a0382166000908152600e60209081526040808320805460ff19166001179055600390915290205461211e908263ffffffff611f2d16565b600160a060020a03831660009081526003602052604090205560025461214a908263ffffffff611f2d16565b600255600b54612160908263ffffffff611f2d16565b600b55600160a060020a03821660008181526003602052604090819020547fada993ad066837289fe186cd37227aa338d27519a8a1547472ecb9831486d272918491905191825260208201526040908101905180910390a281600160a060020a031660006000805160206122d68339815191528360405190815260200160405180910390a35b5050565b6000828211156121f657fe5b508082035b92915050565b600160a060020a0333166000908152600360205260408120548290101561222757600080fd5b600160a060020a033316600090815260036020526040902054612250908363ffffffff6121ea16565b600160a060020a033381166000908152600360205260408082209390935590851681522054612285908363ffffffff611f2d16565b600160a060020a0380851660008181526003602052604090819020939093559133909116906000805160206122d68339815191529085905190815260200160405180910390a35060015b929150505600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a723058200b92f4a92f6662883588a427cb57f970575daf04db1bcdfe0ab30e5e137ffa200029";

    protected IDH(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IDH(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<WalletUpdatedEventResponse> getWalletUpdatedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("WalletUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<WalletUpdatedEventResponse> responses = new ArrayList<WalletUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            WalletUpdatedEventResponse typedResponse = new WalletUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._newWallet = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<WalletUpdatedEventResponse> walletUpdatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("WalletUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, WalletUpdatedEventResponse>() {
            @Override
            public WalletUpdatedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                WalletUpdatedEventResponse typedResponse = new WalletUpdatedEventResponse();
                typedResponse.log = log;
                typedResponse._newWallet = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<AdminWalletUpdatedEventResponse> getAdminWalletUpdatedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("AdminWalletUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<AdminWalletUpdatedEventResponse> responses = new ArrayList<AdminWalletUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AdminWalletUpdatedEventResponse typedResponse = new AdminWalletUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._newAdminWallet = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AdminWalletUpdatedEventResponse> adminWalletUpdatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("AdminWalletUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, AdminWalletUpdatedEventResponse>() {
            @Override
            public AdminWalletUpdatedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                AdminWalletUpdatedEventResponse typedResponse = new AdminWalletUpdatedEventResponse();
                typedResponse.log = log;
                typedResponse._newAdminWallet = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<TokensPerEthUpdatedEventResponse> getTokensPerEthUpdatedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("TokensPerEthUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<TokensPerEthUpdatedEventResponse> responses = new ArrayList<TokensPerEthUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokensPerEthUpdatedEventResponse typedResponse = new TokensPerEthUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._tokensPerEth = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TokensPerEthUpdatedEventResponse> tokensPerEthUpdatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("TokensPerEthUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TokensPerEthUpdatedEventResponse>() {
            @Override
            public TokensPerEthUpdatedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                TokensPerEthUpdatedEventResponse typedResponse = new TokensPerEthUpdatedEventResponse();
                typedResponse.log = log;
                typedResponse._tokensPerEth = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<TokensMintedEventResponse> getTokensMintedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("TokensMinted", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<TokensMintedEventResponse> responses = new ArrayList<TokensMintedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokensMintedEventResponse typedResponse = new TokensMintedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._balance = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TokensMintedEventResponse> tokensMintedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("TokensMinted", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TokensMintedEventResponse>() {
            @Override
            public TokensMintedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                TokensMintedEventResponse typedResponse = new TokensMintedEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._balance = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<TokensIssuedEventResponse> getTokensIssuedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("TokensIssued", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<TokensIssuedEventResponse> responses = new ArrayList<TokensIssuedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokensIssuedEventResponse typedResponse = new TokensIssuedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._balance = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._etherContributed = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TokensIssuedEventResponse> tokensIssuedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("TokensIssued", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TokensIssuedEventResponse>() {
            @Override
            public TokensIssuedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                TokensIssuedEventResponse typedResponse = new TokensIssuedEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._balance = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._etherContributed = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<RefundEventResponse> getRefundEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Refund", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<RefundEventResponse> responses = new ArrayList<RefundEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RefundEventResponse typedResponse = new RefundEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._tokens = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RefundEventResponse> refundEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Refund", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RefundEventResponse>() {
            @Override
            public RefundEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                RefundEventResponse typedResponse = new RefundEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._tokens = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<AirdropEventResponse> getAirdropEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Airdrop", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<AirdropEventResponse> responses = new ArrayList<AirdropEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AirdropEventResponse typedResponse = new AirdropEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._balance = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AirdropEventResponse> airdropEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Airdrop", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, AirdropEventResponse>() {
            @Override
            public AirdropEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                AirdropEventResponse typedResponse = new AirdropEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._balance = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<LockRemovedEventResponse> getLockRemovedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LockRemoved", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<LockRemovedEventResponse> responses = new ArrayList<LockRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LockRemovedEventResponse typedResponse = new LockRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._participant = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LockRemovedEventResponse> lockRemovedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LockRemoved", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LockRemovedEventResponse>() {
            @Override
            public LockRemovedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                LockRemovedEventResponse typedResponse = new LockRemovedEventResponse();
                typedResponse.log = log;
                typedResponse._participant = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<OwnershipTransferProposedEventResponse> getOwnershipTransferProposedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("OwnershipTransferProposed", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<OwnershipTransferProposedEventResponse> responses = new ArrayList<OwnershipTransferProposedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferProposedEventResponse typedResponse = new OwnershipTransferProposedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipTransferProposedEventResponse> ownershipTransferProposedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("OwnershipTransferProposed", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferProposedEventResponse>() {
            @Override
            public OwnershipTransferProposedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                OwnershipTransferProposedEventResponse typedResponse = new OwnershipTransferProposedEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
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

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<BigInteger> CLAWBACK_PERIOD() {
        final Function function = new Function("CLAWBACK_PERIOD", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> DATE_PRESALE_START() {
        final Function function = new Function("DATE_PRESALE_START", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> BONUS_ICO_WEEK_TWO() {
        final Function function = new Function("BONUS_ICO_WEEK_TWO", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> BONUS_PRESALE() {
        final Function function = new Function("BONUS_PRESALE", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> name() {
        final Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _amount) {
        final Function function = new Function(
                "approve", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> isTransferable() {
        final Function function = new Function("isTransferable", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _amount) {
        final Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> icoEtherContributed(String param0) {
        final Function function = new Function("icoEtherContributed", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setAdminWallet(String _wallet) {
        final Function function = new Function(
                "setAdminWallet", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_wallet)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> adminWallet() {
        final Function function = new Function("adminWallet", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> MIN_FUNDING_GOAL() {
        final Function function = new Function("MIN_FUNDING_GOAL", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> MIN_CONTRIBUTION() {
        final Function function = new Function("MIN_CONTRIBUTION", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> computeAirdrop(String _participant) {
        final Function function = new Function("computeAirdrop", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_participant)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> PRESALE_ETH_CAP() {
        final Function function = new Function("PRESALE_ETH_CAP", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> removeLock(String _participant) {
        final Function function = new Function(
                "removeLock", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_participant)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> wallet() {
        final Function function = new Function("wallet", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> claimAirdrop() {
        final Function function = new Function(
                "claimAirdrop", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> DATE_PRESALE_END() {
        final Function function = new Function("DATE_PRESALE_END", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> tokensClaimedAirdrop() {
        final Function function = new Function("tokensClaimedAirdrop", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> COOLDOWN_PERIOD() {
        final Function function = new Function("COOLDOWN_PERIOD", 
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

    public RemoteCall<BigInteger> TOKEN_SUPPLY_MKT() {
        final Function function = new Function("TOKEN_SUPPLY_MKT", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> mintMarketing(String _participant, BigInteger _tokens) {
        final Function function = new Function(
                "mintMarketing", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_participant), 
                new org.web3j.abi.datatypes.generated.Uint256(_tokens)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                "acceptOwnership", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> atNow() {
        final Function function = new Function("atNow", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> tokensIssuedTotal() {
        final Function function = new Function("tokensIssuedTotal", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> MAX_CONTRIBUTION() {
        final Function function = new Function("MAX_CONTRIBUTION", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> DATE_ICO_START() {
        final Function function = new Function("DATE_ICO_START", 
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

    public RemoteCall<TransactionReceipt> updateTokensPerEth(BigInteger _tokensPerEth) {
        final Function function = new Function(
                "updateTokensPerEth", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_tokensPerEth)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferMultiple(List<String> _addresses, List<BigInteger> _amounts) {
        final Function function = new Function(
                "transferMultiple", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_amounts, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _amount) {
        final Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> adminClaimAirdrop(String _participant) {
        final Function function = new Function(
                "adminClaimAirdrop", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_participant)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> refundClaimed(String param0) {
        final Function function = new Function("refundClaimed", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> ownerClawback() {
        final Function function = new Function(
                "ownerClawback", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> DATE_ICO_END() {
        final Function function = new Function("DATE_ICO_END", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> BONUS_ICO_WEEK_ONE() {
        final Function function = new Function("BONUS_ICO_WEEK_ONE", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> TOKEN_SUPPLY_ICO() {
        final Function function = new Function("TOKEN_SUPPLY_ICO", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> tokensPerEth() {
        final Function function = new Function("tokensPerEth", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> locked(String param0) {
        final Function function = new Function("locked", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> reclaimFunds() {
        final Function function = new Function(
                "reclaimFunds", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> icoTokensReceived(String param0) {
        final Function function = new Function("icoTokensReceived", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> TOKEN_SUPPLY_TOTAL() {
        final Function function = new Function("TOKEN_SUPPLY_TOTAL", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> airdropClaimed(String param0) {
        final Function function = new Function("airdropClaimed", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> newOwner() {
        final Function function = new Function("newOwner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> tokensIssuedIco() {
        final Function function = new Function("tokensIssuedIco", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferAnyERC20Token(String tokenAddress, BigInteger amount) {
        final Function function = new Function(
                "transferAnyERC20Token", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(tokenAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
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

    public RemoteCall<TransactionReceipt> setWallet(String _wallet) {
        final Function function = new Function(
                "setWallet", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_wallet)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> tokensIssuedMkt() {
        final Function function = new Function("tokensIssuedMkt", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> icoThresholdReached() {
        final Function function = new Function("icoThresholdReached", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> adminClaimAirdropMultiple(List<String> _addresses) {
        final Function function = new Function(
                "adminClaimAirdropMultiple", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> icoEtherReceived() {
        final Function function = new Function("icoEtherReceived", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        final Function function = new Function(
                "transferOwnership", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> removeLockMultiple(List<String> _participants) {
        final Function function = new Function(
                "removeLockMultiple", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_participants, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<IDH> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IDH.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<IDH> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IDH.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static IDH load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IDH(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IDH load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IDH(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class WalletUpdatedEventResponse {
        public Log log;

        public String _newWallet;
    }

    public static class AdminWalletUpdatedEventResponse {
        public Log log;

        public String _newAdminWallet;
    }

    public static class TokensPerEthUpdatedEventResponse {
        public Log log;

        public BigInteger _tokensPerEth;
    }

    public static class TokensMintedEventResponse {
        public Log log;

        public String _owner;

        public BigInteger _tokens;

        public BigInteger _balance;
    }

    public static class TokensIssuedEventResponse {
        public Log log;

        public String _owner;

        public BigInteger _tokens;

        public BigInteger _balance;

        public BigInteger _etherContributed;
    }

    public static class RefundEventResponse {
        public Log log;

        public String _owner;

        public BigInteger _amount;

        public BigInteger _tokens;
    }

    public static class AirdropEventResponse {
        public Log log;

        public String _owner;

        public BigInteger _amount;

        public BigInteger _balance;
    }

    public static class LockRemovedEventResponse {
        public Log log;

        public String _participant;
    }

    public static class OwnershipTransferProposedEventResponse {
        public Log log;

        public String _from;

        public String _to;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String _from;

        public String _to;
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
}
