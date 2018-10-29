package me.exrates.config;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.model.dto.MosaicIdDto;
import me.exrates.model.dto.merchants.neo.AssetMerchantCurrencyDto;
import me.exrates.model.dto.merchants.neo.NeoAsset;
import me.exrates.service.BitcoinService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.impl.BitcoinServiceImpl;
import me.exrates.service.lisk.*;
import me.exrates.service.nem.XemMosaicService;
import me.exrates.service.nem.XemMosaicServiceImpl;
import me.exrates.service.neo.NeoService;
import me.exrates.service.neo.NeoServiceImpl;
import me.exrates.service.waves.WavesService;
import me.exrates.service.waves.WavesServiceImpl;
import org.nem.core.model.primitive.Supply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Log4j2(topic = "config")
@Configuration
public class CryptocurrencyConfig {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;

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
                "NYC", "NYC", 4, 20, false, true);
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

    @Bean(name = "bclServiceImpl")
    public BitcoinService bitcoinCleanService() {
        return new BitcoinServiceImpl("merchants/bcl_wallet.properties",
                "BitcoinClean", "BCL", 4, 20, false);
    }

    @Bean(name = "brecoServiceImpl")
    public BitcoinService brecoService() {
        return new BitcoinServiceImpl("merchants/breco_wallet.properties",
                "BRECO", "BRECO", 4, 20, false,
                false, true, true);
    }

    @Bean(name = "ftoServiceImpl")
    public BitcoinService ftoService() {
        return new BitcoinServiceImpl("merchants/fto_wallet.properties",
                "FTO", "FTO", 4, 20, false, false);
    }

    @Bean(name = "sabrServiceImpl")
    public BitcoinService sabrService() {
        return new BitcoinServiceImpl("merchants/sabr_wallet.properties",
                "SABR", "SABR", 4, 20, false, false);
    }

    @Bean(name = "eqlServiceImpl")
    public BitcoinService eqlService() {
        return new BitcoinServiceImpl("merchants/eql_wallet.properties",
                "EQL", "EQL", 4, 20, false);
    }

    @Bean(name = "lbtcServiceImpl")
    public BitcoinService lbtcService() {
        return new BitcoinServiceImpl("merchants/lbtc_wallet.properties",
                "LBTC", "LBTC", 4, 20, false);
    }

    @Bean(name = "brbServiceImpl")
    public BitcoinService brbService() {
        return new BitcoinServiceImpl("merchants/brb_wallet.properties",
                "BRB", "BRB", 4, 20, false, false);
    }

