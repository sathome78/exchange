package me.exrates.service.impl;

import me.exrates.dao.KYCSettingsDao;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;
import me.exrates.service.KYCSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KYCSettingsServiceImpl implements KYCSettingsService {

    @Autowired
    private KYCSettingsDao kycSettingsDao;

    @Override
    public List<KycCountryDto> getCountriesDictionary() {
        return kycSettingsDao.getCountriesDictionary();
    }

    @Override
    public List<KycLanguageDto> getLanguagesDictionary() {
        return kycSettingsDao.getLanguagesDictionary();
    }
}