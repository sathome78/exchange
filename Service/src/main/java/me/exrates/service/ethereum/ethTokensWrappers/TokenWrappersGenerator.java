package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "F:\\workspace\\exrates\\Controller\\src\\main\\resources/FST.bin",
                "F:\\workspace\\exrates\\Controller\\src\\main\\resources/FST.abi",
                "-o",
                "F:\\workspace\\exrates\\Service\\src\\main\\java",
                "-p",
                "me.exrates.service.ethereum.ethTokensWrappers"});
    }

}
