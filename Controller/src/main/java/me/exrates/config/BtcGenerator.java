package me.exrates.config;

import com.ibm.wsdl.ServiceImpl;
import me.exrates.config.CryptocurrencyConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BtcGenerator {

    private static void generate(String ticker, int minConf, boolean fee) throws IOException {
        File sourceFile = new File(new File("").getAbsoluteFile() + "/Controller/src/main/java/me/exrates/config/" + "CryptocurrencyConfig.java");

        FileReader reader = new FileReader(sourceFile);
        int c;
        StringBuilder builder = new StringBuilder();
        while ((c = reader.read()) != -1){
            builder.append((char)c);
        }
        String s = "// LISK-like cryptos";
        String bean = "@Bean(name = \"" + ticker.toLowerCase() + "ServiceImpl\")\n\tpublic BitcoinService " + ticker.toLowerCase()
                + "ServiceImpl() {\n\t\treturn new BitcoinServiceImpl(\"merchants/"+ticker.toLowerCase()+"_wallet.properties\","
                + "\"" + ticker + "\"," + "\"" + ticker + "\", " + minConf +", 20, false, " + fee + ");\n\t}" + "\n\n\t"+s;
        String replace = builder.toString().replace(s, bean);
        System.out.println(replace);
        FileWriter writer = new FileWriter(sourceFile, false);
        writer.append(replace).flush();

    }


    public static void main(String[] args) throws IOException {
        generate("ZALUPA", 100, false);
    }
}