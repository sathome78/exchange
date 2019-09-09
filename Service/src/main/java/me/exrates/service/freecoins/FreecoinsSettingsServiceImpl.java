package me.exrates.service.freecoins;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.FreecoinsSettingsRepository;
import me.exrates.model.dto.freecoins.FreecoinsSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Log4j2(topic = "free-coins")
@Transactional
@Service
public class FreecoinsSettingsServiceImpl implements FreecoinsSettingsService {

    private final FreecoinsSettingsRepository freecoinsSettingsRepository;

    @Autowired
    public FreecoinsSettingsServiceImpl(FreecoinsSettingsRepository freecoinsSettingsRepository) {
        this.freecoinsSettingsRepository = freecoinsSettingsRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public FreecoinsSettingsDto get(int currencyId) {
        return freecoinsSettingsRepository.get(currencyId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FreecoinsSettingsDto> getAll() {
        return freecoinsSettingsRepository.getAll();
    }

    @Override
    public boolean set(int currencyId, BigDecimal minAmount, BigDecimal minPartialAmount) {
        return freecoinsSettingsRepository.set(currencyId, minAmount, minPartialAmount);
    }
}