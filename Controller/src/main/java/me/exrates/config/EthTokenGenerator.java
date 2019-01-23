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
        generate("HOT", "HoloToken",
                "0x6c6ee5e31d828de241282b9606c8e98ea48526e2", true, 18,
                "60606040526004805460ff1916905560008054600160a060020a033316600160a060020a0319909116179055610e348061003a6000396000f3006060604052600436106101325763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305d2035b811461013757806306fdde031461015e57806307546172146101e8578063095ea7b31461021757806311367b261461023957806318160ddd1461024c57806323b872dd1461027157806327e235e314610299578063313ce567146102b857806340c10f19146102e157806342966c68146103035780635c6581651461031b57806366188463146103405780636a7301b81461036257806370a08231146103815780637d64bcb4146103a05780638da5cb5b146103b357806395d89b41146103c6578063a9059cbb146103d9578063d73dd623146103fb578063dd62ed3e1461041d578063f2fde38b14610442578063fca3b5aa14610461575b600080fd5b341561014257600080fd5b61014a610480565b604051901515815260200160405180910390f35b341561016957600080fd5b610171610489565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101ad578082015183820152602001610195565b50505050905090810190601f1680156101da5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101f357600080fd5b6101fb6104c0565b604051600160a060020a03909116815260200160405180910390f35b341561022257600080fd5b61014a600160a060020a03600435166024356104cf565b341561024457600080fd5b6101fb61054c565b341561025757600080fd5b61025f610560565b60405190815260200160405180910390f35b341561027c57600080fd5b61014a600160a060020a0360043581169060243516604435610566565b34156102a457600080fd5b61025f600160a060020a03600435166106fa565b34156102c357600080fd5b6102cb61070c565b60405160ff909116815260200160405180910390f35b34156102ec57600080fd5b61014a600160a060020a0360043516602435610711565b341561030e57600080fd5b61031960043561080f565b005b341561032657600080fd5b61025f600160a060020a03600435811690602435166108fa565b341561034b57600080fd5b61014a600160a060020a0360043516602435610917565b341561036d57600080fd5b610319600160a060020a0360043516610a11565b341561038c57600080fd5b61025f600160a060020a0360043516610a61565b34156103ab57600080fd5b61014a610a7c565b34156103be57600080fd5b6101fb610ad9565b34156103d157600080fd5b610171610ae8565b34156103e457600080fd5b61014a600160a060020a0360043516602435610b1f565b341561040657600080fd5b61014a600160a060020a0360043516602435610c2c565b341561042857600080fd5b61025f600160a060020a0360043581169060243516610cd0565b341561044d57600080fd5b610319600160a060020a0360043516610cfb565b341561046c57600080fd5b610319600160a060020a0360043516610d96565b60045460ff1681565b60408051908101604052600981527f486f6c6f546f6b656e0000000000000000000000000000000000000000000000602082015281565b600554600160a060020a031681565b60045460009060ff1615156104e357600080fd5b600160a060020a03338116600081815260036020908152604080832094881680845294909152908190208590557f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a350600192915050565b6004546101009004600160a060020a031681565b60015481565b60045460009060ff16151561057a57600080fd5b600160a060020a038316151561058f57600080fd5b600160a060020a0384166000908152600260205260409020548211156105b457600080fd5b600160a060020a03808516600090815260036020908152604080832033909416835292905220548211156105e757600080fd5b600160a060020a038416600090815260026020526040902054610610908363ffffffff610de016565b600160a060020a038086166000908152600260205260408082209390935590851681522054610645908363ffffffff610df216565b600160a060020a0380851660009081526002602090815260408083209490945587831682526003815283822033909316825291909152205461068d908363ffffffff610de016565b600160a060020a03808616600081815260036020908152604080832033861684529091529081902093909355908516917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060019392505050565b60026020526000908152604090205481565b601281565b60055460009033600160a060020a0390811691161461072f57600080fd5b60045460ff161561073f57600080fd5b600160a060020a0383166000908152600260205260409020548281011161076557600080fd5b6001548281011161077557600080fd5b600154610788908363ffffffff610df216565b600155600160a060020a0383166000908152600260205260409020546107b4908363ffffffff610df216565b600160a060020a0384166000818152600260205260409081902092909255907f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968859084905190815260200160405180910390a250600192915050565b60045433600160a060020a03908116610100909204161461082f57600080fd5b6004546101009004600160a060020a031660009081526002602052604090205481901080159061085f5750600081115b151561086a57600080fd5b6004546101009004600160a060020a03166000908152600260205260409020546108949082610de0565b6004546101009004600160a060020a03166000908152600260205260409020556001546108c19082610de0565b6001557fb90306ad06b2a6ff86ddc9327db583062895ef6540e62dc50add009db5b356eb8160405190815260200160405180910390a150565b600360209081526000928352604080842090915290825290205481565b600160a060020a0333811660009081526003602090815260408083209386168352929052908120548083111561097457600160a060020a0333811660009081526003602090815260408083209388168352929052908120556109ab565b610984818463ffffffff610de016565b600160a060020a033381166000908152600360209081526040808320938916835292905220555b600160a060020a0333811660008181526003602090815260408083209489168084529490915290819020547f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925915190815260200160405180910390a35060019392505050565b60005433600160a060020a03908116911614610a2c57600080fd5b60048054600160a060020a039092166101000274ffffffffffffffffffffffffffffffffffffffff0019909216919091179055565b600160a060020a031660009081526002602052604090205490565b60055460009033600160a060020a03908116911614610a9a57600080fd5b6004805460ff191660011790557fb828d9b5c78095deeeeff2eca2e5d4fe046ce3feb4c99702624a3fd384ad2dbc60405160405180910390a150600190565b600054600160a060020a031681565b60408051908101604052600381527f484f540000000000000000000000000000000000000000000000000000000000602082015281565b60045460009060ff161515610b3357600080fd5b600160a060020a0383161515610b4857600080fd5b600160a060020a033316600090815260026020526040902054821115610b6d57600080fd5b600160a060020a033316600090815260026020526040902054610b96908363ffffffff610de016565b600160a060020a033381166000908152600260205260408082209390935590851681522054610bcb908363ffffffff610df216565b600160a060020a0380851660008181526002602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a350600192915050565b600160a060020a033381166000908152600360209081526040808320938616835292905290812054610c64908363ffffffff610df216565b600160a060020a0333811660008181526003602090815260408083209489168084529490915290819020849055919290917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591905190815260200160405180910390a350600192915050565b600160a060020a03918216600090815260036020908152604080832093909416825291909152205490565b60005433600160a060020a03908116911614610d1657600080fd5b600160a060020a0381161515610d2b57600080fd5b600054600160a060020a0380831691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60005433600160a060020a03908116911614610db157600080fd5b6005805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600082821115610dec57fe5b50900390565b600082820183811015610e0157fe5b93925050505600a165627a7a723058204221a25d326558196a818e387d635875fd978d9c808705f736bb498658d4e7ab0029",
                "[{\"constant\":true,\"inputs\":[],\"name\":\"mintingFinished\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"minter\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"destroyer\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"balances\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_amount\",\"type\":\"uint256\"}],\"name\":\"mint\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_amount\",\"type\":\"uint256\"}],\"name\":\"burn\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"address\"}],\"name\":\"allowed\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_subtractedValue\",\"type\":\"uint256\"}],\"name\":\"decreaseApproval\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_destroyer\",\"type\":\"address\"}],\"name\":\"setDestroyer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"balance\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"finishMinting\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_addedValue\",\"type\":\"uint256\"}],\"name\":\"increaseApproval\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"newOwner\",\"type\":\"address\"}],\"name\":\"transferOwnership\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_minter\",\"type\":\"address\"}],\"name\":\"setMinter\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"spender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Mint\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[],\"name\":\"MintingFinished\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Burn\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"previousOwner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"newOwner\",\"type\":\"address\"}],\"name\":\"OwnershipTransferred\",\"type\":\"event\"}]");
    }
}*/
