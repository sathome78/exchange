package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.Commission;
import me.exrates.model.Wallet;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;

/**
 * Created by maks on 22.06.2017.
 */
@Builder(toBuilder = true)
@Data
public class TransferDto {

    @JsonIgnore
    private Wallet walletUserFrom;
    @JsonIgnore
    private Wallet walletUserTo;
    private String userToNickName;
    private int currencyId;
    private int userFromId;
    private int userToId;
    private Commission commission;
    private String notyAmount;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal initialAmount;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal comissionAmount;
}
