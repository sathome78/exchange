package me.exrates.model.dto.merchants.lisk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiskAccount {
    private String address;
    private BigDecimal unconfirmedBalance;
    private BigDecimal balance;
    private String publicKey;
}
