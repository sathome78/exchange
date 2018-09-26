package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Maks on 15.02.2018.
 */
@Data
public class TransferCodeDto {

    @NotNull
    private String code;
}
