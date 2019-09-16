package me.exrates.dao.impl;

import config.DataComparisonTest;
import me.exrates.dao.InputOutputDao;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.vo.PaginationWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {InputOutputDaoImplTest.InnerConf.class})
public class InputOutputDaoImplTest extends DataComparisonTest {
    private final String TABLE_COMMISSION = "COMMISSION";
    private final String TABLE_CURRENCY = "CURRENCY";
    private final String TABLE_MERCHANT = "MERCHANT";
    private final String TABLE_REFILL_REQUEST = "REFILL_REQUEST";
    private final String TABLE_USER = "USER";
    private final String TABLE_WALLET = "WALLET";
    private final String TABLE_TRANSACTION = "TRANSACTION";
    private final String TABLE_WITHDRAW_REQUEST = "WITHDRAW_REQUEST";
    private final String TABLE_INVOICE_BANK = "INVOICE_BANK";
    private final String TABLE_OPERATION_TYPE = "OPERATION_TYPE";
    private final String TABLE_REFILL_REQUEST_ADDRESS = "REFILL_REQUEST_ADDRESS";
    private final String TABLE_REFILL_REQUEST_CONFIRMATION = "REFILL_REQUEST_CONFIRMATION";
    private final String TABLE_REFILL_REQUEST_PARAM = "REFILL_REQUEST_PARAM";

    @Autowired
    private InputOutputDao inputOutputDao;

