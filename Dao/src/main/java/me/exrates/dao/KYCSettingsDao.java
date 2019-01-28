package me.exrates.dao;

import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;

import java.util.List;

public interface KYCSettingsDao {

    List<KycCountryDto> getCountriesDictionary();

    List<KycLanguageDto> getLanguagesDictionary();
}