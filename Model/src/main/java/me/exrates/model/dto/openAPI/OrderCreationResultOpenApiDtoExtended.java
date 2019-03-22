package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.dto.OrderCreationResultDto;

import java.util.List;

@Getter@Setter
public class OrderCreationResultOpenApiDtoExtended extends OrderCreationResultOpenApiDto {

    @JsonProperty("fully_accepted_orders_ids")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Integer> fullyAcceptedOrdersIds;

    @JsonProperty("partially_accepted_order_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer partiallyAcceptedOrderId; /*order that was splitted for accept*/

    @JsonProperty("order_id_to_accept")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer orderIdToAccept; /*order that was opened and accepted*/

    @JsonProperty("order_id_to_open")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer orderIdToOpen; /*order that was opened and placed in common stack*/

    public OrderCreationResultOpenApiDtoExtended(OrderCreationResultDto orderCreationResultDto) {
        super(orderCreationResultDto);
        this.fullyAcceptedOrdersIds = orderCreationResultDto.getFullyAcceptedOrdersIds();
        this.partiallyAcceptedOrderId = orderCreationResultDto.getPartiallyAcceptedId();
        this.orderIdToAccept = orderCreationResultDto.getOrderIdToAccept();
        this.orderIdToOpen = orderCreationResultDto.getOrderIdToOpen();
    }
}
