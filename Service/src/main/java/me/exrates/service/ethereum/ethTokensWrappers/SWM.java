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
public class SWM extends Contract implements ethTokenERC20{
    private static final String BINARY = "606060405260408051908101604052600781527f4d4d545f302e3100000000000000000000000000000000000000000000000000602082015260049080516200004d929160200190620001e0565b50600b805460a860020a60ff02191690556000600d5562375f00600e556008600f5534156200007b57600080fd5b604051602080620021d4833981016040528080519150505b806000806040805190810160405280601081526020017f537761726d2046756e6420546f6b656e00000000000000000000000000000000815250601260408051908101604052600381527f53574d0000000000000000000000000000000000000000000000000000000000602082015260015b868686868686865b868686868686865b5b60008054600160a060020a03191633600160a060020a03161790555b600b805461010060a860020a031916610100600160a060020a038a1602179055600184805162000168929160200190620001e0565b506002805460ff191660ff851617905560038280516200018d929160200190620001e0565b5060058054600160a060020a031916600160a060020a0388161790556006859055600b805460ff1916821515179055436007555b505050505050505b505050505050505b505050505050505b506200028a565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200022357805160ff191683800117855562000253565b8280016001018555821562000253579182015b828111156200025357825182559160200191906001019062000236565b5b506200026292915062000266565b5090565b6200028791905b808211156200026257600081556001016200026d565b5090565b90565b611f3a806200029a6000396000f3006060604052361561019b5763ffffffff60e060020a60003504166305d2035b81146101a457806306fdde03146101cb578063095ea7b3146102565780630e2d1a2a1461028c57806317634514146102bd57806318160ddd146102e257806323b872dd1461030757806328f68d39146103435780632b2f4d841461036e578063313ce5671461038c5780633cebb823146103b557806340c10f19146103d65780634ee2cd7e1461040c57806354fd4d50146104405780636638c087146104cb57806370a082311461058f5780637d64bcb4146105c057806380a54001146105e7578063827f32c014610616578063843cfb9e1461064c5780638cadd6a21461067157806395d89b41146106a2578063981b24d01461072d578063a8660a7814610755578063a9059cbb1461077a578063b2d27832146107b0578063bef97c87146107d5578063c5bcc4f1146107fc578063cae9ca5114610821578063d3ce77fe1461089a578063dd62ed3e146108d0578063df8de3e714610907578063e77772fe14610928578063f41e60c514610957578063f77c479114610971575b5b600080fd5b5b005b34156101af57600080fd5b6101b76109a0565b604051901515815260200160405180910390f35b34156101d657600080fd5b6101de6109b0565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561021b5780820151818401525b602001610202565b50505050905090810190601f1680156102485780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561026157600080fd5b6101b7600160a060020a0360043516602435610a4e565b604051901515815260200160405180910390f35b341561029757600080fd5b6102ab600160a060020a0360043516610b07565b60405190815260200160405180910390f35b34156102c857600080fd5b6102ab610b3e565b60405190815260200160405180910390f35b34156102ed57600080fd5b6102ab610b44565b60405190815260200160405180910390f35b341561031257600080fd5b6101b7600160a060020a0360043581169060243516604435610b55565b604051901515815260200160405180910390f35b341561034e57600080fd5b6102ab600435602435610ba1565b60405190815260200160405180910390f35b341561037957600080fd5b6101a1600435602435604435610bd0565b005b341561039757600080fd5b61039f610c01565b60405160ff909116815260200160405180910390f35b34156103c057600080fd5b6101a1600160a060020a0360043516610c0a565b005b34156103e157600080fd5b6101b7600160a060020a0360043516602435610c52565b604051901515815260200160405180910390f35b341561041757600080fd5b6102ab600160a060020a0360043516602435610d17565b60405190815260200160405180910390f35b341561044b57600080fd5b6101de610e5d565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561021b5780820151818401525b602001610202565b50505050905090810190601f1680156102485780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156104d657600080fd5b61057360046024813581810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803560ff1690602001909190803590602001908201803590602001908080601f016020809104026020016040519081016040528181529291906020840183838082843750949650508435946020013515159350610efb92505050565b604051600160a060020a03909116815260200160405180910390f35b341561059a57600080fd5b6102ab600160a060020a036004351661112b565b60405190815260200160405180910390f35b34156105cb57600080fd5b6101b761113f565b604051901515815260200160405180910390f35b34156105f257600080fd5b610573611186565b604051600160a060020a03909116815260200160405180910390f35b341561062157600080fd5b6101b7600160a060020a0360043516602435611195565b604051901515815260200160405180910390f35b341561065757600080fd5b6102ab611268565b60405190815260200160405180910390f35b341561067c57600080fd5b6102ab60043560243560443560643561126e565b60405190815260200160405180910390f35b34156106ad57600080fd5b6101de611320565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561021b5780820151818401525b602001610202565b50505050905090810190601f1680156102485780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561073857600080fd5b6102ab6004356113be565b60405190815260200160405180910390f35b341561076057600080fd5b6102ab6114b6565b60405190815260200160405180910390f35b341561078557600080fd5b6101b7600160a060020a03600435166024356114bc565b604051901515815260200160405180910390f35b34156107bb57600080fd5b6102ab611506565b60405190815260200160405180910390f35b34156107e057600080fd5b6101b761150c565b604051901515815260200160405180910390f35b341561080757600080fd5b6102ab611515565b60405190815260200160405180910390f35b341561082c57600080fd5b6101b760048035600160a060020a03169060248035919060649060443590810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284375094965061151b95505050505050565b604051901515815260200160405180910390f35b34156108a557600080fd5b6101b7600160a060020a0360043516602435611639565b604051901515815260200160405180910390f35b34156108db57600080fd5b6102ab600160a060020a0360043581169060243516611706565b60405190815260200160405180910390f35b341561091257600080fd5b6101a1600160a060020a0360043516611733565b005b341561093357600080fd5b6105736118e0565b604051600160a060020a03909116815260200160405180910390f35b341561096257600080fd5b6101a160043515156118f4565b005b341561097c57600080fd5b610573611922565b604051600160a060020a03909116815260200160405180910390f35b600b5460a860020a900460ff1681565b60018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a465780601f10610a1b57610100808354040283529160200191610a46565b820191906000526020600020905b815481529060010190602001808311610a2957829003601f168201915b505050505081565b600b5460009060ff161515610a6257600080fd5b811580610a925750600160a060020a03338116600090815260096020908152604080832093871683529290522054155b1515610a9d57600080fd5b600160a060020a03338116600081815260096020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a35060015b92915050565b600160a060020a0381166000908152600c6020526040812054610b3690610b2d8461112b565b600d544261126e565b90505b919050565b60075481565b6000610b4f436113be565b90505b90565b60008382600b60159054906101000a900460ff161515610b7457600080fd5b610b7d82610b07565b811115610b8957600080fd5b610b94868686611931565b92505b5b50509392505050565b600e54600090610bc790610bbb848663ffffffff6119d316565b9063ffffffff6119ea16565b90505b92915050565b60005433600160a060020a03908116911614610beb57600080fd5b600d839055600f829055600e8190555b5b505050565b60025460ff1681565b60005433600160a060020a03908116911614610c2557600080fd5b6000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b50565b6000805433600160a060020a03908116911614610c6e57600080fd5b600b5460a860020a900460ff1615610c8557600080fd5b610c8f8383611195565b50600160a060020a0383166000908152600c6020526040902054610cb9908363ffffffff611a0616565b600160a060020a0384166000818152600c60205260409081902092909255907f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968859084905190815260200160405180910390a25060015b5b5b92915050565b600160a060020a0382166000908152600860205260408120541580610d775750600160a060020a038316600090815260086020526040812080548492908110610d5c57fe5b906000526020600020900160005b50546001608060020a0316115b15610e2d57600554600160a060020a031615610e2057600554600654600160a060020a0390911690634ee2cd7e908590610db2908690611a20565b60006040516020015260405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401602060405180830381600087803b1515610dfe57600080fd5b6102c65a03f11515610e0f57600080fd5b505050604051805190509050610b01565b506000610b01565b610b01565b600160a060020a0383166000908152600860205260409020610e4f9083611a3a565b9050610b01565b5b92915050565b60048054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a465780601f10610a1b57610100808354040283529160200191610a46565b820191906000526020600020905b815481529060010190602001808311610a2957829003601f168201915b505050505081565b600080831515610f09574393505b600b546101009004600160a060020a0316635b7b72c130868a8a8a8960006040516020015260405160e060020a63ffffffff8916028152600160a060020a038716600482019081526024820187905260ff8516606483015282151560a483015260c0604483019081529091608481019060c40187818151815260200191508051906020019080838360005b83811015610fad5780820151818401525b602001610f94565b50505050905090810190601f168015610fda5780820380516001836020036101000a031916815260200191505b50838103825285818151815260200191508051906020019080838360005b838110156110115780820151818401525b602001610ff8565b50505050905090810190601f16801561103e5780820380516001836020036101000a031916815260200191505b5098505050505050505050602060405180830381600087803b151561106257600080fd5b6102c65a03f1151561107357600080fd5b5050506040518051915050600160a060020a038116633cebb8233360405160e060020a63ffffffff8416028152600160a060020a039091166004820152602401600060405180830381600087803b15156110cc57600080fd5b6102c65a03f115156110dd57600080fd5b50505080600160a060020a03167f086c875b377f900b07ce03575813022f05dd10ed7640b5282cf6d3c3fc352ade8560405190815260200160405180910390a28091505b5095945050505050565b6000610b368243610d17565b90505b919050565b6000805433600160a060020a0390811691161461115b57600080fd5b600b5460a860020a900460ff161561117257600080fd5b42600d55610b4f611bae565b90505b5b5b90565b600554600160a060020a031681565b600080548190819033600160a060020a039081169116146111b557600080fd5b6111bd610b44565b9150838201829010156111cf57600080fd5b6111d88561112b565b9050838101819010156111ea57600080fd5b6111f7600a858401611c3b565b600160a060020a038516600090815260086020526040902061121b90828601611c3b565b84600160a060020a031660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8660405190815260200160405180910390a3600192505b5b505092915050565b600f5481565b600080600080858510156112855760009350611315565b6112ac61129f600f54600e54611d3e90919063ffffffff16565b879063ffffffff611a0616565b85106112ba57869350611315565b6112c48686610ba1565b600f549093506112da908463ffffffff6119d316565b600f5490925061130090610bbb8a8563ffffffff611d3e16565b9063ffffffff6119ea16565b9050611312878263ffffffff6119d316565b93505b505050949350505050565b60038054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a465780601f10610a1b57610100808354040283529160200191610a46565b820191906000526020600020905b815481529060010190602001808311610a2957829003601f168201915b505050505081565b600a5460009015806113f7575081600a60008154811015156113dc57fe5b906000526020600020900160005b50546001608060020a0316115b1561149e57600554600160a060020a03161561149157600554600654600160a060020a039091169063981b24d090611430908590611a20565b60006040516020015260405160e060020a63ffffffff84160281526004810191909152602401602060405180830381600087803b151561146f57600080fd5b6102c65a03f1151561148057600080fd5b505050604051805190509050610b39565b506000610b39565b610b39565b6114a9600a83611a3a565b9050610b39565b5b919050565b600d5481565b60003382600b60159054906101000a900460ff1615156114db57600080fd5b6114e482610b07565b8111156114f057600080fd5b6114fa8585611d6d565b92505b5b505092915050565b600e5481565b600b5460ff1681565b60065481565b60006115278484610a4e565b151561153257600080fd5b83600160a060020a0316638f4ffcb1338530866040518563ffffffff1660e060020a0281526004018085600160a060020a0316600160a060020a0316815260200184815260200183600160a060020a0316600160a060020a0316815260200180602001828103825283818151815260200191508051906020019080838360005b838110156115cb5780820151818401525b6020016115b2565b50505050905090810190601f1680156115f85780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b151561161957600080fd5b6102c65a03f1151561162a57600080fd5b505050600190505b9392505050565b600080548190819033600160a060020a0390811691161461165957600080fd5b611661610b44565b91508382101561167057600080fd5b6116798561112b565b90508381101561168857600080fd5b611695600a858403611c3b565b600160a060020a03851660009081526008602052604090206116b990858303611c3b565b600085600160a060020a03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8660405190815260200160405180910390a3600192505b5b505092915050565b600160a060020a038083166000908152600960209081526040808320938516835292905220545b92915050565b60008054819033600160a060020a0390811691161461175157600080fd5b600160a060020a038316151561179f57600054600160a060020a039081169030163180156108fc0290604051600060405180830381858888f19350505050151561179a57600080fd5b610bfb565b82915081600160a060020a03166370a082313060006040516020015260405160e060020a63ffffffff8416028152600160a060020a039091166004820152602401602060405180830381600087803b15156117f957600080fd5b6102c65a03f1151561180a57600080fd5b505050604051805160008054919350600160a060020a03808616935063a9059cbb92169084906040516020015260405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401602060405180830381600087803b151561187a57600080fd5b6102c65a03f1151561188b57600080fd5b50505060405180515050600054600160a060020a039081169084167ff931edb47c50b4b4104c187b5814a9aef5f709e17e2ecf9617e860cacade929c8360405190815260200160405180910390a35b5b505050565b600b546101009004600160a060020a031681565b60005433600160a060020a0390811691161461190f57600080fd5b600b805460ff19168215151790555b5b50565b600054600160a060020a031681565b6000805433600160a060020a039081169116146119be57600b5460ff16151561195957600080fd5b600160a060020a03808516600090815260096020908152604080832033909416835292905220548290101561199057506000611632565b600160a060020a03808516600090815260096020908152604080832033909416835292905220805483900390555b6119c9848484611d95565b90505b9392505050565b6000828211156119df57fe5b508082035b92915050565b60008082848115156119f857fe5b0490508091505b5092915050565b600082820183811015611a1557fe5b8091505b5092915050565b6000818310611a2f5781610bc7565b825b90505b92915050565b600080600080858054905060001415611a565760009350611ba5565b855486906000198101908110611a6857fe5b906000526020600020900160005b50546001608060020a03168510611acd57855486906000198101908110611a9957fe5b906000526020600020900160005b505470010000000000000000000000000000000090046001608060020a03169350611ba5565b856000815481101515611adc57fe5b906000526020600020900160005b50546001608060020a0316851015611b055760009350611ba5565b8554600093506000190191505b82821115611b675760026001838501015b049050848682815481101515611b3557fe5b906000526020600020900160005b50546001608060020a031611611b5b57809250611b62565b6001810391505b611b12565b8583815481101515611b7557fe5b906000526020600020900160005b505470010000000000000000000000000000000090046001608060020a031693505b50505092915050565b6000805433600160a060020a03908116911614611bca57600080fd5b600b5460a860020a900460ff1615611be157600080fd5b600b805475ff000000000000000000000000000000000000000000191660a860020a1790557fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0860405160405180910390a15060015b5b5b90565b815460009081901580611c7857508354439085906000198101908110611c5d57fe5b906000526020600020900160005b50546001608060020a0316105b15611cee5783548490611c8e8260018301611ec3565b81548110611c9857fe5b906000526020600020900160005b5080546001608060020a03858116700100000000000000000000000000000000024382166fffffffffffffffffffffffffffffffff1990931692909217161781559150611d37565b835484906000198101908110611d0057fe5b906000526020600020900160005b5080546001608060020a0380861670010000000000000000000000000000000002911617815590505b5b50505050565b6000828202831580611d5a5750828482811515611d5757fe5b04145b1515611a1557fe5b8091505b5092915050565b600b5460009060ff161515611d8157600080fd5b610bc7338484611d95565b90505b92915050565b60008080831515611da95760019250610b97565b600654439010611db857600080fd5b600160a060020a03851615801590611de2575030600160a060020a031685600160a060020a031614155b1515611ded57600080fd5b611df78643610d17565b915083821015611e0a5760009250610b97565b600160a060020a0386166000908152600860205260409020611e2e90858403611c3b565b611e388543610d17565b905083810181901015611e4a57600080fd5b600160a060020a0385166000908152600860205260409020611e6e90828601611c3b565b84600160a060020a031686600160a060020a03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8660405190815260200160405180910390a3600192505b50509392505050565b815481835581811511610bfb57600083815260209020610bfb918101908301611eed565b5b505050565b610b5291905b80821115611f075760008155600101611ef3565b5090565b905600a165627a7a72305820d6138bbb1454d36904341954f7aeb7da4c13131facb8916fd8937b07d731396800290000000000000000000000007a951603a189708ae7ad9aa6c4883ce5ceb24a11\n";

