package me.exrates.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class BtcGenerator {
    private static final String SQL_PATCH = "INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)\n" +
            "VALUES ('zalupaCoin', 'TCR', 2, 'beannameServiceImpl', 'CRYPTO');\n" +
            "INSERT IGNORE INTO `CURRENCY` (`name`, `description`, `hidden`, `max_scale_for_refill`, `max_scale_for_withdraw`, `max_scale_for_transfer`)\n" +
            "VALUES ('TCR', 'zalupaCoin', 0, 8, 8, 8);\n" +
            "\n" +
            "INSERT IGNORE INTO COMPANY_WALLET_EXTERNAL(currency_id) VALUES ((SELECT id from CURRENCY WHERE name='TCR'));\n" +
            "\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)\n" +
            "  VALUES ((SELECT id from MERCHANT WHERE name='TCR'),\n" +
            "          (SELECT id from CURRENCY WHERE name='TCR'),\n" +
            "          0.0001);\n" +
            "\n" +
            "INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='TCR')\n" +
            ", '/client/img/merchants/TCR.png', 'TCR', (SELECT id from CURRENCY WHERE name='TCR'));\n" +
            "\n" +
            "INSERT IGNORE INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='TCR') from USER;\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)\n" +
            "  SELECT (select id from CURRENCY where name = 'TCR'), operation_type_id, user_role_id, min_sum, max_sum\n" +
            "  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDC');\n" +
            "\n" +
            "INSERT IGNORE INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'TCR'));\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, ticker_name)\n" +
            "VALUES((select id from CURRENCY where name = 'TCR'), (select id from CURRENCY where name = 'USD'), 'TCR/USD', 170, 0, 'TCR/USD');\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)\n" +
            "  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP\n" +
            "  JOIN USER_ROLE UR\n" +
            "  JOIN ORDER_TYPE OT where CP.name='TCR/USD';\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)\n" +
            "VALUES((select id from CURRENCY where name = 'TCR'), (select id from CURRENCY where name = 'BTC'), 'TCR/BTC', 160, 0, 'BTC', 'TCR/BTC');\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)\n" +
            "  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP\n" +
            "    JOIN USER_ROLE UR\n" +
            "    JOIN ORDER_TYPE OT where CP.name='TCR/BTC';\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)\n" +
            "VALUES((select id from CURRENCY where name = 'TCR'), (select id from CURRENCY where name = 'ETH'), 'TCR/ETH', 160, 0, 'ETH', 'TCR/ETH');\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)\n" +
            "  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP\n" +
            "    JOIN USER_ROLE UR\n" +
            "    JOIN ORDER_TYPE OT where CP.name='TCR/ETH';\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)\n" +
            "VALUES ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), (select id from CURRENCY where name = 'TCR'), 0.0001, 1, 1, 0);\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)\n" +
            "VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), (select id from CURRENCY where name = 'TCR'), 0.0001, 1, 1, 0);\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)\n" +
            "VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), (select id from CURRENCY where name = 'TCR'), 0.0001, 1, 1, 0);\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES\n" +
            "  ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), '/client/img/merchants/transfer.png', 'Transfer', (select id from CURRENCY where name = 'TCR'));\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES\n" +
            "  ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), '/client/img/merchants/voucher.png', 'Voucher', (select id from CURRENCY where name = 'TCR'));\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES\n" +
            "  ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), '/client/img/merchants/voucher_free.png', 'Free voucher', (select id from CURRENCY where name = 'TCR'));\n" +
            "\n" +
            "INSERT IGNORE INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)\n" +
            "  SELECT BT.id, CP.id FROM BOT_TRADER BT\n" +
            "    JOIN CURRENCY_PAIR CP WHERE CP.name IN ('TCR/USD', 'TCR/BTC', 'TCR/ETH');\n" +
            "\n" +
            "INSERT IGNORE INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)\n" +
            "  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH\n" +
            "    JOIN ORDER_TYPE OT\n" +
            "  WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('TCR/USD', 'TCR/BTC', 'TCR/ETH'));\n" +
            "\n" +
            "INSERT IGNORE INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code, passphrase)\n" +
            "VALUES ((SELECT id from MERCHANT WHERE name='TCR'), (select id from CURRENCY where name='TCR'), 'CRYPWallet.title', 'pass123');\n" +
            "\n" +
            "INSERT IGNORE INTO INTERNAL_WALLET_BALANCES (currency_id, role_id)\n" +
            "SELECT cur.id AS currency_id, ur.id AS role_id\n" +
            "FROM CURRENCY cur CROSS JOIN USER_ROLE ur\n" +
            "WHERE cur.name IN ('TCR')\n" +
            "ORDER BY cur.id, ur.id;\n" +
            "\n" +
            "INSERT IGNORE INTO COMPANY_EXTERNAL_WALLET_BALANCES (currency_id)\n" +
            "SELECT cur.id\n" +
            "FROM CURRENCY cur\n" +
            "WHERE cur.name IN ('TCR');" +
            "INSERT IGNORE INTO CURRENT_CURRENCY_RATES (currency_id, currency_name)\n" +
            "SELECT cur.id, cur.name\n" +
            "FROM CURRENCY cur\n" +
            "WHERE cur.name = 'TCR';" +
            "INSERT IGNORE INTO CURRENT_CURRENCY_BALANCES (currency_id, currency_name)\n" +
            "SELECT cur.id, cur.name\n" +
            "FROM CURRENCY cur\n" +
            "WHERE cur.name = 'TCR';";

    private static final String WALLET_SCRIPT_PROPERTIES =
            "backup.folder=/data/backup/\n" +
            "node.propertySource=node_config/node_config_zalupa.properties\n" +
            "node.zmqEnabled=isZmq\n" +
            "node.supportInstantSend=false\n" +
            "node.isEnabled=true\n";

    private static final String ENVS[] = new String[]{"dev", "uat", "prod", "devtest"};
    private static final String NODE_CONFIG = "node.bitcoind.rpc.protocol = http\n" +
            "node.bitcoind.rpc.host = $host\n" +
            "node.bitcoind.rpc.port = $port\n" +
            "node.bitcoind.http.auth_scheme = Basic\n" +
            "node.bitcoind.notification.block.port = $block\n" +
            "node.bitcoind.notification.wallet.port = $wallet\n";

    private static void generate(String ticker, String description, int minConf, boolean isSubstructFee, boolean isZmq, String host, int port, int blockPort) throws IOException {
        createBean(ticker, minConf, isSubstructFee);
        createSql(ticker, description);
        createProps(ticker, isZmq, host, port, blockPort);
    }

    private static void createBean(String ticker, int minConf, boolean fee) throws IOException {
        File cryptoCurrency = new File(new File("").getAbsoluteFile() + "/Controller/src/main/java/me/exrates/config/" + "CryptocurrencyConfig.java");

        FileReader reader = new FileReader(cryptoCurrency);
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

        FileWriter writer = new FileWriter(cryptoCurrency, false);
        writer.append(replace).flush();
    }

    private static void createProps(String ticker, boolean isZmq, String host, int port, int blockPort) throws IOException {
        for (String env : ENVS) {
            File merchantProps = new File(new File("").getAbsoluteFile() + "/Controller/src/main/" + env + "/merchants/" + ticker.toLowerCase() + "_wallet.properties");
            if(!merchantProps.createNewFile()) throw new RuntimeException("Can not create file with pass " + merchantProps.getAbsolutePath() + "\n maybe have not permission!");
            FileWriter writer = new FileWriter(merchantProps);
            writer.append(WALLET_SCRIPT_PROPERTIES.replace("zalupa", ticker.toLowerCase()).replace("isZmq", String.valueOf(isZmq))).flush();

            File nodeConfig = new File(new File("").getAbsoluteFile() + "/Controller/src/main/" + env + "/node_config/node_config_" + ticker.toLowerCase() + ".properties");
            writer = new FileWriter(nodeConfig);
            writer.append(NODE_CONFIG.replace("$host", host).replace("$port", String.valueOf(port)).replace("$block", String.valueOf(blockPort))
            .replace("$wallet", String.valueOf(blockPort + 1))).flush();
        }
    }

    private static void createSql(String ticker, String description) throws IOException {
        File newMigration = new File(new File("").getAbsoluteFile() + "/Controller/src/main/resources/db/migration/" + getSqlName(ticker) + ".sql");
        if(!newMigration.createNewFile()) throw new RuntimeException("Can not create file with pass " + newMigration.getAbsolutePath() + "\n maybe have not permission!");

        FileWriter writer = new FileWriter(newMigration);
        writer.append(SQL_PATCH.replace("TCR", ticker).replace("beanname", ticker.toLowerCase()).replace("zalupaCoin", description)).flush();
    }

    private static String getSqlName(String ticker){
        LocalDateTime localDateTime = LocalDateTime.now();
        String month = String.valueOf(localDateTime.getMonthValue()).length() == 1 ? "0" + localDateTime.getMonthValue() : String.valueOf(localDateTime.getMonthValue());
        String dayOfMonth = String.valueOf(localDateTime.getDayOfMonth()).length() == 1 ? "0" + localDateTime.getDayOfMonth() : String.valueOf(localDateTime.getDayOfMonth());
        String version = "1." + localDateTime.getYear() + month + dayOfMonth
                + localDateTime.getHour() + localDateTime.getMinute();
        return "V" + version + "__Bitcoin_fork_" + ticker;
    }











    public static void main(String[] args) throws IOException {
        generate("TSL", "TreasureSL", 30, false, false, "172.10.13.6", 8090, 12201);
    }


}