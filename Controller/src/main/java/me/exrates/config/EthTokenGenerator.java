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
                "\t\treturn new EthTokenServiceImpl(tokensList, \"" + ticker.toUpperCase() + "\"," + "\"" + ticker.toUpperCase() + "\", " + isERC20 +", "+ "ExConvert.Unit."+ enumValueForDecimals + ");\n" +
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

        String s = "public class "+ticker.toUpperCase()+" extends Contract";
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
        generate("NPXS", "Pundi X Token",
                "0xa15c7ebe1f07caf6bff097d8a589fb8ac49ae5b3", true, 18,
                "60606040526000600360146101000a81548160ff0219169083151502179055506000600360156101000a81548160ff021916908315150217905550341561004557600080fd5b33600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506121f2806100956000396000f300606060405260043610610133576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806305d2035b1461013857806306fdde0314610165578063095ea7b3146101f357806318160ddd1461024d57806323b872dd14610276578063313ce567146102ef5780633f4ba83a1461031e5780634000aea01461033357806340c10f19146103b857806342966c68146104125780635c975abb14610435578063620346c614610462578063661884631461048757806370a08231146104e15780637ab216131461052e5780637d64bcb41461055b5780638456cb59146105885780638da5cb5b1461059d57806395d89b41146105f2578063a9059cbb14610680578063d73dd623146106da578063dd62ed3e14610734578063f2fde38b146107a0575b600080fd5b341561014357600080fd5b61014b6107d9565b604051808215151515815260200191505060405180910390f35b341561017057600080fd5b6101786107ec565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101b857808201518184015260208101905061019d565b50505050905090810190601f1680156101e55780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101fe57600080fd5b610233600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610825565b604051808215151515815260200191505060405180910390f35b341561025857600080fd5b610260610855565b6040518082815260200191505060405180910390f35b341561028157600080fd5b6102d5600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803590602001909190505061085b565b604051808215151515815260200191505060405180910390f35b34156102fa57600080fd5b610302610892565b604051808260ff1660ff16815260200191505060405180910390f35b341561032957600080fd5b610331610897565b005b341561033e57600080fd5b6103b6600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610918565b005b34156103c357600080fd5b6103f8600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610c74565b604051808215151515815260200191505060405180910390f35b341561041d57600080fd5b6104336004808035906020019091905050610d05565b005b341561044057600080fd5b610448610d11565b604051808215151515815260200191505060405180910390f35b341561046d57600080fd5b61048560048080351515906020019091905050610d24565b005b341561049257600080fd5b6104c7600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610d7e565b604051808215151515815260200191505060405180910390f35b34156104ec57600080fd5b610518600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610dae565b6040518082815260200191505060405180910390f35b341561053957600080fd5b610541610df7565b604051808215151515815260200191505060405180910390f35b341561056657600080fd5b61056e610e4b565b604051808215151515815260200191505060405180910390f35b341561059357600080fd5b61059b610ef7565b005b34156105a857600080fd5b6105b0610f79565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34156105fd57600080fd5b610605610f9f565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561064557808201518184015260208101905061062a565b50505050905090810190601f1680156106725780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561068b57600080fd5b6106c0600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610fd8565b604051808215151515815260200191505060405180910390f35b34156106e557600080fd5b61071a600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050611066565b604051808215151515815260200191505060405180910390f35b341561073f57600080fd5b61078a600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611096565b6040518082815260200191505060405180910390f35b34156107ab57600080fd5b6107d7600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190505061111d565b005b600360149054906101000a900460ff1681565b6040805190810160405280600d81526020017f50756e6469205820546f6b656e0000000000000000000000000000000000000081525081565b6000600360159054906101000a900460ff1615151561084357600080fd5b61084d83836111f9565b905092915050565b60005481565b600080600360159054906101000a900460ff1615151561087a57600080fd5b610885858585611380565b9050809150509392505050565b601281565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156108f357600080fd5b600360159054906101000a900460ff16151561090e57600080fd5b6109166113b2565b565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415151561095457600080fd5b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205482111515156109a257600080fd5b6109f482600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461147290919063ffffffff16565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610a8982600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461148b90919063ffffffff16565b600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff1663c0ee0b8a3384846000604051602001526040518463ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200183815260200180602001828103825283818151815260200191508051906020019080838360005b83811015610b97578082015181840152602081019050610b7c565b50505050905090810190601f168015610bc45780820380516001836020036101000a031916815260200191505b50945050505050602060405180830381600087803b1515610be457600080fd5b6102c65a03f11515610bf557600080fd5b505050604051805190501515610c0a57600080fd5b8273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3505050565b600080600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610cd357600080fd5b600360149054906101000a900460ff16151515610cef57600080fd5b610cf984846114a9565b90508091505092915050565b610d0e8161167b565b50565b600360159054906101000a900460ff1681565b80600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555050565b6000600360159054906101000a900460ff16151515610d9c57600080fd5b610da68383611786565b905092915050565b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905090565b6000600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610ea957600080fd5b6001600360146101000a81548160ff0219169083151502179055507fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0860405160405180910390a16001905090565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610f5357600080fd5b600360159054906101000a900460ff16151515610f6f57600080fd5b610f77611a17565b565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6040805190810160405280600481526020017f4e5058530000000000000000000000000000000000000000000000000000000081525081565b600080600360159054906101000a900460ff16151515610ff757600080fd5b600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151561105057600080fd5b61105a8484611ad8565b90508091505092915050565b6000600360159054906101000a900460ff1615151561108457600080fd5b61108e8383611b08565b905092915050565b6000600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561117957600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16141515156111b557600080fd5b80600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60008082148061128557506000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054145b151561129057600080fd5b81600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b6000600360159054906101000a900460ff1615151561139e57600080fd5b6113a9848484611d04565b90509392505050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561140e57600080fd5b600360159054906101000a900460ff16151561142957600080fd5b6000600360156101000a81548160ff0219169083151502179055507f7805862f689e2f13df9f062ff482ad3ad112aca9e0847911ed832e158c525b3360405160405180910390a1565b600082821115151561148057fe5b818303905092915050565b600080828401905083811015151561149f57fe5b8091505092915050565b6000600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561150757600080fd5b600360149054906101000a900460ff1615151561152357600080fd5b6115388260005461148b90919063ffffffff16565b60008190555061159082600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461148b90919063ffffffff16565b600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff167f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d4121396885836040518082815260200191505060405180910390a28273ffffffffffffffffffffffffffffffffffffffff1660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a36001905092915050565b6000808211151561168b57600080fd5b3390506116e082600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461147290919063ffffffff16565b600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506117388260005461147290919063ffffffff16565b600081905550818173ffffffffffffffffffffffffffffffffffffffff167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca560405160405180910390a35050565b600080600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905080831115611897576000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555061192b565b6118aa838261147290919063ffffffff16565b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b8373ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a3600191505092915050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611a7357600080fd5b600360159054906101000a900460ff16151515611a8f57600080fd5b6001600360156101000a81548160ff0219169083151502179055507f6985a02210a168e66602d3235cb6db0e70f92b3ba4d376a33c0f3d9434bff62560405160405180910390a1565b6000600360159054906101000a900460ff16151515611af657600080fd5b611b008383611ff0565b905092915050565b6000611b9982600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461148b90919063ffffffff16565b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a36001905092915050565b600080600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff1614151515611d4357600080fd5b600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050611e1483600160008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461147290919063ffffffff16565b600160008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550611ea983600160008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461148b90919063ffffffff16565b600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550611eff838261147290919063ffffffff16565b600260008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508373ffffffffffffffffffffffffffffffffffffffff168573ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef856040518082815260200191505060405180910390a360019150509392505050565b60008073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415151561202d57600080fd5b61207f82600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461147290919063ffffffff16565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555061211482600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461148b90919063ffffffff16565b600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a360019050929150505600a165627a7a72305820793c3b24085efa94c65c4b57e38fddfc1cc23d62dd2aceb042b5f127706e4d3e0029",
                "[{\"constant\":true,\"inputs\":[],\"name\":\"mintingFinished\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"unpause\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_recipient\",\"type\":\"address\"},{\"name\":\"_amount\",\"type\":\"uint256\"},{\"name\":\"_data\",\"type\":\"bytes\"}],\"name\":\"transferAndCall\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_amount\",\"type\":\"uint256\"}],\"name\":\"mint\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"burn\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"paused\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"stop\",\"type\":\"bool\"}],\"name\":\"setStopReceive\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_subtractedValue\",\"type\":\"uint256\"}],\"name\":\"decreaseApproval\",\"outputs\":[{\"name\":\"success\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"balance\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getStopReceive\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"finishMinting\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"pause\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_addedValue\",\"type\":\"uint256\"}],\"name\":\"increaseApproval\",\"outputs\":[{\"name\":\"success\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"remaining\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"newOwner\",\"type\":\"address\"}],\"name\":\"transferOwnership\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[],\"name\":\"Pause\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[],\"name\":\"Unpause\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"burner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Burn\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Mint\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[],\"name\":\"MintFinished\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"spender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}]");
    }
}