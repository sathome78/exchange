package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
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
public class CMIT extends Contract implements ethTokenNotERC20{
    private static final String BINARY = "60c0604052600b60808190527f434f4d494b4554434f494e00000000000000000000000000000000000000000060a0908152620000409160029190620003a4565b506040805180820190915260048082527f434d49540000000000000000000000000000000000000000000000000000000060209092019182526200008791600391620003a4565b506004805460ff19166008908117909155674563918244f4000060055560006006556007805474479865be7ac1034ba10190fcb9561649672e292200600160a860020a03199091161790558054600160a060020a031990811673ca5c301245f2781c2c4304426ab682d9744f7eb61790915560098054821673e4f63d6bb66185ac6c557df63c12eddcfc58c8e8179055600a80548216736b7fd1ee38d01ef65d9555ea879d12d9ef7fc64f179055600b805482167331808f7aa2701a0ba844d1ab5319f6097ea973fe179055600c8054909116737342b2df961b50c8fa60dee208d036579ecb5b901790553480156200017f57600080fd5b5060018054600754600160a060020a03199182163317909116610100909104600160a060020a0316179055600554620001e590606490620001d090600a640100000000620020e56200035282021704565b90640100000000620021106200038c82021704565b6007546101009004600160a060020a03166000908152600d60205260409020556005546200022b90606490620001d090600a640100000000620003528102620020e51704565b600854600160a060020a03166000908152600d60205260409020556005546200026c90606490620001d090601e640100000000620020e56200035282021704565b600954600160a060020a03166000908152600d6020526040902055600554620002ad90606490620001d0906019640100000000620020e56200035282021704565b600a8054600160a060020a03166000908152600d6020526040902091909155600554620002f091606491620001d091640100000000620020e56200035282021704565b600b54600160a060020a03166000908152600d60205260409020556005546200033190606490620001d090600f640100000000620020e56200035282021704565b600c54600160a060020a03166000908152600d602052604090205562000449565b60008083151562000367576000915062000385565b508282028284828115156200037857fe5b04146200038157fe5b8091505b5092915050565b60008082848115156200039b57fe5b04949350505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620003e757805160ff191683800117855562000417565b8280016001018555821562000417579182015b8281111562000417578251825591602001919060010190620003fa565b506200042592915062000429565b5090565b6200044691905b8082111562000425576000815560010162000430565b90565b61217380620004596000396000f30060806040526004361061017c5763ffffffff60e060020a60003504166305d2035b811461018657806306fdde03146101af578063095ea7b31461023957806318160ddd1461025d57806323b872dd14610284578063313ce567146102ae57806340c10f19146102d95780634f25eced146102fd57806364ddc605146103125780636a07089f146103a057806370a08231146103d15780637d64bcb4146103f2578063886c0ee6146104075780638da5cb5b1461041c57806395d89b41146104315780639dc29fac14610446578063a8f11eb91461017c578063a9059cbb1461046a578063b414d4b61461048e578063be45fd62146104af578063c258ff7414610518578063c2ecdf8e1461052d578063c341b9f614610542578063cbbe974b1461059b578063d39b1d48146105bc578063db886f23146105d4578063dd62ed3e146105e9578063e157819a14610610578063e3464b1b14610667578063f0dc41711461067c578063f2fde38b1461070a578063f6368f8a1461072b575b6101846107d2565b005b34801561019257600080fd5b5061019b610944565b604080519115158252519081900360200190f35b3480156101bb57600080fd5b506101c461094d565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101fe5781810151838201526020016101e6565b50505050905090810190601f16801561022b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561024557600080fd5b5061019b600160a060020a03600435166024356109e0565b34801561026957600080fd5b50610272610a46565b60408051918252519081900360200190f35b34801561029057600080fd5b5061019b600160a060020a0360043581169060243516604435610a4c565b3480156102ba57600080fd5b506102c3610c50565b6040805160ff9092168252519081900360200190f35b3480156102e557600080fd5b5061019b600160a060020a0360043516602435610c59565b34801561030957600080fd5b50610272610d59565b34801561031e57600080fd5b506040805160206004803580820135838102808601850190965280855261018495369593946024949385019291829185019084908082843750506040805187358901803560208181028481018201909552818452989b9a998901989297509082019550935083925085019084908082843750949750610d5f9650505050505050565b3480156103ac57600080fd5b506103b5610ec3565b60408051600160a060020a039092168252519081900360200190f35b3480156103dd57600080fd5b50610272600160a060020a0360043516610ed2565b3480156103fe57600080fd5b5061019b610eed565b34801561041357600080fd5b506103b5610f53565b34801561042857600080fd5b506103b5610f62565b34801561043d57600080fd5b506101c4610f71565b34801561045257600080fd5b50610184600160a060020a0360043516602435610fd2565b34801561047657600080fd5b5061019b600160a060020a03600435166024356110b7565b34801561049a57600080fd5b5061019b600160a060020a036004351661117a565b3480156104bb57600080fd5b50604080516020600460443581810135601f810184900484028501840190955284845261019b948235600160a060020a031694602480359536959460649492019190819084018382808284375094975061118f9650505050505050565b34801561052457600080fd5b506103b5611248565b34801561053957600080fd5b506103b5611257565b34801561054e57600080fd5b50604080516020600480358082013583810280860185019096528085526101849536959394602494938501929182918501908490808284375094975050505091351515925061126b915050565b3480156105a757600080fd5b50610272600160a060020a0360043516611375565b3480156105c857600080fd5b50610184600435611387565b3480156105e057600080fd5b506103b56113a3565b3480156105f557600080fd5b50610272600160a060020a03600435811690602435166113b2565b34801561061c57600080fd5b506040805160206004803580820135838102808601850190965280855261019b9536959394602494938501929182918501908490808284375094975050933594506113dd9350505050565b34801561067357600080fd5b506103b561164e565b34801561068857600080fd5b506040805160206004803580820135838102808601850190965280855261019b95369593946024949385019291829185019084908082843750506040805187358901803560208181028481018201909552818452989b9a99890198929750908201955093508392508501908490808284375094975061165d9650505050505050565b34801561071657600080fd5b50610184600160a060020a036004351661196a565b34801561073757600080fd5b50604080516020600460443581810135601f810184900484028501840190955284845261019b948235600160a060020a031694602480359536959460649492019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506119ff9650505050505050565b600060065411801561080557506006546007546101009004600160a060020a03166000908152600d602052604090205410155b80156108215750336000908152600f602052604090205460ff16155b801561083b57503360009081526010602052604090205442115b151561084657600080fd5b600034111561089157600754604051600160a060020a0361010090920491909116903480156108fc02916000818181858888f1935050505015801561088f573d6000803e3d6000fd5b505b6006546007546101009004600160a060020a03166000908152600d60205260409020546108bd91611d1d565b6007546101009004600160a060020a03166000908152600d602052604080822092909255600654338252919020546108f491611d2f565b336000818152600d602090815260409182902093909355600754600654825190815291519293610100909104600160a060020a0316926000805160206121288339815191529281900390910190a3565b60075460ff1681565b60028054604080516020601f60001961010060018716150201909416859004938401819004810282018101909252828152606093909290918301828280156109d65780601f106109ab576101008083540402835291602001916109d6565b820191906000526020600020905b8154815290600101906020018083116109b957829003601f168201915b5050505050905090565b336000818152600e60209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b60055490565b6000600160a060020a03831615801590610a665750600082115b8015610a8a5750600160a060020a0384166000908152600d60205260409020548211155b8015610ab95750600160a060020a0384166000908152600e602090815260408083203384529091529020548211155b8015610ade5750600160a060020a0384166000908152600f602052604090205460ff16155b8015610b035750600160a060020a0383166000908152600f602052604090205460ff16155b8015610b265750600160a060020a03841660009081526010602052604090205442115b8015610b495750600160a060020a03831660009081526010602052604090205442115b1515610b5457600080fd5b600160a060020a0384166000908152600d6020526040902054610b7d908363ffffffff611d1d16565b600160a060020a038086166000908152600d60205260408082209390935590851681522054610bb2908363ffffffff611d2f16565b600160a060020a038085166000908152600d60209081526040808320949094559187168152600e82528281203382529091522054610bf6908363ffffffff611d1d16565b600160a060020a038086166000818152600e602090815260408083203384528252918290209490945580518681529051928716939192600080516020612128833981519152929181900390910190a35060015b9392505050565b60045460ff1690565b600154600090600160a060020a03163314610c7357600080fd5b60075460ff1615610c8357600080fd5b60008211610c9057600080fd5b600554610ca3908363ffffffff611d2f16565b600555600160a060020a0383166000908152600d6020526040902054610ccf908363ffffffff611d2f16565b600160a060020a0384166000818152600d6020908152604091829020939093558051858152905191927f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d412139688592918290030190a2604080518381529051600160a060020a038516916000916000805160206121288339815191529181900360200190a350600192915050565b60065481565b600154600090600160a060020a03163314610d7957600080fd5b60008351118015610d8b575081518351145b1515610d9657600080fd5b5060005b8251811015610ebe578181815181101515610db157fe5b90602001906020020151601060008584815181101515610dcd57fe5b6020908102909101810151600160a060020a031682528101919091526040016000205410610dfa57600080fd5b8181815181101515610e0857fe5b90602001906020020151601060008584815181101515610e2457fe5b6020908102909101810151600160a060020a03168252810191909152604001600020558251839082908110610e5557fe5b90602001906020020151600160a060020a03167f1bd6fb9fa2c39ce5d0d2afa1eaba998963eb5f553fd862c94f131aa9e35c15778383815181101515610e9757fe5b906020019060200201516040518082815260200191505060405180910390a2600101610d9a565b505050565b600b54600160a060020a031681565b600160a060020a03166000908152600d602052604090205490565b600154600090600160a060020a03163314610f0757600080fd5b60075460ff1615610f1757600080fd5b6007805460ff191660011790556040517fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0890600090a150600190565b600a54600160a060020a031681565b600154600160a060020a031681565b60038054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156109d65780601f106109ab576101008083540402835291602001916109d6565b600154600160a060020a03163314610fe957600080fd5b6000811180156110115750600160a060020a0382166000908152600d60205260409020548111155b151561101c57600080fd5b600160a060020a0382166000908152600d6020526040902054611045908263ffffffff611d1d16565b600160a060020a0383166000908152600d6020526040902055600554611071908263ffffffff611d1d16565b600555604080518281529051600160a060020a038416917fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5919081900360200190a25050565b600060606000831180156110db5750336000908152600f602052604090205460ff16155b80156111005750600160a060020a0384166000908152600f602052604090205460ff16155b801561111a57503360009081526010602052604090205442115b801561113d5750600160a060020a03841660009081526010602052604090205442115b151561114857600080fd5b61115184611d3e565b1561116857611161848483611d46565b9150611173565b611161848483611f8a565b5092915050565b600f6020526000908152604090205460ff1681565b600080831180156111b05750336000908152600f602052604090205460ff16155b80156111d55750600160a060020a0384166000908152600f602052604090205460ff16155b80156111ef57503360009081526010602052604090205442115b80156112125750600160a060020a03841660009081526010602052604090205442115b151561121d57600080fd5b61122684611d3e565b1561123d57611236848484611d46565b9050610c49565b611236848484611f8a565b600954600160a060020a031681565b6007546101009004600160a060020a031681565b600154600090600160a060020a0316331461128557600080fd5b825160001061129357600080fd5b5060005b8251811015610ebe5782818151811015156112ae57fe5b60209081029091010151600160a060020a031615156112cc57600080fd5b81600f600085848151811015156112df57fe5b602090810291909101810151600160a060020a03168252810191909152604001600020805460ff1916911515919091179055825183908290811061131f57fe5b90602001906020020151600160a060020a03167f48335238b4855f35377ed80f164e8c6f3c366e54ac00b96a6402d4a9814a03a583604051808215151515815260200191505060405180910390a2600101611297565b60106020526000908152604090205481565b600154600160a060020a0316331461139e57600080fd5b600655565b600c54600160a060020a031681565b600160a060020a039182166000908152600e6020908152604080832093909416825291909152205490565b600080600080841180156113f2575060008551115b801561140e5750336000908152600f602052604090205460ff16155b801561142857503360009081526010602052604090205442115b151561143357600080fd5b611447846305f5e10063ffffffff6120e516565b935061145d8551856120e590919063ffffffff16565b336000908152600d602052604090205490925082111561147c57600080fd5b5060005b845181101561161357848181518110151561149757fe5b90602001906020020151600160a060020a03166000141580156114ef5750600f600086838151811015156114c757fe5b6020908102909101810151600160a060020a031682528101919091526040016000205460ff16155b8015611536575060106000868381518110151561150857fe5b90602001906020020151600160a060020a0316600160a060020a031681526020019081526020016000205442115b151561154157600080fd5b61158684600d6000888581518110151561155757fe5b6020908102909101810151600160a060020a03168252810191909152604001600020549063ffffffff611d2f16565b600d6000878481518110151561159857fe5b6020908102909101810151600160a060020a031682528101919091526040016000205584518590829081106115c957fe5b90602001906020020151600160a060020a031633600160a060020a0316600080516020612128833981519152866040518082815260200191505060405180910390a3600101611480565b336000908152600d6020526040902054611633908363ffffffff611d1d16565b336000908152600d6020526040902055506001949350505050565b600854600160a060020a031681565b60015460009081908190600160a060020a0316331461167b57600080fd5b6000855111801561168d575083518551145b151561169857600080fd5b5060009050805b845181101561194a57600084828151811015156116b857fe5b906020019060200201511180156116f0575084818151811015156116d857fe5b90602001906020020151600160a060020a0316600014155b80156117315750600f6000868381518110151561170957fe5b6020908102909101810151600160a060020a031682528101919091526040016000205460ff16155b8015611778575060106000868381518110151561174a57fe5b90602001906020020151600160a060020a0316600160a060020a031681526020019081526020016000205442115b151561178357600080fd5b6117af6305f5e100858381518110151561179957fe5b602090810290910101519063ffffffff6120e516565b84828151811015156117bd57fe5b6020908102909101015283518490829081106117d557fe5b90602001906020020151600d600087848151811015156117f157fe5b6020908102909101810151600160a060020a0316825281019190915260400160002054101561181f57600080fd5b61187b848281518110151561183057fe5b90602001906020020151600d6000888581518110151561184c57fe5b6020908102909101810151600160a060020a03168252810191909152604001600020549063ffffffff611d1d16565b600d6000878481518110151561188d57fe5b6020908102909101810151600160a060020a031682528101919091526040016000205583516118d9908590839081106118c257fe5b60209081029091010151839063ffffffff611d2f16565b915033600160a060020a031685828151811015156118f357fe5b90602001906020020151600160a060020a0316600080516020612128833981519152868481518110151561192357fe5b906020019060200201516040518082815260200191505060405180910390a360010161169f565b336000908152600d6020526040902054611633908363ffffffff611d2f16565b600154600160a060020a0316331461198157600080fd5b600160a060020a038116151561199657600080fd5b600154604051600160a060020a038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a36001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60008084118015611a205750336000908152600f602052604090205460ff16155b8015611a455750600160a060020a0385166000908152600f602052604090205460ff16155b8015611a5f57503360009081526010602052604090205442115b8015611a825750600160a060020a03851660009081526010602052604090205442115b1515611a8d57600080fd5b611a9685611d3e565b15611d0757336000908152600d6020526040902054841115611ab757600080fd5b336000908152600d6020526040902054611ad7908563ffffffff611d1d16565b336000908152600d602052604080822092909255600160a060020a03871681522054611b09908563ffffffff611d2f16565b600160a060020a0386166000818152600d6020908152604080832094909455925185519293919286928291908401908083835b60208310611b5b5780518252601f199092019160209182019101611b3c565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060e060020a9004903387876040518563ffffffff1660e060020a0281526004018084600160a060020a0316600160a060020a03168152602001838152602001828051906020019080838360005b83811015611bed578181015183820152602001611bd5565b50505050905090810190601f168015611c1a5780820380516001836020036101000a031916815260200191505b50935050505060006040518083038185885af193505050501515611c3a57fe5b826040518082805190602001908083835b60208310611c6a5780518252601f199092019160209182019101611c4b565b51815160209384036101000a6000190180199092169116179052604080519290940182900382208a83529351939550600160a060020a038b16945033937fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c169350918290030190a4604080518581529051600160a060020a0387169133916000805160206121288339815191529181900360200190a3506001611d15565b611d12858585611f8a565b90505b949350505050565b600082821115611d2957fe5b50900390565b600082820183811015610c4957fe5b6000903b1190565b336000908152600d60205260408120548190841115611d6457600080fd5b336000908152600d6020526040902054611d84908563ffffffff611d1d16565b336000908152600d602052604080822092909255600160a060020a03871681522054611db6908563ffffffff611d2f16565b600160a060020a0386166000818152600d602090815260408083209490945592517fc0ee0b8a0000000000000000000000000000000000000000000000000000000081523360048201818152602483018a90526060604484019081528951606485015289518c9850959663c0ee0b8a9693958c958c956084909101928601918190849084905b83811015611e54578181015183820152602001611e3c565b50505050905090810190601f168015611e815780820380516001836020036101000a031916815260200191505b50945050505050600060405180830381600087803b158015611ea257600080fd5b505af1158015611eb6573d6000803e3d6000fd5b50505050826040518082805190602001908083835b60208310611eea5780518252601f199092019160209182019101611ecb565b51815160209384036101000a6000190180199092169116179052604080519290940182900382208a83529351939550600160a060020a038b16945033937fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c169350918290030190a4604080518581529051600160a060020a0387169133916000805160206121288339815191529181900360200190a3506001949350505050565b336000908152600d6020526040812054831115611fa657600080fd5b336000908152600d6020526040902054611fc6908463ffffffff611d1d16565b336000908152600d602052604080822092909255600160a060020a03861681522054611ff8908463ffffffff611d2f16565b600160a060020a0385166000908152600d60209081526040918290209290925551835184928291908401908083835b602083106120465780518252601f199092019160209182019101612027565b51815160209384036101000a6000190180199092169116179052604080519290940182900382208983529351939550600160a060020a038a16945033937fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c169350918290030190a4604080518481529051600160a060020a0386169133916000805160206121288339815191529181900360200190a35060019392505050565b6000808315156120f85760009150611173565b5082820282848281151561210857fe5b0414610c4957fe5b600080828481151561211e57fe5b049493505050505600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a72305820a75d00f0bf039e6c7d87ca1698c014b25f486953898cf16703ad211d07471b850029\n";

