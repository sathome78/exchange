package me.exrates.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

public class BtcBackupChecker {

    private final static String ENV = "prod";
    private final static String CMD_DUDOSER = "/home/dudoser/IdeaProjects/exrates/btc.sh";

    public static void main(String[] args) throws IOException {
        File walletConfig = new File(new File("").getAbsoluteFile() + "/Controller/src/main/" + ENV + "/merchants");

        File[] filesWallet = walletConfig.listFiles();
        for (int i = 0; i< filesWallet.length; i++) {
            File walletFile = filesWallet[i];
            try {
                String s = new String(Files.readAllBytes(walletFile.toPath()));
                if(!s.contains("backup.folder=")) continue;
                String from_folder_to_new_line = s.substring(s.indexOf("backup.folder=") + "backup.folder=".length());
                String backupFolder = from_folder_to_new_line.substring(0, from_folder_to_new_line.indexOf("\n"));
                System.out.println(backupFolder);
                String newProps = s.replace(backupFolder, "/data/backup");
                System.out.println("Replaced = " + s);
                FileWriter fileWriter = new FileWriter(walletFile, false);
                fileWriter.write(newProps);
                fileWriter.flush();
            } catch (Exception e){
                System.out.println("Error with " + walletFile);
            }
        }


    }
}