package me.exrates.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RefreshStatisticDto {

    private String icoData;
    private String mainCurrenciesData;
    private Map<String, String> statisticInfoDtos;
}