package me.exrates.service.util;

import me.exrates.service.ethTokensWrappers.Rep;
import org.web3j.abi.datatypes.Bool;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * Created by Maks on 14.09.2017.
 */
public class EthTokensWrapperGenerator {

    private EthTokensWrapperGenerator() {
    }

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "d:/eth/eos.bin",
                "d:/eth/eos.abi",
                "-o",
                "c:/Users/Maks/IdeaProjects/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service.ethTokensWrappers"});
       /*exeprimental();*/
    }


    private static void exeprimental() throws IOException, CipherException, ExecutionException, InterruptedException {
        String url = "http://localhost:8545/";
        Web3j web3j = Web3j.build(new HttpService(url));
        Credentials credentials = WalletUtils.loadCredentials("sprinter31313",
                "c:/Users/Maks/AppData/Roaming/Ethereum/keystore/UTC--2017-09-14T08-03-01.933401300Z--85c481f3c74cbd72d0bf84ffd68a5cc608c4d700");
        System.out.println(credentials.getEcKeyPair().getPrivateKey() + " " + credentials.getAddress());
        Rep contract = Rep.load("0xE94327D07Fc17907b4DB788E5aDf2ed424adDff6", web3j, credentials, GAS_PRICE, GAS_LIMIT);
        Future<Bool> future = contract.initialized();
        System.out.println("contract initialized " + future.get().getValue());
        System.out.println("gas price " + contract.getGasPrice().toString());
        /*rx.Observable<Rep.TransferEventResponse> observable = contract.transferEventObservable();
        observable.subscribe(s->System.out.println(s));*/
    }
}
