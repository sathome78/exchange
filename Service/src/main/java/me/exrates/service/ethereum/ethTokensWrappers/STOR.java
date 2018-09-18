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
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class STOR extends Contract implements ethTokenERC20 {
    private static final String BINARY = "60606040526004805460a060020a60ff02191690556006805460ff199081169091556000600a55600b8054909116905534156200003857fe5b6040516200228b3803806200228b83398101604090815281516020830151918301516060840151608085015160a086015193860195949094019391929091905b335b5b60038054600160a060020a03191633600160a060020a03161790555b600b805461010060a860020a031916610100600160a060020a038416021790555b5060038054600160a060020a03191633600160a060020a03161790558551620000e990600e906020890190620001b0565b508451620000ff90600f906020880190620001b0565b50600084815560108490556011829055600354600160a060020a031681526001602052604081208590558411156200017d5760035460005460408051600160a060020a039093168352602083019190915280517f30385c845b448a36257a6a1716e6ad2e1bc2cbe333cde1e69fe849ad6511adfe9281900390910190a15b811515620001a2576006805460ff191660011790556000541515620001a25760006000fd5b5b5b5050505050506200025a565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620001f357805160ff191683800117855562000223565b8280016001018555821562000223579182015b828111156200022357825182559160200191906001019062000206565b5b506200023292915062000236565b5090565b6200025791905b808211156200023257600081556001016200023d565b5090565b90565b612021806200026a6000396000f300606060405236156101f35763ffffffff60e060020a60003504166302f652a381146101f557806305d2035b1461021857806306fdde031461023c578063095ea7b3146102cc57806318160ddd146102ff57806323b872dd1461032157806329ff4f531461035a578063313ce567146103785780633d0acdaa1461039a5780633fa615b0146103c857806340c10f19146103ea57806342c1867b1461040b578063432146751461043b57806345977d031461045e57806345e7e140146104735780634eee966f146104a157806351ed17a4146105365780635de4ccb0146105815780635f412d4f146105ad578063600440cb146105bf578063612544b3146105eb5780636ffc22b81461061957806370a08231146106375780637386f0a7146106655780638444b39114610694578063867c2857146106c85780638da5cb5b146106f857806395d89b411461072457806395fe6f1d146107b457806396132521146107e45780639738968c14610808578063a9059cbb1461082c578063b4ecb8471461085f578063c33105171461088f578063c752ff62146108b1578063d1f276d3146108d3578063d7e7088a146108ff578063dd62ed3e1461091d578063df8de3e714610951578063eefa597b1461096f578063f05834d614610993578063f2fde38b14610a90578063ffeb7d7514610aae575bfe5b34156101fd57fe5b610216600160a060020a03600435166024351515610acc565b005b341561022057fe5b610228610b2f565b604080519115158252519081900360200190f35b341561024457fe5b61024c610b38565b604080516020808252835181830152835191928392908301918501908083838215610292575b80518252602083111561029257601f199092019160209182019101610272565b505050905090810190601f1680156102be5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156102d457fe5b610228600160a060020a0360043516602435610bc6565b604080519115158252519081900360200190f35b341561030757fe5b61030f610c6d565b60408051918252519081900360200190f35b341561032957fe5b610228600160a060020a0360043581169060243516604435610c73565b604080519115158252519081900360200190f35b341561036257fe5b610216600160a060020a0360043516610ccb565b005b341561038057fe5b61030f610d31565b60408051918252519081900360200190f35b34156103a257fe5b61030f600160a060020a0360043516610d37565b60408051918252519081900360200190f35b34156103d057fe5b61030f610d59565b60408051918252519081900360200190f35b34156103f257fe5b610216600160a060020a0360043516602435610d5f565b005b341561041357fe5b610228600160a060020a0360043516610f0c565b604080519115158252519081900360200190f35b341561044357fe5b610216600160a060020a03600435166024351515610f21565b005b341561046657fe5b610216600435610fb5565b005b341561047b57fe5b61030f600160a060020a0360043516611125565b60408051918252519081900360200190f35b34156104a957fe5b610216600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284375050604080516020601f89358b0180359182018390048302840183019094528083529799988101979196509182019450925082915084018382808284375094965061114795505050505050565b005b341561053e57fe5b610552600160a060020a03600435166112bb565b604080519586526020860194909452848401929092521515606084015215156080830152519081900360a00190f35b341561058957fe5b6105916112ee565b60408051600160a060020a039092168252519081900360200190f35b34156105b557fe5b6102166112fd565b005b34156105c757fe5b610591611332565b60408051600160a060020a039092168252519081900360200190f35b34156105f357fe5b61030f600160a060020a0360043516611346565b60408051918252519081900360200190f35b341561062157fe5b610216600160a060020a0360043516611365565b005b341561063f57fe5b61030f600160a060020a03600435166113cf565b60408051918252519081900360200190f35b341561066d57fe5b6105916004356113ee565b60408051600160a060020a039092168252519081900360200190f35b341561069c57fe5b6106a4611420565b604051808260048111156106b457fe5b60ff16815260200191505060405180910390f35b34156106d057fe5b610228600160a060020a036004351661146d565b604080519115158252519081900360200190f35b341561070057fe5b610591611482565b60408051600160a060020a039092168252519081900360200190f35b341561072c57fe5b61024c611491565b604080516020808252835181830152835191928392908301918501908083838215610292575b80518252602083111561029257601f199092019160209182019101610272565b505050905090810190601f1680156102be5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156107bc57fe5b610228600160a060020a036004351661151f565b604080519115158252519081900360200190f35b34156107ec57fe5b610228611544565b604080519115158252519081900360200190f35b341561081057fe5b610228611554565b604080519115158252519081900360200190f35b341561083457fe5b610228600160a060020a036004351660243561157a565b604080519115158252519081900360200190f35b341561086757fe5b610228600160a060020a03600435166115d0565b604080519115158252519081900360200190f35b341561089757fe5b61030f6115fa565b60408051918252519081900360200190f35b34156108b957fe5b61030f611600565b60408051918252519081900360200190f35b34156108db57fe5b610591611606565b60408051600160a060020a039092168252519081900360200190f35b341561090757fe5b610216600160a060020a0360043516611615565b005b341561092557fe5b61030f600160a060020a03600435811690602435166117f7565b60408051918252519081900360200190f35b341561095957fe5b610216600160a060020a0360043516611824565b005b341561097757fe5b6102286119a6565b604080519115158252519081900360200190f35b341561099b57fe5b610216600480803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843750506040805187358901803560208181028481018201909552818452989a998901989297509082019550935083925085019084908082843750506040805187358901803560208181028481018201909552818452989a998901989297509082019550935083925085019084908082843750506040805187358901803560208181028481018201909552818452989a9989019892975090820195509350839250850190849080828437509496506119ac95505050505050565b005b3415610a9857fe5b610216600160a060020a0360043516611ac9565b005b3415610ab657fe5b610216600160a060020a0360043516611b62565b005b60035433600160a060020a03908116911614610ae85760006000fd5b60045460009060a060020a900460ff1615610b035760006000fd5b600160a060020a0383166000908152600560205260409020805460ff19168315151790555b5b505b5050565b60065460ff1681565b600e805460408051602060026001851615610100026000190190941693909304601f81018490048402820184019092528181529291830182828015610bbe5780601f10610b9357610100808354040283529160200191610bbe565b820191906000526020600020905b815481529060010190602001808311610ba157829003601f168201915b505050505081565b60008115801590610bfb5750600160a060020a0333811660009081526002602090815260408083209387168352929052205415155b15610c065760006000fd5b600160a060020a03338116600081815260026020908152604080832094881680845294825291829020869055815186815291517f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259281900390910190a35060015b92915050565b60005481565b600454600090849060a060020a900460ff161515610cb357600160a060020a03811660009081526005602052604090205460ff161515610cb35760006000fd5b5b610cbf858585611bca565b91505b5b509392505050565b60035433600160a060020a03908116911614610ce75760006000fd5b60045460009060a060020a900460ff1615610d025760006000fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0384161790555b5b505b50565b60105481565b600160a060020a0381166000908152600860205260409020600101545b919050565b60115481565b600160a060020a03331660009081526007602052604090205460ff161515610d875760006000fd5b60065460ff1615610d985760006000fd5b600054732a3a06742773fd5b96fb10682e58de0dc8f797a16366098d4f9091836000604051602001526040518363ffffffff1660e060020a028152600401808381526020018281526020019250505060206040518083038186803b1515610dfb57fe5b6102c65a03f41515610e0957fe5b50506040805180516000908155600160a060020a038616815260016020908152838220549281019190915282517f66098d4f0000000000000000000000000000000000000000000000000000000081526004810192909252602482018590529151732a3a06742773fd5b96fb10682e58de0dc8f797a193506366098d4f92604480840193919291829003018186803b1515610ea057fe5b6102c65a03f41515610eae57fe5b5050604080518051600160a060020a0386166000818152600160209081528582209390935586845293519094507fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a35b5b5b5050565b60076020526000908152604090205460ff1681565b60035433600160a060020a03908116911614610f3d5760006000fd5b60065460ff1615610f4e5760006000fd5b600160a060020a038216600081815260076020908152604091829020805460ff191685151590811790915582519384529083015280517f4b0adf6c802794c7dde28a08a4e07131abcff3bf9603cd71f14f90bec7865efa9281900390910190a15b5b5b5050565b6000610fbf611420565b905060035b816004811115610fd057fe5b1480610fe8575060045b816004811115610fe657fe5b145b1515610ff45760006000fd5b8115156110015760006000fd5b600160a060020a0333166000908152600160205260409020546110249083611ccd565b600160a060020a0333166000908152600160205260408120919091555461104b9083611ccd565b600055600d5461105b9083611ce4565b600d55600c54604080517f753e88e5000000000000000000000000000000000000000000000000000000008152600160a060020a033381166004830152602482018690529151919092169163753e88e591604480830192600092919082900301818387803b15156110c857fe5b6102c65a03f115156110d657fe5b5050600c54604080518581529051600160a060020a03928316935033909216917f7e5c344a8141a805725cb476f76c6953b842222b967edd1f78ddb6e8b3f397ac9181900360200190a35b5050565b600160a060020a0381166000908152600860205260409020600201545b919050565b60035433600160a060020a039081169116146111635760006000fd5b815161117690600e906020850190611f2b565b50805161118a90600f906020840190611f2b565b5060408051818152600e8054600260001961010060018416150201909116049282018390527fd131ab1e6f279deea74e13a18477e13e2107deb6dc8ae955648948be5841fb46929091600f91819060208201906060830190869080156112315780601f1061120657610100808354040283529160200191611231565b820191906000526020600020905b81548152906001019060200180831161121457829003601f168201915b50508381038252845460026000196101006001841615020190911604808252602090910190859080156112a55780601f1061127a576101008083540402835291602001916112a5565b820191906000526020600020905b81548152906001019060200180831161128857829003601f168201915b505094505050505060405180910390a15b5b5050565b60086020526000908152604090208054600182015460028301546003909301549192909160ff8082169161010090041685565b600c54600160a060020a031681565b60045433600160a060020a039081169116146113195760006000fd5b6006805460ff1916600117905561132e611d0c565b5b5b565b600b546101009004600160a060020a031681565b600160a060020a0381166000908152600860205260409020545b919050565b600160a060020a03331660009081526007602052604081205460ff16151561138d5760006000fd5b60065460ff161561139e5760006000fd5b50600160a060020a038116600090815260086020526040902060038101805461ff0019166101001790555b5b5b5050565b600160a060020a0381166000908152600160205260409020545b919050565b60098054829081106113fc57fe5b906000526020600020900160005b915054906101000a9004600160a060020a031681565b600061142a611554565b151561143857506001611467565b600c54600160a060020a0316151561145257506002611467565b600d54151561146357506003611467565b5060045b5b5b5b90565b60056020526000908152604090205460ff1681565b600354600160a060020a031681565b600f805460408051602060026001851615610100026000190190941693909304601f81018490048402820184019092528181529291830182828015610bbe5780601f10610b9357610100808354040283529160200191610bbe565b820191906000526020600020905b815481529060010190602001808311610ba157829003601f168201915b505050505081565b600160a060020a03811660009081526008602052604090206003015460ff165b919050565b60045460a060020a900460ff1681565b60045460009060a060020a900460ff16801561157357506115736119a6565b5b90505b90565b600454600090339060a060020a900460ff1615156115ba57600160a060020a03811660009081526005602052604090205460ff1615156115ba5760006000fd5b5b6115c58484611d56565b91505b5b5092915050565b600160a060020a038116600090815260086020526040902060030154610100900460ff165b919050565b600a5481565b600d5481565b600454600160a060020a031681565b61161d611554565b15156116295760006000fd5b600160a060020a038116151561163f5760006000fd5b600b5433600160a060020a0390811661010090920416146116605760006000fd5b60045b61166b611420565b600481111561167657fe5b14156116825760006000fd5b600c805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038381169190911791829055604080516000602091820181905282517f61d3d7a6000000000000000000000000000000000000000000000000000000008152925194909316936361d3d7a6936004808501948390030190829087803b151561170857fe5b6102c65a03f1151561171657fe5b5050604051511515905061172a5760006000fd5b60008054600c5460408051602090810185905281517f4b2ba0dd00000000000000000000000000000000000000000000000000000000815291519394600160a060020a0390931693634b2ba0dd936004808501948390030190829087803b151561179057fe5b6102c65a03f1151561179e57fe5b5050604051519190911490506117b45760006000fd5b600c5460408051600160a060020a039092168252517f7845d5aa74cc410e35571258d954f23b82276e160fe8c188fa80566580f279cc9181900360200190a15b50565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b600354600090819033600160a060020a039081169116146118455760006000fd5b600160a060020a038316151561185b5760006000fd5b82915081600160a060020a03166370a08231306000604051602001526040518263ffffffff1660e060020a0281526004018082600160a060020a0316600160a060020a03168152602001915050602060405180830381600087803b15156118be57fe5b6102c65a03f115156118cc57fe5b50506040805180516003546000602093840181905284517fa9059cbb000000000000000000000000000000000000000000000000000000008152600160a060020a039283166004820152602481018490529451929650908716945063a9059cbb936044808201949392918390030190829087803b151561194857fe5b6102c65a03f1151561195657fe5b5050604080516003548482529151600160a060020a039283169350918616917ff931edb47c50b4b4104c187b5814a9aef5f709e17e2ecf9617e860cacade929c9181900360200190a35b5b505050565b60015b90565b60065460009060ff16156119c05760006000fd5b60035433600160a060020a039081169116146119dc5760006000fd5b600b5460ff16156119e957fe5b83518551146119f457fe5b82518451146119ff57fe5b8151835114611a0a57fe5b5060005b8451811015611ab2578451600090869083908110611a2857fe5b60209081029091010151600160a060020a031614611aa857611aa88582815181101515611a5157fe5b906020019060200201518583815181101515611a6957fe5b906020019060200201518584815181101515611a8157fe5b906020019060200201518585815181101515611a9957fe5b90602001906020020151611e0a565b5b5b600101611a0e565b600b805460ff191660011790555b5b5b5050505050565b60035433600160a060020a03908116911614611ae55760006000fd5b600160a060020a0381161515611afb5760006000fd5b600354604051600160a060020a038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a36003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b50565b600160a060020a0381161515611b785760006000fd5b600b5433600160a060020a039081166101009092041614611b995760006000fd5b600b805474ffffffffffffffffffffffffffffffffffffffff001916610100600160a060020a038416021790555b50565b600160a060020a038084166000908152600260209081526040808320338516845282528083205493861683526001909152812054909190611c0b9084611ce4565b600160a060020a038086166000908152600160205260408082209390935590871681522054611c3a9084611ccd565b600160a060020a038616600090815260016020526040902055611c5d8184611ccd565b600160a060020a038087166000818152600260209081526040808320338616845282529182902094909455805187815290519288169391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a3600191505b509392505050565b600082821115611cd957fe5b508082035b92915050565b6000828201838110801590611cf95750828110155b1515611d0157fe5b8091505b5092915050565b60045433600160a060020a03908116911614611d285760006000fd5b6004805474ff0000000000000000000000000000000000000000191660a060020a1790555b5b565b60015b90565b600160a060020a033316600090815260016020526040812054611d799083611ccd565b600160a060020a033381166000908152600160205260408082209390935590851681522054611da89083611ce4565b600160a060020a038085166000818152600160209081526040918290209490945580518681529051919333909316927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a35060015b92915050565b60065460ff1615611e1b5760006000fd5b60035433600160a060020a03908116911614611e375760006000fd5b600160a060020a0384161515611e4957fe5b611e528461151f565b1515611ea1576009805460018101611e6a8382611faa565b916000526020600020900160005b8154600160a060020a038089166101009390930a9283029202191617905550600a805460010190555b6040805160a0810182528481526020808201858152828401858152600160608501818152600060808701818152600160a060020a038d16825260089096529690962094518555915191840191909155516002830155915160039091018054925115156101000261ff001992151560ff1990941693909317919091169190911790555b5b5b50505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10611f6c57805160ff1916838001178555611f99565b82800160010185558215611f99579182015b82811115611f99578251825591602001919060010190611f7e565b5b50611fa6929150611fd4565b5090565b815481835581811511610b2857600083815260209020610b28918101908301611fd4565b5b505050565b61146791905b80821115611fa65760008155600101611fda565b5090565b905600a165627a7a7230582051cdcd01e28bff7e6c64773a244d30a010ae885e9a0e9e16188e1055e87dcd56002900000000000000000000000000000000000000000000000000000000000000c000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000005f5e100000000000000000000000000000000000000000000000000000000000000001153656c662053746f7261676520436f696e000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000453544f5200000000000000000000000000000000000000000000000000000000\n";

    public static final String FUNC_SETTRANSFERAGENT = "setTransferAgent";

    public static final String FUNC_MINTINGFINISHED = "mintingFinished";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_SETRELEASEAGENT = "setReleaseAgent";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_GETRESERVEDPERCENTAGEUNIT = "getReservedPercentageUnit";

    public static final String FUNC_MINCAP = "minCap";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_MINTAGENTS = "mintAgents";

    public static final String FUNC_SETMINTAGENT = "setMintAgent";

    public static final String FUNC_UPGRADE = "upgrade";

    public static final String FUNC_GETRESERVEDPERCENTAGEDECIMALS = "getReservedPercentageDecimals";

    public static final String FUNC_SETTOKENINFORMATION = "setTokenInformation";

    public static final String FUNC_RESERVEDTOKENSLIST = "reservedTokensList";

    public static final String FUNC_UPGRADEAGENT = "upgradeAgent";

    public static final String FUNC_RELEASETOKENTRANSFER = "releaseTokenTransfer";

    public static final String FUNC_UPGRADEMASTER = "upgradeMaster";

    public static final String FUNC_GETRESERVEDTOKENS = "getReservedTokens";

    public static final String FUNC_FINALIZERESERVEDADDRESS = "finalizeReservedAddress";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_RESERVEDTOKENSDESTINATIONS = "reservedTokensDestinations";

    public static final String FUNC_GETUPGRADESTATE = "getUpgradeState";

    public static final String FUNC_TRANSFERAGENTS = "transferAgents";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_ISADDRESSRESERVED = "isAddressReserved";

    public static final String FUNC_RELEASED = "released";

    public static final String FUNC_CANUPGRADE = "canUpgrade";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_ARETOKENSDISTRIBUTEDFORADDRESS = "areTokensDistributedForAddress";

    public static final String FUNC_RESERVEDTOKENSDESTINATIONSLEN = "reservedTokensDestinationsLen";

    public static final String FUNC_TOTALUPGRADED = "totalUpgraded";

    public static final String FUNC_RELEASEAGENT = "releaseAgent";

    public static final String FUNC_SETUPGRADEAGENT = "setUpgradeAgent";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_CLAIMTOKENS = "claimTokens";

    public static final String FUNC_ISTOKEN = "isToken";

    public static final String FUNC_SETRESERVEDTOKENSLISTMULTIPLE = "setReservedTokensListMultiple";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_SETUPGRADEMASTER = "setUpgradeMaster";

    public static final Event UPDATEDTOKENINFORMATION_EVENT = new Event("UpdatedTokenInformation", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event CLAIMEDTOKENS_EVENT = new Event("ClaimedTokens", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
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

    protected STOR(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected STOR(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<BigInteger> getReservedPercentageUnit(String addr) {
        final Function function = new Function(FUNC_GETRESERVEDPERCENTAGEUNIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> minCap() {
        final Function function = new Function(FUNC_MINCAP, 
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

    public RemoteCall<BigInteger> getReservedPercentageDecimals(String addr) {
        final Function function = new Function(FUNC_GETRESERVEDPERCENTAGEDECIMALS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setTokenInformation(String _name, String _symbol) {
        final Function function = new Function(
                FUNC_SETTOKENINFORMATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple5<BigInteger, BigInteger, BigInteger, Boolean, Boolean>> reservedTokensList(String param0) {
        final Function function = new Function(FUNC_RESERVEDTOKENSLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}));
        return new RemoteCall<Tuple5<BigInteger, BigInteger, BigInteger, Boolean, Boolean>>(
                new Callable<Tuple5<BigInteger, BigInteger, BigInteger, Boolean, Boolean>>() {
                    @Override
                    public Tuple5<BigInteger, BigInteger, BigInteger, Boolean, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<BigInteger, BigInteger, BigInteger, Boolean, Boolean>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue(), 
                                (Boolean) results.get(4).getValue());
                    }
                });
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

    public RemoteCall<BigInteger> getReservedTokens(String addr) {
        final Function function = new Function(FUNC_GETRESERVEDTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> finalizeReservedAddress(String addr) {
        final Function function = new Function(
                FUNC_FINALIZERESERVEDADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> reservedTokensDestinations(BigInteger param0) {
        final Function function = new Function(FUNC_RESERVEDTOKENSDESTINATIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteCall<Boolean> isAddressReserved(String addr) {
        final Function function = new Function(FUNC_ISADDRESSRESERVED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteCall<Boolean> areTokensDistributedForAddress(String addr) {
        final Function function = new Function(FUNC_ARETOKENSDISTRIBUTEDFORADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> reservedTokensDestinationsLen() {
        final Function function = new Function(FUNC_RESERVEDTOKENSDESTINATIONSLEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteCall<TransactionReceipt> claimTokens(String _token) {
        final Function function = new Function(
                FUNC_CLAIMTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_token)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isToken() {
        final Function function = new Function(FUNC_ISTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> setReservedTokensListMultiple(List<String> addrs, List<BigInteger> inTokens, List<BigInteger> inPercentageUnit, List<BigInteger> inPercentageDecimals) {
        final Function function = new Function(
                FUNC_SETRESERVEDTOKENSLISTMULTIPLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(inTokens, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(inPercentageUnit, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(inPercentageDecimals, org.web3j.abi.datatypes.generated.Uint256.class))), 
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

    public RemoteCall<TransactionReceipt> setUpgradeMaster(String master) {
        final Function function = new Function(
                FUNC_SETUPGRADEMASTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(master)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<STOR> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable, BigInteger _globalMinCap) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable), 
                new org.web3j.abi.datatypes.generated.Uint256(_globalMinCap)));
        return deployRemoteCall(STOR.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<STOR> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _name, String _symbol, BigInteger _initialSupply, BigInteger _decimals, Boolean _mintable, BigInteger _globalMinCap) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(_initialSupply), 
                new org.web3j.abi.datatypes.generated.Uint256(_decimals), 
                new org.web3j.abi.datatypes.Bool(_mintable), 
                new org.web3j.abi.datatypes.generated.Uint256(_globalMinCap)));
        return deployRemoteCall(STOR.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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

    public static STOR load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new STOR(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static STOR load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new STOR(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class UpdatedTokenInformationEventResponse {
        public Log log;

        public String newName;

        public String newSymbol;
    }

    public static class ClaimedTokensEventResponse {
        public Log log;

        public String _token;

        public String _controller;

        public BigInteger _amount;
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

        public String previousOwner;

        public String newOwner;
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
