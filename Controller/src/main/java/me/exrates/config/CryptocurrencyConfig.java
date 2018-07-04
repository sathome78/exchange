package me.exrates.config;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.BitcoinService;
import me.exrates.service.impl.BitcoinServiceImpl;
import me.exrates.service.lisk.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Log4j2(topic = "config")
@Configuration
public class CryptocurrencyConfig {
    @Bean(name = "bitcoinServiceImpl")
    public BitcoinService bitcoinService() {
        return new BitcoinServiceImpl("merchants/bitcoin_wallet.properties",
                "Bitcoin", "BTC", 4, 15, false);
    }

    @Bean(name = "litecoinServiceImpl")
    public BitcoinService litecoinService() {
        return new BitcoinServiceImpl("merchants/litecoin_wallet.properties",
                "Litecoin", "LTC", 4, 20, false);
    }

    @Bean(name = "dashServiceImpl")
    public BitcoinService dashService() {
        return new BitcoinServiceImpl("merchants/dash_wallet.properties",
                "Dash", "DASH", 4, 20, false);
    }

    @Bean(name = "atbServiceImpl")
    public BitcoinService atbService() {
        return new BitcoinServiceImpl("merchants/atb_wallet.properties",
                "ATB", "ATB", 10, 20, false);
    }
    @Bean(name = "bitcoinCashServiceImpl")
    public BitcoinService bchService() {
        return new BitcoinServiceImpl("merchants/bitcoin_cash_wallet.properties",
                "Bitcoin Cash", "BCH", 4, 20, false);
    }

    @Bean(name = "dogecoinServiceImpl")
    public BitcoinService dogeService() {
        return new BitcoinServiceImpl("merchants/dogecoin_wallet.properties",
                "Dogecoin", "DOGE", 4, 20, false);
    }

    @Bean(name = "btgServiceImpl")
    public BitcoinService btgService() {
        return new BitcoinServiceImpl("merchants/bitcoin_gold_wallet.properties",
                "BTG", "BTG", 4, 20, false);
    }

    @Bean(name = "zcashServiceImpl")
    public BitcoinService zecService() {
        return new BitcoinServiceImpl("merchants/zec_wallet.properties",
                "Zcash", "ZEC", 4, 20, false);
    }

    @Bean(name = "b2xServiceImpl")
    public BitcoinService b2xService() {
        return new BitcoinServiceImpl("merchants/b2x_wallet.properties",
                "B2X", "B2X", 4, 20, false);
    }

    @Bean(name = "bcdServiceImpl")
    public BitcoinService bcdService() {
        return new BitcoinServiceImpl("merchants/bcd_wallet.properties",
                "BCD", "BCD", 4, 20, false);
    }

    @Bean(name = "plcServiceImpl")
    public BitcoinService pbtcService() {
        return new BitcoinServiceImpl("merchants/plc_wallet.properties",
                "PLC", "PLC", 4, 20, false);
    }

    @Bean(name = "bcxServiceImpl")
    public BitcoinService bcxService() {
        return new BitcoinServiceImpl("merchants/bcx_wallet.properties",
                "BCX", "BCX", 4, 20, false);
    }

    @Bean(name = "bciServiceImpl")
    public BitcoinService bciService() {
        return new BitcoinServiceImpl("merchants/bci_wallet.properties",
                "BCI", "BCI", 4, 20, false);
    }

    @Bean(name = "occServiceImpl")
    public BitcoinService occService() {
        return new BitcoinServiceImpl("merchants/occ_wallet.properties",
                "OCC", "OCC", 4, 20, false);
    }

    @Bean(name = "btczServiceImpl")
    public BitcoinService btczService() {
        return new BitcoinServiceImpl("merchants/btcz_wallet.properties",
                "BTCZ", "BTCZ", 4, 20, false);
    }

    @Bean(name = "lccServiceImpl")
    public BitcoinService lccService() {
        return new BitcoinServiceImpl("merchants/lcc_wallet.properties",
                "LCC", "LCC", 4, 20, false);
    }

