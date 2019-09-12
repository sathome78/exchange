package me.exrates.service.impl;

import me.exrates.dao.FreecoinsSettingsRepository;
import me.exrates.model.dto.freecoins.FreecoinsSettingsDto;
import me.exrates.service.freecoins.FreecoinsSettingsService;
import me.exrates.service.freecoins.FreecoinsSettingsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FreecoinsSettingsServiceImplTest {

    @Mock
    private FreecoinsSettingsRepository freecoinsSettingsRepository;

    private FreecoinsSettingsService freecoinsSettingsService;

    @Before
    public void setUp() throws Exception {
        freecoinsSettingsService = spy(new FreecoinsSettingsServiceImpl(freecoinsSettingsRepository));
    }

    @Test
    public void get_ok() {
        doReturn(new FreecoinsSettingsDto())
                .when(freecoinsSettingsRepository)
                .get(anyInt());

        FreecoinsSettingsDto dto = freecoinsSettingsService.get(1);

        assertNotNull(dto);

        verify(freecoinsSettingsRepository, atLeastOnce()).get(anyInt());
    }

    @Test
    public void get_null() {
        doReturn(null)
                .when(freecoinsSettingsRepository)
                .get(anyInt());

        FreecoinsSettingsDto dto = freecoinsSettingsService.get(1);

        assertNull(dto);

        verify(freecoinsSettingsRepository, atLeastOnce()).get(anyInt());
    }

    @Test
    public void getAll_ok() {
        doReturn(Collections.singletonList(new FreecoinsSettingsDto()))
                .when(freecoinsSettingsRepository)
                .getAll();

        List<FreecoinsSettingsDto> list = freecoinsSettingsService.getAll();

        assertFalse(CollectionUtils.isEmpty(list));

        verify(freecoinsSettingsRepository, atLeastOnce()).getAll();
    }

    @Test
    public void getAll_empty_list() {
        doReturn(Collections.emptyList())
                .when(freecoinsSettingsRepository)
                .getAll();

        List<FreecoinsSettingsDto> list = freecoinsSettingsService.getAll();

        assertTrue(CollectionUtils.isEmpty(list));

        verify(freecoinsSettingsRepository, atLeastOnce()).getAll();
    }

    @Test
    public void set_ok() {
        doReturn(true)
                .when(freecoinsSettingsRepository)
                .set(anyInt(), any(BigDecimal.class), any(BigDecimal.class));

        boolean updated = freecoinsSettingsService.set(1, BigDecimal.TEN, BigDecimal.ONE);

        assertTrue(updated);

        verify(freecoinsSettingsRepository, atLeastOnce()).set(anyInt(), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    public void set_not_updated() {
        doReturn(false)
                .when(freecoinsSettingsRepository)
                .set(anyInt(), any(BigDecimal.class), any(BigDecimal.class));

        boolean updated = freecoinsSettingsService.set(1, BigDecimal.TEN, BigDecimal.ONE);

        assertFalse(updated);

        verify(freecoinsSettingsRepository, atLeastOnce()).set(anyInt(), any(BigDecimal.class), any(BigDecimal.class));
    }
}