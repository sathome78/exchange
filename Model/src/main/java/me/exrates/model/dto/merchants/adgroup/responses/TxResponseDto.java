package me.exrates.model.dto.merchants.adgroup.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TxResponseDto {
    @JsonProperty("_id")
    private String id;
    private String act1;
    private Integer active;
    private String address;
    private Double amount;
    private Double balance;
    private String ctime;
    private String ltime;
    private String maker;
    private String mtime;
    private String note;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("ref_id")
    private String refId;
    private Integer removed;
    private String signobject;
    private Long stime;

    @JsonProperty("tariff_charge")
    private Integer tariffCharge;

    @JsonProperty("tx_type")
    private String txType;
}
