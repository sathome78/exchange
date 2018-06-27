package me.exrates.service.impl;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestSetConfirmationsNumberDto;
import me.exrates.model.dto.merchants.btc.*;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.btcCore.CoreWalletService;
import me.exrates.service.exception.BitcoinCoreException;
import me.exrates.service.exception.BtcPaymentNotFoundException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BitcoinServiceImplTest {

    @Mock
    private RefillService refillService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private CoreWalletService bitcoinWalletService;

    @Captor
    private ArgumentCaptor<Map<String, BigDecimal>> paymentCaptor;



    private final String TEST_ADDRESS_1 = "mkF9CpKJYwSaeeW3AZGQefKmW3EBpSjn3y";
    private final String TEST_ADDRESS_2 = "mfsnh7prA1m94RGKjM7n6i2wLD8aTsAckQ";
    private final String TEST_ADDRESS_3 = "mqydecE4zaEw8Qy4iL5Bk8ZiEdBbRQ1Zmh";

    private final String TEST_TX_ID_1 = "a5cce5ae8252f61af4fe17e104acc0f3c820023d9a5704134e91da6af4c52d84";
    private final String TEST_TX_ID_2 = "d8302f411bf29e9ab7ad2223e65e99321dee6a923f78973f381bfee77bbe70c5";

    private Map<String, String> params1;
    private Map<String, String> params2;

    private BtcTransactionDto btcTransactionDto1;
    private BtcTransactionDto btcTransactionDto2;

    private BtcPaymentFlatDto btcPaymentFlatDto1;
    private BtcPaymentFlatDto btcPaymentFlatDto2;

    private RefillRequestFlatDto refillRequestFlatDto1;
    private RefillRequestFlatDto refillRequestFlatDto2;

    @InjectMocks
    private BitcoinServiceImpl bitcoinService = new BitcoinServiceImpl(true,"Bitcoin", "BTC", 4);



    @Before
    public void setUp() {
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn(" ");

        Currency currency = new Currency();
        currency.setId(4);
        currency.setName("BTC");

        Merchant merchant = new Merchant();
        merchant.setId(3);
        merchant.setName("Bitcoin");
        merchant.setServiceBeanName("BitcoinServiceImpl");
        merchant.setProcessType(MerchantProcessType.CRYPTO);
        merchant.setRefillOperationCountLimitForUserPerDay(5);
        merchant.setAdditionalTagForWithdrawAddressIsUsed(false);

        when(currencyService.findByName("BTC")).thenReturn(currency);
        when(merchantService.findByName("Bitcoin")).thenReturn(merchant);

        when(merchantService.getCoreWalletPassword("Bitcoin", "BTC")).thenReturn(Optional.of("pass123"));

        when(bitcoinWalletService.getNewAddress(anyString()))
                .thenReturn(TEST_ADDRESS_1)
                .thenReturn(TEST_ADDRESS_2);

        btcTransactionDto1 = new BtcTransactionDto();
        btcTransactionDto1.setTxId(TEST_TX_ID_1);
        btcTransactionDto1.setAmount(new BigDecimal(0.5));
        btcTransactionDto1.setFee(new BigDecimal(0.0002));
        btcTransactionDto1.setConfirmations(2);
        BtcTxPaymentDto btcTxPaymentDto1 = new BtcTxPaymentDto();
        btcTxPaymentDto1.setAddress(TEST_ADDRESS_1);
        btcTxPaymentDto1.setAmount(new BigDecimal(0.5));
        btcTxPaymentDto1.setFee(new BigDecimal(0.0002));
        btcTxPaymentDto1.setCategory("RECEIVE");
        btcTransactionDto1.setDetails(Collections.singletonList(btcTxPaymentDto1));

        btcTransactionDto2 = new BtcTransactionDto();
        btcTransactionDto2.setTxId(TEST_TX_ID_2);
        btcTransactionDto2.setAmount(new BigDecimal(0.5));
        btcTransactionDto2.setFee(new BigDecimal(0.0002));
        btcTransactionDto2.setConfirmations(5);
        BtcTxPaymentDto btcTxPaymentDto2 = new BtcTxPaymentDto();
        btcTxPaymentDto2.setAddress(TEST_ADDRESS_2);
        btcTxPaymentDto2.setAmount(new BigDecimal(0.5));
        btcTxPaymentDto2.setFee(new BigDecimal(0.0002));
        btcTxPaymentDto2.setCategory("RECEIVE");
        btcTransactionDto2.setDetails(Collections.singletonList(btcTxPaymentDto2));

        btcPaymentFlatDto1 = BtcPaymentFlatDto.builder().address(TEST_ADDRESS_1)
                .amount(btcTransactionDto1.getAmount())
                .confirmations(btcTransactionDto1.getConfirmations())
                .txId(btcTransactionDto1.getTxId())
                .blockhash(btcTransactionDto1.getBlockhash())
                .currencyId(currency.getId())
                .merchantId(merchant.getId())
                .build();
        btcPaymentFlatDto2 = BtcPaymentFlatDto.builder().address(TEST_ADDRESS_2)
                .amount(btcTransactionDto2.getAmount())
                .confirmations(btcTransactionDto2.getConfirmations())
                .txId(btcTransactionDto2.getTxId())
                .blockhash(btcTransactionDto2.getBlockhash())
                .currencyId(currency.getId())
                .merchantId(merchant.getId())
                .build();

        params1 = new HashMap<String, String>() {{
           put("address", TEST_ADDRESS_1);
           put("txId", TEST_TX_ID_1);
        }};
        params2 = new HashMap<String, String>() {{
            put("address", TEST_ADDRESS_2);
            put("txId", TEST_TX_ID_2);
        }};

        refillRequestFlatDto1 = new RefillRequestFlatDto();
        refillRequestFlatDto1.setAmount(new BigDecimal(0.3));
        refillRequestFlatDto1.setAddress(TEST_ADDRESS_1);
        refillRequestFlatDto1.setStatus(RefillStatusEnum.ON_BCH_EXAM);
        refillRequestFlatDto1.setMerchantTransactionId(TEST_TX_ID_1);
        refillRequestFlatDto1.setCurrencyId(currency.getId());
        refillRequestFlatDto1.setMerchantId(merchant.getId());

        refillRequestFlatDto2 = new RefillRequestFlatDto();
        refillRequestFlatDto2.setAmount(new BigDecimal(0.5));
        refillRequestFlatDto2.setAddress(TEST_ADDRESS_2);
        refillRequestFlatDto2.setStatus(RefillStatusEnum.ON_BCH_EXAM);
        refillRequestFlatDto2.setMerchantTransactionId(TEST_TX_ID_2);
        refillRequestFlatDto2.setCurrencyId(currency.getId());
        refillRequestFlatDto2.setMerchantId(merchant.getId());

        when(merchantService.getSubtractFeeFromAmount(merchant.getId(), currency.getId())).thenReturn(false);




    }



    @Test
    public void refillNewAddressTest() {
        RefillRequestCreateDto dto = new RefillRequestCreateDto();
        when(refillService.existsClosedRefillRequestForAddress(anyString(), anyInt(), anyInt())).thenReturn(false);
        assertEquals(new HashMap<String, String>() {{
            put("address", TEST_ADDRESS_1);
            put("message", " ");
            put("qr", TEST_ADDRESS_1);
        }}, bitcoinService.refill(dto));
    }

 //   @Test
    public void refillExistingAddressTest() {
        RefillRequestCreateDto dto = new RefillRequestCreateDto();
        when(refillService.existsClosedRefillRequestForAddress(anyString(), anyInt(), anyInt())).thenReturn(true).thenReturn(false);
        assertEquals(new HashMap<String, String>() {{
            put("address", TEST_ADDRESS_2);
            put("message", " ");
            put("qr", TEST_ADDRESS_2);
        }}, bitcoinService.refill(dto));
    }

 //   @Test(expected = IllegalStateException.class)
    public void refillExistingAddressOverLimitTest() {
        RefillRequestCreateDto dto = new RefillRequestCreateDto();
        when(refillService.existsClosedRefillRequestForAddress(anyString(), anyInt(), anyInt())).thenReturn(true);
        bitcoinService.refill(dto);
    }

    @Test(expected = NullPointerException.class)
    public void processPaymentEmptyParamsTest() throws RefillRequestAppropriateNotFoundException {
        bitcoinService.processPayment(Collections.emptyMap());
    }

    @Test(expected = NullPointerException.class)
    public void processPaymentLackingAddressTest() throws RefillRequestAppropriateNotFoundException {
        bitcoinService.processPayment(Collections.singletonMap("txId", anyString()));
    }

    @Test(expected = NullPointerException.class)
    public void processPaymentLackingTxIdTest() throws RefillRequestAppropriateNotFoundException {
        bitcoinService.processPayment(Collections.singletonMap("address", anyString()));
    }

    @Test(expected = BtcPaymentNotFoundException.class)
    public void processPaymentNotFoundTest() throws RefillRequestAppropriateNotFoundException {
        btcTransactionDto1.getDetails().get(0).setAddress(TEST_ADDRESS_2);
        when(bitcoinWalletService.getTransaction(TEST_TX_ID_1)).thenReturn(btcTransactionDto1);
        bitcoinService.processPayment(params1);
    }

    @Test
    public void processBtcPaymentAlreadyOnBchExamTest() throws RefillRequestAppropriateNotFoundException {
        when(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.of(10));
        bitcoinService.processBtcPayment(btcPaymentFlatDto1);
        verify(refillService, never()).getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(anyString(), anyInt(), anyInt());
    }

    @Test
    public void processBtcPaymentAlreadyCreatedTest() throws RefillRequestAppropriateNotFoundException {
        when(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());
        when(refillService.getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(anyString(), anyInt(), anyInt())).thenReturn(Optional.of(10));
        bitcoinService.processBtcPayment(btcPaymentFlatDto1);
        verify(refillService, never()).createRefillRequestByFact(any());
    }

    @Test
    public void processBtcPaymentNewTest() throws RefillRequestAppropriateNotFoundException {
        when(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());
        when(refillService.getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(anyString(), anyInt(), anyInt())).thenReturn(Optional.empty());
        bitcoinService.processBtcPayment(btcPaymentFlatDto1);
        verify(refillService).createRefillRequestByFact(any());
    }

    @Test
    public void processBtcPaymentNotYetConfirmedTest() throws RefillRequestAppropriateNotFoundException {
        when(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());
        when(refillService.getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(anyString(), anyInt(), anyInt())).thenReturn(Optional.empty());
        when(refillService.createRefillRequestByFact(any())).thenReturn(10);
        bitcoinService.processBtcPayment(btcPaymentFlatDto1);
        verify(refillService).putOnBchExamRefillRequest(any());
    }

    @Test
    public void processBtcPaymentEnoughConfirmationsToAcceptTest() throws RefillRequestAppropriateNotFoundException {
        when(refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Optional.empty());
        when(refillService.getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(anyString(), anyInt(), anyInt())).thenReturn(Optional.empty());
        when(refillService.createRefillRequestByFact(any())).thenReturn(10);
        bitcoinService.processBtcPayment(btcPaymentFlatDto2);
        verify(refillService).autoAcceptRefillRequest(any());
    }

    @Test
    public void onPaymentConflictedNoValidTxTest() {

        when(bitcoinWalletService.handleTransactionConflicts(TEST_TX_ID_1)).thenReturn(Optional.empty());
        bitcoinService.onPayment(btcTransactionDto1);
        verify(refillService, never()).getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), anyString());

    }

    @Test
    public void onPaymentConflictedValidTxTest() {

        when(bitcoinWalletService.handleTransactionConflicts(TEST_TX_ID_1)).thenReturn(Optional.of(btcTransactionDto2));
        bitcoinService.onPayment(btcTransactionDto1);
        verify(refillService).getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(anyString(), anyInt(), anyInt(), eq(TEST_TX_ID_2));

    }

    @Test
    public void changeConfirmationsOrProvideNotConfirmingTest() throws RefillRequestAppropriateNotFoundException {
        RefillRequestSetConfirmationsNumberDto dto = RefillRequestSetConfirmationsNumberDto.builder()
                .address(btcPaymentFlatDto1.getAddress())
                .amount(btcPaymentFlatDto1.getAmount())
                .currencyId(btcPaymentFlatDto1.getCurrencyId())
                .merchantId(btcPaymentFlatDto1.getMerchantId())
                .requestId(25)
                .confirmations(3)
                .blockhash(btcPaymentFlatDto1.getBlockhash())
                .hash(btcPaymentFlatDto1.getTxId()).build();
        bitcoinService.changeConfirmationsOrProvide(dto);

        verify(refillService).setConfirmationCollectedNumber(dto);
        verify(refillService, never()).autoAcceptRefillRequest(any());
    }

    @Test
    public void changeConfirmationsOrProvideConfirmingTest() throws RefillRequestAppropriateNotFoundException {
        RefillRequestSetConfirmationsNumberDto dto = RefillRequestSetConfirmationsNumberDto.builder()
                .address(btcPaymentFlatDto1.getAddress())
                .amount(btcPaymentFlatDto1.getAmount())
                .currencyId(btcPaymentFlatDto1.getCurrencyId())
                .merchantId(btcPaymentFlatDto1.getMerchantId())
                .requestId(25)
                .confirmations(4)
                .blockhash(btcPaymentFlatDto1.getBlockhash())
                .hash(btcPaymentFlatDto1.getTxId()).build();
        bitcoinService.changeConfirmationsOrProvide(dto);

        verify(refillService).setConfirmationCollectedNumber(dto);
        verify(refillService).autoAcceptRefillRequest(any());
    }

    @Test
    public void onIncomingBlockAllCorrectTest() throws RefillRequestAppropriateNotFoundException {

        List<RefillRequestFlatDto> requests = Arrays.asList(refillRequestFlatDto1, refillRequestFlatDto2);

        when(refillService.getInExamineByMerchantIdAndCurrencyIdList(anyInt(), anyInt()))
                .thenReturn(requests);
        when(bitcoinWalletService.handleTransactionConflicts(TEST_TX_ID_1)).thenReturn(Optional.of(btcTransactionDto1));
        when(bitcoinWalletService.handleTransactionConflicts(TEST_TX_ID_2)).thenReturn(Optional.of(btcTransactionDto2));
        BtcBlockDto blockDto = new BtcBlockDto(" ", 123456, System.currentTimeMillis());
        bitcoinService.onIncomingBlock(blockDto);
        verify(refillService, times(requests.size())).setConfirmationCollectedNumber(any());
    }

    @Test
    public void onIncomingBlockTxMultipleAddressesTest() throws RefillRequestAppropriateNotFoundException {
        refillRequestFlatDto2.setMerchantTransactionId(TEST_TX_ID_1);
        List<RefillRequestFlatDto> requests = Arrays.asList(refillRequestFlatDto1, refillRequestFlatDto2);
        when(refillService.getInExamineByMerchantIdAndCurrencyIdList(anyInt(), anyInt()))
                .thenReturn(requests);
        when(bitcoinWalletService.handleTransactionConflicts(TEST_TX_ID_1)).thenReturn(Optional.of(btcTransactionDto1));
        List<BtcTxPaymentDto> totalPayments = new ArrayList<>(btcTransactionDto1.getDetails());
        totalPayments.addAll(btcTransactionDto2.getDetails());
        btcTransactionDto1.setDetails(totalPayments);
        BtcBlockDto blockDto = new BtcBlockDto(" ", 123456, System.currentTimeMillis());
        bitcoinService.onIncomingBlock(blockDto);
        verify(refillService, times(requests.size())).setConfirmationCollectedNumber(any());
    }

    @Test
    public void onIncomingBlockTxMultipleAddressesNotCorrespondingTest() throws RefillRequestAppropriateNotFoundException {
        refillRequestFlatDto2.setMerchantTransactionId(TEST_TX_ID_1);
        refillRequestFlatDto2.setAddress("qwerty");
        List<RefillRequestFlatDto> requests = Arrays.asList(refillRequestFlatDto1, refillRequestFlatDto2);
        when(refillService.getInExamineByMerchantIdAndCurrencyIdList(anyInt(), anyInt()))
                .thenReturn(requests);
        when(bitcoinWalletService.handleTransactionConflicts(TEST_TX_ID_1)).thenReturn(Optional.of(btcTransactionDto1));
        List<BtcTxPaymentDto> totalPayments = new ArrayList<>(btcTransactionDto1.getDetails());
        totalPayments.addAll(btcTransactionDto2.getDetails());
        btcTransactionDto1.setDetails(totalPayments);
        BtcBlockDto blockDto = new BtcBlockDto(" ", 123456, System.currentTimeMillis());
        bitcoinService.onIncomingBlock(blockDto);
        verify(refillService, times(requests.size() - 1)).setConfirmationCollectedNumber(any());
    }

    @Test
    public void sendManyTest_AllDifferentAddresses() {
        when(bitcoinWalletService.sendToMany(anyMapOf(String.class, BigDecimal.class), eq(false)))
                .thenReturn(new BtcPaymentResultDto("111"))
                .thenReturn(new BtcPaymentResultDto("222"));
        List<BtcPaymentResultDetailedDto> response = bitcoinService.sendToMany(Arrays.asList(
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.5)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_2, new BigDecimal(0.7)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_3, new BigDecimal(0.9))
        ));
        assertEquals(response, Arrays.asList(
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.5), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_2, new BigDecimal(0.7), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_3, new BigDecimal(0.9), new BtcPaymentResultDto("111"))
        ));


    }

    @Test
    public void sendManyTest_AllSameAddresses() {
        when(bitcoinWalletService.sendToMany(anyMapOf(String.class, BigDecimal.class), eq(false)))
                .thenReturn(new BtcPaymentResultDto("111"))
                .thenReturn(new BtcPaymentResultDto("222"))
                .thenReturn(new BtcPaymentResultDto("333"));

        List<BtcPaymentResultDetailedDto> response = bitcoinService.sendToMany(Arrays.asList(
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.5)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.7)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.9))
        ));
        verify(bitcoinWalletService, times(3)).sendToMany(paymentCaptor.capture(), eq(false));
        List<Map<String, BigDecimal>> capturedValues = paymentCaptor.getAllValues();
        assertEquals(Collections.singletonMap(TEST_ADDRESS_1, new BigDecimal(0.5)), capturedValues.get(0));
        assertEquals(Collections.singletonMap(TEST_ADDRESS_1, new BigDecimal(0.7)), capturedValues.get(1));
        assertEquals(Collections.singletonMap(TEST_ADDRESS_1, new BigDecimal(0.9)), capturedValues.get(2));
        assertEquals(response, Arrays.asList(
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.5), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.7), new BtcPaymentResultDto("222")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.9), new BtcPaymentResultDto("333"))
        ));

    }

    @Test
    public void sendManyTest_SameAndDifferentAddresses() {
        when(bitcoinWalletService.sendToMany(anyMapOf(String.class, BigDecimal.class), eq(false)))
                .thenReturn(new BtcPaymentResultDto("111"))
                .thenReturn(new BtcPaymentResultDto("222"))
                .thenReturn(new BtcPaymentResultDto("333"));

        List<BtcPaymentResultDetailedDto> response = bitcoinService.sendToMany(Arrays.asList(
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.1)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_2, new BigDecimal(0.2)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.3)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_3, new BigDecimal(0.4)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_2, new BigDecimal(0.5)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_2, new BigDecimal(0.6))
        ));
        verify(bitcoinWalletService, times(3)).sendToMany(paymentCaptor.capture(), eq(false));
        List<Map<String, BigDecimal>> capturedValues = paymentCaptor.getAllValues();
        assertEquals(new HashMap<String, BigDecimal>(){{
            put(TEST_ADDRESS_1, new BigDecimal(0.1));
            put(TEST_ADDRESS_2, new BigDecimal(0.2));
            put(TEST_ADDRESS_3, new BigDecimal(0.4));
        }}, capturedValues.get(0));
        assertEquals(new HashMap<String, BigDecimal>(){{
            put(TEST_ADDRESS_1, new BigDecimal(0.3));
            put(TEST_ADDRESS_2, new BigDecimal(0.5));
        }}, capturedValues.get(1));
        assertEquals(Collections.singletonMap(TEST_ADDRESS_2, new BigDecimal(0.6)), capturedValues.get(2));
        assertEquals(response, Arrays.asList(
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.1), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_2, new BigDecimal(0.2), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_3, new BigDecimal(0.4), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.3), new BtcPaymentResultDto("222")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_2, new BigDecimal(0.5), new BtcPaymentResultDto("222")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_2, new BigDecimal(0.6), new BtcPaymentResultDto("333"))
        ));



    }

    @Test
    public void sendManyTest_SameAndDifferentAddressesAndExceptionThrown() {
        when(bitcoinWalletService.sendToMany(anyMapOf(String.class, BigDecimal.class), eq(false)))
                .thenReturn(new BtcPaymentResultDto("111"))
                .thenReturn(new BtcPaymentResultDto(new BitcoinCoreException("ERROR!")))
                .thenReturn(new BtcPaymentResultDto("333"));
        List<BtcPaymentResultDetailedDto> response = bitcoinService.sendToMany(Arrays.asList(
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.1)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_2, new BigDecimal(0.2)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_1, new BigDecimal(0.3)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_3, new BigDecimal(0.4)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_2, new BigDecimal(0.5)),
                new BtcWalletPaymentItemDto(TEST_ADDRESS_2, new BigDecimal(0.6))
        ));
        assertEquals(response, Arrays.asList(
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.1), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_2, new BigDecimal(0.2), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_3, new BigDecimal(0.4), new BtcPaymentResultDto("111")),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_1, new BigDecimal(0.3), new BtcPaymentResultDto(new BitcoinCoreException("ERROR!"))),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_2, new BigDecimal(0.5), new BtcPaymentResultDto(new BitcoinCoreException("ERROR!"))),
                new BtcPaymentResultDetailedDto(TEST_ADDRESS_2, new BigDecimal(0.6), new BtcPaymentResultDto("333"))
        ));


    }

}