    public static final String FUNC_MINTINGFINISHED = "mintingFinished";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_DISTRIBUTEAMOUNT = "distributeAmount";

    public static final String FUNC_LOCKUPACCOUNTS = "lockupAccounts";

    public static final String FUNC_CONTENTS = "Contents";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_FINISHMINTING = "finishMinting";

    public static final String FUNC_DEVELOP = "Develop";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_AUTODISTRIBUTE = "autoDistribute";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_FROZENACCOUNT = "frozenAccount";

    public static final String FUNC_LIST = "List";

    public static final String FUNC_OWNERCMIT = "ownerCMIT";

    public static final String FUNC_FREEZEACCOUNTS = "freezeAccounts";

    public static final String FUNC_UNLOCKUNIXTIME = "unlockUnixTime";

    public static final String FUNC_SETDISTRIBUTEAMOUNT = "setDistributeAmount";

    public static final String FUNC_MARKETING = "Marketing";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_DISTRIBUTECOMIKETCOIN = "distributeCOMIKETCOIN";

    public static final String FUNC_DROP = "Drop";

    public static final String FUNC_COLLECTTOKENS = "collectTokens";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event FROZENFUNDS_EVENT = new Event("FrozenFunds", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event LOCKEDFUNDS_EVENT = new Event("LockedFunds", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event BURN_EVENT = new Event("Burn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event MINT_EVENT = new Event("Mint", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event MINTFINISHED_EVENT = new Event("MintFinished", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    /*
    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;
    */

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected CMIT(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CMIT(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<TransactionReceipt> mint(String _to, BigInteger _unitAmount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_unitAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> distributeAmount() {
        final Function function = new Function(FUNC_DISTRIBUTEAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> lockupAccounts(List<String> targets, List<BigInteger> unixTimes) {
        final Function function = new Function(
                FUNC_LOCKUPACCOUNTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(targets, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(unixTimes, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> Contents() {
        final Function function = new Function(FUNC_CONTENTS, 
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

    public RemoteCall<TransactionReceipt> finishMinting() {
        final Function function = new Function(
                FUNC_FINISHMINTING, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> Develop() {
        final Function function = new Function(FUNC_DEVELOP, 
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

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> burn(String _from, BigInteger _unitAmount) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.generated.Uint256(_unitAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> autoDistribute(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_AUTODISTRIBUTE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> frozenAccount(String param0) {
        final Function function = new Function(FUNC_FROZENACCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value, byte[] _data) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value), 
                new org.web3j.abi.datatypes.DynamicBytes(_data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> List() {
        final Function function = new Function(FUNC_LIST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> ownerCMIT() {
        final Function function = new Function(FUNC_OWNERCMIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> freezeAccounts(List<String> targets, Boolean isFrozen) {
        final Function function = new Function(
                FUNC_FREEZEACCOUNTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(targets, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.Bool(isFrozen)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> unlockUnixTime(String param0) {
        final Function function = new Function(FUNC_UNLOCKUNIXTIME, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setDistributeAmount(BigInteger _unitAmount) {
        final Function function = new Function(
                FUNC_SETDISTRIBUTEAMOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_unitAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> Marketing() {
        final Function function = new Function(FUNC_MARKETING, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> distributeCOMIKETCOIN(List<String> addresses, BigInteger amount) {
        final Function function = new Function(
                FUNC_DISTRIBUTECOMIKETCOIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(addresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> Drop() {
        final Function function = new Function(FUNC_DROP, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> collectTokens(List<String> addresses, List<BigInteger> amounts) {
        final Function function = new Function(
                FUNC_COLLECTTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(addresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(amounts, org.web3j.abi.datatypes.generated.Uint256.class))), 
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

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value, byte[] _data, String _custom_fallback) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value), 
                new org.web3j.abi.datatypes.DynamicBytes(_data), 
                new org.web3j.abi.datatypes.Utf8String(_custom_fallback)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<CMIT> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CMIT.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<CMIT> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CMIT.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<FrozenFundsEventResponse> getFrozenFundsEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FROZENFUNDS_EVENT, transactionReceipt);
        ArrayList<FrozenFundsEventResponse> responses = new ArrayList<FrozenFundsEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FrozenFundsEventResponse typedResponse = new FrozenFundsEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.frozen = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<FrozenFundsEventResponse> frozenFundsEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, FrozenFundsEventResponse>() {
            @Override
            public FrozenFundsEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FROZENFUNDS_EVENT, log);
                FrozenFundsEventResponse typedResponse = new FrozenFundsEventResponse();
                typedResponse.log = log;
                typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.frozen = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<FrozenFundsEventResponse> frozenFundsEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FROZENFUNDS_EVENT));
        return frozenFundsEventObservable(filter);
    }

    public List<LockedFundsEventResponse> getLockedFundsEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOCKEDFUNDS_EVENT, transactionReceipt);
        ArrayList<LockedFundsEventResponse> responses = new ArrayList<LockedFundsEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LockedFundsEventResponse typedResponse = new LockedFundsEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.locked = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LockedFundsEventResponse> lockedFundsEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LockedFundsEventResponse>() {
            @Override
            public LockedFundsEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOCKEDFUNDS_EVENT, log);
                LockedFundsEventResponse typedResponse = new LockedFundsEventResponse();
                typedResponse.log = log;
                typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.locked = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LockedFundsEventResponse> lockedFundsEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOCKEDFUNDS_EVENT));
        return lockedFundsEventObservable(filter);
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
        return burnEventObservable(filter);
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

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.data = (byte[]) eventValues.getIndexedValues().get(2).getValue();
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
                typedResponse.data = (byte[]) eventValues.getIndexedValues().get(2).getValue();
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

    /*
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
    */

    /*
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
    */

    /*
    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }
    */

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

    public static CMIT load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CMIT(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static CMIT load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CMIT(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class FrozenFundsEventResponse {
        public Log log;

        public String target;

        public Boolean frozen;
    }

    public static class LockedFundsEventResponse {
        public Log log;

        public String target;

        public BigInteger locked;
    }

    public static class BurnEventResponse {
        public Log log;

        public String from;

        public BigInteger amount;
    }

    public static class MintEventResponse {
        public Log log;

        public String to;

        public BigInteger amount;
    }

    public static class MintFinishedEventResponse {
        public Log log;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String previousOwner;

        public String newOwner;
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public byte[] data;

        public BigInteger value;
    }

    /*
    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }
    */

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
