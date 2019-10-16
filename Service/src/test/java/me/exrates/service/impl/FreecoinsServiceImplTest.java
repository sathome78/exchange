package me.exrates.service.impl;

import me.exrates.dao.FreecoinsRepository;
import me.exrates.model.dto.freecoins.AdminGiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayStatus;
import me.exrates.model.dto.freecoins.ReceiveResultDto;
import me.exrates.service.WalletService;
import me.exrates.service.exception.FreecoinsException;
import me.exrates.service.freecoins.FreecoinsService;
import me.exrates.service.freecoins.FreecoinsServiceImpl;
import me.exrates.service.util.CollectionUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FreecoinsServiceImplTest {

    private static final String CURRENCY_NAME = "BTC";
    private static final String CREATOR_EMAIL = "test1@gmail.com";
    private static final String RECEIVER_EMAIL = "test2@gmail.com";

    @Mock
    private FreecoinsRepository freecoinsRepository;
    @Mock
    private WalletService walletService;

    private FreecoinsService freecoinsService;

    @Before
    public void setUp() throws Exception {
        freecoinsService = spy(new FreecoinsServiceImpl(freecoinsRepository, walletService));
    }

    @Test
    public void updateGiveawayStatuses_ok() {
        doReturn(true)
                .when(freecoinsRepository)
                .updateStatuses();

        freecoinsService.updateGiveawayStatuses();

        verify(freecoinsRepository, atLeastOnce()).updateStatuses();
    }

    @Test
    public void processGiveaway_ok() {
        doReturn(true)
                .when(walletService)
                .performFreecoinsGiveawayProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(1)
                .when(freecoinsRepository)
                .saveClaim(any(GiveawayResultDto.class));

        GiveawayResultDto dto = freecoinsService.processGiveaway(CURRENCY_NAME, BigDecimal.TEN, BigDecimal.ONE, true, null, CREATOR_EMAIL);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(CURRENCY_NAME, dto.getCurrencyName());
        assertEquals(BigDecimal.TEN, dto.getAmount());
        assertEquals(BigDecimal.ONE, dto.getPartialAmount());
        assertEquals(10, dto.getTotalQuantity());
        assertTrue(dto.isSingle());
        assertNull(dto.getTimeRange());
        assertEquals(CREATOR_EMAIL, dto.getCreatorEmail());
        assertEquals(GiveawayStatus.CREATED, dto.getStatus());

        verify(walletService, atLeastOnce()).performFreecoinsGiveawayProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).saveClaim(any(GiveawayResultDto.class));
    }

    @Test
    public void processGiveaway_payment_failed() {
        doReturn(false)
                .when(walletService)
                .performFreecoinsGiveawayProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(1)
                .when(freecoinsRepository)
                .saveClaim(any(GiveawayResultDto.class));

        GiveawayResultDto dto = freecoinsService.processGiveaway(CURRENCY_NAME, BigDecimal.TEN, BigDecimal.ONE, true, null, CREATOR_EMAIL);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(CURRENCY_NAME, dto.getCurrencyName());
        assertEquals(BigDecimal.TEN, dto.getAmount());
        assertEquals(BigDecimal.ONE, dto.getPartialAmount());
        assertEquals(10, dto.getTotalQuantity());
        assertTrue(dto.isSingle());
        assertNull(dto.getTimeRange());
        assertEquals(CREATOR_EMAIL, dto.getCreatorEmail());
        assertEquals(GiveawayStatus.FAILED, dto.getStatus());

        verify(walletService, atLeastOnce()).performFreecoinsGiveawayProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).saveClaim(any(GiveawayResultDto.class));
    }

    @Test(expected = FreecoinsException.class)
    public void processGiveaway_claim_not_save() {
        doReturn(true)
                .when(walletService)
                .performFreecoinsGiveawayProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(0)
                .when(freecoinsRepository)
                .saveClaim(any(GiveawayResultDto.class));

        freecoinsService.processGiveaway(CURRENCY_NAME, BigDecimal.TEN, BigDecimal.ONE, true, null, CREATOR_EMAIL);

        verify(walletService, atLeastOnce()).performFreecoinsGiveawayProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).saveClaim(any(GiveawayResultDto.class));
    }

    @Test
    public void processRevokeGiveaway_for_user_ok() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(true)
                .when(walletService)
                .performFreecoinsGiveawayRevokeProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(true)
                .when(freecoinsRepository)
                .updateStatus(anyInt(), any(GiveawayStatus.class));

        boolean revoked = freecoinsService.processRevokeGiveaway(1, true);

        assertTrue(revoked);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(walletService, atLeastOnce()).performFreecoinsGiveawayRevokeProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).updateStatus(anyInt(), any(GiveawayStatus.class));
    }

    @Test
    public void processRevokeGiveaway_for_exchange_ok() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(true)
                .when(freecoinsRepository)
                .updateStatus(anyInt(), any(GiveawayStatus.class));

        boolean revoked = freecoinsService.processRevokeGiveaway(1, false);

        assertTrue(revoked);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(walletService, never()).performFreecoinsGiveawayRevokeProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).updateStatus(anyInt(), any(GiveawayStatus.class));
    }

    @Test(expected = FreecoinsException.class)
    public void processRevokeGiveaway_total_quantity_zero() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(0)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());

        freecoinsService.processRevokeGiveaway(1, true);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(walletService, never()).performFreecoinsGiveawayRevokeProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, never()).updateStatus(anyInt(), any(GiveawayStatus.class));
    }

    @Test
    public void getAllGiveaways_ok() {
        doReturn(Collections.singletonList(new GiveawayResultDto()))
                .when(freecoinsRepository)
                .getAllCreatedClaims();

        List<GiveawayResultDto> list = freecoinsService.getAllGiveaways();

        assertFalse(CollectionUtil.isEmpty(list));

        verify(freecoinsRepository, atLeastOnce()).getAllCreatedClaims();
    }

    @Test
    public void getAllGiveaways_empty_list() {
        doReturn(Collections.emptyList())
                .when(freecoinsRepository)
                .getAllCreatedClaims();

        List<GiveawayResultDto> list = freecoinsService.getAllGiveaways();

        assertTrue(CollectionUtils.isEmpty(list));

        verify(freecoinsRepository, atLeastOnce()).getAllCreatedClaims();
    }

    @Test
    public void processReceive_single_ok1() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(null)
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());
        doReturn(true)
                .when(walletService)
                .performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(1)
                .when(freecoinsRepository)
                .saveProcess(any(ReceiveResultDto.class));
        doReturn(true)
                .when(freecoinsRepository)
                .updateTotalQuantity(anyInt());

        Pair<Boolean, ReceiveResultDto> result = freecoinsService.processReceive(1, RECEIVER_EMAIL);

        assertNotNull(result);
        assertTrue(result.getLeft());

        ReceiveResultDto dto = result.getRight();
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(1, dto.getGiveawayId());
        assertTrue(dto.isReceived());
        assertNull(dto.getLastReceived());
        assertEquals(RECEIVER_EMAIL, dto.getReceiverEmail());

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, atLeastOnce()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, atLeastOnce()).updateTotalQuantity(anyInt());
    }

    @Test(expected = FreecoinsException.class)
    public void processReceive_single_total_quantity_zero() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(0)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());

        freecoinsService.processReceive(1, RECEIVER_EMAIL);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, never()).getProcess(anyInt(), anyString());
        verify(walletService, never()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, never()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateTotalQuantity(anyInt());
    }

    @Test(expected = FreecoinsException.class)
    public void processReceive_single_payment_failed() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(null)
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());
        doReturn(false)
                .when(walletService)
                .performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());

        freecoinsService.processReceive(1, RECEIVER_EMAIL);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, atLeastOnce()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, never()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateTotalQuantity(anyInt());
    }

    @Test(expected = FreecoinsException.class)
    public void processReceive_single_process_not_save() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(null)
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());
        doReturn(true)
                .when(walletService)
                .performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(0)
                .when(freecoinsRepository)
                .saveProcess(any(ReceiveResultDto.class));

        freecoinsService.processReceive(1, RECEIVER_EMAIL);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, atLeastOnce()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateTotalQuantity(anyInt());
    }

    @Test(expected = FreecoinsException.class)
    public void processReceive_single_total_quantity_not_update() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(null)
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());
        doReturn(true)
                .when(walletService)
                .performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(1)
                .when(freecoinsRepository)
                .saveProcess(any(ReceiveResultDto.class));
        doReturn(false)
                .when(freecoinsRepository)
                .updateTotalQuantity(anyInt());

        freecoinsService.processReceive(1, RECEIVER_EMAIL);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, atLeastOnce()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, atLeastOnce()).updateTotalQuantity(anyInt());
    }

    @Test
    public void processReceive_single_ok2() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(ReceiveResultDto.builder()
                .id(1)
                .giveawayId(1)
                .receiverEmail(RECEIVER_EMAIL)
                .received(false)
                .build())
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());
        doReturn(true)
                .when(walletService)
                .performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(true)
                .when(freecoinsRepository)
                .updateProcess(any(ReceiveResultDto.class));
        doReturn(true)
                .when(freecoinsRepository)
                .updateTotalQuantity(anyInt());

        Pair<Boolean, ReceiveResultDto> result = freecoinsService.processReceive(1, RECEIVER_EMAIL);

        assertNotNull(result);
        assertTrue(result.getLeft());

        ReceiveResultDto dto = result.getRight();
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(1, dto.getGiveawayId());
        assertTrue(dto.isReceived());
        assertNull(dto.getLastReceived());
        assertEquals(RECEIVER_EMAIL, dto.getReceiverEmail());

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, atLeastOnce()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, never()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, atLeastOnce()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, atLeastOnce()).updateTotalQuantity(anyInt());
    }

    @Test(expected = FreecoinsException.class)
    public void processReceive_single_already_received() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(true)
                .timeRange(null)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(ReceiveResultDto.builder()
                .id(1)
                .giveawayId(1)
                .receiverEmail(RECEIVER_EMAIL)
                .received(true)
                .build())
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());

        freecoinsService.processReceive(1, RECEIVER_EMAIL);

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, never()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, never()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateTotalQuantity(anyInt());
    }

    @Test
    public void processReceive_not_single_ok() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(false)
                .timeRange(10)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(null)
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());
        doReturn(true)
                .when(walletService)
                .performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        doReturn(1)
                .when(freecoinsRepository)
                .saveProcess(any(ReceiveResultDto.class));
        doReturn(true)
                .when(freecoinsRepository)
                .updateTotalQuantity(anyInt());

        Pair<Boolean, ReceiveResultDto> result = freecoinsService.processReceive(1, RECEIVER_EMAIL);

        assertNotNull(result);
        assertTrue(result.getLeft());

        ReceiveResultDto dto = result.getRight();
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(1, dto.getGiveawayId());
        assertFalse(dto.isReceived());
        assertNotNull(dto.getLastReceived());
        assertEquals(RECEIVER_EMAIL, dto.getReceiverEmail());

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, atLeastOnce()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, atLeastOnce()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, atLeastOnce()).updateTotalQuantity(anyInt());
    }

    @Test
    public void processReceive_not_single_receive_twice_in_a_one_time() {
        doReturn(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(false)
                .timeRange(10)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build())
                .when(freecoinsRepository)
                .getClaim(anyInt());
        doReturn(ReceiveResultDto.builder()
                .id(1)
                .receiverEmail(RECEIVER_EMAIL)
                .giveawayId(1)
                .received(false)
                .lastReceived(LocalDateTime.now())
                .build())
                .when(freecoinsRepository)
                .getProcess(anyInt(), anyString());

        Pair<Boolean, ReceiveResultDto> result = freecoinsService.processReceive(1, RECEIVER_EMAIL);

        assertNotNull(result);
        assertFalse(result.getLeft());

        ReceiveResultDto dto = result.getRight();
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(1, dto.getGiveawayId());
        assertFalse(dto.isReceived());
        assertNotNull(dto.getLastReceived());
        assertEquals(RECEIVER_EMAIL, dto.getReceiverEmail());

        verify(freecoinsRepository, atLeastOnce()).getClaim(anyInt());
        verify(freecoinsRepository, atLeastOnce()).getProcess(anyInt(), anyString());
        verify(walletService, never()).performFreecoinsReceiveProcess(anyString(), any(BigDecimal.class), anyString());
        verify(freecoinsRepository, never()).saveProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateProcess(any(ReceiveResultDto.class));
        verify(freecoinsRepository, never()).updateTotalQuantity(anyInt());
    }

    @Test
    public void getAllReceives_ok() {
        doReturn(Collections.singletonList(new ReceiveResultDto()))
                .when(freecoinsRepository)
                .getAllUserProcess(anyString());

        List<ReceiveResultDto> list = freecoinsService.getAllReceives(RECEIVER_EMAIL);

        assertFalse(CollectionUtils.isEmpty(list));

        verify(freecoinsRepository, atLeastOnce()).getAllUserProcess(anyString());
    }

    @Test
    public void getAllReceives_empty_list() {
        doReturn(Collections.emptyList())
                .when(freecoinsRepository)
                .getAllUserProcess(anyString());

        List<ReceiveResultDto> list = freecoinsService.getAllReceives(RECEIVER_EMAIL);

        assertTrue(CollectionUtil.isEmpty(list));

        verify(freecoinsRepository, atLeastOnce()).getAllUserProcess(anyString());
    }

    @Test
    public void getAllGiveawaysForAdmin_ok() {
        doReturn(Collections.singletonList(GiveawayResultDto.builder()
                .currencyName(CURRENCY_NAME)
                .amount(BigDecimal.TEN)
                .partialAmount(BigDecimal.ONE)
                .totalQuantity(10)
                .isSingle(false)
                .timeRange(10)
                .creatorEmail(CREATOR_EMAIL)
                .status(GiveawayStatus.CREATED)
                .build()))
                .when(freecoinsRepository)
                .getAllClaims();
        doReturn(2)
                .when(freecoinsRepository)
                .getUniqueAcceptorsByClaimId(anyInt());

        List<AdminGiveawayResultDto> list = freecoinsService.getAllGiveawaysForAdmin();

        assertFalse(CollectionUtils.isEmpty(list));

        verify(freecoinsRepository, atLeastOnce()).getAllClaims();
        verify(freecoinsRepository, atLeastOnce()).getUniqueAcceptorsByClaimId(anyInt());
    }

    @Test
    public void getAllGiveawaysForAdmin_empty_list() {
        doReturn(Collections.emptyList())
                .when(freecoinsRepository)
                .getAllClaims();

        List<AdminGiveawayResultDto> list = freecoinsService.getAllGiveawaysForAdmin();

        assertTrue(CollectionUtils.isEmpty(list));

        verify(freecoinsRepository, atLeastOnce()).getAllClaims();
        verify(freecoinsRepository, never()).getUniqueAcceptorsByClaimId(anyInt());
    }
}