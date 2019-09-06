package me.exrates.dao;

import me.exrates.model.dto.freecoins.FreecoinsSettingsDto;

import java.math.BigDecimal;
import java.util.List;

public interface FreecoinsSettingsRepository {

    FreecoinsSettingsDto get(int currencyId);

    List<FreecoinsSettingsDto> getAll();

    boolean set(int currencyId, BigDecimal minAmount, BigDecimal minPartialAmount);
}