    @Bean(name = "bitcoinAtomServiceImpl")
    public BitcoinService bitcoinAtomService() {
        return new BitcoinServiceImpl("merchants/bca_wallet.properties",
                "BitcoinAtom", "BCA", 4, 20, false);
    }
    @Bean(name = "btcpServiceImpl")
    public BitcoinService btcpService() {
        return new BitcoinServiceImpl("merchants/btcp_wallet.properties",
                "BTCP", "BTCP", 4, 20, false);
    }

    @Bean(name = "szcServiceImpl")
    public BitcoinService szcService() {
        return new BitcoinServiceImpl("merchants/szc_wallet.properties",
                "SZC", "SZC", 4, 20, false, false);
    }

    @Bean(name = "btxServiceImpl")
    public BitcoinService btxService() {
        return new BitcoinServiceImpl("merchants/btx_wallet.properties",
                "BTX", "BTX", 4, 20, false, false);
    }

    @Bean(name = "bitdollarServiceImpl")
    public BitcoinService bitdollarService() {
        return new BitcoinServiceImpl("merchants/xbd_wallet.properties",
                "BitDollar", "XBD", 4, 20, false, false);
    }

    @Bean(name = "beetServiceImpl")
    public BitcoinService beetService() {
        return new BitcoinServiceImpl("merchants/beet_wallet.properties",
                "BEET", "BEET", 4, 20, false, false);
    }

    @Bean(name = "nycoinServiceImpl")
    public BitcoinService nycoinService() {
        return new BitcoinServiceImpl("merchants/nyc_wallet.properties",
                "NYC", "NYC", 4, 20, false, false);
    }

    @Bean(name = "ptcServiceImpl")
    public BitcoinService ptcService() {
        return new BitcoinServiceImpl("merchants/perfectcoin_wallet.properties",
                "Perfectcoin", "PTC", 4, 20, false, false);
    }

    @Bean(name = "fgcServiceImpl")
    public BitcoinService fgcService() {
        return new BitcoinServiceImpl("merchants/fgc_wallet.properties",
                "FGC", "FGC", 4, 20, false, false);
    }

    @Bean(name = "brecoServiceImpl")
    public BitcoinService brecoService() {
        return new BitcoinServiceImpl("merchants/breco_wallet.properties",
                "BRECO", "BRECO", 4, 20, false,
                false, true, true);
    }

    // LISK-like cryptos


    @Bean(name = "liskServiceImpl")
    public LiskService liskService() {
        LiskRestClient restClient = liskRestClient();
        return new LiskServiceImpl(restClient, new LiskSpecialMethodServiceImpl(restClient),
                "Lisk", "LSK", "merchants/lisk.properties");
    }

    @Bean(name = "btwServiceImpl")
    public LiskService btwService() {
        LiskRestClient restClient = liskRestClient();
        return new LiskServiceImpl(restClient, new LiskSpecialMethodServiceImpl(restClient), "BitcoinWhite", "BTW", "merchants/bitcoin_white.properties");
    }

    @Bean(name = "riseServiceImpl")
    public LiskService riseService() {
        LiskRestClient restClient = liskRestClient();
        return new LiskServiceImpl(restClient, new LiskSpecialMethodServiceImpl(restClient),
                "RiseVision", "RISE", "merchants/rise_vision.properties");
    }

    @Bean(name = "arkServiceImpl")
    public LiskService arkService() {
        return new LiskServiceImpl(liskRestClient(), arkSendTxService(), "Ark", "ARK", "merchants/ark.properties");
    }

    @Bean
    @Scope("prototype")
    public LiskRestClient liskRestClient() {
        return new LiskRestClientImpl();
    }

    @Bean
    @Scope("prototype")
    public LiskSpecialMethodService arkSendTxService() {
        return new ArkSpecialMethodServiceImpl("merchants/ark.properties");
    }

}