//    @Bean(name = "rizServiceImpl")
//    public BitcoinService rizService() {
//        return new BitcoinServiceImpl("merchants/riz_wallet.properties",
//                "RIZ", "RIZ", 4, 20, false);
//    }
//
//    @Bean(name = "sicServiceImpl")
//    public BitcoinService sicService() {
//        return new BitcoinServiceImpl("merchants/sic_wallet.properties", "SIC", "SIC", 4, 20, false, false);
//    }
//
//    @Bean(name = "clxServiceImpl")
//    public BitcoinService clxService() {
//        return new BitcoinServiceImpl("merchants/clx_wallet.properties",
//                "CLX", "CLX", 4, 20, false, false);
//    }
//
//    @Bean(name = "qrkServiceImpl")
//    public BitcoinService qrkService() {
//        return new BitcoinServiceImpl("merchants/qrk_wallet.properties",
//                "QRK", "QRK", 4, 20, false, false);
//    }
//
//    @Bean(name="cmkServiceImpl")
//    public BitcoinService cmkService(){
//        return new BitcoinServiceImpl("merchants/cmk_wallet.properties", "CMK", "CMK", 4, 20, false, true);
//    }
//
//    @Bean(name="mbcServiceImpl")
//    public BitcoinService mbcService(){
//        return new BitcoinServiceImpl("merchants/mbc_wallet.properties", "MBC", "MBC", 4, 20, false, true);
//    }
//
//
//    @Bean(name = "ddxServiceImpl")
//    public BitcoinService ddxService() {
//        return new BitcoinServiceImpl("merchants/ddx_wallet.properties",
//                "DDX", "DDX", 1, 20, false, true);
//    }
//
//
//
//    @Bean(name="lpcServiceImpl")
//    public BitcoinService lpcService(){
//        return new BitcoinServiceImpl("merchants/lpc_wallet.properties", "LPC", "LPC", 4, 20, false, false);
//    }
//    @Bean(name = "xfcServiceImpl")
//    public BitcoinService xfcServiceImpl() {
//        return new BitcoinServiceImpl("merchants/xfc_wallet.properties",
//                "XFC", "XFC", 4, 20, false, false);
//    }
//
//    @Bean(name="TOAServiceImpl")
//    public BitcoinService taoServiceImpl(){
//        return new BitcoinServiceImpl("merchants/toa_wallet.properties", "TOA", "TOA", 4, 20, false, false);
//    }
//
//    @Bean(name = "crypServiceImpl")
//    public BitcoinService crypService() {
//        return new BitcoinServiceImpl("merchants/cryp_wallet.properties", "CRYP", "CRYP", 4, 20, false, true);
//    }
//
//    @Bean(name = "cbcServiceImpl")
//    public BitcoinService cbcService() {
//        return new BitcoinServiceImpl("merchants/cbc_wallet.properties",
//                "CBC", "CBC", 4, 20, false, false);
//    }
//
//    @Bean(name = "abbcServiceImpl")
//    public BitcoinService abbcService() {
//        return new BitcoinServiceImpl("merchants/abbc_wallet.properties", "ABBC", "ABBC", 4, 20, false, false);
//    }

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


    // WAVES-like

    @Bean(name = "wavesServiceImpl")
    public WavesService wavesService() {
        return new WavesServiceImpl("WAVES", "Waves", "merchants/waves.properties");
    }

    @Bean(name = "lunesServiceImpl")
    public WavesService lunesService() {
        return new WavesServiceImpl("LUNES", "LUNES", "merchants/lunes.properties");
    }



    @Bean(name = "digicServiceImpl")
    public XemMosaicService npxsService() {
        return new XemMosaicServiceImpl(
                "DIGIT",
                "DIGIT",
                new MosaicIdDto("digit", "coin"),
                1000000,
                6,
                new Supply(8999999999L),
                10);
    }
    //NEO and Forks
    @Bean(name = "neoServiceImpl")
    public NeoService neoService() {
        Merchant mainMerchant = merchantService.findByName(NeoAsset.NEO.name());
        me.exrates.model.Currency mainCurrency = currencyService.findByName(NeoAsset.NEO.name());
        Map<String, AssetMerchantCurrencyDto> neoAssetMap = new HashMap<String, AssetMerchantCurrencyDto>() {{
            put(NeoAsset.NEO.getId(), new AssetMerchantCurrencyDto(NeoAsset.NEO, mainMerchant, mainCurrency));
            put(NeoAsset.GAS.getId(), new AssetMerchantCurrencyDto(NeoAsset.GAS, merchantService.findByName(NeoAsset.GAS.name()), currencyService.findByName(NeoAsset.GAS.name())));
        }};
        return new NeoServiceImpl(mainMerchant, mainCurrency, neoAssetMap, "merchants/neo.properties");
    }

    @Bean(name = "kazeServiceImpl")
    public NeoService kazeService() {
        Merchant mainMerchant = merchantService.findByName(NeoAsset.KAZE.name());
        me.exrates.model.Currency mainCurrency = currencyService.findByName(NeoAsset.KAZE.name());
        Map<String, AssetMerchantCurrencyDto> neoAssetMap = new HashMap<String, AssetMerchantCurrencyDto>() {{
            put(NeoAsset.KAZE.getId(), new AssetMerchantCurrencyDto(NeoAsset.KAZE, mainMerchant, mainCurrency));
            put(NeoAsset.STREAM.getId(), new AssetMerchantCurrencyDto(NeoAsset.STREAM, merchantService.findByName(NeoAsset.STREAM.name()), currencyService.findByName(NeoAsset.STREAM.name())));
        }};
        return new NeoServiceImpl(mainMerchant, mainCurrency, neoAssetMap, "merchants/kaze.properties");
    }



}
