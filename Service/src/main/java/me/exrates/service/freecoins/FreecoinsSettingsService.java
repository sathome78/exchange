package me.exrates.service.freecoins;

import me.exrates.model.dto.freecoins.FreecoinsSettingsDto;

import java.math.BigDecimal;
import java.util.List;

public interface FreecoinsSettingsService {

    FreecoinsSettingsDto get(int currencyId);

    List<FreecoinsSettingsDto> getAll();

    boolean set(int currencyId, BigDecimal minAmount, BigDecimal minPartialAmount);
}