package me.exrates.service.aidos;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.WithdrawService;
import me.exrates.service.ripple.RippleService;
import me.exrates.service.ripple.RippleServiceImpl;
import me.exrates.service.ripple.RippleWsServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdkServiceImplTest {


    @Mock
    private MessageSource messageSource;
    @Mock
    private MerchantService merchantService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private RefillService refillService;

    @Mock
    private AidosNodeServiceImpl aidosNodeService;

    @InjectMocks
    private AdkServiceImpl adkService;

   /* private AdkService adkService = spy(new AdkServiceImpl(aidosNodeService, messageSource, merchantService, currencyService, refillService));*/

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void listAllTransactions() {
    }



    private String responseData =  "[{\"account\":\"\",\"address\":\"HNQTGSYWJFQXCLPWNWUWHMFJQUIORNQRH9MLPQTHNJYDRTUYLIALRPBHDTWORZDZJZGCVTXYWOUJIVKSUKWRBJYZVV\",\"category\":\"receive\"" +
            ",\"amount\":0.01,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1538037715," +
            "\"txid\":\"QFYTYUVVJIBVRDVURSGPQIGHKRK9AEMISDTECJWCUZEOAYIFCHNHH9EKIFZERJMNEPMYJUUZFMMVHJJRF\",\"walletconflicts\":[],\"time\":1538037715,\"timereceived\":1538037715," +
            "\"bip125-replaceable\":\"no\"},{\"account\":\"\",\"address\":\"PY9DJ9CTSDDGKJMSE9BYBEUFWRPJWHWTEHRLJRZDZRYHGBDZOCQZQLWWZSHVEUPDUVKFQLGDHQRUSMWIGRSWXRGIHA\",\"category\":" +
            "\"receive\",\"amount\":0.0998,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1537980175," +
            "\"txid\":\"LKYBBNWPECIXK9MWOVAYUEIDUGMSGMVGJNV9XRZNGVQCZYOLOGPREIHLPTR9JFJVDUWN9ZBTWTCQWOMKA\",\"walletconflicts\":[],\"time\":1537980175,\"timereceived\":1537980175," +
            "\"bip125-replaceable\":\"no\"},{\"account\":\"\",\"address\":\"M9LTTJTUCJMIJDYYHQNGNMGXVSRKJGEFVKDHULEZQASQBKXTEWXPYQDOKJNGBC9ETHSFCRTMARSUDVMDRKEL9DEPXE\",\"category\":\"send\"," +
            "\"amount\":0,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1537980175," +
            "\"txid\":\"LKYBBNWPECIXK9MWOVAYUEIDUGMSGMVGJNV9XRZNGVQCZYOLOGPREIHLPTR9JFJVDUWN9ZBTWTCQWOMKA\",\"walletconflicts\":[],\"time\":1537980175,\"timereceived\":1537980175," +
            "\"bip125-replaceable\":\"no\",\"abandoned\":false},{\"account\":\"\",\"address\":\"M9LTTJTUCJMIJDYYHQNGNMGXVSRKJGEFVKDHULEZQASQBKXTEWXPYQDOKJNGBC9ETHSFCRTMARSUDVMDRKEL9DEPXE\"," +
            "\"category\":\"send\",\"amount\":-0.1,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1537980175," +
            "\"txid\":\"LKYBBNWPECIXK9MWOVAYUEIDUGMSGMVGJNV9XRZNGVQCZYOLOGPREIHLPTR9JFJVDUWN9ZBTWTCQWOMKA\",\"walletconflicts\":[],\"time\":1537980175,\"timereceived\":1537980175," +
            "\"bip125-replaceable\":\"no\",\"abandoned\":false},{\"account\":\"\",\"address\":\"M9LTTJTUCJMIJDYYHQNGNMGXVSRKJGEFVKDHULEZQASQBKXTEWXPYQDOKJNGBC9ETHSFCRTMARSUDVMDRKEL9DEPXE\"," +
            "\"category\":\"receive\",\"amount\":0.1,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1537975142," +
            "\"txid\":\"GIRWBYYBFYJZEBOIUYXOLCJRZJMXWAWSQEKQEVQ9NCLYCFNSWIWZNREEOJFUOLCKAEQQYXSAACDWLKXHX\",\"walletconflicts\":[],\"time\":1537975142,\"timereceived\":1537975142," +
            "\"bip125-replaceable\":\"no\"},{\"account\":\"\",\"address\":\"DQIPQNLSPJIWEFZNSF9BESNNRTP9JCHWRRBBDYFX9MPRDSDTXEWICMQXVXRCSOWKTJMUWTJXU9WUBDMKGIDNLD9JBR\",\"category\":\"send\"," +
            "\"amount\":0,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1537973137," +
            "\"txid\":\"EMKSAYPZ9MXAPETSVTXJYTT9DUKMYBNUOTNZEKGSTG9TV9JHHSEAXONSFZDA9AQJSTBRBTWRQSKYUREPT\",\"walletconflicts\":[],\"time\":1537973137,\"timereceived\":1537973137," +
            "\"bip125-replaceable\":\"no\",\"abandoned\":false},{\"account\":\"\",\"address\":\"DQIPQNLSPJIWEFZNSF9BESNNRTP9JCHWRRBBDYFX9MPRDSDTXEWICMQXVXRCSOWKTJMUWTJXU9WUBDMKGIDNLD9JBR\"," +
            "\"category\":\"send\",\"amount\":-0.001,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1537973137," +
            "\"txid\":\"EMKSAYPZ9MXAPETSVTXJYTT9DUKMYBNUOTNZEKGSTG9TV9JHHSEAXONSFZDA9AQJSTBRBTWRQSKYUREPT\",\"walletconflicts\":[],\"time\":1537973137,\"timereceived\":1537973137," +
            "\"bip125-replaceable\":\"no\",\"abandoned\":false},{\"account\":\"\",\"address\":\"DQIPQNLSPJIWEFZNSF9BESNNRTP9JCHWRRBBDYFX9MPRDSDTXEWICMQXVXRCSOWKTJMUWTJXU9WUBDMKGIDNLD9JBR\"," +
            "\"category\":\"receive\",\"amount\":0.001,\"vout\":0,\"fee\":0,\"confirmations\":100000,\"blockhash\":\"\",\"blockindex\":0,\"blocktime\":1537880611," +
            "\"txid\":\"OZJEKZGEUBBYZDMCOFPNXXMWEMTRRJLNYYJGZJHFBPFIMXHTFAELWYCTOIPUCVXCBTNHVNGAKTYGTJTHK\",\"walletconflicts\":[],\"time\":1537880611,\"timereceived\":1537880611," +
            "\"bip125-replaceable\":\"no\"}]";
}