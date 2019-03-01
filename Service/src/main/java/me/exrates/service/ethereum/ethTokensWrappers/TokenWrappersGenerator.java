package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "F:\\workspace\\exrates\\Controller\\src\\main\\resources/VRA.bin",
                "F:\\workspace\\exrates\\Controller\\src\\main\\resources/VRA.abi",
                "-o",
                "F:\\workspace\\exrates\\Service\\src\\main\\java",
                "-p",
                "me.exrates.service.ethereum.ethTokensWrappers"});
    }

    public static void generateWrapper(String ticker, String filePathToBinAbiFiles, String filePathToWrappers, String wrappersPackage) throws Exception{
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                filePathToBinAbiFiles+ticker+".bin",
                filePathToBinAbiFiles+ticker+".abi",
                "-o",
                filePathToWrappers,
                "-p",
                wrappersPackage});
    }
}
