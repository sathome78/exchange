package me.exrates.utils;

import java.io.IOException;

public class BtcGen {

    public static void main(String[] args) throws IOException {
        String[] env = {"PATH=/bin:/usr/bin/"};
        String cmd = "/home/dudoser/IdeaProjects/exrates/btc.sh";  //e.g test.sh -dparam1 -oout.txt
        Process p = new ProcessBuilder(cmd, "zalupa").start();
    }

}