    public static final String FUNC_MINTINGFINISHED = "mintingFinished";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_VESTEDBALANCEOF = "vestedBalanceOf";

    public static final String FUNC_CREATIONBLOCK = "creationBlock";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_GETVESTINGPERIODSCOMPLETED = "getVestingPeriodsCompleted";

    public static final String FUNC_SETVESTINGPARAMS = "setVestingParams";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_CHANGECONTROLLER = "changeController";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_BALANCEOFAT = "balanceOfAt";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_CREATECLONETOKEN = "createCloneToken";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_FINISHMINTING = "finishMinting";

    public static final String FUNC_PARENTTOKEN = "parentToken";

    public static final String FUNC_GENERATETOKENS = "generateTokens";

    public static final String FUNC_VESTINGTOTALPERIODS = "vestingTotalPeriods";

    public static final String FUNC_GETVESTEDBALANCE = "getVestedBalance";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLYAT = "totalSupplyAt";

    public static final String FUNC_VESTINGSTARTTIME = "vestingStartTime";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_VESTINGPERIODTIME = "vestingPeriodTime";

    public static final String FUNC_TRANSFERSENABLED = "transfersEnabled";

    public static final String FUNC_PARENTSNAPSHOTBLOCK = "parentSnapShotBlock";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_DESTROYTOKENS = "destroyTokens";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_CLAIMTOKENS = "claimTokens";

