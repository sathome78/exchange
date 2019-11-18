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
public class MEXC extends Contract implements ethTokenNotERC20 {
    private static final String BINARY = "60c0604052600a60808190527f4d45584320546f6b656e0000000000000000000000000000000000000000000060a0908152620000409160069190620002a9565b506040805180820190915260048082527f4d4558430000000000000000000000000000000000000000000000000000000060209092019182526200008791600791620002a9565b506008805460ff199081166012179091556b058a061ec4dc1559a7080000600955600a80549091166001179055348015620000c157600080fd5b50620000d6336001600160e01b036200012d16565b6001600455600580546001600160a01b0319163317908190556040516001600160a01b0391909116906000907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a36200034e565b620001488160036200017f60201b62001f031790919060201c565b6040516001600160a01b038216907f6ae172837ea30b801fbfcdd4108aa1d5bf8ff775444fd70256b44e6bf3dfc3f690600090a250565b6200019482826001600160e01b036200022616565b156200020157604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152601f60248201527f526f6c65733a206163636f756e7420616c72656164792068617320726f6c6500604482015290519081900360640190fd5b6001600160a01b0316600090815260209190915260409020805460ff19166001179055565b60006001600160a01b03821662000289576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260228152602001806200261c6022913960400191505060405180910390fd5b506001600160a01b03166000908152602091909152604090205460ff1690565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620002ec57805160ff19168380011785556200031c565b828001600101855582156200031c579182015b828111156200031c578251825591602001919060010190620002ff565b506200032a9291506200032e565b5090565b6200034b91905b808211156200032a576000815560010162000335565b90565b6122be806200035e6000396000f3fe608060405234801561001057600080fd5b50600436106101e55760003560e01c80638da5cb5b1161010f578063aa271e1a116100a2578063cd8c063b11610071578063cd8c063b146106eb578063d5abeb01146106f3578063dd62ed3e146106fb578063f2fde38b14610729576101e5565b8063aa271e1a14610656578063b7eb5e0a1461067c578063b921e163146106a2578063c4be6bb4146106bf576101e5565b806398650275116100de57806398650275146105ca5780639dc29fac146105d2578063a457c2d7146105fe578063a9059cbb1461062a576101e5565b80638da5cb5b146105705780638f32d59b1461059457806395d89b411461059c578063983b2d56146105a4576101e5565b8063313ce5671161018757806341cc89121161015657806341cc8912146103cb5780634a4fbeec146104f857806370a082311461051e57806379cc679014610544576101e5565b8063313ce5671461032f57806334a90d021461034d578063395093511461037357806340c10f191461039f576101e5565b8063212c8157116101c3578063212c8157146102c15780632185810b146102c957806323b872dd146102d15780633092afd514610307576101e5565b806306fdde03146101ea578063095ea7b31461026757806318160ddd146102a7575b600080fd5b6101f261074f565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561022c578181015183820152602001610214565b50505050905090810190601f1680156102595780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6102936004803603604081101561027d57600080fd5b506001600160a01b0381351690602001356107dd565b604080519115158252519081900360200190f35b6102af61096e565b60408051918252519081900360200190f35b610293610975565b6102936109ce565b610293600480360360608110156102e757600080fd5b506001600160a01b03813581169160208101359091169060400135610a2b565b61032d6004803603602081101561031d57600080fd5b50356001600160a01b0316610bbf565b005b610337610c0f565b6040805160ff9092168252519081900360200190f35b61032d6004803603602081101561036357600080fd5b50356001600160a01b0316610c18565b6102936004803603604081101561038957600080fd5b506001600160a01b038135169060200135610cba565b610293600480360360408110156103b557600080fd5b506001600160a01b038135169060200135610dab565b61032d600480360360408110156103e157600080fd5b8101906020810181356401000000008111156103fc57600080fd5b82018360208201111561040e57600080fd5b8035906020019184600183028401116401000000008311171561043057600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929594936020810193503591505064010000000081111561048357600080fd5b82018360208201111561049557600080fd5b803590602001918460018302840111640100000000831117156104b757600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610ea5945050505050565b6102936004803603602081101561050e57600080fd5b50356001600160a01b0316611018565b6102af6004803603602081101561053457600080fd5b50356001600160a01b0316611036565b61032d6004803603604081101561055a57600080fd5b506001600160a01b038135169060200135611051565b61057861105f565b604080516001600160a01b039092168252519081900360200190f35b61029361106e565b6101f261107f565b61032d600480360360208110156105ba57600080fd5b50356001600160a01b03166110da565b61032d611127565b61032d600480360360408110156105e857600080fd5b506001600160a01b038135169060200135611132565b6102936004803603604081101561061457600080fd5b506001600160a01b03813516906020013561125f565b6102936004803603604081101561064057600080fd5b506001600160a01b038135169060200135611350565b6102936004803603602081101561066c57600080fd5b50356001600160a01b0316611441565b61032d6004803603602081101561069257600080fd5b50356001600160a01b0316611454565b610293600480360360208110156106b857600080fd5b50356114f3565b610293600480360360408110156106d557600080fd5b506001600160a01b03813516906020013561158e565b610293611684565b6102af61168d565b6102af6004803603604081101561071157600080fd5b506001600160a01b0381358116916020013516611693565b61032d6004803603602081101561073f57600080fd5b50356001600160a01b03166116be565b6006805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107d55780601f106107aa576101008083540402835291602001916107d5565b820191906000526020600020905b8154815290600101906020018083116107b857829003601f168201915b505050505081565b60006107e761105f565b6001600160a01b0316336001600160a01b03161415610864576004805460010190819055610815848461171f565b5060019150600454811461085e576040805162461bcd60e51b815260206004820152601f60248201526000805160206120e1833981519152604482015290519081900360640190fd5b50610968565b600a5460ff166108a9576040805162461bcd60e51b815260206004820152601d6024820152600080516020612101833981519152604482015290519081900360640190fd5b336000908152600b602052604090205460ff1615610908576040805162461bcd60e51b815260206004820152601760248201527613515610ce881858d8dbdd5b9d081a5cc81b1bd8dad959604a1b604482015290519081900360640190fd5b600480546001019081905561091d848461171f565b50600191506004548114610966576040805162461bcd60e51b815260206004820152601f60248201526000805160206120e1833981519152604482015290519081900360640190fd5b505b92915050565b6002545b90565b600061097f61106e565b6109be576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b50600a805460ff19169055600190565b60006109d861106e565b610a17576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b50600a805460ff1916600190811790915590565b6000610a3561105f565b6001600160a01b0316336001600160a01b03161415610ab3576004805460010190819055610a6485858561172c565b50600191506004548114610aad576040805162461bcd60e51b815260206004820152601f60248201526000805160206120e1833981519152604482015290519081900360640190fd5b50610bb8565b600a5460ff16610af8576040805162461bcd60e51b815260206004820152601d6024820152600080516020612101833981519152604482015290519081900360640190fd5b336000908152600b602052604090205460ff1615610b57576040805162461bcd60e51b815260206004820152601760248201527613515610ce881858d8dbdd5b9d081a5cc81b1bd8dad959604a1b604482015290519081900360640190fd5b6004805460010190819055610b6d85858561172c565b50600191506004548114610bb6576040805162461bcd60e51b815260206004820152601f60248201526000805160206120e1833981519152604482015290519081900360640190fd5b505b9392505050565b610bc833611441565b610c035760405162461bcd60e51b81526004018080602001828103825260308152602001806121696030913960400191505060405180910390fd5b610c0c81611783565b50565b60085460ff1681565b610c2061106e565b610c5f576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b6001600160a01b0381166000818152600b6020908152604091829020805460ff19166001179055815192835290517f8a7c4419c95cb4931471e12f2ad5fb760abfc1389479f0e9a588c2dc98f2dceb9281900390910190a150565b6000610cc461105f565b6001600160a01b0316336001600160a01b03161415610cf257600480546001019081905561081584846117cb565b600a5460ff16610d37576040805162461bcd60e51b815260206004820152601d6024820152600080516020612101833981519152604482015290519081900360640190fd5b336000908152600b602052604090205460ff1615610d96576040805162461bcd60e51b815260206004820152601760248201527613515610ce881858d8dbdd5b9d081a5cc81b1bd8dad959604a1b604482015290519081900360640190fd5b600480546001019081905561091d84846117cb565b6000610db633611441565b610df15760405162461bcd60e51b81526004018080602001828103825260308152602001806121696030913960400191505060405180910390fd5b610df961106e565b610e38576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b6004805460010190819055600954610e5e84610e5261096e565b9063ffffffff61180716565b1115610e9b5760405162461bcd60e51b81526004018080602001828103825260248152602001806122666024913960400191505060405180910390fd5b61091d8484611861565b610ead61106e565b610eec576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b8151610eff906007906020850190612025565b508051610f13906006906020840190612025565b507fa5c92e86140feb7eb0bbbc3f7d785a90e1d168989b528ff22962e995f7ef21df8282604051808060200180602001838103835285818151815260200191508051906020019080838360005b83811015610f78578181015183820152602001610f60565b50505050905090810190601f168015610fa55780820380516001836020036101000a031916815260200191505b50838103825284518152845160209182019186019080838360005b83811015610fd8578181015183820152602001610fc0565b50505050905090810190601f1680156110055780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15050565b6001600160a01b03166000908152600b602052604090205460ff1690565b6001600160a01b031660009081526020819052604090205490565b61105b82826118b1565b5050565b6005546001600160a01b031690565b6005546001600160a01b0316331490565b6007805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107d55780601f106107aa576101008083540402835291602001916107d5565b6110e333611441565b61111e5760405162461bcd60e51b81526004018080602001828103825260308152602001806121696030913960400191505060405180910390fd5b610c0c816118f6565b61113033611783565b565b61113b33611441565b6111765760405162461bcd60e51b81526004018080602001828103825260308152602001806121696030913960400191505060405180910390fd5b61117e61106e565b6111bd576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b60048054600101908190556111d2838361193e565b604080516001600160a01b03851681526020810184905281517f7c21cf5d524274e2d794116265284572dceb98c78e6f691aa7072e1925a29e03929181900390910190a1600454811461125a576040805162461bcd60e51b815260206004820152601f60248201526000805160206120e1833981519152604482015290519081900360640190fd5b505050565b600061126961105f565b6001600160a01b0316336001600160a01b031614156112975760048054600101908190556108158484611948565b600a5460ff166112dc576040805162461bcd60e51b815260206004820152601d6024820152600080516020612101833981519152604482015290519081900360640190fd5b336000908152600b602052604090205460ff161561133b576040805162461bcd60e51b815260206004820152601760248201527613515610ce881858d8dbdd5b9d081a5cc81b1bd8dad959604a1b604482015290519081900360640190fd5b600480546001019081905561091d8484611948565b600061135a61105f565b6001600160a01b0316336001600160a01b031614156113885760048054600101908190556108158484611984565b600a5460ff166113cd576040805162461bcd60e51b815260206004820152601d6024820152600080516020612101833981519152604482015290519081900360640190fd5b336000908152600b602052604090205460ff161561142c576040805162461bcd60e51b815260206004820152601760248201527613515610ce881858d8dbdd5b9d081a5cc81b1bd8dad959604a1b604482015290519081900360640190fd5b600480546001019081905561091d8484611984565b600061096860038363ffffffff61199116565b61145c61106e565b61149b576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b6001600160a01b0381166000818152600b6020908152604091829020805460ff19169055815192835290517fe097c69c634880f1c9ee76294a0c05b4b9b14becebfa150ded7979498f732e809281900390910190a150565b60006114fd61106e565b61153c576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b60095461154f908363ffffffff61180716565b600981905560408051918252517f6af448a356e1211a28bdca85758a1e5df103f84609147923fef12d451bc61ab89181900360200190a1506001919050565b600061159933611441565b6115d45760405162461bcd60e51b81526004018080602001828103825260308152602001806121696030913960400191505060405180910390fd5b6115dc61106e565b61161b576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b60095461162a83610e5261096e565b11156116675760405162461bcd60e51b81526004018080602001828103825260248152602001806122666024913960400191505060405180910390fd5b6116718383610dab565b5061167b83610c18565b50600192915050565b600a5460ff1690565b60095481565b6001600160a01b03918216600090815260016020908152604080832093909416825291909152205490565b6116c661106e565b611705576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b61170e816119f8565b611717816110da565b610c0c611127565b600061167b338484611a48565b6000611739848484611b34565b6001600160a01b038416600090815260016020908152604080832033808552925290912054611779918691611774908663ffffffff611c7616565b611a48565b5060019392505050565b61179460038263ffffffff611cd316565b6040516001600160a01b038216907fe94479a9f7e1952cc78f2d6baab678adc1b772d936c6583def489e524cb6669290600090a250565b3360008181526001602090815260408083206001600160a01b0387168452909152812054909161167b918590611774908663ffffffff61180716565b600082820183811015610bb8576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b600061186c33611441565b6118a75760405162461bcd60e51b81526004018080602001828103825260308152602001806121696030913960400191505060405180910390fd5b61167b8383611d3a565b6118bb8282611e2a565b6001600160a01b03821660009081526001602090815260408083203380855292529091205461105b918491611774908563ffffffff611c7616565b61190760038263ffffffff611f0316565b6040516001600160a01b038216907f6ae172837ea30b801fbfcdd4108aa1d5bf8ff775444fd70256b44e6bf3dfc3f690600090a250565b61105b8282611e2a565b3360008181526001602090815260408083206001600160a01b0387168452909152812054909161167b918590611774908663ffffffff611c7616565b600061167b338484611b34565b60006001600160a01b0382166119d85760405162461bcd60e51b81526004018080602001828103825260228152602001806121da6022913960400191505060405180910390fd5b506001600160a01b03166000908152602091909152604090205460ff1690565b611a0061106e565b611a3f576040805162461bcd60e51b815260206004820181905260248201526000805160206121ba833981519152604482015290519081900360640190fd5b610c0c81611f84565b6001600160a01b038316611a8d5760405162461bcd60e51b81526004018080602001828103825260248152602001806122426024913960400191505060405180910390fd5b6001600160a01b038216611ad25760405162461bcd60e51b81526004018080602001828103825260228152602001806121476022913960400191505060405180910390fd5b6001600160a01b03808416600081815260016020908152604080832094871680845294825291829020859055815185815291517f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259281900390910190a3505050565b6001600160a01b038316611b795760405162461bcd60e51b815260040180806020018281038252602581526020018061221d6025913960400191505060405180910390fd5b6001600160a01b038216611bbe5760405162461bcd60e51b81526004018080602001828103825260238152602001806120be6023913960400191505060405180910390fd5b6001600160a01b038316600090815260208190526040902054611be7908263ffffffff611c7616565b6001600160a01b038085166000908152602081905260408082209390935590841681522054611c1c908263ffffffff61180716565b6001600160a01b038084166000818152602081815260409182902094909455805185815290519193928716927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a3505050565b600082821115611ccd576040805162461bcd60e51b815260206004820152601e60248201527f536166654d6174683a207375627472616374696f6e206f766572666c6f770000604482015290519081900360640190fd5b50900390565b611cdd8282611991565b611d185760405162461bcd60e51b81526004018080602001828103825260218152602001806121996021913960400191505060405180910390fd5b6001600160a01b0316600090815260209190915260409020805460ff19169055565b6001600160a01b038216611d95576040805162461bcd60e51b815260206004820152601f60248201527f45524332303a206d696e7420746f20746865207a65726f206164647265737300604482015290519081900360640190fd5b600254611da8908263ffffffff61180716565b6002556001600160a01b038216600090815260208190526040902054611dd4908263ffffffff61180716565b6001600160a01b0383166000818152602081815260408083209490945583518581529351929391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a35050565b6001600160a01b038216611e6f5760405162461bcd60e51b81526004018080602001828103825260218152602001806121fc6021913960400191505060405180910390fd5b600254611e82908263ffffffff611c7616565b6002556001600160a01b038216600090815260208190526040902054611eae908263ffffffff611c7616565b6001600160a01b038316600081815260208181526040808320949094558351858152935191937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a35050565b611f0d8282611991565b15611f5f576040805162461bcd60e51b815260206004820152601f60248201527f526f6c65733a206163636f756e7420616c72656164792068617320726f6c6500604482015290519081900360640190fd5b6001600160a01b0316600090815260209190915260409020805460ff19166001179055565b6001600160a01b038116611fc95760405162461bcd60e51b81526004018080602001828103825260268152602001806121216026913960400191505060405180910390fd5b6005546040516001600160a01b038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a3600580546001600160a01b0319166001600160a01b0392909216919091179055565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061206657805160ff1916838001178555612093565b82800160010185558215612093579182015b82811115612093578251825591602001919060010190612078565b5061209f9291506120a3565b5090565b61097291905b8082111561209f57600081556001016120a956fe45524332303a207472616e7366657220746f20746865207a65726f20616464726573735265656e7472616e637947756172643a207265656e7472616e742063616c6c004d4558433a207472616e73666572206973206e6f7420616c6c6f7765640000004f776e61626c653a206e6577206f776e657220697320746865207a65726f206164647265737345524332303a20617070726f766520746f20746865207a65726f20616464726573734d696e746572526f6c653a2063616c6c657220646f6573206e6f74206861766520746865204d696e74657220726f6c65526f6c65733a206163636f756e7420646f6573206e6f74206861766520726f6c654f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572526f6c65733a206163636f756e7420697320746865207a65726f206164647265737345524332303a206275726e2066726f6d20746865207a65726f206164647265737345524332303a207472616e736665722066726f6d20746865207a65726f206164647265737345524332303a20617070726f76652066726f6d20746865207a65726f20616464726573734d4558433a20657863656564696e6720746865206d6178537570706c7920616d6f756e74a265627a7a72315820623f92e6cd9ddfb2d3de67447edb79950d19ffa3fa70292118d19f1bbc1c460064736f6c634300050b0032526f6c65733a206163636f756e7420697320746865207a65726f2061646472657373\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_DISALLOWTRANSFERS = "disallowTransfers";

