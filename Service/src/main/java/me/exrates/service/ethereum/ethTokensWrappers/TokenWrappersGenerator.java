package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "/home/sevak/eth/LEDU.bin",
                "/home/sevak/eth/LEDU.abi",
                "-o",
                "/home/sevak/IdeaProjects/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service.ethereum.ethTokensWrappers"});
    }
}
