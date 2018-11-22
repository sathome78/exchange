package me.exrates.utils;

import advcash.wsm.ExchangeCurrencyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;


public class BtcBackupChecker {

    private final static String ENV = "dev";
    private final static String CMD_DUDOSER = "/home/dudoser/IdeaProjects/exrates/btc.sh";

    public static void main(String[] args) throws IOException {
        File walletConfig = new File(new File("").getAbsoluteFile() + "/Controller/src/main/" + ENV + "/merchants");

        File[] filesWallet = walletConfig.listFiles();
        for (int i = 0; i< filesWallet.length; i++) {
            File walletFile = filesWallet[i];
            try {
                File nodeConfig = new File(new File("").getAbsoluteFile() + "/Controller/src/main/" + ENV + "/node_config/"
                        + "node_config_" + walletFile.getName().substring(0, walletFile.getName().indexOf("_")) + ".properties");

                String name = walletFile.getName();

                Properties properties = new Properties();
                properties.load(new FileInputStream(walletFile));

                Properties nodeProperties = new Properties();
                nodeProperties.load(new FileInputStream(nodeConfig));

//                System.out.println("Checking coin " + name.substring(0, name.indexOf("_")) + " with node config file " + nodeConfig.getName() + " and host " + nodeProperties.getProperty("node.bitcoind.rpc.host"));
                new ProcessBuilder(CMD_DUDOSER, properties.getProperty("backup.folder"), nodeProperties.getProperty("node.bitcoind.rpc.host")).start();
            } catch (Exception e){
                System.out.println("Error with " + walletFile);
            }
        }


    }
}
