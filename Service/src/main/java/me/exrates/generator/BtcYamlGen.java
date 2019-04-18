package me.exrates.generator;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class BtcYamlGen {

    public static final String prod = "/home/dudoser/git/exrates/Controller/src/main/prod/";

    @SneakyThrows
    public static void main(String[] args) {
        Map<String, BtcProperty> walletPropsBtcPropsMap = new HashMap<>();
        File[] merchants = new File(prod + "merchants").listFiles();
        for (File merchantFile : merchants) {
            try {
                BtcProperty btcProperty = new BtcProperty();
                if (merchantFile.getName().contains("_wallet")) {
                    setWalletProps(merchantFile, btcProperty);
                    setNodeProps(btcProperty);
                    walletPropsBtcPropsMap.put(merchantFile.getName(), btcProperty);
                }
            } catch (Exception e){
                System.out.println("Exception with " + merchantFile.getName());
                e.printStackTrace();
            }
        }

        f(walletPropsBtcPropsMap);
        createYaml(walletPropsBtcPropsMap);
    }

    private static void createYaml(Map<String, BtcProperty> walletPropsBtcPropsMap) throws IOException {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, BtcProperty> entry : walletPropsBtcPropsMap.entrySet()) {
            try {

                BtcProperty p = entry.getValue();
                b.append("  " + p.getMerchantName().toLowerCase() + ":\n");
                b.append("    merchant-name: " + p.getMerchantName()).append("\n");
                b.append("    currency-name: " + p.getCurrencyName()).append("\n");
                b.append("    min-confirmations: " + p.getMinConf()).append("\n");
                b.append("    block-target-for-fee: " + 20).append("\n");
                b.append("    raw-tx-enabled: " + false).append("\n");
                b.append("    support-subtract-fee: " + p.getSubsrFee()).append("\n");
                b.append("    support-wallet-notifications: " + true).append("\n");
                b.append("    support-reference-line: " + false).append("\n");
                b.append("    backup-folder: " + p.getBackup_folder()).append("\n");

                b.append("    node:\n");
                b.append("      zmq-enabled: " + p.isNode_zmqEnabled()).append("\n");
                b.append("      support-instant-send: " + p.isNode_supportInstantSend()).append("\n");
                b.append("      enabled: " + p.isNode_isEnabled()).append("\n");
                b.append("      rpc-protocol: " + p.getNode_bitcoind_rpc_protocol()).append("\n");
                b.append("      rpc-host: " + p.getNode_bitcoind_rpc_host()).append("\n");
                b.append("      rpc-port: " + p.getNode_bitcoind_rpc_port()).append("\n");
                b.append("      rpc-user: " + p.getNode_bitcoind_rpc_user()).append("\n");
                b.append("      rpc-password: " + p.getNode_bitcoind_rpc_password()).append("\n");
                b.append("      http-auth-schema: " + p.getNode_bitcoind_http_auth_scheme()).append("\n");
                b.append("      notification-alert-port: " + p.getNode_bitcoind_notification_alert_port()).append("\n");
                b.append("      notification-block-port: " + p.getNode_bitcoind_notification_block_port()).append("\n");
                b.append("      notification-wallet-port: " + p.getNode_bitcoind_notification_wallet_port()).append("\n");
            } catch (Exception e){
                System.out.println("Exception with " + entry.getKey());
                e.printStackTrace();
            }
        }

        File f = new File("/home/dudoser/Desktop/yaml.txt");
        f.createNewFile();
        new FileWriter(f).write(b.toString());
    }

    private static void f(Map<String, BtcProperty> walletPropsBtcPropsMap) throws IOException {
        String next = null;
            File crypto = new File("/home/dudoser/.local/share/Trash/files/exrates-master/Controller/src/main/java/me/exrates/config/CryptocurrencyConfig.java");

            String full = new String(Files.readAllBytes(crypto.toPath()), StandardCharsets.UTF_8);

            String substring = full.substring(full.indexOf("@Bean"));
            String[] beanDef = substring.split("@Bean");
            List<String> resultList = Arrays.stream(beanDef).collect(Collectors.toList());
            Iterator<String> iterator = resultList.iterator();
            while (iterator.hasNext()) {
                try {

                    next = iterator.next();
                if (!next.contains("BitcoinServiceImpl")) {
                    iterator.remove();
                    continue;
                }

                String s = "new BitcoinServiceImpl(";
                String temp = next.substring(next.indexOf(s) + s.length()).replaceAll("\"", "").replace(")", "").replaceAll("}", "");
                String[] params = temp.trim().replace(";", "").replace("\n", "").
                        replace("// LISK-like cryptos", "")
                        .replace("merchants/", "").trim().split(",");
                params = (String[]) Arrays.stream(params).map(String::trim).toArray(String[]::new);
                BtcProperty property = walletPropsBtcPropsMap.get(params[0]);
                Arrays.stream(params).forEach(System.out::println);
                property.setMerchantName(params[1]);
                property.setCurrencyName(params[2]);
                property.setMinConf(Integer.parseInt(params[3]));
                property.setSubsrFee(params.length >= 7 ? Boolean.valueOf(params[6]) : true);

                } catch (Exception e){
                    System.out.println("Exception with " + next);
                    e.printStackTrace();
                }
            }



    }

    private static void setNodeProps(BtcProperty btcProperty) throws IOException {
        File node_props = new File(prod + btcProperty.getNode_propertySource());
        Properties props = new Properties();
        props.load(new FileInputStream(node_props));
        btcProperty.setNode_bitcoind_rpc_protocol(props.getProperty("node.bitcoind.rpc.protocol"));
        btcProperty.setNode_bitcoind_rpc_host(props.getProperty("node.bitcoind.rpc.host"));
        btcProperty.setNode_bitcoind_rpc_port(Integer.parseInt(props.getProperty("node.bitcoind.rpc.port")));
        btcProperty.setNode_bitcoind_rpc_user(props.getProperty("node.bitcoind.rpc.user"));
        btcProperty.setNode_bitcoind_rpc_password(props.getProperty("node.bitcoind.rpc.password"));
        btcProperty.setNode_bitcoind_http_auth_scheme(props.getProperty("node.bitcoind.http.auth_scheme"));
        btcProperty.setNode_bitcoind_notification_alert_port(11);
        btcProperty.setNode_bitcoind_notification_block_port(Integer.parseInt(props.getProperty("node.bitcoind.notification.block.port")));
        btcProperty.setNode_bitcoind_notification_wallet_port(Integer.parseInt(props.getProperty("node.bitcoind.notification.wallet.port")));
    }

    private static void setWalletProps(File merchantFile, BtcProperty btcProperty) throws IOException {
        Properties val = new Properties();
        val.load(new FileInputStream(merchantFile));
        btcProperty.setWallet_password(val.getProperty("wallet.password"));
        btcProperty.setBackup_folder(val.getProperty("backup.folder"));
        btcProperty.setNode_propertySource(val.getProperty("node.propertySource"));
        btcProperty.setNode_zmqEnabled(Boolean.valueOf(val.getProperty("node.zmqEnabled")));
        btcProperty.setNode_supportInstantSend(Boolean.valueOf(val.getProperty("node.supportInstantSend")));
        btcProperty.setNode_isEnabled(Boolean.valueOf(val.getProperty("node.isEnabled")));
    }

    @Data
    public static class BtcProperty {
        String wallet_password;//=cwg59fVvMCTyPWHA5N2tjwdVcfE6CHmd6D57
        String backup_folder;
        String node_propertySource;//=node_config/node_config_b2x_properties
        boolean node_zmqEnabled;//=true
        boolean node_supportInstantSend;//=false
        boolean node_isEnabled;//=true
        String node_bitcoind_rpc_protocol;//= http
        String node_bitcoind_rpc_host;// = ip-172-31-14-168_us-east-2_compute_internal
        int node_bitcoind_rpc_port;// = 38552
        String node_bitcoind_rpc_user;// = devprod
        String node_bitcoind_rpc_password;// = ZcergOfTzoSbDEFNdcxaEBRDRJh3VOOs-anma1viJPk=
        String node_bitcoind_http_auth_scheme;// = Basic
        int node_bitcoind_notification_alert_port;// = 5868
        int node_bitcoind_notification_block_port;// 5869
        int node_bitcoind_notification_wallet_port;// = 5870

        String merchantName;
        String currencyName;
        int minConf;
        Boolean subsrFee;

    }
}
