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
        generate("WaBi", "Tael",
                "0x286bda1413a2df81731d4930ce2f862a35a609fe", false, 18,
                "606060405260038054600160b860020a03191633600160a060020a0316179055610c3f8061002e6000396000f3006060604052600436106101115763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305d2035b811461011657806306fdde031461013d578063095ea7b3146101c757806318160ddd146101e957806323b872dd1461020e578063313ce567146102365780633f4ba83a1461025f57806340c10f19146102745780634cd412d5146102965780635c975abb146102a957806370a08231146102bc5780637d64bcb4146102db5780638456cb59146102ee5780638da5cb5b1461030157806395d89b411461013d578063a9059cbb14610330578063dd62ed3e14610352578063f1b50c1d14610377578063f2fde38b1461038a578063f669052a146103a9575b600080fd5b341561012157600080fd5b6101296103bc565b604051901515815260200160405180910390f35b341561014857600080fd5b6101506103de565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561018c578082015183820152602001610174565b50505050905090810190601f1680156101b95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101d257600080fd5b610129600160a060020a0360043516602435610415565b34156101f457600080fd5b6101fc610440565b60405190815260200160405180910390f35b341561021957600080fd5b610129600160a060020a0360043581169060243516604435610446565b341561024157600080fd5b6102496104c0565b60405160ff909116815260200160405180910390f35b341561026a57600080fd5b6102726104c5565b005b341561027f57600080fd5b610129600160a060020a0360043516602435610544565b34156102a157600080fd5b610129610583565b34156102b457600080fd5b610129610593565b34156102c757600080fd5b6101fc600160a060020a03600435166105a3565b34156102e657600080fd5b6101296105be565b34156102f957600080fd5b61027261065c565b341561030c57600080fd5b6103146106e0565b604051600160a060020a03909116815260200160405180910390f35b341561033b57600080fd5b610129600160a060020a03600435166024356106ef565b341561035d57600080fd5b6101fc600160a060020a0360043581169060243516610760565b341561038257600080fd5b61012961078b565b341561039557600080fd5b610272600160a060020a03600435166107d5565b34156103b457600080fd5b6101fc610834565b6003547501000000000000000000000000000000000000000000900460ff1681565b60408051908101604052600481527f5761426900000000000000000000000000000000000000000000000000000000602082015281565b60035460009060a060020a900460ff161561042f57600080fd5b6104398383610843565b9392505050565b60005481565b60035460009060a060020a900460ff161561046057600080fd5b60035460b060020a900460ff16151561047857600080fd5b30600160a060020a031683600160a060020a0316141580156104a25750600160a060020a03831615155b15156104ad57600080fd5b6104b88484846108e9565b949350505050565b601281565b60035433600160a060020a039081169116146104e057600080fd5b60035460a060020a900460ff1615156104f857600080fd5b6003805474ff0000000000000000000000000000000000000000191690557f7805862f689e2f13df9f062ff482ad3ad112aca9e0847911ed832e158c525b3360405160405180910390a1565b60035460009060a060020a900460ff161561055e57600080fd5b60035433600160a060020a0390811691161461057957600080fd5b61043983836109fc565b60035460b060020a900460ff1681565b60035460a060020a900460ff1681565b600160a060020a031660009081526001602052604090205490565b60035460009060a060020a900460ff16156105d857600080fd5b60035433600160a060020a039081169116146105f357600080fd5b6003805475ff000000000000000000000000000000000000000000191675010000000000000000000000000000000000000000001790557fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0860405160405180910390a150600190565b60035433600160a060020a0390811691161461067757600080fd5b60035460a060020a900460ff161561068e57600080fd5b6003805474ff0000000000000000000000000000000000000000191660a060020a1790557f6985a02210a168e66602d3235cb6db0e70f92b3ba4d376a33c0f3d9434bff62560405160405180910390a1565b600354600160a060020a031681565b60035460009060a060020a900460ff161561070957600080fd5b60035460b060020a900460ff16151561072157600080fd5b30600160a060020a031683600160a060020a03161415801561074b5750600160a060020a03831615155b151561075657600080fd5b6104398383610b33565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b60035460009033600160a060020a039081169116146107a957600080fd5b506003805476ff00000000000000000000000000000000000000000000191660b060020a179055600190565b60035433600160a060020a039081169116146107f057600080fd5b600160a060020a038116151561080557600080fd5b6003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b6a52b7d2dcc80cd2e400000081565b60008115806108755750600160a060020a03338116600090815260026020908152604080832093871683529290522054155b151561088057600080fd5b600160a060020a03338116600081815260026020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b600160a060020a038084166000908152600260209081526040808320338516845282528083205493861683526001909152812054909190610930908463ffffffff610bf216565b600160a060020a038086166000908152600160205260408082209390935590871681522054610965908463ffffffff610c0116565b600160a060020a03861660009081526001602052604090205561098e818463ffffffff610c0116565b600160a060020a03808716600081815260026020908152604080832033861684529091529081902093909355908616917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9086905190815260200160405180910390a3506001949350505050565b6003546000907501000000000000000000000000000000000000000000900460ff1615610a2857600080fd5b6000546a52b7d2dcc80cd2e400000090610a48908463ffffffff610bf216565b1115610a5357600080fd5b600054610a66908363ffffffff610bf216565b6000908155600160a060020a038416815260016020526040902054610a91908363ffffffff610bf216565b600160a060020a0384166000818152600160205260409081902092909255907f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968859084905190815260200160405180910390a282600160a060020a031630600160a060020a03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8460405190815260200160405180910390a350600192915050565b600160a060020a033316600090815260016020526040812054610b5c908363ffffffff610c0116565b600160a060020a033381166000908152600160205260408082209390935590851681522054610b91908363ffffffff610bf216565b600160a060020a0380851660008181526001602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a350600192915050565b60008282018381101561043957fe5b600082821115610c0d57fe5b509003905600a165627a7a7230582070bc61013f2ba9be3f25f52784228212304e7d690f1f222e0fe5d803e213e6bc0029",
                "[\n" +
                        "    {\n" +
                        "        \"constant\": true,\n" +
                        "        \"inputs\": [],\n" +
                        "        \"name\": \"name\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"\",\n" +
                        "                \"type\": \"string\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": true,\n" +
                        "        \"inputs\": [],\n" +
                        "        \"name\": \"symbol\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"\",\n" +
                        "                \"type\": \"string\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": true,\n" +
                        "        \"inputs\": [],\n" +
                        "        \"name\": \"decimals\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"\",\n" +
                        "                \"type\": \"uint8\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": true,\n" +
                        "        \"inputs\": [],\n" +
                        "        \"name\": \"totalSupply\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": true,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"_owner\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"balanceOf\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"balance\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": false,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"_to\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_value\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"transfer\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"success\",\n" +
                        "                \"type\": \"bool\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": false,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"_from\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_to\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_value\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"transferFrom\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"success\",\n" +
                        "                \"type\": \"bool\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": false,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"_spender\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_value\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"approve\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"success\",\n" +
                        "                \"type\": \"bool\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": true,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"_owner\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_spender\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"allowance\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"remaining\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"anonymous\": false,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"indexed\": true,\n" +
                        "                \"name\": \"_from\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"indexed\": true,\n" +
                        "                \"name\": \"_to\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"indexed\": false,\n" +
                        "                \"name\": \"_value\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"Transfer\",\n" +
                        "        \"type\": \"event\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"anonymous\": false,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"indexed\": true,\n" +
                        "                \"name\": \"_owner\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"indexed\": true,\n" +
                        "                \"name\": \"_spender\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"indexed\": false,\n" +
                        "                \"name\": \"_value\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"Approval\",\n" +
                        "        \"type\": \"event\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"_initialAmount\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_tokenName\",\n" +
                        "                \"type\": \"string\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_decimalUnits\",\n" +
                        "                \"type\": \"uint8\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_tokenSymbol\",\n" +
                        "                \"type\": \"string\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"constructor\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": false,\n" +
                        "        \"inputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"_spender\",\n" +
                        "                \"type\": \"address\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_value\",\n" +
                        "                \"type\": \"uint256\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"_extraData\",\n" +
                        "                \"type\": \"bytes\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"name\": \"approveAndCall\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"success\",\n" +
                        "                \"type\": \"bool\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"constant\": true,\n" +
                        "        \"inputs\": [],\n" +
                        "        \"name\": \"version\",\n" +
                        "        \"outputs\": [\n" +
                        "            {\n" +
                        "                \"name\": \"\",\n" +
                        "                \"type\": \"string\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"payable\": false,\n" +
                        "        \"type\": \"function\"\n" +
                        "    }\n" +
                        "]");
    }
}