package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;

import java.util.List;
import java.util.Map;

@Data
public class RefreshStatDto {

    private String icoData;
    private String maincurrenciesData;
    private Map<String, String> statInfoDtos;

}
