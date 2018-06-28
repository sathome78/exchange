package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "c:/eth/TAVITT.bin",
                "c:/eth/TAVITT.abi",
                "-o",
                "c:/Users/Ajet/IdeaProjects/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service.ethereum.ethTokensWrappers"});
    }
}
