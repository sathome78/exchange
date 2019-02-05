/*
package me.exrates.config;

import me.exrates.service.ethereum.ExConvert;
import me.exrates.service.ethereum.ethTokensWrappers.TokenWrappersGenerator;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EthTokenGenerator {
    private static final String SQL_PATCH = "INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`, `tokens_parrent_id`)\n" +
            "VALUES ('replacementEthereumTokenCoinDescription', 'TCR', 2, 'ethereumServiceImpl', 'CRYPTO', 16);\n" +
            "INSERT IGNORE INTO `CURRENCY` (`name`, `description`, `hidden`, `max_scale_for_refill`, `max_scale_for_withdraw`, `max_scale_for_transfer`)\n" +
            "VALUES ('TCR', 'replacementEthereumTokenCoinDescription', 0, 8, 8, 8);\n" +
            "\n" +
            "INSERT IGNORE INTO COMPANY_WALLET_EXTERNAL(currency_id) VALUES ((SELECT id from CURRENCY WHERE name='TCR'));\n" +
            "\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, refill_block, withdraw_block)\n" +
            "  VALUES ((SELECT id from MERCHANT WHERE name='TCR'),\n" +
            "          (SELECT id from CURRENCY WHERE name='TCR'),\n" +
            "          0.00000001, TRUE, TRUE);\n" +
            "\n" +
            "INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='TCR')\n" +
            ", '/client/img/merchants/TCR.png', 'TCR', (SELECT id from CURRENCY WHERE name='TCR'));\n" +
            "\n" +
            "INSERT IGNORE INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='TCR') from USER;\n" +
            "\n" +
            "INSERT IGNORE INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)\n" +
            "  SELECT (select id from CURRENCY where name = 'TCR'), operation_type_id, user_role_id, min_sum, max_sum\n" +
            "  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDR');\n" +
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
            "VALUES ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), (select id from CURRENCY where name = 'TCR'), 0.000001, 1, 1, 0);\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)\n" +
            "VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), (select id from CURRENCY where name = 'TCR'), 0.000001, 1, 1, 0);\n" +
            "\n" +
            "INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)\n" +
            "VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), (select id from CURRENCY where name = 'TCR'), 0.000001, 1, 1, 0);\n" +
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
            "INSERT IGNORE INTO INTERNAL_WALLET_BALANCES (currency_id, role_id)\n" +
            "SELECT cur.id AS currency_id, ur.id AS role_id\n" +
            "FROM CURRENCY cur CROSS JOIN USER_ROLE ur\n" +
            "WHERE cur.name IN ('TCR')\n" +
            "ORDER BY cur.id, ur.id;\n" +
            "\n" +
            "INSERT IGNORE INTO COMPANY_EXTERNAL_WALLET_BALANCES (currency_id)\n" +
            "SELECT cur.id\n" +
            "FROM CURRENCY cur\n" +
            "WHERE cur.name IN ('TCR');";

    private static void generate(String ticker, String description, String contract, boolean isERC20, int decimals, String bin, String abi) throws Exception {
        createBean(ticker, contract, isERC20, decimals);
        createSql(ticker, description);
        createTokenWrapperGenerator(ticker, isERC20, bin, abi);
    }
    private static void createBean(String ticker, String contract, boolean isERC20, int decimals) throws IOException {
        File cryptoCurrency = new File(new File("").getAbsoluteFile() + "/Controller/src/main/java/me/exrates/config/" + "WebAppConfig.java");

        FileReader reader = new FileReader(cryptoCurrency);

        int c;
        StringBuilder builder = new StringBuilder();
        while ((c = reader.read()) != -1){
            builder.append((char)c);
        }

        String enumValueForDecimals = ExConvert.Unit.getListPossibleDecimalForEthereumTokens()
                .stream().filter(e -> e.getFactor() == decimals).findFirst().get().toString().toUpperCase();
        String s = "//    Qtum tokens:";
        String bean = "@Bean(name = \"" + ticker.toLowerCase() + "ServiceImpl\")\n" +
                "\tpublic EthTokenService " + ticker.toLowerCase() + "ServiceImpl(){\n" +
                "\t\tList<String> tokensList = new ArrayList<>();\n" +
                "\t\ttokensList.add(\""+contract+"\");\n" +
                "\t\treturn new EthTokenServiceImpl(tokensList, \"" + ticker + "\"," + "\"" + ticker + "\", " + isERC20 +", "+ "ExConvert.Unit."+ enumValueForDecimals + ");\n" +
                "\t}" + "\n\n\t"+s;

        String replace = builder.toString().replace(s, bean);

        FileWriter writer = new FileWriter(cryptoCurrency, false);
        writer.append(replace).flush();
    }

    private static void createSql(String ticker, String description) throws IOException {
        File newMigration = new File(new File("").getAbsoluteFile() + "/Controller/src/main/resources/db/migration/" + getSqlName(ticker) + ".sql");
        if(!newMigration.createNewFile()) throw new RuntimeException("Can not create file with pass " + newMigration.getAbsolutePath() + "\n maybe have not permission!");

        FileWriter writer = new FileWriter(newMigration);
        writer.append(SQL_PATCH.replace("TCR", ticker).replace("replacementEthereumTokenCoinDescription", description)).flush();
    }

    private static String getSqlName(String name){
        File migrantions = new File(new File("").getAbsoluteFile() + "/Controller/src/main/resources/db/migration/");
        File[] files = migrantions.listFiles();
        double[] versions = new double[files.length];

        for (int i = 0; i < versions.length - 1; i++) {
            String nameOfSql = files[i].getName();
            if (!nameOfSql.contains("V")) continue;
            String substring = nameOfSql.replace("V", "").substring(0, nameOfSql.indexOf("__") - 1);
            versions[i] = Integer.valueOf(substring.replace("1.", ""));
        }

        double lastVersion = Arrays.stream(versions).max().getAsDouble();
        String version = "1." + String.valueOf(++lastVersion).replace(".0", "");
        return "V" + version + "__Ethereum_token_" + name;
    }

    private static void createTokenWrapperGenerator(String ticker, boolean isERC20, String bin, String abi) throws Exception {
        PrintWriter binFile = new PrintWriter(FILE_PATH_TO_BIN_ABI_FILES+ticker.toUpperCase()+".bin", "UTF-8");
        binFile.println(bin);
        binFile.close();

        PrintWriter abiFile = new PrintWriter(FILE_PATH_TO_BIN_ABI_FILES+ticker.toUpperCase()+".abi", "UTF-8");
        abiFile.println(abi);
        abiFile.close();

        TokenWrappersGenerator.generateWrapper(ticker, FILE_PATH_TO_BIN_ABI_FILES, FILE_PATH_TO_WRAPPERS, WRAPPERS_PACKAGE);

        File ethereumToken = new File(FILE_PATH_TO_WRAPPERS + "/"+ WRAPPERS_PACKAGE.replace(".", "/") +"/"+ ticker.toUpperCase()+".java");

        FileReader reader = new FileReader(ethereumToken);

        int c;
        StringBuilder builder = new StringBuilder();
        while ((c = reader.read()) != -1){
            builder.append((char)c);
        }

        String s = "public class "+ticker+" extends Contract";
        String implementsToken = isERC20 ? "implements ethTokenERC20" : "implements ethTokenNotERC20";
        String title = s+ " "+implementsToken;

        String replace = builder.toString().replace(s, title);

        FileWriter writer = new FileWriter(ethereumToken, false);
        writer.append(replace).flush();
    }



































    public static final String FILE_PATH_TO_BIN_ABI_FILES = "/Users/vlad.dziubak/crypto/eth/";
    public static final String FILE_PATH_TO_WRAPPERS = "/Users/vlad.dziubak/workspace/exrates/Service/src/main/java";
    public static final String WRAPPERS_PACKAGE = "me.exrates.service.ethereum.ethTokensWrappers";

    public static void main(String[] args) throws Exception {
        generate("PLTC", "PlatonCoin",
                "0x0c6e8a8358cbde54f8e4cd7f07d5ac38aec8c5a4", true, 18,
                "6080604052670de0b6b3a76400006301406f4002600055670de0b6b3a764000062d37ca002600155670de0b6b3a764000062233f7002600255670de0b6b3a7640000620cd14002600355670de0b6b3a7640000620cd14002600455670de0b6b3a7640000623010b0026005556000600860006101000a81548160ff02191690831515021790555034801561009257600080fd5b5033600660006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060015460096000600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef60096000600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a3612780806102346000396000f30060806040526004361061016a576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063037c983a1461016f57806306fdde0314610186578063095ea7b314610216578063170594c61461027b57806318160ddd146102a65780631ca8b6cb146102d157806323b872dd146102fc57806323e5942a1461038157806327e235e314610424578063313ce5671461047b57806342966c68146104ac5780635b7f415c146104f1578063661884631461051c57806370a0823114610581578063715018a6146105d857806378231978146105ef5780638a51d0fc1461061a5780638da5cb5b1461064557806395d89b411461069c5780639a9d09351461072c578063a9059cbb14610757578063bd7d3836146107bc578063d5e0e131146107d3578063d73dd623146107fe578063d89135cd14610863578063dc39d06d1461088e578063dd62ed3e146108f3578063f2fde38b1461096a575b600080fd5b34801561017b57600080fd5b506101846109ad565b005b34801561019257600080fd5b5061019b610a26565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101db5780820151818401526020810190506101c0565b50505050905090810190601f1680156102085780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561022257600080fd5b50610261600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610a5f565b604051808215151515815260200191505060405180910390f35b34801561028757600080fd5b50610290610ba9565b6040518082815260200191505060405180910390f35b3480156102b257600080fd5b506102bb610baf565b6040518082815260200191505060405180910390f35b3480156102dd57600080fd5b506102e6610bbb565b6040518082815260200191505060405180910390f35b34801561030857600080fd5b50610367600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610bc1565b604051808215151515815260200191505060405180910390f35b34801561038d57600080fd5b50610422600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061101f565b005b34801561043057600080fd5b50610465600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611530565b6040518082815260200191505060405180910390f35b34801561048757600080fd5b50610490611548565b604051808260ff1660ff16815260200191505060405180910390f35b3480156104b857600080fd5b506104d76004803603810190808035906020019092919050505061154d565b604051808215151515815260200191505060405180910390f35b3480156104fd57600080fd5b506105066117a2565b6040518082815260200191505060405180910390f35b34801561052857600080fd5b50610567600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506117ae565b604051808215151515815260200191505060405180910390f35b34801561058d57600080fd5b506105c2600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611a5b565b6040518082815260200191505060405180910390f35b3480156105e457600080fd5b506105ed611aa4565b005b3480156105fb57600080fd5b50610604611bc5565b6040518082815260200191505060405180910390f35b34801561062657600080fd5b5061062f611bcb565b6040518082815260200191505060405180910390f35b34801561065157600080fd5b5061065a611bd1565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156106a857600080fd5b506106b1611bf7565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156106f15780820151818401526020810190506106d6565b50505050905090810190601f16801561071e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561073857600080fd5b50610741611c30565b6040518082815260200191505060405180910390f35b34801561076357600080fd5b506107a2600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050611c36565b604051808215151515815260200191505060405180910390f35b3480156107c857600080fd5b506107d1611eef565b005b3480156107df57600080fd5b506107e8611f68565b6040518082815260200191505060405180910390f35b34801561080a57600080fd5b50610849600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050611f6e565b604051808215151515815260200191505060405180910390f35b34801561086f57600080fd5b50610878612186565b6040518082815260200191505060405180910390f35b34801561089a57600080fd5b506108d9600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061218c565b604051808215151515815260200191505060405180910390f35b3480156108ff57600080fd5b50610954600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061230e565b6040518082815260200191505060405180910390f35b34801561097657600080fd5b506109ab600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612426565b005b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610a0957600080fd5b6000600860006101000a81548160ff021916908315150217905550565b6040805190810160405280600a81526020017f506c61746f6e436f696e0000000000000000000000000000000000000000000081525081565b6000600860009054906101000a900460ff16151515610a7d57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614151515610ab957600080fd5b81600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b60025481565b60008054905080905090565b60005481565b6000600860009054906101000a900460ff16151515610bdf57600080fd5b6000821415610c56578273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a360019050611018565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614151515610c9257600080fd5b81600960008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410158015610d5d575081600a60008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410155b8015610d6a575060008210155b1515610d7557600080fd5b610dc782600960008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461271f90919063ffffffff16565b600960008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610e9982600a60008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461271f90919063ffffffff16565b600a60008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610f6b82600960008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461273890919063ffffffff16565b600960008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190505b9392505050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561107b57600080fd5b600860009054906101000a900460ff1615151561109757600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff16141580156111015750600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614155b801561113a5750600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614155b80156111735750600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614155b151561117e57600080fd5b600254600960008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550600354600960008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550600454600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550600554600960008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508373ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef600960008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a38273ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef600960008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a38173ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef600960008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a38073ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef600960008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a350505050565b60096020528060005260406000206000915090505481565b601281565b600080600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156115ac57600080fd5b600860009054906101000a900460ff161515156115c857600080fd5b600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054831115151561161657600080fd5b33905061166b83600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461271f90919063ffffffff16565b600960008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506116c38360005461271f90919063ffffffff16565b6000819055506116de8360075461273890919063ffffffff16565b6007819055508073ffffffffffffffffffffffffffffffffffffffff167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5846040518082815260200191505060405180910390a2600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef856040518082815260200191505060405180910390a36001915050919050565b670de0b6b3a764000081565b600080600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050600860009054906101000a900460ff1615151561184c57600080fd5b808311156118db576000600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555061196f565b6118ee838261271f90919063ffffffff16565b600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b8373ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a3600191505092915050565b6000600960008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611b0057600080fd5b600860009054906101000a900460ff16151515611b1c57600080fd5b6000600660006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167ff8df31144d9c2f0f6b59d69b8b98abd5459d07f2742c4df920b25aae33c6482060405160405180910390a2565b60015481565b60055481565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6040805190810160405280600481526020017f504c54430000000000000000000000000000000000000000000000000000000081525081565b60045481565b6000600860009054906101000a900460ff16151515611c5457600080fd5b6000821415611ccb578273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a360019050611ee9565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614151515611d0757600080fd5b81600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410151515611d5557600080fd5b611da782600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461271f90919063ffffffff16565b600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550611e3c82600960008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461273890919063ffffffff16565b600960008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190505b92915050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611f4b57600080fd5b6001600860006101000a81548160ff021916908315150217905550565b60035481565b6000600860009054906101000a900460ff16151515611f8c57600080fd5b61201b82600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461273890919063ffffffff16565b600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a36001905092915050565b60075481565b6000600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156121ea57600080fd5b600860009054906101000a900460ff1615151561220657600080fd5b8273ffffffffffffffffffffffffffffffffffffffff1663a9059cbb600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16846040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1580156122cb57600080fd5b505af11580156122df573d6000803e3d6000fd5b505050506040513d60208110156122f557600080fd5b8101908080519060200190929190505050905092915050565b6000600860009054906101000a900460ff1615151561232c57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16141580156123965750600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614155b15156123a157600080fd5b600a60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561248257600080fd5b600860009054906101000a900460ff1615151561249e57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16141515156124da57600080fd5b61258d60096000600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461273890919063ffffffff16565b600960008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550600060096000600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555080600660006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508073ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef600960008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a350565b600082821115151561272d57fe5b818303905092915050565b6000818301905082811015151561274b57fe5b809050929150505600a165627a7a723058205a82e12601ac57b9f5a0e5785037231fa112a58c4f5ae7dc8b7614ab3dc961ea0029",
                "[{\"constant\":false,\"inputs\":[],\"name\":\"resumeCrowdSale\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_tokens\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalTeamSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"_totalSupply\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalTokenSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_teamWallet\",\"type\":\"address\"},{\"name\":\"_advisorWallet\",\"type\":\"address\"},{\"name\":\"_bountyWallet\",\"type\":\"address\"},{\"name\":\"_earlyInvWallet\",\"type\":\"address\"}],\"name\":\"initWallets\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"balances\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"burn\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"TOKEN_DECIMALS\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_subtractedValue\",\"type\":\"uint256\"}],\"name\":\"decreaseApproval\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceOwnership\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalSaleSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalEarlyInvSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalBountySupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"},{\"name\":\"_tokens\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"pauseCrowdSale\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalAdvisorsSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_addedValue\",\"type\":\"uint256\"}],\"name\":\"increaseApproval\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalBurned\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_tokenAddress\",\"type\":\"address\"},{\"name\":\"_tokens\",\"type\":\"uint256\"}],\"name\":\"transferAnyERC20Token\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_newOwner\",\"type\":\"address\"}],\"name\":\"transferOwnership\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"fallback\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_burner\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"Burn\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_previousOwner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"_newOwner\",\"type\":\"address\"}],\"name\":\"OwnershipTransferred\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_previousOwner\",\"type\":\"address\"}],\"name\":\"OwnershipRenounced\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"_spender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"_to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}]");
    }
}*/
