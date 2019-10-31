package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "/home/unitomega13/EtherTokens/MoCo/MoCo.bin",
                "/home/unitomega13/EtherTokens/MoCo/MoCo.abi",
                "-o",
                "/home/unitomega13/IdeaProjects/exrates/Service/src/main/java",

                "-p",
                "me.exrates.service.ethereum.ethTokensWrappers"});
    }

}