    @Override
    protected void before() {
        try {
            truncateTables(
                    TABLE_COMMISSION,
                    TABLE_CURRENCY,
                    TABLE_MERCHANT,
                    TABLE_REFILL_REQUEST,
                    TABLE_USER,
                    TABLE_WALLET,
                    TABLE_TRANSACTION,
                    TABLE_WITHDRAW_REQUEST,
                    TABLE_INVOICE_BANK,
                    TABLE_OPERATION_TYPE,
                    TABLE_REFILL_REQUEST_ADDRESS,
                    TABLE_REFILL_REQUEST_CONFIRMATION,
                    TABLE_REFILL_REQUEST_PARAM
            );

            String step_1 = "INSERT INTO COMMISSION (id, operation_type, value, date, user_role) VALUES (6, 1, 0, '2017-01-27 16:00:33', 4);";

            String step_2 = "INSERT INTO CURRENCY (id, name, description, hidden, max_scale_for_refill, max_scale_for_withdraw,\n" +
                    "                      max_scale_for_transfer, process_type, scale)\n" +
                    "VALUES (1, 'RUB', 'Russian Ruble', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (2, 'USD', 'US Dollar', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (3, 'EUR', 'Euro', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (4, 'BTC', 'Bitcoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (5, 'LTC', 'Litecoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (6, 'EDRC', 'EDR-coin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (7, 'UAH', 'Ukrainian Hryvnia', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (8, 'CNY', 'Chinese Yuan Renminbi', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (9, 'EDC', 'E-DinarCoin', 0, 8, 3, 3, 'CRYPTO', 8),\n" +
                    "       (10, 'IDR', 'Indonesian Rupiah', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (11, 'THB', 'Thai Baht', 1, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (12, 'INR', 'Indian Rupee', 1, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (13, 'NGN', 'Nigerian Naira', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (14, 'ETH', 'Ethereum', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (17, 'VND', 'Vietnamese Dong', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (18, 'TRY', 'Turkish Lira', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (19, 'ETC', 'Ethereum Classic', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (20, 'AED', 'Emirati Dirham', 0, 2, 2, 2, 'FIAT', 2),\n" +
                    "       (21, 'DASH', 'Dash', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (22, 'XRP', 'Ripple', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (23, 'XLM', 'Stellar Lumens', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (24, 'XEM', 'NEM', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (25, 'ATB', 'ATB-coin', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (26, 'BCH-old', 'Bitcoin Cash Old', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (27, 'IOTA', 'IOTA', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (28, 'DOGE', 'Dogecoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (29, 'LSK', 'Lisk', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (30, 'XMR', 'Monero', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (31, 'EOS', 'EOS', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (32, 'REP', 'Augur', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (33, 'GNT', 'Golem', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (34, 'BTG', 'Bitcoin-gold', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (35, 'B2X', 'Segwit2x', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (36, 'BCD', 'Bitcoin-diamond', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (37, 'NEO', 'NEO', 0, 0, 0, 0, 'CRYPTO', 8),\n" +
                    "       (38, 'GAS', 'Gas', 0, 4, 4, 4, 'CRYPTO', 8),\n" +
                    "       (39, 'ZEC', 'Zcash', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (40, 'OMG', 'OmiseGO', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (41, 'WAVES', 'Waves', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (42, 'BCX', 'Bitcoin-X', 0, 4, 4, 8, 'CRYPTO', 8),\n" +
                    "       (43, 'ATL', 'ATLANT', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (44, 'BCA', 'BitcoinAtom', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (45, 'BNB', 'BinanceCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (47, 'SBTC', 'Super Bitcoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (48, 'OCC', 'Octoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (49, 'RNTB', 'BitRent', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (50, 'ETZ', 'Ether Zero', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (51, 'NIO', 'Autonio', 0, 0, 0, 0, 'CRYPTO', 8),\n" +
                    "       (52, 'GOS', 'GOSSAMER', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (53, 'BTCZ', 'BitcoinZ', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (54, 'BTW', 'BitWhite', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (55, 'BPTN', 'BPTN', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (56, 'QTUM', 'Qtum', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (57, 'BTCP', 'Bitcoin Private', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (58, 'TAXI', 'TAXI', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (59, 'LCC', 'LitecoinCash', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (60, 'GX', 'GameX', 1, 2, 2, 2, 'CRYPTO', 8),\n" +
                    "       (61, 'INK', 'Ink', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (62, 'DIM', 'DimCoin', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (63, 'NBTK', 'Nebeus Crypto Bank', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (64, 'NBC', 'Niobium', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (65, 'UCASH', 'U.CASH', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (66, 'PLC', 'Platincoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (67, 'NAC', 'Nami', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (68, 'ECHT', 'ECHAT TOKEN', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (69, 'DDX_old', 'DietBitcoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (70, 'DIT', 'Ditcoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (71, 'IDH', 'indaHash Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (72, 'SZC', 'ShopZCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (73, 'COBC', 'Com Bill Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (74, 'BTX', 'BitCore', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (75, 'BCI', 'Bitcoin Interest', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (76, 'LBTC', 'Lightning Bitcoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (77, 'BCS', 'BCShop.io', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (78, 'XBD', 'BitDollar', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (79, 'SLT', 'Smartlands', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (80, 'UQC', 'Uquid Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (81, 'INO', 'Ino Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (82, 'ORME', 'Ormeus Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (83, 'PROFIT', 'PROFIT', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (84, 'BEZ', 'Bezop Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (85, 'AMN', 'Amon', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (86, 'RISE', 'RiseVision', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (87, 'GET', 'Guaranteed Entrance', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (88, 'SIM', 'Simmitri', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (89, 'FLOT', 'Fire Lotto', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (90, 'ABTC', 'AML Bitcoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (91, 'VDG', 'Veri Doc Global', 0, 0, 0, 0, 'CRYPTO', 8),\n" +
                    "       (92, 'NPXSXEM', 'Pundix', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (93, 'DRONE', 'DEEP AERO', 0, 0, 0, 0, 'CRYPTO', 8),\n" +
                    "       (94, 'DGTX', 'Digitex Futures', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (95, 'NSR', 'NuShares', 1, 4, 4, 8, 'CRYPTO', 8),\n" +
                    "       (96, 'ARK', 'Ark', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (97, 'WDSC', 'WINDORSCOIN', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (98, 'FSBT', 'Forty Seven Bank Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (99, 'RTH', 'Rotharium', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (100, 'IPR', 'iPRONTO', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (101, 'CAS', 'Cashaa', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (102, 'SPC', 'Space Chain', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (103, 'SPD', 'SPINDLE', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (104, 'BBX', ' BBCashCoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (105, 'BEET', 'Beetlecoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (106, 'MTC', 'Medical Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (107, 'HLC', 'Halal Chain', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (108, 'DTRC', 'Datarius Credit', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (109, 'CLO', 'CALLISTO', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (110, 'TNR', 'Tonestra', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (111, 'CEEK', 'CEEK', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (112, 'NYC', 'New York Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (113, 'B2G', 'Bitcoiin2Gen', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (114, 'ENGT', 'Engagement Token', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (115, 'ARN', 'Aeron', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (116, 'ANY', 'AnyCoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (117, 'FGC', 'Fantasy Gold', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (118, 'TGAME', 'TGAME', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (119, 'HST', 'Decision Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (120, 'TAVITT', 'Tavittcoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (121, 'PTC', 'Perfectcoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (122, 'MTL', 'MTL', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (123, 'GOL', 'Goldiam', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (124, 'LEDU', 'Education', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (125, 'HSR', ' HShare', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (126, 'CNET', 'ContractNet', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (127, 'CEDEX', 'CEDEX', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (128, 'LUNES', 'Lunes', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (129, 'ADB', 'AdBank', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (130, 'CHE', 'Crypto Harbor Exchange', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (131, 'GST', 'Gamestars', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (132, 'BCL', 'BitcoinClean', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (133, 'BRECO', 'Bitcoinreco', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (134, 'ACT', 'ACHAIN', 0, 5, 5, 8, 'CRYPTO', 8),\n" +
                    "       (135, 'VEX', 'Vexanium', 0, 5, 5, 8, 'CRYPTO', 8),\n" +
                    "       (136, 'FTO', 'FuturoCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (137, 'UMT', 'Universal Mobile Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (138, 'SABR', 'SABR', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (139, 'EQL', 'Equalizer', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (140, 'MASP', 'MaspToken', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (141, 'VNT', 'Ventory', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (143, 'SKILL', 'SKILLCOIN', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (144, 'SAT', 'Social Activity Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (145, 'STOR', 'Self Storage Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (146, 'DACC', 'Decentralized Accessible Content Chain', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (147, 'QUiNT', 'QUiNTillion Burgh', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (148, 'TERN', 'Ternio', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (149, 'TTC', 'Tabs Tracking Chain', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (150, 'NTY', 'Nexty', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (151, 'BFG', 'Blocform Global', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (152, 'SUMO', 'Sumokoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (153, 'KGS', 'KGS', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (154, 'BRB', 'Breakbits', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (155, 'JET', 'Jetcoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (156, 'RIZ', 'RizWave', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (157, 'PAT', 'PATRON', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (158, 'KWATT', 'KWATT', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (159, 'eMTV', 'Multiversum', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (160, 'FPWR', 'FireToken', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (161, 'TUSD', 'TrueUSD', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (162, 'CRBT', 'CRUISEBIT', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (163, 'HIVE', 'Hive token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (164, 'TRX', 'Tron', 0, 6, 6, 8, 'CRYPTO', 8),\n" +
                    "       (165, 'HDR', 'Hedger', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (166, 'CMIT', 'CMITCOIN', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (167, 'RAC', 'RoboAdvisorCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (169, 'IQN', 'IQeon', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (170, 'ETI', 'Etherinc', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (171, 'GEX', 'GREENX', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (172, 'SIC', 'Swisscoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (173, 'IXE', 'IXTUS Edutainment', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (174, 'CLX', 'CryptoLux', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (175, 'PHI', 'PHI Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (176, 'NER', 'Nerves', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (177, 'MFTU', 'Mainstream for the Underground', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (178, 'RET', 'RealTract', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (179, 'CMK', 'MarketC', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (180, 'GIGC', 'GIG Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (181, 'DCR', 'Decred', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (182, 'SWM', 'Swarm Fund', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (183, 'BNC', 'Bionic', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (184, 'TIC', 'Thingschain', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (185, 'uDOO', 'uDOO', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (186, 'WTL', 'Welltrado token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (187, 'ADK', 'Aidos Kuneen', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (188, 'QRK', 'Quark', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (189, 'LPC', 'Lightpaycoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (190, 'TOA', 'TOAcoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (191, 'XFC', 'FootballCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (192, 'CRYP', 'Cryptic Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (193, 'DDX', 'DietBitcoin', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (194, 'MBC', 'MicroBitcoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (195, 'APL', 'Apollo', 0, 1, 0, 1, 'CRYPTO', 8),\n" +
                    "       (196, 'XAU', 'Au Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (197, 'KAZE', 'KAZECoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (198, 'STREAM', 'KazeSTREAM', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (199, 'TTP', 'Trent', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (200, 'USDC', 'USD Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (201, 'AUNIT', 'Aunit Coin', 0, 5, 5, 5, 'CRYPTO', 8),\n" +
                    "       (202, 'ABBC', 'ABBC Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (203, 'CBC', 'Cashbery Coin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (204, 'BCH', 'Bitcoin Cash', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (205, 'BSV', 'Bitcoin SV', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (207, 'Q', 'Quick', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (208, 'VAI', 'VIOLET', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (209, 'UNC', 'Unicom', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (210, 'MODL', 'MODULE', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (212, 'DIME', 'DimeCoin', 0, 5, 5, 5, 'CRYPTO', 8),\n" +
                    "       (213, 'MGX', 'MEGAX', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (214, 'MNC', 'MainCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (215, 'S4F', 'S4FE', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (216, 'ECTE', 'EurocoinToken', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (217, 'CTX', 'Centauri', 0, 5, 5, 5, 'CRYPTO', 8),\n" +
                    "       (218, 'EDT', 'ENDO Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (219, 'HT', 'Huobi token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (220, 'RIME', 'RimeCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (222, 'TCAT', 'TheCurrencyAnalytics', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (223, 'WaBi', 'Tael', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (224, 'POA', 'POA ERC20 on Foundation', 1, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (225, 'MCO', 'Crypto.com', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (226, 'ZIL', 'Zilliqa', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (227, 'MANA', 'Decentraland', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (228, 'EXO', 'Exosis', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (229, 'GRS', 'GroestlCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (230, 'KOD', 'KODCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (231, 'HCXP', 'HCXPay', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (232, 'QKC', 'QuarkChain Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (233, 'NPXS', 'Pundi X Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (234, 'HOT', 'HoloToken', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (235, 'ZRX', '0x', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (236, 'BAT', 'Basic Attention Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (237, 'RDN', 'Raiden Network Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (238, 'DIM.EUR', 'DIM.EUR', 0, 2, 2, 8, 'CRYPTO', 8),\n" +
                    "       (239, 'DIM.USD', 'DIM.USD', 0, 2, 2, 8, 'CRYPTO', 8),\n" +
                    "       (240, 'DIGIT', 'DIGIT coin', 0, 6, 6, 6, 'CRYPTO', 8),\n" +
                    "       (241, 'ELT', 'Ethereum Lendo Token', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (242, 'HNI', 'HUNI', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (243, 'WOLF', 'WolfCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (244, 'REN', 'Republic Protocol', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (245, 'MET', 'Metronome', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (246, 'USDT', 'Tether US', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (247, 'PLTC', 'PlatonCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (248, 'DIVI', 'Divi Project', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (249, 'BTT', 'BitTorrent', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (250, 'PPY', 'PeerPlays', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (251, 'VRBS', 'Viribustoken', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (252, 'ZUBE', 'Zuzubecoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (253, 'ELC', 'EconomicLeisureCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (254, 'CSC', 'CasinoCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (255, 'OWC', 'OduwaCoin', 0, 8, 8, 8, 'CRYPTO', 8),\n" +
                    "       (256, 'RBC', 'RobboCoach', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (257, 'REB', 'REBGLO', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (258, 'TTT', 'TTV', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (259, 'CREA', 'Crea', 0, 3, 3, 3, 'CRYPTO', null),\n" +
                    "       (260, 'AISI', 'Aisicoin', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (261, 'RVC', 'RenvaleCoin', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (262, 'BIO', 'BioCrypt ', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (264, 'ETA', 'Etheera ', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (265, 'KAT', 'Kambria ', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (266, 'VRA', 'VERA', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (267, 'BRC', 'BaerChain', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (268, 'CRON', 'CRON', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (269, 'GNY', 'GNY token', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (270, 'NOVA', 'Novachain', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (271, 'DARC', 'Konstellation', 0, 6, 6, 6, 'CRYPTO', null),\n" +
                    "       (272, 'TSL', 'TreasureSL', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (273, 'VOLLAR', 'V-Dimension', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (274, 'GAPI', 'GAPICOIN', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (275, 'FST', '1irstcoin', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (276, 'RVT', 'Renvale Token', 0, 8, 8, 8, 'CRYPTO', null),\n" +
                    "       (277, 'RWDS', 'Rewards4u', 0, 6, 6, 6, 'CRYPTO', null),\n" +
                    "       (278, 'LHT', 'LightHouse', 0, 5, 5, 5, 'CRYPTO', null);";

            String step_3 = "INSERT INTO MERCHANT (id, description, name, merchant_order, transaction_source_type_id, service_bean_name,\n" +
                    "                      process_type, tokens_parrent_id, needVerification)\n" +
                    "VALUES (1, 'Yandex kassa', 'Yandex kassa', 20, 2, 'yandexKassaServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (2, 'Perfect Money', 'Perfect Money', 1, 2, 'perfectMoneyServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (3, 'Bitcoin', 'Bitcoin', 21, 8, 'bitcoinServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (4, 'EDR Coin', 'EDR Coin', 22, 2, 'edrcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (5, 'Advcash Money', 'Advcash Money', 3, 2, 'advcashServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (6, 'Yandex.Money', 'Yandex.Money', 23, 2, 'yandexMoneyServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (7, 'LiqPay', 'LiqPay', 24, 2, 'liqpayServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (8, 'Nix Money', 'Nix Money', 4, 2, 'nixMoneyServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (9, 'Privat24', 'Privat24', 25, 2, 'privat24ServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (10, 'Interkassa', 'Interkassa', 26, 2, 'interkassaServiceImpl', 'MERCHANT', null, 1),\n" +
                    "       (12, 'Invoice', 'Invoice', 27, 7, 'invoiceServiceImpl', 'INVOICE', null, 0),\n" +
                    "       (13, 'E-DinarCoin', 'EDC', 28, 2, 'EDCServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (14, 'OkPay', 'OkPay', 2, 2, 'okPayServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (15, 'Payeer', 'Payeer', null, 2, 'payeerServiceImpl', 'MERCHANT', null, 0),\n" +
                    "       (16, 'Ethereum', 'Ethereum', null, 2, 'ethereumServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (17, 'Litecoin', 'Litecoin', null, 8, 'litecoinServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (18, 'Ethereum Classic', 'Ethereum Classic', null, 2, 'ethereumClassicServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (19, 'Dash', 'Dash', null, 8, 'dashServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (20, 'Ripple', 'Ripple', null, 2, 'rippleServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (21, 'Stellar', 'Stellar', null, 2, 'stellarServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (30, 'SimpleTransfer', 'SimpleTransfer', null, 2, 'transferSimpleServiceImpl', 'TRANSFER', null, 0),\n" +
                    "       (31, 'VoucherTransfer', 'VoucherTransfer', null, 2, 'transferVoucherServiceImpl', 'TRANSFER', null, 0),\n" +
                    "       (32, 'VoucherFreeTransfer', 'VoucherFreeTransfer', null, 2, 'transferVoucherFreeServiceImpl', 'TRANSFER', null,0),\n" +
                    "       (33, 'NEM', 'NEM', null, 2, 'nemServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (34, 'ATB-coin', 'ATB', null, 2, 'atbServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (35, 'Bitcoin Cash', 'Bitcoin Cash', null, 2, 'bitcoinCashServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (36, 'IOTA', 'IOTA', null, 2, 'iotaServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (37, 'Dogecoin', 'Dogecoin', null, 2, 'dogecoinServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (38, 'Lisk', 'Lisk', null, 2, 'liskServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (39, 'Monero', 'Monero', null, 2, 'moneroServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (40, 'EOS', 'EOS', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (41, 'REP', 'REP', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (42, 'Golem', 'Golem', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (43, 'BTG', 'BTG', null, 2, 'btgServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (44, 'B2X', 'B2X', null, 2, 'b2xServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (45, 'BCD', 'BCD', null, 2, 'bcdServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (46, 'NEO', 'NEO', null, 2, 'neoServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (47, 'GAS', 'GAS', null, 2, 'neoServiceImpl', 'CRYPTO', 46, 0),\n" +
                    "       (48, 'Zcash', 'Zcash', null, 2, 'zcashServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (49, 'OmiseGO', 'OmiseGO', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (50, 'Waves', 'Waves', null, 2, 'wavesServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (51, 'BCX', 'BCX', null, 2, 'bcxServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (52, 'ATLANT', 'ATLANT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (53, 'BitcoinAtom', 'BitcoinAtom', null, 2, 'bitcoinAtomServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (54, 'BinanceCoin', 'BinanceCoin', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (56, 'SBTC', 'SBTC', null, 2, 'sbtcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (57, 'Octoin', 'OCC', null, 2, 'occServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (58, 'BitRent', 'BitRent', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (59, 'ETZ', 'EtherZero', null, 2, 'etzServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (60, 'NIO', 'NIO', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (61, 'GOS', 'GOS', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (62, 'BitcoinZ', 'BTCZ', null, 2, 'btczServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (63, 'BitcoinWhite', 'BitcoinWhite', null, 2, 'btwServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (64, 'BPTN', 'BPTN', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (65, 'Qtum', 'Qtum', null, 2, 'qtumServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (66, 'BTCP', 'BTCP', null, 2, 'btcpServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (67, 'TAXI', 'TAXI', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (68, 'LitecoinCash', 'LCC', null, 2, 'lccServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (69, 'GameX', 'GameX', null, 2, 'wavesServiceImpl', 'CRYPTO', 50, 0),\n" +
                    "       (70, 'Ink', 'Ink', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (71, 'DimCoin', 'DimCoin', null, 2, 'nemServiceImpl', 'CRYPTO', 33, 0),\n" +
                    "       (72, 'Nebeus Crypto Bank', 'NBTK', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (73, 'NBC', 'NBC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (74, 'U.CASH', 'UCASH', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (75, 'PLC', 'PLC', null, 2, 'plcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (76, 'Nami', 'NAC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (77, 'ECHAT TOKEN', 'ECHT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (78, 'DietBitcoin', 'DDX_old', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (79, 'DIT', 'DIT', null, 2, 'ditcoinServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (80, 'indaHash Coin', 'IDH', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (81, 'ShopZCoin', 'SZC', null, 2, 'szcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (82, 'Com Bill Token', 'COBC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (83, 'BitCore', 'BTX', null, 2, 'btxServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (84, 'BCI', 'BCI', null, 2, 'bciServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (85, 'Lightning Bitcoin', 'LBTC', null, 2, 'lbtcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (86, 'BCShop.io', 'BCS', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (87, 'BitDollar', 'BitDollar', null, 2, 'bitdollarServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (88, 'Smartlands', 'SLT', null, 2, 'stellarServiceImpl', 'CRYPTO', 23, 0),\n" +
                    "       (89, 'Uquid Coin', 'UQC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (90, 'Ino Coin', 'INO', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (91, 'Ormeus Coin', 'ORME', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (92, 'PROFIT', 'PROFIT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (93, 'Bezop Coin', 'BEZ', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (94, 'Amon ', 'AMN', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (95, 'RiseVision', 'RiseVision', null, 2, 'riseServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (96, 'Guaranteed Entrance', 'GET', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (97, 'Simmitri', 'SIM', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (98, 'Fire Lotto', 'FLOT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (99, 'AML', 'AML', null, 2, 'amlServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (100, 'Veri Doc Global', 'VDG', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (101, 'Pundix', 'NPXSXEM', null, 2, 'nemServiceImpl', 'CRYPTO', 33, 0),\n" +
                    "       (102, 'DEEP AERO', 'DRONE', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (103, 'Digitex Futures', 'DGTX', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (104, 'NuShares', 'NuShares', null, 2, 'nsrServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (105, 'Ark', 'Ark', null, 2, 'arkServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (106, 'WINDORSCOIN', 'WDSC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (107, 'Forty Seven Bank Token', 'FSBT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (108, 'Rotharium', 'RTH', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (109, 'iPRONTO', 'IPR', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (110, 'Cashaa', 'CAS', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (111, 'Space Chain', 'SPC', null, 2, 'qtumServiceImpl', 'CRYPTO', 65, 0),\n" +
                    "       (112, 'SPINDLE', 'SPD', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (113, 'BBCashCoin', 'BBX', null, 2, 'bbccServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (114, 'Beetlecoin', 'BEET', null, 2, 'beetServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (115, 'Medical Token', 'MTC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (116, 'Halal Chain', 'HLC', null, 2, 'qtumServiceImpl', 'CRYPTO', 65, 0),\n" +
                    "       (117, 'Datarius Credit', 'DTRC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (118, 'CALLISTO', 'CLO', null, 2, 'cloServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (119, 'Tonestra', 'TNR', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (120, 'CEEK', 'CEEK', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (121, 'NYC', 'NYC', null, 2, 'nycoinServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (122, 'Bitcoiin2Gen', 'B2G', null, 2, 'b2gServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (123, 'Engagement Token', 'ENGT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (124, 'Aeron', 'ARN', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (125, 'AnyCoin', 'ANY', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (126, 'Fantasy Gold', 'FGC', null, 2, 'fgcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (127, 'TGAME', 'TGAME', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (128, 'Decision Token', 'HST', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (129, 'Tavittcoin', 'TAVITT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (130, 'Perfectcoin', 'Perfectcoin', null, 2, 'ptcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (131, 'Metal', 'MTL', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (132, 'Goldiam', 'GOL', null, 2, 'golServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (133, 'Education', 'LEDU', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (134, 'HShare', 'HSR', null, 2, 'hsrServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (135, 'ContractNet', 'CNET', null, 2, 'cnetServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (136, 'CEDEX', 'CEDEX', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (137, 'Lunes', 'LUNES', null, 2, 'lunesServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (138, 'AdBank', 'ADB', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (139, 'Crypto Harbor Exchange', 'CHE', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (140, 'Gamestars', 'GST', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (141, 'BitcoinClean', 'BitcoinClean', null, 2, 'bclServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (142, 'Bitcoinreco', 'BRECO', null, 2, 'brecoServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (143, 'ACHAIN', 'ACHAIN', null, 2, 'achainServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (144, 'Vexanium', 'VEX', null, 2, 'achainServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (145, 'FuturoCoin', 'FTO', null, 2, 'ftoServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (146, 'Universal Mobile Token', 'UMT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (147, 'SABR', 'SABR', null, 2, 'sabrServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (148, 'Equalizer', 'EQL', null, 2, 'eqlServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (149, 'MaspToken', 'MASP', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (150, 'Ventory', 'VNT', null, 2, 'stellarServiceImpl', 'CRYPTO', 23, 0),\n" +
                    "       (153, 'SKILLCOIN', 'SKILL', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (154, 'Social Activity Token', 'SAT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (155, 'Self Storage Coin', 'STOR', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (156, 'Decentralized Accessible Content Chain', 'DACC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (157, 'QUiNTillion Burgh', 'QUiNT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (158, 'Ternio', 'TERN', null, 2, 'stellarServiceImpl', 'CRYPTO', 23, 0),\n" +
                    "       (159, 'Tabs Tracking Chain', 'TTC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (160, 'Nexty', 'NTY', null, 2, 'ntyServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (161, 'Blocform Global', 'BFG', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (162, 'Sumokoin', 'SUMO', null, 2, 'sumoServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (163, 'KGS', 'KGS', null, 2, 'icoServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (164, 'Breakbits', 'BRB', null, 2, 'brbServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (165, 'Jetcoin', 'JET', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (166, 'RizWave', 'RIZ', null, 2, 'rizServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (167, 'PATRON', 'PAT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (168, 'KWATT', 'KWATT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (169, 'Multiversum', 'eMTV', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (170, 'FireToken', 'FPWR', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (171, 'TrueUSD', 'TUSD', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (172, 'CRUISEBIT', 'CRBT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (173, 'Hive token', 'HIVE', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (174, 'Tron', 'TRX', null, 2, 'tronServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (175, 'Hedger', 'HDR', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (176, 'CMITCOIN', 'CMIT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (177, 'RoboAdvisorCoin', 'RAC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (179, 'IQeon', 'IQN', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (180, 'Etherinc', 'ETI', null, 2, 'etherincServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (181, 'GREENX', 'GEX', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (182, 'Swisscoin', 'SIC', null, 2, 'sicServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (183, 'IXTUS Edutainment', 'IXE', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (184, 'CryptoLux', 'CLX', null, 2, 'clxServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (185, 'PHI Token', 'PHI', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (186, 'Nerves', 'NER', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (187, 'Mainstream for the Underground', 'MFTU', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (188, 'RealTract', 'RET', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (189, 'MarketC', 'CMK', null, 2, 'cmkServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (190, 'GIG Coin', 'GIGC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (191, 'Decred', 'DCR', null, 2, 'decredServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (192, 'Swarm Fund', 'SWM', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (193, 'Bionic', 'BNC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (194, 'Thingschain', 'TIC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (195, 'uDOO', 'uDOO', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (196, 'Welltrado token', 'WTL', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (197, 'Aidos Kuneen', 'ADK', null, 2, 'adkServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (198, 'Quark', 'QRK', null, 2, 'qrkServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (199, 'Lightpaycoin', 'LPC', null, 2, 'lpcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (200, 'TOAcoin', 'TOA', null, 2, 'TOAServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (201, 'FootballCoin', 'XFC', null, 2, 'xfcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (202, 'Cryptic Coin', 'CRYP', null, 2, 'crypServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (203, 'DietBitcoin', 'DDX', null, 2, 'ddxServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (204, 'MicroBitcoin', 'MBC', null, 2, 'mbcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (205, 'Apollo', 'APL', null, 2, 'apolloServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (206, 'Au Coin', 'XAU', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (207, 'KAZECoin', 'KAZE', null, 2, 'kazeServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (208, 'KazeSTREAM', 'STREAM', null, 2, 'kazeServiceImpl', 'CRYPTO', 207, 0),\n" +
                    "       (209, 'Trent', 'TTP', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (210, 'USD Coin', 'USDC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (211, 'Aunit Coin', 'AUNIT', null, 2, 'aunitServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (212, 'ABBC Coin', 'ABBC', null, 2, 'abbcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (213, 'Cashbery Coin', 'CBC', null, 2, 'cbcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (214, 'Bitcoin Cash', 'BCH', null, 2, 'bchServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (215, 'Bitcoin SV', 'BSV', null, 2, 'bsvServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (217, 'Quick', 'Q', null, 2, 'qServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (218, 'VIOLET', 'VAI', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (219, 'Unicom', 'UNC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (220, 'MODULE', 'MODL', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (222, 'DimeCoin', 'DIME', null, 2, 'dimeServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (223, 'QIWI', 'QIWI', null, 2, 'qiwiServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (224, 'MEGAX', 'MGX', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (225, 'MainCoin', 'MNC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (226, 'S4FE', 'S4F', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (227, 'EurocoinToken', 'ECTE', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (228, 'Centauri', 'CTX', null, 2, 'ctxServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (229, 'ENDO Token', 'EDT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (230, 'Huobi token', 'HT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (231, 'RimeCoin', 'RIME', null, 2, 'rimeServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (233, 'TheCurrencyAnalytics', 'TCAT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (234, 'Tael', 'WaBi', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (235, 'POA ERC20 on Foundation', 'POA', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (236, 'Crypto.com', 'MCO', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (237, 'Zilliqa', 'ZIL', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (238, 'Decentraland', 'MANA', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (239, 'Exosis', 'EXO', null, 2, 'exoServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (240, 'GroestlCoin', 'GRS', null, 2, 'grsServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (241, 'KODCoin', 'KOD', null, 2, 'kodServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (242, 'HCXPay', 'HCXP', null, 2, 'hcxpServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (243, 'QuarkChain Token', 'QKC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (244, 'Pundi X Token', 'NPXS', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (245, 'HoloToken', 'HOT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (246, '0x', 'ZRX', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (247, 'Basic Attention Token', 'BAT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (248, 'Raiden Network Token', 'RDN', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (249, 'DIM.EUR', 'DIM.EUR', null, 2, 'nemServiceImpl', 'CRYPTO', 33, 0),\n" +
                    "       (250, 'DIM.USD', 'DIM.USD', null, 2, 'nemServiceImpl', 'CRYPTO', 33, 0),\n" +
                    "       (251, 'DIGIT coin', 'DIGIT', null, 2, 'nemServiceImpl', 'CRYPTO', 33, 0),\n" +
                    "       (252, 'Ethereum Lendo Token', 'ELT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (253, 'HUNI', 'HNI', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (254, 'WolfCoin', 'WOLF', null, 2, 'wolfServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (255, 'Republic Protocol', 'REN', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (256, 'Metronome', 'MET', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (257, 'Tether US', 'USDT', null, 2, 'omniServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (258, 'PlatonCoin', 'PLTC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (259, 'Divicoin', 'DIVI', null, 2, 'diviServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (260, 'BitTorrent', 'BTT', null, 2, 'tronServiceImpl', 'CRYPTO', 174, 0),\n" +
                    "       (261, 'PeerPlays', 'PPY', null, 2, 'ppyServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (262, 'Viribustoken', 'VRBS', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (263, 'Zuzubecoin', 'ZUBE', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (264, 'OduwaCoin', 'OWC', null, 2, 'owcServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (265, 'EconomicLeisureCoin', 'ELC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (266, 'CasinoCoin', 'CSC', null, 2, 'casinoCoinServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (267, 'RobboCoach', 'RBC', null, 2, 'wavesServiceImpl', 'CRYPTO', 50, 0),\n" +
                    "       (268, 'TTV', 'TTT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (269, 'REBGLO', 'REB', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (271, 'Qubera', 'Qubera', null, 2, 'quberaServiceImpl', 'MERCHANT', null, 1),\n" +
                    "       (276, 'Crea', 'CREA', null, 2, 'creaServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (278, 'Aisicoin', 'AISI', null, 2, 'aisiServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (279, 'RenvaleCoin', 'RVC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (280, 'BioCrypt ', 'BIO', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (281, 'Etheera ', 'ETA', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (282, 'Kambria ', 'KAT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (283, 'VERA', 'VRA', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (284, 'BaerChain', 'BRC', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (285, 'CRONFoundation', 'CRON', null, 2, 'cronServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (286, 'GNY token', 'GNY', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (287, 'Novachain', 'NOVA', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (290, 'tesieo', 'IEO12', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (291, 'Konstellation', 'DARC', null, 2, 'nemServiceImpl', 'CRYPTO', 33, 0),\n" +
                    "       (292, 'exrates token 1234567890', 'EXTT', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (303, 'ICOTest', 'ICT', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (304, 'new test', 'NWT', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (305, 'TreasureSL', 'TSL', null, 2, 'tslServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (306, 'V-Dimension', 'VOLLAR', null, 2, 'vollarServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (307, 'GAPICOIN', 'GAPI', null, 2, 'gapiServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (308, '1irstcoin', 'FST', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (309, 'Renvale Token', 'RVT', null, 2, 'ethereumServiceImpl', 'CRYPTO', 16, 0),\n" +
                    "       (310, 'Rewards4u', 'RWDS', null, 2, 'nemServiceImpl', 'CRYPTO', 33, 0),\n" +
                    "       (311, 'LightHouse', 'LHT', null, 2, 'lightHouseServiceImpl', 'CRYPTO', null, 0),\n" +
                    "       (312, 'description', 'HBO', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (313, 'KYC test', 'KYC', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (314, 'BOM Test', 'BOM 2019', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (315, 'new scam', 'NWCCOI', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (316, 'ACDC', 'ACDC test', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (317, 'MMM Test', 'MMM', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (318, 'GOOD Test', 'Good Test', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (319, 'MAN Test', 'MAN MAN', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (320, ' GAP Test', 'Test GAP', null, 2, 'no_bean', 'CRYPTO', null, 0),\n" +
                    "       (321, 'UAA', 'UAA Test', null, 2, 'no_bean', 'CRYPTO', null, 0);";

            String step_4 = "INSERT INTO REFILL_REQUEST (id, amount, date_creation, status_id, status_modification_date, " +
                    "currency_id, user_id, commission_id, merchant_id, merchant_transaction_id, refill_request_param_id, " +
                    "refill_request_address_id, admin_holder_id, import_note, remark, inner_transfer_hash) " +
                    "VALUES " +
                    "(26, 0.000100000, '2016-03-29 19:24:50', 3, '2017-03-14 15:35:49', 6, 8, 6, 4, " +
                    "'9d19651a1f14b3193cd5c95e503e71645448053454d8954d1a1d3152fbb9e29a', null, 26, null, 'FROM PENDING_PAYMENT (MERCHANT) FOR provided0', null, null)," +
                    "(49, 0.000100000, '2016-03-31 06:57:36', 8, '2017-03-14 15:35:49', 6, 8, 6, 4, " +
                    "'f2cd2aa8c691e5d914ff34013d5ecd93e6116523056e8cd0bd1487364dd47fcb', null, 49, null, 'FROM PENDING_PAYMENT (MERCHANT) FOR provided0', null, null)," +
                    "(50, 0.000100000, '2016-03-31 06:57:49', 2, '2017-03-14 15:35:49', 6, 8, 6, 4, " +
                    "'e82691e974ea7f54078d3a297032b969a6beade7980eb4d8ea6488d525c33608', null, 50, null, 'FROM PENDING_PAYMENT (MERCHANT) FOR provided0', null, null)," +
                    "(53, 0.000100000, '2016-03-31 07:25:22', 4, '2017-03-14 15:35:49', 6, 8, 6, 4, " +
                    "'758703666a03beb3f47f33be515a86e6fae245ebbd5178f199ea85dda8fdab64', null, 53, null, 'FROM PENDING_PAYMENT (MERCHANT) FOR provided0', null, null)," +
                    "(54, 0.010000000, '2016-03-31 07:35:10', 4, '2017-03-14 15:35:49', 6, 8, 6, 4, " +
                    "'77008f16fc44d6c898b301bf3a2a04ea1eac8f6bd0975a13baf82efbedfe53ec', null, 54, null, 'FROM PENDING_PAYMENT (MERCHANT) FOR provided0', null, null)," +
                    "(324, 123.000000000, '2016-06-11 07:30:37', 4, '2017-03-14 15:35:49', 6, 8, 6, 4, " +
                    "'e40f32d0804958b901ad8dea4431a46b2b139e6d14d5ce1651be8a4ca0ddd65c', null, 324, null, 'FROM PENDING_PAYMENT (MERCHANT) FOR provided0', null, null)," +
                    "(325, 1234.000000000, '2016-06-11 07:31:23', 4, '2017-03-14 15:35:49', 6, 8, 6, 4, " +
                    "'a7617d2c21035e32f1f6117c1d7f2822b259c3478ae8e540f80de9c6eb7ce86e', null, 325, null, 'FROM PENDING_PAYMENT (MERCHANT) FOR provided0', null, null);";

            String step_5 = "INSERT INTO USER (id, pub_id, nickname, email, password, regdate, phone, finpassword, status, " +
                    "ipaddress, roleid, preferred_lang, avatar_path, tmp_poll_passed, login_pin, use2fa, " +
                    "`2fa_last_notify_date`, withdraw_pin, transfer_pin, temporary_banned, change_2fa_setting_pin, " +
                    "api_token_setting_pin, GA, kyc_verification_step, kyc_status, kyc_reference, country, firstName, " +
                    "lastName, birthDay) VALUES (13, 'b4e48ce4495d86e7fcc2', 'const', 'shvets.k@gmail.com', " +
                    "'$2a$10$q5YPNsi/DlP9xMXbNWFhdOOhVyKRfPC15lqiV2Y.FXhwmSamm6WK2', '2016-02-19 12:58:52', '', " +
                    "'$2a$10$kFmrNPeuzdGB3ZUUgfge2.99ikQWGTW.btHwkIBHfZhuyzsCZjYea', 2, null, 7, 'ru', null, 1, " +
                    "'$2a$10$gNUk.IS8qusXth776yWuq.EVItGFo0ioG3fH.ezJTord7nDmNVWXG', 0, '2018-09-23 00:00:00', null, null, " +
                    "0, null, null, '', 0, 'none', null, null, null, null, null);";

            String step_6 = "INSERT INTO TRANSACTION (id, user_wallet_id, company_wallet_id, amount, commission_amount, commission_id,\n" +
                    "                         operation_type_id, currency_id, merchant_id, datetime, provided, confirmation, order_id,\n" +
                    "                         status_id, status_modification_date, active_balance_before, reserved_balance_before,\n" +
                    "                         company_balance_before, company_commission_balance_before, source_type, source_id,\n" +
                    "                         provided_modification_date, description)\n" +
                    "VALUES (86182661, 870, 2, 2.8, 0, 25, 1, 2, null, '2018-08-02 19:20:21', 1, null, null, 1, '2018-08-02 19:20:21', 0, 0,\n" +
                    "        56059.978649808, 255382.470720122, 'ORDER', 14540649, null, 'OPENED::ACCEPT'),\n" +
                    "       (86290075, 870, 2, 35.55, 0, 17, 1, 2, null, '2018-08-09 20:05:24', 1, null, null, 1, '2018-08-09 20:05:24', 2.8,\n" +
                    "        0, 54538.588649808, 255435.825564139, 'USER_TRANSFER', 1284, null, null),\n" +
                    "       (86451605, 870, 2, 79.199901, 0, 25, 1, 2, null, '2018-08-20 08:30:20', 1, null, null, 1, '2018-08-20 08:30:20',\n" +
                    "        38.35, 0, 54031.228649808, 255510.445923745, 'ORDER', 15616996, null, 'OPENED::ACCEPT'),\n" +
                    "       (86485062, 870, 2, 32.20546, 0.06454, 8, 1, 2, null, '2018-08-22 05:23:30', 1, null, null, 1,\n" +
                    "        '2018-08-22 05:23:30', 117.549901, 0, 53479.788649808, 255524.891381644, 'ORDER', 15735473, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86485332, 870, 2, 10.405148, 0.020852, 8, 1, 2, null, '2018-08-22 05:43:25', 1, null, null, 1,\n" +
                    "        '2018-08-22 05:43:25', 149.755361, 0, 53479.788649808, 255525.148765664, 'ORDER', 15736471, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86485357, 870, 2, 21.657598, 0.043402, 8, 1, 2, null, '2018-08-22 05:43:35', 1, null, null, 1,\n" +
                    "        '2018-08-22 05:43:35', 160.160509, 0, 53479.788649808, 255525.23544884, 'ORDER', 15736477, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86485389, 870, 2, 6.497978, 0.013022, 8, 1, 2, null, '2018-08-22 05:43:40', 1, null, null, 1,\n" +
                    "        '2018-08-22 05:43:40', 181.818107, 0, 53479.788649808, 255525.405771306, 'ORDER', 15736483, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86487004, 870, 2, 11.398158, 0.022842, 8, 1, 2, null, '2018-08-22 06:51:49', 1, null, null, 1,\n" +
                    "        '2018-08-22 06:51:49', 188.316085, 0, 53479.788649808, 255525.456085676, 'ORDER', 15739638, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86487035, 870, 2, 38.094658, 0.076342, 8, 1, 2, null, '2018-08-22 06:51:58', 1, null, null, 1,\n" +
                    "        '2018-08-22 06:51:58', 199.714243, 0, 53479.788649808, 255525.54997148, 'ORDER', 15739658, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86487067, 870, 2, 39.981876, 0.080124, 8, 1, 2, null, '2018-08-22 06:52:03', 1, null, null, 1,\n" +
                    "        '2018-08-22 06:52:03', 237.808901, 0, 53479.788649808, 255525.844097474, 'ORDER', 15739674, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86487095, 870, 2, 41.662508, 0.083492, 8, 1, 2, null, '2018-08-22 06:52:09', 1, null, null, 1,\n" +
                    "        '2018-08-22 06:52:09', 277.790777, 0, 53479.788649808, 255526.149754585, 'ORDER', 15739694, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86487122, 870, 2, 23.234438, 0.046562, 8, 1, 2, null, '2018-08-22 06:52:15', 1, null, null, 1,\n" +
                    "        '2018-08-22 06:52:15', 319.453285, 0, 53479.788649808, 255526.468261664, 'ORDER', 15739708, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86487219, 870, 2, 94.81, 0.19, 8, 1, 2, null, '2018-08-22 06:56:25', 1, null, null, 1, '2018-08-22 06:56:25',\n" +
                    "        342.687723, 0, 53479.788649808, 255526.960289677, 'ORDER', 15739844, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86487230, 870, 2, 0.92814, 0.00186, 8, 1, 2, null, '2018-08-22 06:57:30', 1, null, null, 1,\n" +
                    "        '2018-08-22 06:57:30', 437.497723, 0, 53479.788649808, 255527.288989677, 'ORDER', 15739848, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86513458, 870, 2, 0.480038, 0.000962, 8, 1, 2, null, '2018-08-23 17:32:59', 1, null, null, 1,\n" +
                    "        '2018-08-23 17:32:59', 438.425863, 0, 53410.788649808, 255531.9965468, 'ORDER', 15739849, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86554367, 870, 2, 121.756, 0.244, 8, 1, 2, null, '2018-08-25 12:04:24', 1, null, null, 1, '2018-08-25 12:04:24',\n" +
                    "        438.905901, 0, 53046.918649808, 255541.966891766, 'ORDER', 15941408, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86554687, 870, 2, 0.205783227, 0.000412391, 8, 1, 2, null, '2018-08-25 12:09:39', 1, null, null, 1,\n" +
                    "        '2018-08-25 12:09:39', 560.661901, 0, 53046.918649808, 255542.406091766, 'ORDER', 15941532, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (87511025, 870, 2, 13.446702185, 0.026947299, 8, 1, 2, null, '2018-09-03 15:33:45', 1, null, null, 1,\n" +
                    "        '2018-09-03 15:33:45', 560.867684227, 0, 52593.118649808, 255574.059205043, 'ORDER', 16905030, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (87709264, 870, 2, 128.859918588, 0.25823631, 8, 1, 2, null, '2018-09-04 16:19:42', 1, null, null, 1,\n" +
                    "        '2018-09-04 16:19:42', 574.314386412, 0, 52676.638649808, 255583.324480427, 'ORDER', 16905031, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86160115, 1892, 4, 0.010188717, 0, 25, 1, 4, null, '2018-08-01 13:46:08', 1, null, null, 1,\n" +
                    "        '2018-08-01 13:46:08', 0, 0, 101.562540487, 706.702720001, 'ORDER', 14461828, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160121, 1892, 4, 0.0040185, 0, 25, 1, 4, null, '2018-08-01 13:46:09', 1, null, null, 1, '2018-08-01 13:46:09',\n" +
                    "        0.010188717, 0, 101.562540487, 706.702740378, 'ORDER', 14456225, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160129, 1892, 4, 0.00920542, 0, 25, 1, 4, null, '2018-08-01 13:46:09', 1, null, null, 1,\n" +
                    "        '2018-08-01 13:46:09', 0.014207217, 0, 101.562540487, 706.702747048, 'ORDER', 14460312, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160591, 1892, 4, 0.000005238, 0, 25, 1, 4, null, '2018-08-01 13:51:39', 1, null, null, 1,\n" +
                    "        '2018-08-01 13:51:39', 0.023412637, 0, 101.562540487, 706.702766877, 'ORDER', 14441576, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160597, 1892, 4, 0.00000522, 0, 25, 1, 4, null, '2018-08-01 13:51:39', 1, null, null, 1,\n" +
                    "        '2018-08-01 13:51:39', 0.023417875, 0, 101.562540487, 706.702766887, 'ORDER', 14441571, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160603, 1892, 4, 0.000003444, 0, 25, 1, 4, null, '2018-08-01 13:51:39', 1, null, null, 1,\n" +
                    "        '2018-08-01 13:51:39', 0.023423095, 0, 101.562540487, 706.702766897, 'ORDER', 14441570, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160609, 1892, 4, 0.0000043, 0, 25, 1, 4, null, '2018-08-01 13:51:39', 1, null, null, 1, '2018-08-01 13:51:39',\n" +
                    "        0.023426539, 0, 101.562540487, 706.702766904, 'ORDER', 14441563, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160615, 1892, 4, 0.004870668, 0, 25, 1, 4, null, '2018-08-01 13:51:39', 1, null, null, 1,\n" +
                    "        '2018-08-01 13:51:39', 0.023430839, 0, 101.562540487, 706.702766913, 'ORDER', 14404965, null, 'OPENED::ACCEPT'),\n" +
                    "       (86160624, 1892, 4, 0.050361482, 0, 25, 1, 4, null, '2018-08-01 13:51:39', 1, null, null, 1,\n" +
                    "        '2018-08-01 13:51:39', 0.028301507, 0, 101.562540487, 706.702776654, 'ORDER', 14465261, null, 'OPENED::ACCEPT'),\n" +
                    "       (86161833, 1892, 4, 0.007549218, 0.000015129, 8, 1, 4, null, '2018-08-01 15:09:10', 1, null, null, 1,\n" +
                    "        '2018-08-01 15:09:10', 0.078662989, 0, 101.250808677, 706.711472313, 'ORDER', 14468634, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86164228, 1892, 4, 0.005712552, 0.000011448, 8, 1, 4, null, '2018-08-01 18:30:09', 1, null, null, 1,\n" +
                    "        '2018-08-01 18:30:09', 0.086212207, 0, 100.446076967, 706.718013352, 'ORDER', 14477197, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86173039, 1892, 4, 0.001033021, 0, 25, 1, 4, null, '2018-08-02 06:23:55', 1, null, null, 1,\n" +
                    "        '2018-08-02 06:23:55', 0.091924759, 0, 100.456918587, 706.723687782, 'ORDER', 14498842, null, 'OPENED::ACCEPT'),\n" +
                    "       (86173048, 1892, 4, 0.007724079, 0, 25, 1, 4, null, '2018-08-02 06:23:55', 1, null, null, 1,\n" +
                    "        '2018-08-02 06:23:55', 0.09295778, 0, 100.456918587, 706.723689848, 'ORDER', 14507426, null, 'OPENED::ACCEPT'),\n" +
                    "       (86173086, 1892, 4, 0.0006375, 0, 25, 1, 4, null, '2018-08-02 06:25:30', 1, null, null, 1, '2018-08-02 06:25:30',\n" +
                    "        0.100681859, 0, 100.456918587, 706.723727052, 'ORDER', 14507450, null, 'OPENED::ACCEPT'),\n" +
                    "       (86173366, 1892, 4, 0.005004243, 0.000010029, 8, 1, 4, null, '2018-08-02 06:43:54', 1, null, null, 1,\n" +
                    "        '2018-08-02 06:43:54', 0.101319359, 0, 100.459918587, 706.724467518, 'ORDER', 14508444, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86173467, 1892, 4, 0.01886719, 0.00003781, 8, 1, 4, null, '2018-08-02 06:49:28', 1, null, null, 1,\n" +
                    "        '2018-08-02 06:49:28', 0.106323602, 0, 100.459918587, 706.724657098, 'ORDER', 14508510, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86174464, 1892, 4, 0.00263946, 0.00000529, 8, 1, 4, null, '2018-08-02 08:31:59', 1, null, null, 1,\n" +
                    "        '2018-08-02 08:31:59', 0.125190792, 0, 100.479384987, 706.724843789, 'ORDER', 14512992, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86174671, 1892, 4, 0.000000013, 0, 8, 1, 4, null, '2018-08-02 08:43:07', 1, null, null, 1,\n" +
                    "        '2018-08-02 08:43:07', 0.127830252, 0, 100.479384987, 706.724862044, 'ORDER', 14513524, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86177668, 1892, 4, 0.000449421, 0, 25, 1, 4, null, '2018-08-02 12:08:20', 1, null, null, 1,\n" +
                    "        '2018-08-02 12:08:20', 0.127830265, 0, 101.135566947, 706.727907117, 'ORDER', 14512330, null, 'OPENED::ACCEPT'),\n" +
                    "       (86177677, 1892, 4, 0.007956579, 0, 25, 1, 4, null, '2018-08-02 12:08:20', 1, null, null, 1,\n" +
                    "        '2018-08-02 12:08:20', 0.128279686, 0, 101.135566947, 706.727908016, 'ORDER', 14522383, null, 'OPENED::ACCEPT'),\n" +
                    "       (86178429, 1892, 4, 0.003855973, 0.000007727, 8, 1, 4, null, '2018-08-02 13:13:50', 1, null, null, 1,\n" +
                    "        '2018-08-02 13:13:50', 0.136236265, 0, 101.123211057, 706.72958369, 'ORDER', 14524934, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86179286, 1892, 4, 0.00397595, 0.000007968, 8, 1, 4, null, '2018-08-02 14:04:33', 1, null, null, 1,\n" +
                    "        '2018-08-02 14:04:33', 0.140092238, 0, 101.123211057, 706.73011445, 'ORDER', 14524935, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86181138, 1892, 4, 0.020639738, 0.000041362, 8, 1, 4, null, '2018-08-02 17:19:02', 1, null, null, 1,\n" +
                    "        '2018-08-02 17:19:02', 0.144068188, 0, 101.402898457, 706.730623175, 'ORDER', 14535581, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86185354, 1892, 4, 0.0022455, 0.0000045, 8, 1, 4, null, '2018-08-02 20:27:29', 1, null, null, 1,\n" +
                    "        '2018-08-02 20:27:29', 0.164707926, 0, 101.288783197, 706.735458618, 'ORDER', 14543810, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86190313, 1892, 4, 0.001366162, 0.000002738, 8, 1, 4, null, '2018-08-03 05:47:26', 1, null, null, 1,\n" +
                    "        '2018-08-03 05:47:26', 0.166953426, 0, 102.980780117, 706.744353487, 'ORDER', 14543812, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86290058, 1892, 4, 0.02430222, 0, 17, 1, 4, null, '2018-08-09 20:02:22', 1, null, null, 1,\n" +
                    "        '2018-08-09 20:02:22', 0.168319588, 0, 101.021646967, 706.867413943, 'USER_TRANSFER', 1283, null, null),\n" +
                    "       (86451579, 1892, 4, 0.007979, 0, 25, 1, 4, null, '2018-08-20 08:27:56', 1, null, null, 1, '2018-08-20 08:27:56',\n" +
                    "        0.192621808, 0, 98.478964297, 707.053949452, 'ORDER', 15616758, null, 'OPENED::ACCEPT'),\n" +
                    "       (86535465, 1892, 4, 0.020574768, 0.000041232, 8, 1, 4, null, '2018-08-24 16:06:37', 1, null, null, 1,\n" +
                    "        '2018-08-24 16:06:37', 0.200600808, 0, 99.402674897, 707.170964245, 'ORDER', 15888853, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86537934, 1892, 4, 0.002682624, 0.000005376, 8, 1, 4, null, '2018-08-24 18:15:08', 1, null, null, 1,\n" +
                    "        '2018-08-24 18:15:08', 0.221175576, 0, 99.389826507, 707.206713613, 'ORDER', 15894498, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86538416, 1892, 4, 0.003656339, 0.000007327, 8, 1, 4, null, '2018-08-24 18:32:43', 1, null, null, 1,\n" +
                    "        '2018-08-24 18:32:43', 0.2238582, 0, 99.457428527, 707.206819844, 'ORDER', 15895096, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86538983, 1892, 4, 0.003970131, 0.000007956, 8, 1, 4, null, '2018-08-24 18:56:41', 1, null, null, 1,\n" +
                    "        '2018-08-24 18:56:41', 0.227514539, 0, 99.511902687, 707.206867488, 'ORDER', 15896123, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86539136, 1892, 4, 0.004741661, 0.000009502, 8, 1, 4, null, '2018-08-24 19:04:09', 1, null, null, 1,\n" +
                    "        '2018-08-24 19:04:09', 0.23148467, 0, 99.511902687, 707.206904431, 'ORDER', 15896584, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86539238, 1892, 4, 0.0027944, 0.0000056, 8, 1, 4, null, '2018-08-24 19:08:21', 1, null, null, 1,\n" +
                    "        '2018-08-24 19:08:21', 0.236226331, 0, 99.511902687, 707.206938883, 'ORDER', 15896651, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86541055, 1892, 4, 0.033436078, 0.000067006, 8, 1, 4, null, '2018-08-24 20:22:49', 1, null, null, 1,\n" +
                    "        '2018-08-24 20:22:49', 0.239020731, 0, 99.527303727, 707.207496571, 'ORDER', 15896652, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86553182, 1892, 4, 0.0238, 0, 25, 1, 4, null, '2018-08-25 10:41:59', 1, null, null, 1, '2018-08-25 10:41:59',\n" +
                    "        0.272456809, 0, 99.125998787, 707.213482565, 'ORDER', 15933591, null, 'OPENED::ACCEPTED'),\n" +
                    "       (95321828, 1892, 4, 0.000027, 0, 29, 1, 4, null, '2018-09-23 18:12:30', 1, null, null, 1, '2018-09-23 18:12:30',\n" +
                    "        0.296256809, 0, 100.867285977, 709.086523083, 'ORDER', 21687577, null, 'OPENED::ACCEPT'),\n" +
                    "       (95321835, 1892, 4, 0.00129, 0, 29, 1, 4, null, '2018-09-23 18:12:30', 1, null, null, 1, '2018-09-23 18:12:30',\n" +
                    "        0.296283809, 0, 100.867285977, 709.086523132, 'ORDER', 21603177, null, 'OPENED::ACCEPT'),\n" +
                    "       (95321843, 1892, 4, 0.007095, 0, 29, 1, 4, null, '2018-09-23 18:12:30', 1, null, null, 1, '2018-09-23 18:12:30',\n" +
                    "        0.297573809, 0, 100.867285977, 709.086525454, 'ORDER', 21627998, null, 'OPENED::ACCEPT'),\n" +
                    "       (95321850, 1892, 4, 0.00277, 0, 29, 1, 4, null, '2018-09-23 18:12:30', 1, null, null, 1, '2018-09-23 18:12:30',\n" +
                    "        0.304668809, 0, 100.867285977, 709.086538225, 'ORDER', 21415862, null, 'OPENED::ACCEPT'),\n" +
                    "       (95321856, 1892, 4, 0.00078709, 0, 29, 1, 4, null, '2018-09-23 18:12:30', 1, null, null, 1,\n" +
                    "        '2018-09-23 18:12:30', 0.307438809, 0, 100.867285977, 709.086543765, 'ORDER', 21371040, null, 'OPENED::ACCEPT'),\n" +
                    "       (95321862, 1892, 4, 0.008555161, 0, 29, 1, 4, null, '2018-09-23 18:12:30', 1, null, null, 1,\n" +
                    "        '2018-09-23 18:12:30', 0.308225899, 0, 100.867285977, 709.086545339, 'ORDER', 21583017, null, 'OPENED::ACCEPT'),\n" +
                    "       (95321868, 1892, 4, 0.0393545, 0, 29, 1, 4, null, '2018-09-23 18:12:30', 1, null, null, 1, '2018-09-23 18:12:30',\n" +
                    "        0.31678106, 0, 100.867285977, 709.086562449, 'ORDER', 21368933, null, 'OPENED::ACCEPT'),\n" +
                    "       (97160454, 1892, 4, 0.0075848, 0.0000152, 8, 1, 4, null, '2018-09-26 10:51:34', 1, null, null, 1,\n" +
                    "        '2018-09-26 10:51:34', 0.35613556, 0, 99.358509007, 709.279004254, 'ORDER', 22734794, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97194891, 1892, 4, 0.00403192, 0.00000808, 8, 1, 4, null, '2018-09-26 12:30:48', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:30:48', 0.36372036, 0, 99.398901337, 709.279799083, 'ORDER', 22753255, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97196901, 1892, 4, 0.00592812, 0.00001188, 8, 1, 4, null, '2018-09-26 12:37:45', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:37:45', 0.36775228, 0, 99.569274297, 709.279876289, 'ORDER', 22754453, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97240227, 1892, 4, 0.115928399, 0.000232321, 8, 1, 4, null, '2018-09-26 15:02:21', 1, null, null, 1,\n" +
                    "        '2018-09-26 15:02:21', 0.3736804, 0, 99.896974297, 709.282223545, 'ORDER', 22754454, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97240238, 1892, 4, 0.307713992, 0, 29, 1, 4, null, '2018-09-26 15:02:21', 1, null, null, 1,\n" +
                    "        '2018-09-26 15:02:21', 0.489608799, 0, 99.896974297, 709.282432634, 'ORDER', 22732594, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86173117, 205756, 14, 0.014256, 0, 25, 1, 14, null, '2018-08-02 06:27:01', 1, null, null, 1,\n" +
                    "        '2018-08-02 06:27:01', 0, 0, 582.160400533, 75.695356304, 'ORDER', 14506399, null, 'OPENED::ACCEPT'),\n" +
                    "       (86173126, 205756, 14, 0.001744, 0, 25, 1, 14, null, '2018-08-02 06:27:01', 1, null, null, 1,\n" +
                    "        '2018-08-02 06:27:01', 0.014256, 0, 582.160400533, 75.695384816, 'ORDER', 14507582, null, 'OPENED::ACCEPT'),\n" +
                    "       (86173287, 205756, 14, 0.208065, 0, 25, 1, 14, null, '2018-08-02 06:41:17', 1, null, null, 1,\n" +
                    "        '2018-08-02 06:41:17', 0.016, 0, 582.160400533, 75.695384816, 'ORDER', 14506410, null, 'OPENED::ACCEPT'),\n" +
                    "       (86173325, 205756, 14, 0.336, 0, 25, 1, 14, null, '2018-08-02 06:42:19', 1, null, null, 1, '2018-08-02 06:42:19',\n" +
                    "        0.224065, 0, 582.160400533, 75.695803652, 'ORDER', 14508433, null, 'OPENED::ACCEPT'),\n" +
                    "       (86178223, 205756, 14, 0.01875, 0, 25, 1, 14, null, '2018-08-02 13:02:19', 1, null, null, 1,\n" +
                    "        '2018-08-02 13:02:19', 0.560065, 0, 584.881422123, 75.700920102, 'ORDER', 14507524, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86181464, 205756, 14, 0.0790416, 0.0001584, 8, 1, 14, null, '2018-08-02 17:29:56', 1, null, null, 1,\n" +
                    "        '2018-08-02 17:29:56', 0.578815, 0, 573.428264483, 75.764153123, 'ORDER', 14536044, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86188348, 205756, 14, 0.17475, 0, 25, 1, 14, null, '2018-08-03 02:53:52', 1, null, null, 1,\n" +
                    "        '2018-08-03 02:53:52', 0.6578566, 0, 583.778164643, 75.80942503, 'ORDER', 14540535, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86191838, 205756, 14, 0.0007984, 0.0000016, 8, 1, 14, null, '2018-08-03 07:36:56', 1, null, null, 1,\n" +
                    "        '2018-08-03 07:36:56', 0.8326066, 0, 584.499725397, 75.91375981, 'ORDER', 14536045, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86192377, 205756, 14, 0.0003675, 0, 25, 1, 14, null, '2018-08-03 08:29:39', 1, null, null, 1,\n" +
                    "        '2018-08-03 08:29:39', 0.833405, 0, 585.904259107, 75.913762536, 'ORDER', 14359161, null, 'OPENED::ACCEPT'),\n" +
                    "       (86192386, 205756, 14, 0.0285915, 0, 25, 1, 14, null, '2018-08-03 08:29:40', 1, null, null, 1,\n" +
                    "        '2018-08-03 08:29:40', 0.8337725, 0, 585.904259107, 75.913763271, 'ORDER', 14574027, null, 'OPENED::ACCEPT'),\n" +
                    "       (86193876, 205756, 14, 0.0135728, 0.0000272, 8, 1, 14, null, '2018-08-03 10:32:44', 1, null, null, 1,\n" +
                    "        '2018-08-03 10:32:44', 0.862364, 0, 587.520605597, 75.958079519, 'ORDER', 14579270, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86197609, 205756, 14, 0.095808, 0.000192, 8, 1, 14, null, '2018-08-03 17:40:07', 1, null, null, 1,\n" +
                    "        '2018-08-03 17:40:07', 0.8759368, 0, 594.237742827, 75.979783562, 'ORDER', 14597188, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86197820, 205756, 14, 0.308871747, 0.000618981, 8, 1, 14, null, '2018-08-03 18:02:47', 1, null, null, 1,\n" +
                    "        '2018-08-03 18:02:47', 0.9717448, 0, 595.255535667, 75.982138878, 'ORDER', 14597189, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86197829, 205756, 14, 0.401840734, 0.000805292, 8, 1, 14, null, '2018-08-03 18:02:48', 1, null, null, 1,\n" +
                    "        '2018-08-03 18:02:48', 1.280616547, 0, 595.255535667, 75.983314942, 'ORDER', 14598061, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86213546, 205756, 14, 0.001, 0, 25, 1, 14, null, '2018-08-04 19:27:36', 1, null, null, 1, '2018-08-04 19:27:36',\n" +
                    "        1.682457281, 0, 581.290151313, 76.103833185, 'ORDER', 14634419, null, 'OPENED::ACCEPT'),\n" +
                    "       (86213556, 205756, 14, 0.731179366, 0, 25, 1, 14, null, '2018-08-04 19:27:36', 1, null, null, 1,\n" +
                    "        '2018-08-04 19:27:36', 1.683457281, 0, 581.290151313, 76.103834985, 'ORDER', 14661911, null, 'OPENED::ACCEPT'),\n" +
                    "       (86244269, 205756, 14, 0.00016467, 0.00000033, 8, 1, 14, null, '2018-08-06 22:39:12', 1, null, null, 1,\n" +
                    "        '2018-08-06 22:39:12', 2.414636647, 0, 606.105269031, 76.356352124, 'ORDER', 14790502, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86244365, 205756, 14, 0.0247005, 0.0000495, 8, 1, 14, null, '2018-08-06 22:49:54', 1, null, null, 1,\n" +
                    "        '2018-08-06 22:49:54', 2.414801317, 0, 606.105269031, 76.356352751, 'ORDER', 14790992, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86244377, 205756, 14, 0.00016467, 0.00000033, 8, 1, 14, null, '2018-08-06 22:50:36', 1, null, null, 1,\n" +
                    "        '2018-08-06 22:50:36', 2.439501817, 0, 606.105269031, 76.356446801, 'ORDER', 14790995, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86244417, 205756, 14, 0.000059281, 0.000000119, 8, 1, 14, null, '2018-08-06 22:52:50', 1, null, null, 1,\n" +
                    "        '2018-08-06 22:52:50', 2.439666487, 0, 606.105269031, 76.356447428, 'ORDER', 14791191, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86249802, 205756, 14, 0.633590879, 0.001269721, 8, 1, 14, null, '2018-08-07 09:01:03', 1, null, null, 1,\n" +
                    "        '2018-08-07 09:01:03', 2.439725768, 0, 639.867627751, 76.485435613, 'ORDER', 14791192, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86289491, 205756, 14, 0.202, 0, 25, 1, 14, null, '2018-08-09 19:16:30', 1, null, null, 1, '2018-08-09 19:16:30',\n" +
                    "        3.073316647, 0, 586.588518937, 77.237286644, 'ORDER', 14662021, null, 'OPENED::ACCEPTED'),\n" +
                    "       (86290074, 24, 2, 35.55, 0, 22, 2, 2, null, '2018-08-09 20:05:24', 1, null, null, 1, '2018-08-09 20:05:24'," +
                    "        35.551984495, 0, 54538.588649808, 255435.825564139, 'USER_TRANSFER', 1284, null, null),\n" +
                    "       (86416197, 205756, 14, 0.3656672, 0.0007328, 8, 1, 14, null, '2018-08-17 15:57:40', 1, null, null, 1,\n" +
                    "        '2018-08-17 15:57:40', 3.275316647, 0, 628.464915164, 78.840009254, 'ORDER', 15447725, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86416259, 205756, 14, 0.122727054, 0.000245946, 8, 1, 14, null, '2018-08-17 16:00:29', 1, null, null, 1,\n" +
                    "        '2018-08-17 16:00:29', 3.640983847, 0, 624.240274054, 78.862524794, 'ORDER', 15447726, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86440473, 205756, 14, 0.163672, 0.000328, 8, 1, 14, null, '2018-08-19 11:19:31', 1, null, null, 1,\n" +
                    "        '2018-08-19 11:19:31', 3.763710901, 0, 631.845541395, 79.063954371, 'ORDER', 15561653, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86446371, 205756, 14, 0.249354266, 0.000499708, 8, 1, 14, null, '2018-08-19 23:09:18', 1, null, null, 1,\n" +
                    "        '2018-08-19 23:09:18', 3.927382901, 0, 710.774178555, 79.194928171, 'ORDER', 15561654, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86451648, 205756, 14, 1.3006435, 0, 25, 1, 14, null, '2018-08-20 08:34:54', 1, null, null, 1,\n" +
                    "        '2018-08-20 08:34:54', 4.176737167, 0, 705.395160455, 79.409130607, 'ORDER', 15617092, null, 'OPENED::ACCEPT'),\n" +
                    "       (86457999, 205756, 14, 1.3496952, 0.0027048, 8, 1, 14, null, '2018-08-20 16:12:00', 1, null, null, 1,\n" +
                    "        '2018-08-20 16:12:00', 5.477380667, 0, 725.287604915, 79.467285854, 'ORDER', 15637398, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86463681, 205756, 14, 2.7944, 0.0056, 8, 1, 14, null, '2018-08-20 22:12:44', 1, null, null, 1,\n" +
                    "        '2018-08-20 22:12:44', 6.827075867, 0, 723.151672915, 79.537926678, 'ORDER', 15653492, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86463834, 205756, 14, 0.633741976, 0.001270024, 8, 1, 14, null, '2018-08-20 22:35:25', 1, null, null, 1,\n" +
                    "        '2018-08-20 22:35:25', 9.621475867, 0, 723.151672915, 79.548941678, 'ORDER', 15654531, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86464317, 205756, 14, 0.252082824, 0.000505176, 8, 1, 14, null, '2018-08-20 23:16:23', 1, null, null, 1,\n" +
                    "        '2018-08-20 23:16:23', 10.255217843, 0, 723.151672915, 79.551733695, 'ORDER', 15654532, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86473888, 205756, 14, 1.6032, 0, 25, 1, 14, null, '2018-08-21 13:30:58', 1, null, null, 1,\n" +
                    "        '2018-08-21 13:30:58', 10.507300667, 0, 749.701102905, 79.614946896, 'ORDER', 15617126, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (86549986, 205756, 14, 0.0190501, 0, 25, 1, 14, null, '2018-08-25 09:26:50', 1, null, null, 1,\n" +
                    "        '2018-08-25 09:26:50', 12.110500667, 0, 834.825275067, 81.510118252, 'ORDER', 15926536, null, 'OPENED::ACCEPT'),\n" +
                    "       (86549995, 205756, 14, 0.32838431, 0, 25, 1, 14, null, '2018-08-25 09:26:50', 1, null, null, 1,\n" +
                    "        '2018-08-25 09:26:50', 12.129550767, 0, 834.825275067, 81.510156352, 'ORDER', 15933554, null, 'OPENED::ACCEPT'),\n" +
                    "       (86562760, 205756, 14, 0.748, 0, 25, 1, 14, null, '2018-08-26 00:19:01', 1, null, null, 1, '2018-08-26 00:19:01',\n" +
                    "        12.457935077, 0, 833.950069507, 81.708001019, 'ORDER', 15933648, null, 'OPENED::ACCEPTED'),\n" +
                    "       (87624657, 205756, 14, 0.401279832, 0.000804168, 8, 1, 14, null, '2018-09-04 06:39:35', 1, null, null, 1,\n" +
                    "        '2018-09-04 06:39:35', 13.205935077, 0, 1008.686230086, 84.97591419, 'ORDER', 16994624, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (87625197, 205756, 14, 0.000045476, 0.000000091, 8, 1, 14, null, '2018-09-04 06:41:36', 1, null, null, 1,\n" +
                    "        '2018-09-04 06:41:36', 13.607214909, 0, 1008.686230086, 84.988065492, 'ORDER', 16994899, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (87628017, 205756, 14, 0.115546444, 0.000231556, 8, 1, 14, null, '2018-09-04 06:57:25', 1, null, null, 1,\n" +
                    "        '2018-09-04 06:57:25', 13.607260385, 0, 1010.929954456, 84.988160782, 'ORDER', 16996775, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (87628334, 205756, 14, 0.129825828, 0.000260172, 8, 1, 14, null, '2018-09-04 06:59:50', 1, null, null, 1,\n" +
                    "        '2018-09-04 06:59:50', 13.722806829, 0, 1010.929954456, 84.988600826, 'ORDER', 16996946, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (87632016, 205756, 14, 0.08184242, 0.000164013, 8, 1, 14, null, '2018-09-04 07:25:33', 1, null, null, 1,\n" +
                    "        '2018-09-04 07:25:33', 13.852632657, 0, 1011.536494456, 84.995751689, 'ORDER', 16996947, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (95322256, 205756, 14, 0.07294238, 0, 29, 1, 14, null, '2018-09-23 18:13:14', 1, null, null, 1,\n" +
                    "        '2018-09-23 18:13:14', 13.934475077, 0, 969.54006411, 96.9556719, 'ORDER', 21626379, null, 'OPENED::ACCEPT'),\n" +
                    "       (97150525, 1892, 4, 0, 0, 34, 2, 4, null, '2018-09-26 10:31:12', 1, null, null, 1, '2018-09-26 10:31:12',\n" +
                    "        0.35613556, 0, 99.326820017, 709.277381622, 'ORDER', 22729017, null, 'OPENED::ACCEPT'),\n" +
                    "       (97160452, 205756, 14, 0.227305491, 0, 8, 2, 14, null, '2018-09-26 10:51:34', 1, null, null, 1,\n" +
                    "        '2018-09-26 10:51:34', 1.031422948, 12.975994509, 1048.474542932, 99.869473666, 'ORDER', 22734794, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (97194889, 205756, 14, 0.120830814, 0, 8, 2, 14, null, '2018-09-26 12:30:48', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:30:48', 0.924948271, 12.855163695, 1050.474542932, 99.869901176, 'ORDER', 22753255, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (97196899, 205756, 14, 0.177657186, 0, 8, 2, 14, null, '2018-09-26 12:37:45', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:37:45', 0.981774643, 12.677506509, 1050.474542932, 99.869901176, 'ORDER', 22754453, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (97240224, 205756, 14, 3.474206509, 0, 8, 2, 14, null, '2018-09-26 15:02:21', 1, null, null, 1,\n" +
                    "        '2018-09-26 15:02:21', 4.278323966, 9.2033, 1050.474542932, 99.889388992, 'ORDER', 22754454, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (97240236, 205756, 14, 9.2033, 0, 29, 2, 14, null, '2018-09-26 15:02:21', 1, null, null, 1,\n" +
                    "        '2018-09-26 15:02:21', 10.007417457, 0, 1050.474542932, 99.889388992, 'ORDER', 22732594, null,\n" +
                    "        'OPENED::ACCEPTED'),\n" +
                    "       (97156653, 205756, null, -9.2033, 0, null, 5, 14, null, '2018-09-26 10:44:10', 1, null, null, 1,\n" +
                    "        '2018-09-26 10:44:10', 14.007417457, 0, null, null, 'ORDER', 22732594, null, '::CREATE'),\n" +
                    "       (97157011, 205756, null, -4, 0, null, 5, 14, null, '2018-09-26 10:44:50', 1, null, null, 2,\n" +
                    "        '2018-09-26 10:51:33', 4.804117457, 9.2033, null, null, 'ORDER', 22732761, null, '::CREATE'),\n" +
                    "       (97160447, 205756, null, 4, 0, null, 5, 14, null, '2018-09-26 10:51:33', 1, null, null, 1, '2018-09-26 10:51:33',\n" +
                    "        0.804117457, 13.2033, null, null, 'ORDER', 22732761, null, 'OPENED::DELETE_SPLIT'),\n" +
                    "       (97160448, 205756, null, -0.227305491, 0, null, 5, 14, null, '2018-09-26 10:51:33', 1, null, null, 1,\n" +
                    "        '2018-09-26 10:51:33', 4.804117457, 9.2033, null, null, 'ORDER', 22734794, null, '::CREATE'),\n" +
                    "       (97160450, 205756, null, -3.772694509, 0, null, 5, 14, null, '2018-09-26 10:51:33', 1, null, null, 2,\n" +
                    "        '2018-09-26 12:30:47', 4.576811966, 9.430605491, null, null, 'ORDER', 22734795, null, '::CREATE_SPLIT'),\n" +
                    "       (97160451, 205756, null, 0.227305491, 0, null, 5, 14, null, '2018-09-26 10:51:34', 1, null, null, 1,\n" +
                    "        '2018-09-26 10:51:34', 0.804117457, 13.2033, null, null, 'ORDER', 22734794, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97194885, 205756, null, 3.772694509, 0, null, 5, 14, null, '2018-09-26 12:30:47', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:30:47', 0.804117457, 12.975994509, null, null, 'ORDER', 22734795, null, 'OPENED::DELETE_SPLIT'),\n" +
                    "       (97194886, 205756, null, -0.120830814, 0, null, 5, 14, null, '2018-09-26 12:30:48', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:30:48', 4.576811966, 9.2033, null, null, 'ORDER', 22753255, null, '::CREATE'),\n" +
                    "       (97194887, 205756, null, -3.651863695, 0, null, 5, 14, null, '2018-09-26 12:30:48', 1, null, null, 2,\n" +
                    "        '2018-09-26 12:37:45', 4.455981152, 9.324130814, null, null, 'ORDER', 22753256, null, '::CREATE_SPLIT'),\n" +
                    "       (97194888, 205756, null, 0.120830814, 0, null, 5, 14, null, '2018-09-26 12:30:48', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:30:48', 0.804117457, 12.975994509, null, null, 'ORDER', 22753255, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97196895, 205756, null, 3.651863695, 0, null, 5, 14, null, '2018-09-26 12:37:45', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:37:45', 0.804117457, 12.855163695, null, null, 'ORDER', 22753256, null, 'OPENED::DELETE_SPLIT'),\n" +
                    "       (97196896, 205756, null, -0.177657186, 0, null, 5, 14, null, '2018-09-26 12:37:45', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:37:45', 4.455981152, 9.2033, null, null, 'ORDER', 22754453, null, '::CREATE'),\n" +
                    "       (97196897, 205756, null, -3.474206509, 0, null, 5, 14, null, '2018-09-26 12:37:45', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:37:45', 4.278323966, 9.380957186, null, null, 'ORDER', 22754454, null, '::CREATE_SPLIT'),\n" +
                    "       (97196898, 205756, null, 0.177657186, 0, null, 5, 14, null, '2018-09-26 12:37:45', 1, null, null, 1,\n" +
                    "        '2018-09-26 12:37:45', 0.804117457, 12.855163695, null, null, 'ORDER', 22754453, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97240222, 205756, null, 3.474206509, 0, null, 5, 14, null, '2018-09-26 15:02:21', 1, null, null, 1,\n" +
                    "        '2018-09-26 15:02:21', 0.804117457, 12.677506509, null, null, 'ORDER', 22754454, null, 'OPENED::ACCEPTED'),\n" +
                    "       (97240235, 205756, null, 9.2033, 0, null, 5, 14, null, '2018-09-26 15:02:21', 1, null, null, 1,\n" +
                    "        '2018-09-26 15:02:21', 0.804117457, 9.2033, null, null, 'ORDER', 22732594, null, 'OPENED::ACCEPTED');";

            String step_7 = "INSERT INTO WALLET (id, currency_id, user_id, active_balance, reserved_balance, ieo_reserve)" +
                    "VALUES (24, 2, 8, 100000000, 0, 0)," +
                    "       (359, 1, 13, 100000000, 0, 0)," +
                    "       (870, 2, 13, 100000000, 0, 0)," +
                    "       (1382, 3, 13, 100000000, 0, 0)," +
                    "       (1892, 4, 13, 100000000, 0, 0)," +
                    "       (290819, 5, 13, 100000000, 0, 0)," +
                    "       (2403, 6, 13, 100000000, 0, 0)," +
                    "       (2916, 7, 13, 100000000, 0, 0)," +
                    "       (3429, 8, 13, 100000000, 0, 0)," +
                    "       (5058, 9, 13, 100000000, 0, 0)," +
                    "       (6396, 10, 13, 100000000, 0, 0)," +
                    "       (10062, 11, 13, 100000000, 0, 0)," +
                    "       (49925, 12, 13, 100000000, 0, 0)," +
                    "       (59225, 13, 13, 100000000, 0, 0)," +
                    "       (205756, 14, 13, 100000000, 0, 0)," +
                    "       (257725, 17, 13, 100000000, 0, 0)," +
                    "       (274108, 18, 13, 100000000, 0, 0)," +
                    "       (307204, 19, 13, 100000000, 0, 0)," +
                    "       (324827, 20, 13, 100000000, 0, 0)," +
                    "       (341737, 21, 13, 100000000, 0, 0)," +
                    "       (369776, 22, 13, 100000000, 0, 0)," +
                    "       (386159, 23, 13, 100000000, 0, 0)," +
                    "       (410478, 24, 13, 100000000, 0, 0)," +
                    "       (428876, 25, 13, 100000000, 0, 0)," +
                    "       (446096, 26, 13, 100000000, 0, 0)," +
                    "       (464060, 27, 13, 100000000, 0, 0)," +
                    "       (483326, 28, 13, 100000000, 0, 0)," +
                    "       (509102, 29, 13, 100000000, 0, 0)," +
                    "       (525485, 30, 13, 100000000, 0, 0)," +
                    "       (558633, 31, 13, 100000000, 0, 0)," +
                    "       (575016, 32, 13, 100000000, 0, 0)," +
                    "       (596328, 33, 13, 100000000, 0, 0)," +
                    "       (619469, 34, 13, 100000000, 0, 0)," +
                    "       (635852, 35, 13, 100000000, 3610, 0)," +
                    "       (751559, 36, 13, 100000000, 0, 0)," +
                    "       (780920, 37, 13, 100000000, 0, 0)," +
                    "       (797303, 38, 13, 100000000, 0, 0)," +
                    "       (834791, 39, 13, 100000000, 0, 0)," +
                    "       (851174, 40, 13, 100000000, 0, 0)," +
                    "       (874109, 41, 13, 100000000, 0, 0)," +
                    "       (1150808, 42, 13, 100000000, 0, 0)," +
                    "       (1604793, 43, 13, 100000000, 0, 0)," +
                    "       (1637560, 44, 13, 100000000, 0, 0)," +
                    "       (1707875, 45, 13, 100000000, 0, 0)," +
                    "       (1788585, 47, 13, 100000000, 0, 0)," +
                    "       (2029504, 48, 13, 100000000, 0, 0)," +
                    "       (2269919, 49, 13, 100000000, 0, 0)," +
                    "       (2482173, 50, 13, 100000000, 0, 0)," +
                    "       (2797566, 51, 13, 100000000, 0, 0)," +
                    "       (3112770, 52, 13, 100000000, 0, 0)," +
                    "       (3270789, 53, 13, 100000000, 0, 0)," +
                    "       (3336324, 54, 13, 100000000, 0, 0)," +
                    "       (3419751, 55, 13, 100000000, 0, 0)," +
                    "       (3485286, 56, 13, 100000000, 0, 0)," +
                    "       (3564933, 57, 13, 100000000, 0, 0)," +
                    "       (3649305, 58, 13, 100000000, 0, 0)," +
                    "       (3714840, 59, 13, 100000000, 0, 0)," +
                    "       (3823152, 60, 13, 100000000, 0, 0)," +
                    "       (3888687, 61, 13, 100000000, 0, 0)," +
                    "       (3963672, 62, 13, 100000000, 0, 0)," +
                    "       (4040043, 63, 13, 100000000, 0, 0)," +
                    "       (4142244, 64, 13, 100000000, 0, 0)," +
                    "       (4270275, 65, 13, 100000000, 0, 0)," +
                    "       (4335810, 66, 13, 100000000, 0, 0)," +
                    "       (4450107, 67, 13, 100000000, 0, 0)," +
                    "       (4535327, 68, 13, 100000000, 0, 0)," +
                    "       (4614070, 69, 13, 100000000, 0, 0)," +
                    "       (4773458, 70, 13, 100000000, 0, 0)," +
                    "       (4898556, 71, 13, 100000000, 0, 0)," +
                    "       (4964091, 72, 13, 100000000, 0, 0)," +
                    "       (5134909, 73, 13, 100000000, 0, 0)," +
                    "       (5200444, 74, 13, 100000000, 0, 0)," +
                    "       (5350434, 75, 13, 100000000, 0, 0)," +
                    "       (5438702, 76, 13, 100000000, 0, 0)," +
                    "       (5582723, 77, 13, 100000000, 0, 0)," +
                    "       (5724839, 78, 13, 100000000, 0, 0)," +
                    "       (5813488, 79, 13, 100000000, 0, 0)," +
                    "       (5879023, 80, 13, 100000000, 0, 0)," +
                    "       (6304934, 81, 13, 100000000, 0, 0)," +
                    "       (6394472, 82, 13, 100000000, 0, 0)," +
                    "       (6460007, 83, 13, 100000000, 0, 0)," +
                    "       (6607076, 84, 13, 100000000, 0, 0)," +
                    "       (6710203, 85, 13, 100000000, 0, 0)," +
                    "       (6826919, 86, 13, 100000000, 0, 0)," +
                    "       (6892454, 87, 13, 100000000, 0, 0)," +
                    "       (6977674, 88, 13, 100000000, 0, 0)," +
                    "       (7043209, 89, 13, 100000000, 0, 0)," +
                    "       (7126270, 90, 13, 100000000, 0, 0)," +
                    "       (7191805, 91, 13, 100000000, 0, 0)," +
                    "       (7280835, 92, 13, 100000000, 0, 0)," +
                    "       (7479593, 93, 13, 100000000, 0, 0)," +
                    "       (7545128, 94, 13, 100000000, 0, 0)," +
                    "       (7902636, 95, 13, 100000000, 0, 0)," +
                    "       (7968171, 96, 13, 100000000, 0, 0)," +
                    "       (8320980, 97, 13, 100000000, 0, 0)," +
                    "       (8419281, 98, 13, 100000000, 0, 0)," +
                    "       (8484816, 99, 13, 100000000, 0, 0)," +
                    "       (8602009, 100, 13, 100000000, 0, 0)," +
                    "       (8667544, 101, 13, 100000000, 0, 0)," +
                    "       (8733079, 102, 13, 100000000, 0, 0)," +
                    "       (8903897, 103, 13, 100000000, 0, 0)," +
                    "       (9162218, 104, 13, 100000000, 0, 0)," +
                    "       (9293415, 105, 13, 100000000, 0, 0)," +
                    "       (9470205, 106, 13, 100000000, 0, 0)," +
                    "       (9690556, 107, 13, 100000000, 0, 0)," +
                    "       (9821626, 108, 13, 100000000, 0, 0)," +
                    "       (9952696, 109, 13, 100000000, 0, 0)," +
                    "       (10106245, 110, 13, 100000000, 0, 0)," +
                    "       (10252555, 111, 13, 100000000, 0, 0)," +
                    "       (10383625, 112, 13, 100000000, 0, 0)," +
                    "       (10532475, 113, 13, 100000000, 0, 0)," +
                    "       (10679293, 114, 13, 100000000, 0, 0)," +
                    "       (10955778, 115, 13, 100000000, 0, 0)," +
                    "       (11112883, 116, 13, 100000000, 0, 0)," +
                    "       (11400163, 117, 13, 100000000, 0, 0)," +
                    "       (11531233, 118, 13, 100000000, 0, 0)," +
                    "       (11701419, 119, 13, 100000000, 0, 0)," +
                    "       (11860302, 120, 13, 100000000, 0, 0)," +
                    "       (12021471, 121, 13, 100000000, 0, 0)," +
                    "       (12152541, 122, 13, 100000000, 0, 0)," +
                    "       (12358668, 123, 13, 100000000, 0, 0)," +
                    "       (12489738, 124, 13, 100000000, 0, 0)," +
                    "       (12650399, 125, 13, 100000000, 0, 0)," +
                    "       (12805218, 126, 13, 100000000, 0, 0)," +
                    "       (12978706, 127, 13, 100000000, 0, 0)," +
                    "       (13186357, 128, 13, 100000000, 0, 0)," +
                    "       (13364163, 129, 13, 100000000, 0, 0)," +
                    "       (13495233, 130, 13, 100000000, 0, 0)," +
                    "       (13626303, 131, 13, 100000000, 0, 0)," +
                    "       (13840503, 132, 13, 100000000, 0, 0)," +
                    "       (14035323, 133, 13, 100000000, 0, 0)," +
                    "       (14435418, 134, 13, 100000000, 0, 0)," +
                    "       (14566488, 135, 13, 100000000, 0, 0)," +
                    "       (14776863, 136, 13, 100000000, 0, 0)," +
                    "       (14907933, 137, 13, 100000000, 0, 0)," +
                    "       (15117288, 138, 13, 100000000, 0, 0)," +
                    "       (15301398, 139, 13, 100000000, 0, 0)," +
                    "       (15482193, 140, 13, 100000000, 0, 0)," +
                    "       (15789213, 141, 13, 100000000, 0, 0)," +
                    "       (16005198, 143, 13, 100000000, 0, 0)," +
                    "       (16179618, 144, 13, 100000000, 0, 0)," +
                    "       (16310688, 145, 13, 100000000, 0, 0)," +
                    "       (16488933, 146, 13, 100000000, 0, 0)," +
                    "       (16620003, 147, 13, 100000000, 0, 0)," +
                    "       (16831143, 148, 13, 100000000, 0, 0)," +
                    "       (17209818, 149, 13, 100000000, 0, 0)," +
                    "       (17700693, 150, 13, 100000000, 0, 0)," +
                    "       (18093393, 151, 13, 100000000, 0, 0)," +
                    "       (18224463, 152, 13, 100000000, 0, 0)," +
                    "       (18487878, 153, 13, 100000000, 0, 0)," +
                    "       (18620988, 154, 13, 100000000, 0, 0)," +
                    "       (18752058, 155, 13, 100000000, 0, 0)," +
                    "       (18972123, 156, 13, 100000000, 0, 0)," +
                    "       (19334478, 157, 13, 100000000, 0, 0)," +
                    "       (19519608, 158, 13, 100000000, 0, 0)," +
                    "       (19650678, 159, 13, 100000000, 0, 0)," +
                    "       (20013800, 160, 13, 100000000, 0, 0)," +
                    "       (20176490, 161, 13, 100000000, 0, 0)," +
                    "       (20337650, 162, 13, 100000000, 0, 0)," +
                    "       (20468720, 163, 13, 100000000, 0, 0)," +
                    "       (20664464, 164, 13, 100000000, 0, 0)," +
                    "       (20795534, 165, 13, 100000000, 0, 0)," +
                    "       (20926604, 166, 13, 100000000, 0, 0)," +
                    "       (21253770, 167, 13, 100000000, 0, 0)," +
                    "       (21392491, 169, 13, 100000000, 0, 0)," +
                    "       (21585526, 170, 13, 100000000, 0, 0)," +
                    "       (21716596, 171, 13, 100000000, 0, 0)," +
                    "       (21933091, 172, 13, 100000000, 0, 0)," +
                    "       (22167692, 173, 13, 100000000, 0, 0)," +
                    "       (22298762, 174, 13, 100000000, 0, 0)," +
                    "       (22459495, 175, 13, 100000000, 0, 0)," +
                    "       (22699960, 176, 13, 100000000, 0, 0)," +
                    "       (22859335, 177, 13, 100000000, 0, 0)," +
                    "       (23036560, 178, 13, 100000000, 0, 0)," +
                    "       (23210215, 179, 13, 100000000, 0, 0)," +
                    "       (23365255, 180, 13, 100000000, 0, 0)," +
                    "       (23496325, 181, 13, 100000000, 0, 0)," +
                    "       (23724040, 182, 13, 100000000, 0, 0)," +
                    "       (23956855, 183, 13, 100000000, 0, 0)," +
                    "       (24113170, 184, 13, 100000000, 0, 0)," +
                    "       (24333669, 185, 13, 100000000, 0, 0)," +
                    "       (24511914, 186, 13, 100000000, 0, 0)," +
                    "       (24696024, 187, 13, 100000000, 0, 0)," +
                    "       (24938019, 188, 13, 100000000, 0, 0)," +
                    "       (25174659, 189, 13, 100000000, 0, 0)," +
                    "       (25422775, 190, 13, 100000000, 0, 0)," +
                    "       (25553845, 191, 13, 100000000, 0, 0)," +
                    "       (25795771, 192, 13, 100000000, 0, 0)," +
                    "       (25926841, 193, 13, 100000000, 0, 0)," +
                    "       (26152006, 194, 13, 100000000, 0, 0)," +
                    "       (26283076, 195, 13, 100000000, 0, 0)," +
                    "       (26491666, 196, 13, 100000000, 0, 0)," +
                    "       (26696686, 197, 13, 100000000, 0, 0)," +
                    "       (26827756, 198, 13, 100000000, 0, 0)," +
                    "       (26958826, 199, 13, 100000000, 0, 0)," +
                    "       (27089896, 200, 13, 100000000, 0, 0)," +
                    "       (27368806, 201, 13, 100000000, 0, 0)," +
                    "       (27890536, 202, 13, 100000000, 0, 0)," +
                    "       (28021606, 203, 13, 100000000, 0, 0)," +
                    "       (28337806, 204, 13, 100000000, 0, 0)," +
                    "       (28468876, 205, 13, 100000000, 0, 0)," +
                    "       (28734586, 207, 13, 100000000, 0, 0)," +
                    "       (28884781, 208, 13, 100000000, 0, 0)," +
                    "       (29087761, 209, 13, 100000000, 0, 0)," +
                    "       (29376370, 210, 13, 100000000, 0, 0)," +
                    "       (30082924, 212, 13, 100000000, 0, 0)," +
                    "       (30570229, 213, 13, 100000000, 0, 0)," +
                    "       (30753829, 214, 13, 100000000, 0, 0)," +
                    "       (30915244, 215, 13, 100000000, 0, 0)," +
                    "       (31046314, 216, 13, 100000000, 0, 0)," +
                    "       (31200334, 217, 13, 100000000, 0, 0)," +
                    "       (31533874, 218, 13, 100000000, 0, 0)," +
                    "       (31664944, 219, 13, 100000000, 0, 0)," +
                    "       (31796014, 220, 13, 100000000, 0, 0)," +
                    "       (32235380, 222, 13, 100000000, 0, 0)," +
                    "       (32369765, 223, 13, 100000000, 0, 0)," +
                    "       (32500835, 224, 13, 100000000, 0, 0)," +
                    "       (32631905, 225, 13, 100000000, 0, 0)," +
                    "       (32762975, 226, 13, 100000000, 0, 0)," +
                    "       (32894045, 227, 13, 100000000, 0, 0)," +
                    "       (33068720, 228, 13, 100000000, 0, 0)," +
                    "       (33297200, 229, 13, 100000000, 0, 0)," +
                    "       (33428270, 230, 13, 100000000, 0, 0)," +
                    "       (33766400, 231, 13, 100000000, 0, 0)," +
                    "       (33920675, 232, 13, 100000000, 0, 0)," +
                    "       (34051745, 233, 13, 100000000, 0, 0)," +
                    "       (34182815, 234, 13, 100000000, 0, 0)," +
                    "       (34347035, 235, 13, 100000000, 0, 0)," +
                    "       (34478105, 236, 13, 100000000, 0, 0)," +
                    "       (34609175, 237, 13, 100000000, 0, 0)," +
                    "       (34917444, 238, 13, 100000000, 0, 0)," +
                    "       (35048514, 239, 13, 100000000, 0, 0)," +
                    "       (35179584, 240, 13, 100000000, 0, 0)," +
                    "       (35353749, 241, 13, 100000000, 0, 0)," +
                    "       (35484819, 242, 13, 100000000, 0, 0)," +
                    "       (35615889, 243, 13, 100000000, 0, 0)," +
                    "       (35746959, 244, 13, 100000000, 0, 0)," +
                    "       (35878029, 245, 13, 100000000, 0, 0)," +
                    "       (36109569, 246, 13, 100000000, 0, 0)," +
                    "       (36396444, 247, 13, 100000000, 0, 0)," +
                    "       (36775358, 248, 13, 100000000, 0, 0)," +
                    "       (36906428, 249, 13, 100000000, 0, 0)," +
                    "       (37013073, 250, 13, 100000000, 0, 0)," +
                    "       (37144143, 251, 13, 100000000, 0, 0)," +
                    "       (37275213, 252, 13, 100000000, 0, 0)," +
                    "       (37413168, 253, 13, 100000000, 0, 0)," +
                    "       (37544238, 254, 13, 100000000, 0, 0)," +
                    "       (37675308, 255, 13, 100000000, 0, 0)," +
                    "       (37806378, 256, 13, 100000000, 0, 0)," +
                    "       (37938468, 257, 13, 100000000, 0, 0)," +
                    "       (38069538, 258, 13, 100000000, 0, 0)," +
                    "       (38200867, 263, 13, 100000000, 0, 0)," +
                    "       (38332448, 265, 13, 100000000, 0, 0)," +
                    "       (38463773, 266, 13, 100000000, 0, 0)," +
                    "       (38595608, 267, 13, 100000000, 0, 0)," +
                    "       (38726932, 268, 13, 100000000, 0, 0)," +
                    "       (38858002, 269, 13, 100000000, 0, 0)," +
                    "       (38989072, 270, 13, 100000000, 0, 0)," +
                    "       (39120142, 271, 13, 100000000, 0, 0)," +
                    "       (39251723, 272, 13, 100000000, 0, 0)," +
                    "       (39382793, 273, 13, 100000000, 0, 0)," +
                    "       (39513863, 274, 13, 100000000, 0, 0)," +
                    "       (39907073, 277, 13, 100000000, 0, 0)," +
                    "       (40038143, 278, 13, 100000000, 0, 0)," +
                    "       (40169213, 279, 13, 100000000, 0, 0)," +
                    "       (41481446, 291, 13, 100000000, 0, 0)," +
                    "       (41612516, 292, 13, 100000000, 0, 0)," +
                    "       (41749718, 293, 13, 100000000, 0, 0)," +
                    "       (41880788, 294, 13, 100000000, 0, 0)," +
                    "       (42012369, 295, 13, 0, 0, 0)," +
                    "       (42143439, 296, 13, 0, 0, 0)," +
                    "       (42275020, 297, 13, 0, 0, 0)," +
                    "       (42406090, 298, 13, 0, 0, 0)," +
                    "       (42541760, 299, 13, 0, 0, 0)," +
                    "       (42672831, 300, 13, 0, 0, 0)," +
                    "       (42808500, 301, 13, 0, 0, 0)," +
                    "       (42939570, 302, 13, 0, 0, 0)," +
                    "       (43070640, 303, 13, 0, 0, 0)," +
                    "       (43201710, 304, 13, 0, 0, 0)," +
                    "       (43332780, 305, 13, 0, 0, 0)," +
                    "       (43463850, 306, 13, 0, 0, 0)," +
                    "       (43594920, 307, 13, 0, 0, 0)," +
                    "       (43725990, 308, 13, 0, 0, 0)," +
                    "       (43857060, 309, 13, 0, 0, 0);";

            String step_8 = "INSERT INTO WITHDRAW_REQUEST (id, acceptance, wallet, processed_by, status, recipient_bank_name, recipient_bank_code,\n" +
                    "                              user_full_name, remark, amount, commission, merchant_commission, date_creation, status_id,\n" +
                    "                              status_modification_date, currency_id, user_id, commission_id, merchant_id,\n" +
                    "                              merchant_image_id, admin_holder_id, destination_tag, transaction_hash, additional_params)\n" +
                    "VALUES (17591, null, 'U055853607198', null, 1, null, null, null, null, 5.000000000, 0.010000000, 0.100000000,\n" +
                    "        '2017-12-03 14:10:10', 9, '2017-12-03 18:13:02', 2, 13, 7, 5, null, 2969, '', null, null);";

            prepareTestData(
                    step_1,
                    step_2,
                    step_3,
                    step_4,
                    step_5,
                    step_6,
                    step_7, step_8);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findMyInputOutputHistoryByOperationType_Ok() {
        String email = "shvets.k@gmail.com";
        int offset = 0;
        int limit = 15;
        List<Integer> operationTypeIdList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Locale locale = Locale.ENGLISH;

        List<MyInputOutputHistoryDto> actual = inputOutputDao.findMyInputOutputHistoryByOperationType(
                email,
                offset,
                limit,
                operationTypeIdList,
                locale);

        assertNotNull(actual);
        assertEquals(15, actual.size());
    }

    @Test
    public void findMyInputOutputHistoryByOperationType_NotFound() {
        String email = "wrongemail@gmail.com";
        int offset = 0;
        int limit = 15;
        List<Integer> operationTypeIdList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Locale locale = Locale.ENGLISH;

        List<MyInputOutputHistoryDto> actual = inputOutputDao.findMyInputOutputHistoryByOperationType(
                email,
                offset,
                limit,
                operationTypeIdList,
                locale);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    public void findMyInputOutputHistoryByOperationType_USD_No_Operations_Other_Args() {
        String userEmail = "shvets.k@gmail.com";
        int currencyId = 2;
        String currencyName = "USD";
        LocalDateTime dateTimeFrom = LocalDateTime.of(2017, 1, 1, 1, 0, 0);
        LocalDateTime dateTimeTo = LocalDateTime.now();
        int offset = 0;
        int limit = 15;
        List<Integer> operationTypesList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Locale locale = Locale.ENGLISH;

        List<MyInputOutputHistoryDto> actual = inputOutputDao.findMyInputOutputHistoryByOperationType(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                operationTypesList,
                locale);

        assertEquals(1, actual.size());
    }

    @Test
    public void findMyInputOutputHistoryByOperationType_NotFound_Other_Args() {
        String userEmail = "wrongemail@gmail.com";
        int currencyId = 2;
        String currencyName = "USD";
        LocalDateTime dateTimeFrom = LocalDateTime.of(2017, 1, 20, 5, 0, 0);
        LocalDateTime dateTimeTo = LocalDateTime.now();
        int offset = 0;
        int limit = 15;
        List<Integer> operationTypesList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Locale locale = Locale.ENGLISH;

        List<MyInputOutputHistoryDto> actual = inputOutputDao.findMyInputOutputHistoryByOperationType(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                operationTypesList,
                locale);

        assertEquals(Collections.EMPTY_LIST, actual);
    }

    @Test
    public void findMyInputOutputHistoryByOperationType_dateTimeFrom_Null() {
        String userEmail = "shvets.k@gmail.com";
        int currencyId = 2;
        String currencyName = "USD";
        LocalDateTime dateTimeFrom = null;
        LocalDateTime dateTimeTo = LocalDateTime.now();
        int offset = 0;
        int limit = 15;
        List<Integer> operationTypesList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Locale locale = Locale.ENGLISH;

        List<MyInputOutputHistoryDto> actual = inputOutputDao.findMyInputOutputHistoryByOperationType(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                operationTypesList,
                locale);

        assertEquals(1, actual.size());
    }

    @Test
    public void findMyInputOutputHistoryByOperationType_dateTimeTo_Null() {
        String userEmail = "shvets.k@gmail.com";
        int currencyId = 2;
        String currencyName = "USD";
        LocalDateTime dateTimeFrom = LocalDateTime.of(2017, 1, 20, 5, 0, 0);
        LocalDateTime dateTimeTo = null;
        int offset = 0;
        int limit = 15;
        List<Integer> operationTypesList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Locale locale = Locale.ENGLISH;

        List<MyInputOutputHistoryDto> actual = inputOutputDao.findMyInputOutputHistoryByOperationType(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                operationTypesList,
                locale);

        assertEquals(1, actual.size());
    }

    @Test
    public void findMyInputOutputHistoryByOperationType_CurrencyId_Zero() {
        String userEmail = "shvets.k@gmail.com";
        int currencyId = 0;
        String currencyName = "USD";
        LocalDateTime dateTimeFrom = LocalDateTime.of(2017, 1, 20, 5, 0, 0);
        LocalDateTime dateTimeTo = LocalDateTime.now();
        int offset = 0;
        int limit = 15;
        List<Integer> operationTypesList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Locale locale = Locale.ENGLISH;

        List<MyInputOutputHistoryDto> actual = inputOutputDao.findMyInputOutputHistoryByOperationType(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                operationTypesList,
                locale);

        assertEquals(1, actual.size());
    }

    @Test
    public void findUnconfirmedInvoices_Ok() {
        int userId = 8;
        int currencyId = 6;
        int limit = 15;
        int offset = 0;

        PaginationWrapper<List<MyInputOutputHistoryDto>> actual = inputOutputDao.findUnconfirmedInvoices(
                userId,
                currencyId,
                limit,
                offset);

        assertEquals(2, actual.getTotalCount());
        assertEquals(1, actual.getPagesCount());
        assertEquals(limit, actual.getPageSize());
        assertEquals(2, actual.getData().size());
    }

    @Test
    public void findUnconfirmedInvoices_NotFound() {
        int wrongUserId = 15;
        int currencyId = 6;
        int limit = 15;
        int offset = 0;

        PaginationWrapper<List<MyInputOutputHistoryDto>> actual = inputOutputDao.findUnconfirmedInvoices(
                wrongUserId,
                currencyId,
                limit,
                offset);

        assertEquals(0, actual.getTotalCount());
        assertEquals(0, actual.getPagesCount());
        assertEquals(limit, actual.getPageSize());
        assertEquals(Collections.EMPTY_LIST, actual.getData());
    }

    @Test
    public void getInputOutputSummary_Ok() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(6);
        LocalDateTime endTime = LocalDateTime.now();
        List<Integer> userRoleIdList = Collections.singletonList(4);

        List<CurrencyInputOutputSummaryDto> actual = inputOutputDao.getInputOutputSummary(startTime, endTime, userRoleIdList);

        assertNotNull(actual);
        assertEquals(269, actual.size());
    }

    @Test
    public void getInputOutputSummaryWithCommissions_Ok() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(6);
        LocalDateTime endTime = LocalDateTime.now();
        List<Integer> userRoleIdList = Collections.singletonList(4);

        List<InOutReportDto> actual = inputOutputDao.getInputOutputSummaryWithCommissions(startTime, endTime, userRoleIdList);

        assertNotNull(actual);
        assertEquals(269, actual.size());
    }

    @Configuration
    static class InnerConf extends AppContextConfig {

        @Override
        protected String getSchema() {
            return "InputOutputDaoImplTest";
        }

        @Bean
        public InputOutputDao inputOutputDao() {
            return new InputOutputDaoImpl();
        }

        @Bean
        public MessageSource messageSource() {
            return Mockito.mock(MessageSource.class);
        }
    }
}