    public static final String FUNC_TOKENFACTORY = "tokenFactory";

    public static final String FUNC_ENABLETRANSFERS = "enableTransfers";

    public static final String FUNC_CONTROLLER = "controller";

    public static final Event MINT_EVENT = new Event("Mint", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event MINTFINISHED_EVENT = new Event("MintFinished", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event CLAIMEDTOKENS_EVENT = new Event("ClaimedTokens", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event NEWCLONETOKEN_EVENT = new Event("NewCloneToken", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected SWM(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SWM(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _amount) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> vestedBalanceOf(String _owner) {
        final Function function = new Function(FUNC_VESTEDBALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> creationBlock() {
        final Function function = new Function(FUNC_CREATIONBLOCK, 
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

    public RemoteCall<BigInteger> getVestingPeriodsCompleted(BigInteger _vestingStartTime, BigInteger _currentTime) {
        final Function function = new Function(FUNC_GETVESTINGPERIODSCOMPLETED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_vestingStartTime), 
                new org.web3j.abi.datatypes.generated.Uint256(_currentTime)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setVestingParams(BigInteger _vestingStartTime, BigInteger _vestingTotalPeriods, BigInteger _vestingPeriodTime) {
        final Function function = new Function(
                FUNC_SETVESTINGPARAMS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_vestingStartTime), 
                new org.web3j.abi.datatypes.generated.Uint256(_vestingTotalPeriods), 
                new org.web3j.abi.datatypes.generated.Uint256(_vestingPeriodTime)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> changeController(String _newController) {
        final Function function = new Function(
                FUNC_CHANGECONTROLLER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newController)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mint(String _to, BigInteger _amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOfAt(String _owner, BigInteger _blockNumber) {
        final Function function = new Function(FUNC_BALANCEOFAT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.generated.Uint256(_blockNumber)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> version() {
        final Function function = new Function(FUNC_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> createCloneToken(String _cloneTokenName, BigInteger _cloneDecimalUnits, String _cloneTokenSymbol, BigInteger _snapshotBlock, Boolean _transfersEnabled) {
        final Function function = new Function(
                FUNC_CREATECLONETOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cloneTokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_cloneDecimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_cloneTokenSymbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_snapshotBlock), 
                new org.web3j.abi.datatypes.Bool(_transfersEnabled)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> finishMinting() {
        final Function function = new Function(
                FUNC_FINISHMINTING, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> parentToken() {
        final Function function = new Function(FUNC_PARENTTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> generateTokens(String _owner, BigInteger _amount) {
        final Function function = new Function(
                FUNC_GENERATETOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> vestingTotalPeriods() {
        final Function function = new Function(FUNC_VESTINGTOTALPERIODS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getVestedBalance(BigInteger _initialBalance, BigInteger _currentBalance, BigInteger _vestingStartTime, BigInteger _currentTime) {
        final Function function = new Function(FUNC_GETVESTEDBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialBalance), 
                new org.web3j.abi.datatypes.generated.Uint256(_currentBalance), 
                new org.web3j.abi.datatypes.generated.Uint256(_vestingStartTime), 
                new org.web3j.abi.datatypes.generated.Uint256(_currentTime)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> totalSupplyAt(BigInteger _blockNumber) {
        final Function function = new Function(FUNC_TOTALSUPPLYAT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_blockNumber)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> vestingStartTime() {
        final Function function = new Function(FUNC_VESTINGSTARTTIME, 
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

    public RemoteCall<BigInteger> vestingPeriodTime() {
        final Function function = new Function(FUNC_VESTINGPERIODTIME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> transfersEnabled() {
        final Function function = new Function(FUNC_TRANSFERSENABLED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> parentSnapShotBlock() {
        final Function function = new Function(FUNC_PARENTSNAPSHOTBLOCK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(String _spender, BigInteger _amount, byte[] _extraData) {
        final Function function = new Function(
                FUNC_APPROVEANDCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount), 
                new org.web3j.abi.datatypes.DynamicBytes(_extraData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> destroyTokens(String _owner, BigInteger _amount) {
        final Function function = new Function(
                FUNC_DESTROYTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
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

    public RemoteCall<TransactionReceipt> claimTokens(String _token) {
        final Function function = new Function(
                FUNC_CLAIMTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_token)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> tokenFactory() {
        final Function function = new Function(FUNC_TOKENFACTORY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> enableTransfers(Boolean _transfersEnabled) {
        final Function function = new Function(
                FUNC_ENABLETRANSFERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Bool(_transfersEnabled)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> controller() {
        final Function function = new Function(FUNC_CONTROLLER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<SWM> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _tokenFactory) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_tokenFactory)));
        return deployRemoteCall(SWM.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<SWM> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _tokenFactory) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_tokenFactory)));
        return deployRemoteCall(SWM.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINT_EVENT, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MintEventResponse> mintEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINT_EVENT));
        return mintEventObservable(filter);
    }

    public List<MintFinishedEventResponse> getMintFinishedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINTFINISHED_EVENT, transactionReceipt);
        ArrayList<MintFinishedEventResponse> responses = new ArrayList<MintFinishedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintFinishedEventResponse>() {
            @Override
            public MintFinishedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINTFINISHED_EVENT, log);
                MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTFINISHED_EVENT));
        return mintFinishedEventObservable(filter);
    }

    public List<ClaimedTokensEventResponse> getClaimedTokensEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CLAIMEDTOKENS_EVENT, transactionReceipt);
        ArrayList<ClaimedTokensEventResponse> responses = new ArrayList<ClaimedTokensEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimedTokensEventResponse typedResponse = new ClaimedTokensEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._token = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._controller = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ClaimedTokensEventResponse> claimedTokensEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ClaimedTokensEventResponse>() {
            @Override
            public ClaimedTokensEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CLAIMEDTOKENS_EVENT, log);
                ClaimedTokensEventResponse typedResponse = new ClaimedTokensEventResponse();
                typedResponse.log = log;
                typedResponse._token = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._controller = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ClaimedTokensEventResponse> claimedTokensEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CLAIMEDTOKENS_EVENT));
        return claimedTokensEventObservable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<NewCloneTokenEventResponse> getNewCloneTokenEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWCLONETOKEN_EVENT, transactionReceipt);
        ArrayList<NewCloneTokenEventResponse> responses = new ArrayList<NewCloneTokenEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewCloneTokenEventResponse typedResponse = new NewCloneTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._cloneToken = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._snapshotBlock = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<NewCloneTokenEventResponse> newCloneTokenEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, NewCloneTokenEventResponse>() {
            @Override
            public NewCloneTokenEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWCLONETOKEN_EVENT, log);
                NewCloneTokenEventResponse typedResponse = new NewCloneTokenEventResponse();
                typedResponse.log = log;
                typedResponse._cloneToken = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._snapshotBlock = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<NewCloneTokenEventResponse> newCloneTokenEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWCLONETOKEN_EVENT));
        return newCloneTokenEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public static SWM load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SWM(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static SWM load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SWM(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class MintEventResponse {
        public Log log;

        public String to;

        public BigInteger amount;
    }

    public static class MintFinishedEventResponse {
        public Log log;
    }

    public static class ClaimedTokensEventResponse {
        public Log log;

        public String _token;

        public String _controller;

        public BigInteger _amount;
    }

    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _amount;
    }

    public static class NewCloneTokenEventResponse {
        public Log log;

        public String _cloneToken;

        public BigInteger _snapshotBlock;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _amount;
    }
}
