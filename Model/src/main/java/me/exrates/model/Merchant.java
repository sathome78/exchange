package me.exrates.model;

import lombok.*;
import me.exrates.model.enums.MerchantProcessType;

import java.io.Serializable;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
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