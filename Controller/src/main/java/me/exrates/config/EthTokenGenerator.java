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



































    private static final String FILE_PATH_TO_BIN_ABI_FILES = "/Users/vlad.dziubak/crypto/eth/";
    private static final String FILE_PATH_TO_WRAPPERS = "/Users/vlad.dziubak/workspace/exrates/Service/src/main/java";
    private static final String WRAPPERS_PACKAGE = "me.exrates.service.ethereum.ethTokensWrappers";

    public static void main(String[] args) throws Exception {
        generate("ELC", "EconomicLeisureCoin",
                "0x2ab2ffaa942851922a50fd640893f5c42b82474e", false, 8,
                "0x6080604052600436106101275763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305fefda7811461012c57806306fdde0314610149578063095ea7b3146101d357806318160ddd1461020b57806323b872dd14610232578063313ce5671461025c57806342966c68146102875780634b7503341461029f57806370a08231146102b457806379c65068146102d557806379cc6790146102f95780638620410b1461031d5780638da5cb5b1461033257806395d89b4114610363578063a6f2ae3a14610378578063a9059cbb14610380578063b414d4b6146103a4578063cae9ca51146103c5578063dd62ed3e1461042e578063e4849b3214610455578063e724529c1461046d578063f2fde38b14610493575b600080fd5b34801561013857600080fd5b506101476004356024356104b4565b005b34801561015557600080fd5b5061015e6104d6565b6040805160208082528351818301528351919283929083019185019080838360005b83811015610198578181015183820152602001610180565b50505050905090810190601f1680156101c55780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101df57600080fd5b506101f7600160a060020a0360043516602435610563565b604080519115158252519081900360200190f35b34801561021757600080fd5b506102206105c9565b60408051918252519081900360200190f35b34801561023e57600080fd5b506101f7600160a060020a03600435811690602435166044356105cf565b34801561026857600080fd5b5061027161063e565b6040805160ff9092168252519081900360200190f35b34801561029357600080fd5b506101f7600435610647565b3480156102ab57600080fd5b506102206106bf565b3480156102c057600080fd5b50610220600160a060020a03600435166106c5565b3480156102e157600080fd5b50610147600160a060020a03600435166024356106d7565b34801561030557600080fd5b506101f7600160a060020a036004351660243561078d565b34801561032957600080fd5b5061022061085e565b34801561033e57600080fd5b50610347610864565b60408051600160a060020a039092168252519081900360200190f35b34801561036f57600080fd5b5061015e610873565b6101476108cb565b34801561038c57600080fd5b506101f7600160a060020a03600435166024356108eb565b3480156103b057600080fd5b506101f7600160a060020a0360043516610901565b3480156103d157600080fd5b50604080516020600460443581810135601f81018490048402850184019095528484526101f7948235600160a060020a03169460248035953695946064949201919081908401838280828437509497506109169650505050505050565b34801561043a57600080fd5b50610220600160a060020a0360043581169060243516610a2f565b34801561046157600080fd5b50610147600435610a4c565b34801561047957600080fd5b50610147600160a060020a03600435166024351515610aa0565b34801561049f57600080fd5b50610147600160a060020a0360043516610b1b565b600054600160a060020a031633146104cb57600080fd5b600791909155600855565b60018054604080516020600284861615610100026000190190941693909304601f8101849004840282018401909252818152929183018282801561055b5780601f106105305761010080835404028352916020019161055b565b820191906000526020600020905b81548152906001019060200180831161053e57829003601f168201915b505050505081565b336000818152600660209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b60045481565b600160a060020a03831660009081526006602090815260408083203384529091528120548211156105ff57600080fd5b600160a060020a0384166000908152600660209081526040808320338452909152902080548390039055610634848484610b61565b5060019392505050565b60035460ff1681565b3360009081526005602052604081205482111561066357600080fd5b3360008181526005602090815260409182902080548690039055600480548690039055815185815291517fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59281900390910190a2506001919050565b60075481565b60056020526000908152604090205481565b600054600160a060020a031633146106ee57600080fd5b600160a060020a03821660009081526005602090815260408083208054850190556004805485019055805184815290513093927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef928290030190a3604080518281529051600160a060020a0384169130917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9181900360200190a35050565b600160a060020a0382166000908152600560205260408120548211156107b257600080fd5b600160a060020a03831660009081526006602090815260408083203384529091529020548211156107e257600080fd5b600160a060020a0383166000818152600560209081526040808320805487900390556006825280832033845282529182902080548690039055600480548690039055815185815291517fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59281900390910190a250600192915050565b60085481565b600054600160a060020a031681565b6002805460408051602060018416156101000260001901909316849004601f8101849004840282018401909252818152929183018282801561055b5780601f106105305761010080835404028352916020019161055b565b6000600854348115156108da57fe5b0490506108e8303383610b61565b50565b60006108f8338484610b61565b50600192915050565b60096020526000908152604090205460ff1681565b6000836109238185610563565b15610a27576040517f8f4ffcb10000000000000000000000000000000000000000000000000000000081523360048201818152602483018790523060448401819052608060648501908152875160848601528751600160a060020a03871695638f4ffcb195948b94938b939192909160a490910190602085019080838360005b838110156109bb5781810151838201526020016109a3565b50505050905090810190601f1680156109e85780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b158015610a0a57600080fd5b505af1158015610a1e573d6000803e3d6000fd5b50505050600191505b509392505050565b600660209081526000928352604080842090915290825290205481565b6007543090820281311015610a6057600080fd5b610a6b333084610b61565b6007546040513391840280156108fc02916000818181858888f19350505050158015610a9b573d6000803e3d6000fd5b505050565b600054600160a060020a03163314610ab757600080fd5b600160a060020a038216600081815260096020908152604091829020805460ff191685151590811790915582519384529083015280517f48335238b4855f35377ed80f164e8c6f3c366e54ac00b96a6402d4a9814a03a59281900390910190a15050565b600054600160a060020a03163314610b3257600080fd5b6000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600160a060020a0382161515610b7657600080fd5b600160a060020a038316600090815260056020526040902054811115610b9b57600080fd5b600160a060020a0382166000908152600560205260409020548181011015610bc257600080fd5b600160a060020a03831660009081526009602052604090205460ff1615610be857600080fd5b600160a060020a03821660009081526009602052604090205460ff1615610c0e57600080fd5b600160a060020a03808416600081815260056020908152604080832080548790039055938616808352918490208054860190558351858152935191937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a35050505600a165627a7a7230582091328bd91712bdaf163c52792921f20e1992b21d3438c039cc3f7392241ebbdd0029",
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
