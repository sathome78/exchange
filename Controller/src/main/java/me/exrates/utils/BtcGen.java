package me.exrates.utils;

import java.io.IOException;

public class BtcGen {

    public static void main(String[] args) throws IOException {
        String[] env = {"PATH=/bin:/usr/bin/"};
        String cmdDudoser = "/home/dudoser/IdeaProjects/exrates/btc.sh";
        String cmdVdziubak = "/home/yagi/workspace/becomeJavaSenior/exrates/btc.sh";
        Process p = new ProcessBuilder(cmdDudoser, "/data/zalupa").start();
    }

}
