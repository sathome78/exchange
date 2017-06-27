package me.exrates.model;

import lombok.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Merchant {
    private int id;
    private String name;
    private String description;
    private  String serviceBeanName;
    private String processType;
    private Integer refillOperationCountLimitForUserPerDay;
    private Boolean additionalTagForWithdrawAddressIsUsed;


    public Merchant(int id) {
        this.id = id;
    }

    public Merchant(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}