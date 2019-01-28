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
        generate("ELT", "Ethereum Lendo Token",
                "0x45d0bdfdfbfd62e14b64b0ea67dc6eac75f95d4d", true, 8,
                "608060405234801561001057600080fd5b5060405161094e38038061094e8339810180604052810190808051906020019092919080518201929190602001805182019291906020018051906020019092919080519060200190929190805190602001909291908051906020019092919050505080336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050856009908051906020019061010b929190610235565b5084600a9080519060200190610122929190610235565b5082600281905550600254600a0a8402600381905550600354600460008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef6003546040518082815260200191505060405180910390a3816008819055506000600760006101000a81548160ff021916908315150217905550505050505050506102da565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061027657805160ff19168380011785556102a4565b828001600101855582156102a4579182015b828111156102a3578251825591602001919060010190610288565b5b5090506102b191906102b5565b5090565b6102d791905b808211156102d35760008160009055506001016102bb565b5090565b90565b610665806102e96000396000f300608060405260043610610099576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde031461012d57806318160ddd146101bd578063313ce567146101e85780636aede5cd146102135780636ee31a181461023e5780638da5cb5b1461028157806395d89b41146102d85780639613252114610368578063a842f0f214610397575b3480156100a557600080fd5b5060006060600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1691506000368080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505090508051602082016000808383875af46040518160008114610128573d6000833e3d82f35b600082fd5b34801561013957600080fd5b506101426103ee565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610182578082015181840152602081019050610167565b50505050905090810190601f1680156101af5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101c957600080fd5b506101d261048c565b6040518082815260200191505060405180910390f35b3480156101f457600080fd5b506101fd610492565b6040518082815260200191505060405180910390f35b34801561021f57600080fd5b50610228610498565b6040518082815260200191505060405180910390f35b34801561024a57600080fd5b5061027f600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061049e565b005b34801561028d57600080fd5b5061029661053d565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156102e457600080fd5b506102ed610562565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561032d578082015181840152602081019050610312565b50505050905090810190601f16801561035a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561037457600080fd5b5061037d610600565b604051808215151515815260200191505060405180910390f35b3480156103a357600080fd5b506103ac610613565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b60098054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104845780601f1061045957610100808354040283529160200191610484565b820191906000526020600020905b81548152906001019060200180831161046757829003601f168201915b505050505081565b60035481565b60025481565b60085481565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156104f957600080fd5b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600a8054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105f85780601f106105cd576101008083540402835291602001916105f8565b820191906000526020600020905b8154815290600101906020018083116105db57829003601f168201915b505050505081565b600760009054906101000a900460ff1681565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16815600a165627a7a7230582051014b418e497c8997ad794525f5fa29f70f34c415143a38d86be041ca77bd2600290000000000000000000000002f659480f13cb89ffac70160abe5d431862eb23d00000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001200000000000000000000000000000000000000000000000000000000047868c000000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000005ad46638000000000000000000000000a08c496c6e57976e6ca84d30e94cb9a20f09a59d0000000000000000000000000000000000000000000000000000000000000014457468657265756d204c656e646f20546f6b656e0000000000000000000000000000000000000000000000000000000000000000000000000000000000000003454c540000000000000000000000000000000000000000000000000000000000",
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