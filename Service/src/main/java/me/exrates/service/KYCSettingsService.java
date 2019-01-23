package me.exrates.service;

import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;

import java.util.List;

public interface KYCSettingsService {

    List<KycCountryDto> getCountriesDictionary();

    List<KycLanguageDto> getLanguagesDictionary();
}