    public static final String FUNC_ALLOWTRANSFERS = "allowTransfers";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_REMOVEMINTER = "removeMinter";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_LOCKADDRESS = "lockAddress";

    public static final String FUNC_INCREASEALLOWANCE = "increaseAllowance";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_RENAMETOKEN = "renameToken";

    public static final String FUNC_ISLOCKED = "isLocked";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_ISOWNER = "isOwner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_ADDMINTER = "addMinter";

    public static final String FUNC_RENOUNCEMINTER = "renounceMinter";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_DECREASEALLOWANCE = "decreaseAllowance";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_ISMINTER = "isMinter";

    public static final String FUNC_UNLOCKADDRESS = "unlockAddress";

    public static final String FUNC_INCREASESUPPLY = "increaseSupply";

    public static final String FUNC_MINTTHENLOCK = "mintThenLock";

    public static final String FUNC_ISTRANSFERALLOWED = "isTransferAllowed";

    public static final String FUNC_MAXSUPPLY = "maxSupply";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event BURNTOKENEVENT_EVENT = new Event("burnTokenEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOCKADDRESSEVENT_EVENT = new Event("lockAddressEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event UNLOCKADDRESSEVENT_EVENT = new Event("unlockAddressEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event TOKENISRENAMED_EVENT = new Event("tokenIsRenamed", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event SUPPLYHASINCREASED_EVENT = new Event("supplyHasIncreased", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event MINTERADDED_EVENT = new Event("MinterAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event MINTERREMOVED_EVENT = new Event("MinterRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected MEXC(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MEXC(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String spender, BigInteger value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> disallowTransfers() {
        final Function function = new Function(
                FUNC_DISALLOWTRANSFERS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> allowTransfers() {
        final Function function = new Function(
                FUNC_ALLOWTRANSFERS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String sender, String recipient, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(sender), 
                new org.web3j.abi.datatypes.Address(recipient), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> removeMinter(String account) {
        final Function function = new Function(
                FUNC_REMOVEMINTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> lockAddress(String _addr) {
        final Function function = new Function(
                FUNC_LOCKADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> increaseAllowance(String spender, BigInteger addedValue) {
        final Function function = new Function(
                FUNC_INCREASEALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), 
                new org.web3j.abi.datatypes.generated.Uint256(addedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mint(String account, BigInteger amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> renameToken(String _symbol, String _name) {
        final Function function = new Function(
                FUNC_RENAMETOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_symbol), 
                new org.web3j.abi.datatypes.Utf8String(_name)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isLocked(String _addr) {
        final Function function = new Function(FUNC_ISLOCKED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> balanceOf(String account) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> burnFrom(String account, BigInteger amount) {
        final Function function = new Function(
                FUNC_BURNFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> isOwner() {
        final Function function = new Function(FUNC_ISOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> addMinter(String account) {
        final Function function = new Function(
                FUNC_ADDMINTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> renounceMinter() {
        final Function function = new Function(
                FUNC_RENOUNCEMINTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burn(String account, BigInteger value) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> decreaseAllowance(String spender, BigInteger subtractedValue) {
        final Function function = new Function(
                FUNC_DECREASEALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), 
                new org.web3j.abi.datatypes.generated.Uint256(subtractedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String recipient, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(recipient), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isMinter(String account) {
        final Function function = new Function(FUNC_ISMINTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> unlockAddress(String _addr) {
        final Function function = new Function(
                FUNC_UNLOCKADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> increaseSupply(BigInteger supply) {
        final Function function = new Function(
                FUNC_INCREASESUPPLY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(supply)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mintThenLock(String account, BigInteger amount) {
        final Function function = new Function(
                FUNC_MINTTHENLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isTransferAllowed() {
        final Function function = new Function(FUNC_ISTRANSFERALLOWED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> maxSupply() {
        final Function function = new Function(FUNC_MAXSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> allowance(String owner, String spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.Address(spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<MEXC> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MEXC.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<MEXC> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MEXC.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<BurnTokenEventEventResponse> getBurnTokenEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURNTOKENEVENT_EVENT, transactionReceipt);
        ArrayList<BurnTokenEventEventResponse> responses = new ArrayList<BurnTokenEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnTokenEventEventResponse typedResponse = new BurnTokenEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnTokenEventEventResponse> burnTokenEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnTokenEventEventResponse>() {
            @Override
            public BurnTokenEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURNTOKENEVENT_EVENT, log);
                BurnTokenEventEventResponse typedResponse = new BurnTokenEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<BurnTokenEventEventResponse> burnTokenEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURNTOKENEVENT_EVENT));
        return burnTokenEventEventObservable(filter);
    }

    public List<LockAddressEventEventResponse> getLockAddressEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOCKADDRESSEVENT_EVENT, transactionReceipt);
        ArrayList<LockAddressEventEventResponse> responses = new ArrayList<LockAddressEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LockAddressEventEventResponse typedResponse = new LockAddressEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LockAddressEventEventResponse> lockAddressEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LockAddressEventEventResponse>() {
            @Override
            public LockAddressEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOCKADDRESSEVENT_EVENT, log);
                LockAddressEventEventResponse typedResponse = new LockAddressEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LockAddressEventEventResponse> lockAddressEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOCKADDRESSEVENT_EVENT));
        return lockAddressEventEventObservable(filter);
    }

    public List<UnlockAddressEventEventResponse> getUnlockAddressEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UNLOCKADDRESSEVENT_EVENT, transactionReceipt);
        ArrayList<UnlockAddressEventEventResponse> responses = new ArrayList<UnlockAddressEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnlockAddressEventEventResponse typedResponse = new UnlockAddressEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UnlockAddressEventEventResponse> unlockAddressEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UnlockAddressEventEventResponse>() {
            @Override
            public UnlockAddressEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UNLOCKADDRESSEVENT_EVENT, log);
                UnlockAddressEventEventResponse typedResponse = new UnlockAddressEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<UnlockAddressEventEventResponse> unlockAddressEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNLOCKADDRESSEVENT_EVENT));
        return unlockAddressEventEventObservable(filter);
    }

    public List<TokenIsRenamedEventResponse> getTokenIsRenamedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENISRENAMED_EVENT, transactionReceipt);
        ArrayList<TokenIsRenamedEventResponse> responses = new ArrayList<TokenIsRenamedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenIsRenamedEventResponse typedResponse = new TokenIsRenamedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.symbol = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.name = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TokenIsRenamedEventResponse> tokenIsRenamedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TokenIsRenamedEventResponse>() {
            @Override
            public TokenIsRenamedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENISRENAMED_EVENT, log);
                TokenIsRenamedEventResponse typedResponse = new TokenIsRenamedEventResponse();
                typedResponse.log = log;
                typedResponse.symbol = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.name = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TokenIsRenamedEventResponse> tokenIsRenamedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENISRENAMED_EVENT));
        return tokenIsRenamedEventObservable(filter);
    }

    public List<SupplyHasIncreasedEventResponse> getSupplyHasIncreasedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUPPLYHASINCREASED_EVENT, transactionReceipt);
        ArrayList<SupplyHasIncreasedEventResponse> responses = new ArrayList<SupplyHasIncreasedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SupplyHasIncreasedEventResponse typedResponse = new SupplyHasIncreasedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.newSupply = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SupplyHasIncreasedEventResponse> supplyHasIncreasedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, SupplyHasIncreasedEventResponse>() {
            @Override
            public SupplyHasIncreasedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUPPLYHASINCREASED_EVENT, log);
                SupplyHasIncreasedEventResponse typedResponse = new SupplyHasIncreasedEventResponse();
                typedResponse.log = log;
                typedResponse.newSupply = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<SupplyHasIncreasedEventResponse> supplyHasIncreasedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPPLYHASINCREASED_EVENT));
        return supplyHasIncreasedEventObservable(filter);
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

    public List<MinterAddedEventResponse> getMinterAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINTERADDED_EVENT, transactionReceipt);
        ArrayList<MinterAddedEventResponse> responses = new ArrayList<MinterAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MinterAddedEventResponse typedResponse = new MinterAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MinterAddedEventResponse> minterAddedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MinterAddedEventResponse>() {
            @Override
            public MinterAddedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINTERADDED_EVENT, log);
                MinterAddedEventResponse typedResponse = new MinterAddedEventResponse();
                typedResponse.log = log;
                typedResponse.account = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MinterAddedEventResponse> minterAddedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTERADDED_EVENT));
        return minterAddedEventObservable(filter);
    }

    public List<MinterRemovedEventResponse> getMinterRemovedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINTERREMOVED_EVENT, transactionReceipt);
        ArrayList<MinterRemovedEventResponse> responses = new ArrayList<MinterRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MinterRemovedEventResponse typedResponse = new MinterRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MinterRemovedEventResponse> minterRemovedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MinterRemovedEventResponse>() {
            @Override
            public MinterRemovedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINTERREMOVED_EVENT, log);
                MinterRemovedEventResponse typedResponse = new MinterRemovedEventResponse();
                typedResponse.log = log;
                typedResponse.account = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MinterRemovedEventResponse> minterRemovedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTERREMOVED_EVENT));
        return minterRemovedEventObservable(filter);
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

    public static MEXC load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MEXC(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MEXC load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MEXC(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class BurnTokenEventEventResponse {
        public Log log;

        public String account;

        public BigInteger value;
    }

    public static class LockAddressEventEventResponse {
        public Log log;

        public String account;
    }

    public static class UnlockAddressEventEventResponse {
        public Log log;

        public String account;
    }

    public static class TokenIsRenamedEventResponse {
        public Log log;

        public String symbol;

        public String name;
    }

    public static class SupplyHasIncreasedEventResponse {
        public Log log;

        public BigInteger newSupply;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String previousOwner;

        public String newOwner;
    }

    public static class MinterAddedEventResponse {
        public Log log;

        public String account;
    }

    public static class MinterRemovedEventResponse {
        public Log log;

        public String account;
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
