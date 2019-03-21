package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by OLEG on 21.11.2016.
 */
@Getter@Setter@ToString
public class OrderCreationResultDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer createdOrderId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer autoAcceptedQuantity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal partiallyAcceptedAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal partiallyAcceptedOrderFullAmount;
    @JsonIgnore
    private List<Integer> fullyAcceptedOrdersIds;
    @JsonIgnore
    private Integer partiallyAcceptedId; /*order that was splitted for accept*/
    @JsonIgnore
    private Integer orderIdToAccept; /*order that was opened and accepted*/
    @JsonIgnore
    private Integer orderIdToOpen; /*order that was opened and placed in common stack*/

}
