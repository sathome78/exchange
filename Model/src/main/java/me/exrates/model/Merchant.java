package me.exrates.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.MerchantProcessType;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Merchant implements Serializable {
    private int id;
    private String name;
    private String description;
    private String serviceBeanName;
    private MerchantProcessType processType;
    private Integer refillOperationCountLimitForUserPerDay;
    private Boolean additionalTagForWithdrawAddressIsUsed;
    private Integer tokensParrentId;
    private Boolean needVerification;


    public Merchant(int id) {
        this.id = id;
    }

    public Merchant(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}