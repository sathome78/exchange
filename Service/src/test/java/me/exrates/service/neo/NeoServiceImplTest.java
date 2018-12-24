package me.exrates.service.neo;

import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.neo.AssetMerchantCurrencyDto;
import me.exrates.model.dto.merchants.neo.Block;
import me.exrates.model.dto.merchants.neo.NeoAsset;
import me.exrates.model.dto.merchants.neo.NeoTransaction;
import me.exrates.model.dto.merchants.neo.NeoVout;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NeoServiceImplTest {

    @Mock
    private RefillService refillService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private MerchantSpecParamsDao specParamsDao;

    @Mock
    private NeoNodeService neoNodeService;

    @Mock
    private MessageSource messageSource;

    private final String TEST_ADDRESS_1 = "Adh6MktFHDS4SZei8DDHQCCEqjiBWrNVXH";
    private final String TEST_ADDRESS_2 = "AGMdjwDEpagVJzNooZpCQUq9TFCBmybFpo";
    private final String TEST_ADDRESS_3 = "AVQx83Tm7fFGFqd7gfxZgp1uUqqMWJYpdC";
    private final String TEST_ADDRESS_4 = "ARQz2ywwqKDUJno8kfu5ZDAfWWJwfikxSh";

    private final String TEST_ADDRESS_MAIN = "AMF4Yqv1PMry2BDc9ELttPsMsohaw2D3fT";

    private Merchant merchantNeo;
    private Merchant merchantGas;
    private Currency currencyNeo;
    private Currency currencyGas;

    private NeoTransaction neoTransaction1;
    private NeoTransaction neoTransaction2;

    private Block block1;
    private Block block2;

    private RefillRequestFlatDto refillRequestFlatDto1;
    private RefillRequestFlatDto refillRequestFlatDto2;

    @InjectMocks
    private NeoServiceImpl neoService = new NeoServiceImpl(null, null, null, "neo.properties");

    @Before
    public void setUp() {

        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn(" ");

        currencyNeo = new Currency();
        currencyNeo.setId(1);
        currencyNeo.setName("NEO");

        currencyGas = new Currency();
        currencyGas.setId(2);
        currencyGas.setName("GAS");

        merchantNeo = new Merchant();
        merchantNeo.setId(3);
        merchantNeo.setName("NEO");
        merchantNeo.setServiceBeanName("neoServiceImpl");
        merchantNeo.setProcessType(MerchantProcessType.CRYPTO);
        merchantNeo.setRefillOperationCountLimitForUserPerDay(5);
        merchantNeo.setAdditionalTagForWithdrawAddressIsUsed(false);
        merchantGas = new Merchant();
        merchantGas.setId(4);
        merchantGas.setName("GAS");
        merchantGas.setServiceBeanName("neoServiceImpl");
        merchantGas.setProcessType(MerchantProcessType.CRYPTO);
        merchantGas.setRefillOperationCountLimitForUserPerDay(5);
        merchantGas.setAdditionalTagForWithdrawAddressIsUsed(false);

        when(currencyService.findByName("NEO")).thenReturn(currencyNeo);
        when(merchantService.findByName("NEO")).thenReturn(merchantNeo);
        when(currencyService.findByName("GAS")).thenReturn(currencyGas);
        when(merchantService.findByName("GAS")).thenReturn(merchantGas);

        neoTransaction1 = new NeoTransaction("0x0c41d4278a2f1ee537db3647f3a61f6b07b224f185d979692a33a8ee7ecf465d", "ContractTransaction",
                Arrays.asList(
                        new NeoVout(0, "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7", "3", TEST_ADDRESS_1),
                        new NeoVout(1, "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7", "5", TEST_ADDRESS_2),
                        new NeoVout(2, "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7", "992", "AXADAvUk6wpVsrfK952pXuxvyuoNPGFS8H"),
                        new NeoVout(3, "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b", "2", TEST_ADDRESS_2),
                        new NeoVout(4, "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b", "18", "AU2JJwUFKToQ7p7eW6NZnC5ty6j7aK9fJT")
                ), null, null);


        neoTransaction2 = new NeoTransaction("0xd7fe01030380b1d910007f19c047c262baa60fcd7272fe56ece2bf19c33b1a98", "ContractTransaction",
                Arrays.asList(
                        new NeoVout(0, "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b", "94", TEST_ADDRESS_4),
                        new NeoVout(1, "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b", "86", "APtsaZ1sYDtiyrLasiV9rAtak19CEYn2WU")
                ), null, null);

        block1 = new Block();
        block1.setIndex(12345);
        block1.setHash("0xb22be86e26227a7eef26fb24fc808f1006a0c50bfff86766a14555cf0d093315");
        block1.setTime(System.currentTimeMillis());
        block1.setConfirmations(0);
        block1.setTx(Arrays.asList(
                new NeoTransaction("0x7fdd4dcb68ded00059da90d6477e89ded1db486f14266eb6974adf5822957f44", "MinerTransaction",
                        Collections.emptyList(), null, null),
                new NeoTransaction("0x7882b6d7ea52b3e2ec06680fd4c1d831732a761471c30dc7dc40dbb6ccf5d2c0", "ContractTransaction",
                        Collections.emptyList(), null, null), neoTransaction1,
                new NeoTransaction("0xb7f7a42a96813f01267eb733edfbe094eedb2643521d4663234492086fae443e", "ContractTransaction",
                        Arrays.asList(new NeoVout(0, "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7",
                                "0.00048704", "ANaydHNQ3z63s25WvWkZtpVyhQymGWGci7"),
                                new NeoVout(1, "some_unknown_asset",
                                        "0.00048704", TEST_ADDRESS_2)
                                ), null, null)
        ));

        block2 = new Block();
        block2.setIndex(12344);
        block2.setHash("0xdaf54694276e04cd55c8674867bca40768d58b3e1899809aa517f944917f7c53");
        block2.setTime(System.currentTimeMillis() - 120000L);
        block2.setConfirmations(2);
        block2.setTx(Arrays.asList(
                new NeoTransaction("0xbc11aed32cf94699739269e75a8ebbd0bfa080e7ed84d4769846dfa076f2e8aa", "MinerTransaction",
                        Collections.emptyList(), null, null),
                new NeoTransaction("0xf6ab4bfe13beefe5bc5d4431d17c22a058cf631e5f084d840157908fa7719c7f", "ContractTransaction",
                        Collections.emptyList(), null, null), neoTransaction2,
                new NeoTransaction("0x3aa0a7de9606f8338cb6891af9a1983bfb7949b2f7f67d8aa8e50c2482af0ce1", "ContractTransaction",
                        Arrays.asList(new NeoVout(0, "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7",
                                "0.05", TEST_ADDRESS_1),
                                new NeoVout(1, "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7",
                                        "9.95", "ANaydHNQ3z63s25WvWkZtpVyhQymGWGci7")), null, null)
        ));


        when(neoNodeService.getTransactionById("0x0c41d4278a2f1ee537db3647f3a61f6b07b224f185d979692a33a8ee7ecf465d")).thenReturn(Optional.of(neoTransaction1));
        when(neoNodeService.getTransactionById("0xd7fe01030380b1d910007f19c047c262baa60fcd7272fe56ece2bf19c33b1a98")).thenReturn(Optional.of(neoTransaction2));
        when(neoNodeService.getBlock(12345)).thenReturn(Optional.of(block1));
        when(neoNodeService.getBlock(12344)).thenReturn(Optional.of(block2));
        when(neoNodeService.getBlockCount()).thenReturn(12346);
        MerchantSpecParamDto merchantSpecParamDto = new MerchantSpecParamDto();
        merchantSpecParamDto.setParamValue("12344");
        when(specParamsDao.getByMerchantNameAndParamName(anyString(), anyString())).thenReturn(merchantSpecParamDto);
        when(refillService.findAllAddresses(merchantNeo.getId(), currencyNeo.getId())).thenReturn(Arrays.asList(TEST_ADDRESS_1, TEST_ADDRESS_2, TEST_ADDRESS_3));

        refillRequestFlatDto1 = new RefillRequestFlatDto();
        refillRequestFlatDto1.setAmount(new BigDecimal(3));
        refillRequestFlatDto1.setAddress(TEST_ADDRESS_1);
        refillRequestFlatDto1.setStatus(RefillStatusEnum.ON_BCH_EXAM);
        refillRequestFlatDto1.setMerchantTransactionId("0x0c41d4278a2f1ee537db3647f3a61f6b07b224f185d979692a33a8ee7ecf465d");
        refillRequestFlatDto1.setCurrencyId(currencyGas.getId());
        refillRequestFlatDto1.setMerchantId(merchantGas.getId());


        refillRequestFlatDto2 = new RefillRequestFlatDto();
        refillRequestFlatDto2.setAmount(new BigDecimal(94));
        refillRequestFlatDto2.setAddress(TEST_ADDRESS_4);
        refillRequestFlatDto2.setStatus(RefillStatusEnum.ON_BCH_EXAM);
        refillRequestFlatDto2.setMerchantTransactionId("0xd7fe01030380b1d910007f19c047c262baa60fcd7272fe56ece2bf19c33b1a98");
        refillRequestFlatDto2.setCurrencyId(currencyNeo.getId());
        refillRequestFlatDto2.setMerchantId(merchantNeo.getId());

        when(refillService.getInExamineByMerchantIdAndCurrencyIdList(merchantNeo.getId(), currencyNeo.getId())).thenReturn(Arrays.asList(refillRequestFlatDto2));
        when(refillService.getInExamineByMerchantIdAndCurrencyIdList(merchantGas.getId(), currencyGas.getId())).thenReturn(Arrays.asList(refillRequestFlatDto1));
        when(refillService.createRefillRequestByFact(any())).thenReturn(20);
        when(refillService.getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(anyString(), anyInt(), anyInt())).thenReturn(Optional.empty());
        when(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString())).thenReturn(Optional.empty());
        Map<String, AssetMerchantCurrencyDto> neoAssetMap = new HashMap<String, AssetMerchantCurrencyDto>() {{
            put(NeoAsset.NEO.getId(), new AssetMerchantCurrencyDto(NeoAsset.NEO, merchantNeo, currencyNeo));
            put(NeoAsset.GAS.getId(), new AssetMerchantCurrencyDto(NeoAsset.GAS, merchantGas, currencyGas));
        }};
        ReflectionTestUtils.setField(neoService, "mainAccount", TEST_ADDRESS_MAIN);
        ReflectionTestUtils.setField(neoService, "minConfirmations", 10);
        ReflectionTestUtils.setField(neoService, "mainMerchant", merchantNeo);
        ReflectionTestUtils.setField(neoService, "mainCurency", currencyNeo);
        ReflectionTestUtils.setField(neoService, "neoAssetMap", neoAssetMap);
        neoService.init();
    }



    @Test
    public void changeConfirmationsOrProvideTest_NotConfirming() throws RefillRequestAppropriateNotFoundException {
        ReflectionTestUtils.setField(neoService, "neoNodeService", neoNodeService);
        RefillRequestSetConfirmationsNumberDto dto = RefillRequestSetConfirmationsNumberDto.builder()
                .confirmations(3).build();
        neoService.changeConfirmationsOrProvide(dto, "");
        verify(refillService).setConfirmationCollectedNumber(dto);
        verify(refillService, never()).autoAcceptRefillRequest(any());
        verify(neoNodeService, never()).sendToAddress(any(NeoAsset.class), anyString(), any(), anyString());
        ReflectionTestUtils.setField(neoService, "neoNodeService", null);
    }

    @Test
    public void changeConfirmationsOrProvideTest_Confirming() throws RefillRequestAppropriateNotFoundException {
        ReflectionTestUtils.setField(neoService, "neoNodeService", neoNodeService);
        RefillRequestSetConfirmationsNumberDto dto = RefillRequestSetConfirmationsNumberDto.builder()
                .amount(new BigDecimal(25))
                .currencyId(currencyNeo.getId())
                .merchantId(merchantNeo.getId())
                .requestId(25)
                .confirmations(10).build();
        neoService.changeConfirmationsOrProvide(dto, "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b");

        verify(refillService).setConfirmationCollectedNumber(dto);
        verify(refillService).autoAcceptRefillRequest(any());
        verify(neoNodeService).sendToAddress(eq(NeoAsset.NEO), eq(TEST_ADDRESS_MAIN), eq(new BigDecimal(25)), eq(TEST_ADDRESS_MAIN));
        ReflectionTestUtils.setField(neoService, "neoNodeService", null);
    }

    @Test
    public void processNeoPaymentTest_New() throws RefillRequestAppropriateNotFoundException {

        ArgumentCaptor<RefillRequestAcceptDto> requestArgumentCaptor = ArgumentCaptor.forClass(RefillRequestAcceptDto.class);
        when(refillService.createRefillRequestByFact(requestArgumentCaptor.capture())).thenReturn(20);
        neoService.processNeoPayment(neoTransaction1.getTxid(), neoTransaction1.getVout().get(0), block1.getConfirmations(), block1.getHash());
        verify(refillService).putOnBchExamRefillRequest(any());
        RefillRequestAcceptDto actual = requestArgumentCaptor.getValue();
        assertEquals(new BigDecimal(3), actual.getAmount());
        assertEquals(Integer.valueOf(currencyGas.getId()), actual.getCurrencyId());
        assertEquals(Integer.valueOf(merchantGas.getId()), actual.getMerchantId());
    }

    @Test
    public void processNeoPaymentTest_NewConfirming() throws RefillRequestAppropriateNotFoundException {
        ReflectionTestUtils.setField(neoService, "neoNodeService", neoNodeService);
        block1.setConfirmations(20);
        ArgumentCaptor<RefillRequestAcceptDto> requestArgumentCaptor = ArgumentCaptor.forClass(RefillRequestAcceptDto.class);
        when(refillService.createRefillRequestByFact(requestArgumentCaptor.capture())).thenReturn(20);
        neoService.processNeoPayment(neoTransaction1.getTxid(), neoTransaction1.getVout().get(0), block1.getConfirmations(), block1.getHash());
        verify(refillService, never()).putOnBchExamRefillRequest(any());
        verify(refillService).setConfirmationCollectedNumber(any());
        RefillRequestAcceptDto actual = requestArgumentCaptor.getValue();
        assertEquals(new BigDecimal(3), actual.getAmount());
        assertEquals(Integer.valueOf(currencyGas.getId()), actual.getCurrencyId());
        assertEquals(Integer.valueOf(merchantGas.getId()), actual.getMerchantId());
        ReflectionTestUtils.setField(neoService, "neoNodeService", null);
    }

    @Test
    public void processNeoPaymentTest_Existing() throws RefillRequestAppropriateNotFoundException {
        block2.setConfirmations(5);
        when(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString())).thenReturn(Optional.of(20));
        ArgumentCaptor<RefillRequestSetConfirmationsNumberDto> requestArgumentCaptor = ArgumentCaptor.forClass(RefillRequestSetConfirmationsNumberDto.class);

        neoService.processNeoPayment(neoTransaction2.getTxid(), neoTransaction2.getVout().get(0), block2.getConfirmations(), block2.getHash());
        verify(refillService, never()).createRefillRequestByFact(any());
        verify(refillService, never()).putOnBchExamRefillRequest(any());
        verify(refillService).setConfirmationCollectedNumber(requestArgumentCaptor.capture());
        RefillRequestSetConfirmationsNumberDto actual = requestArgumentCaptor.getValue();
        assertEquals(new BigDecimal(94), actual.getAmount());

        assertEquals(Integer.valueOf(currencyNeo.getId()), actual.getCurrencyId());
        assertEquals(Integer.valueOf(merchantNeo.getId()), actual.getMerchantId());
    }


    /*@Test
    public void scanBlocksTest_AllNew() throws RefillRequestAppropriateNotFoundException {
        ReflectionTestUtils.setField(neoService, "neoNodeService", neoNodeService);
        ArgumentCaptor<RefillRequestPutOnBchExamDto> requestArgumentCaptor = ArgumentCaptor.forClass(RefillRequestPutOnBchExamDto.class);
        neoService.scanBlocks();
        verify(refillService, times(4)).putOnBchExamRefillRequest(requestArgumentCaptor.capture());
        List<RefillRequestPutOnBchExamDto> actualResults = requestArgumentCaptor.getAllValues();
        actualResults.sort(Comparator.comparing(RefillRequestPutOnBchExamDto::getBlockhash));
        assertEquals(new BigDecimal(3), actualResults.get(0).getAmount());
        assertEquals(new BigDecimal(5), actualResults.get(1).getAmount());
        assertEquals(new BigDecimal(2), actualResults.get(2).getAmount());
        assertEquals(new BigDecimal(0.05).setScale(2, BigDecimal.ROUND_HALF_DOWN), actualResults.get(3).getAmount());

        assertEquals(Integer.valueOf(currencyNeo.getId()), actualResults.get(2).getCurrencyId());
        assertEquals(Integer.valueOf(currencyGas.getId()), actualResults.get(0).getCurrencyId());

        assertEquals(actualResults.get(0).getAddress(), TEST_ADDRESS_1);
        assertEquals(actualResults.get(2).getAddress(), TEST_ADDRESS_2);
        ReflectionTestUtils.setField(neoService, "neoNodeService", null);
    }*/

    /*@Test
    public void scanBlocksTest_GetBlockException() throws RefillRequestAppropriateNotFoundException {
        ReflectionTestUtils.setField(neoService, "neoNodeService", neoNodeService);
        when(neoNodeService.getBlock(12344)).thenReturn(Optional.empty());
        ArgumentCaptor<RefillRequestPutOnBchExamDto> requestArgumentCaptor = ArgumentCaptor.forClass(RefillRequestPutOnBchExamDto.class);
        neoService.scanBlocks();
        verify(refillService, times(3)).putOnBchExamRefillRequest(requestArgumentCaptor.capture());
        List<RefillRequestPutOnBchExamDto> actualResults = requestArgumentCaptor.getAllValues();
        assertEquals(new BigDecimal(3), actualResults.get(0).getAmount());
        assertEquals(new BigDecimal(5), actualResults.get(1).getAmount());
        assertEquals(new BigDecimal(2), actualResults.get(2).getAmount());

        assertEquals(Integer.valueOf(currencyNeo.getId()), actualResults.get(2).getCurrencyId());
        assertEquals(Integer.valueOf(currencyGas.getId()), actualResults.get(0).getCurrencyId());

        assertEquals(actualResults.get(0).getAddress(), TEST_ADDRESS_1);
        assertEquals(actualResults.get(2).getAddress(), TEST_ADDRESS_2);
        ReflectionTestUtils.setField(neoService, "neoNodeService", null);
    }*/

   /* @Test
    public void updateExistingPaymentsTest() throws RefillRequestAppropriateNotFoundException {
        ReflectionTestUtils.setField(neoService, "neoNodeService", neoNodeService);
        neoTransaction1.setConfirmations(5);
        neoTransaction2.setConfirmations(20);
        ArgumentCaptor<RefillRequestSetConfirmationsNumberDto> requestArgumentCaptor = ArgumentCaptor.forClass(RefillRequestSetConfirmationsNumberDto.class);
        neoService.updateExistingPayments();
        verify(refillService, times(2)).setConfirmationCollectedNumber(requestArgumentCaptor.capture());
        List<RefillRequestSetConfirmationsNumberDto> actualResults = requestArgumentCaptor.getAllValues();
        assertEquals(new BigDecimal(3), actualResults.get(0).getAmount());
        assertEquals(new BigDecimal(94), actualResults.get(1).getAmount());

        assertEquals(Integer.valueOf(currencyGas.getId()), actualResults.get(0).getCurrencyId());
        assertEquals(Integer.valueOf(currencyNeo.getId()), actualResults.get(1).getCurrencyId());

        assertEquals(actualResults.get(0).getAddress(), TEST_ADDRESS_1);
        assertEquals(actualResults.get(1).getAddress(), TEST_ADDRESS_4);
        ReflectionTestUtils.setField(neoService, "neoNodeService", null);
    }*/


    /*@Test
    public void processPaymentTest_OK() throws RefillRequestAppropriateNotFoundException {
        ReflectionTestUtils.setField(neoService, "neoNodeService", neoNodeService);
        neoTransaction1.setConfirmations(5);
        neoTransaction2.setConfirmations(20);
        ArgumentCaptor<RefillRequestSetConfirmationsNumberDto> requestArgumentCaptor = ArgumentCaptor.forClass(RefillRequestSetConfirmationsNumberDto.class);
        Map<String, String> params = new HashMap<>();
        params.put("txId", "0x0c41d4278a2f1ee537db3647f3a61f6b07b224f185d979692a33a8ee7ecf465d");
        params.put("address", TEST_ADDRESS_1);

        neoService.updateExistingPayments();
        verify(refillService, times(2)).setConfirmationCollectedNumber(requestArgumentCaptor.capture());
        List<RefillRequestSetConfirmationsNumberDto> actualResults = requestArgumentCaptor.getAllValues();
        assertEquals(new BigDecimal(3), actualResults.get(0).getAmount());
        assertEquals(new BigDecimal(94), actualResults.get(1).getAmount());

        assertEquals(Integer.valueOf(currencyGas.getId()), actualResults.get(0).getCurrencyId());
        assertEquals(Integer.valueOf(currencyNeo.getId()), actualResults.get(1).getCurrencyId());

        assertEquals(actualResults.get(0).getAddress(), TEST_ADDRESS_1);
        assertEquals(actualResults.get(1).getAddress(), TEST_ADDRESS_4);
        ReflectionTestUtils.setField(neoService, "neoNodeService", null);
    }*/















}
