package me.exrates.model.dto.merchants.adgroup.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionResponseDto {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("tx")
    private List<TxResponseDto> tx;
    @JsonProperty("src_user")
    private String srcUser;
    @JsonProperty("dest_user")
    private String destUser;
    @JsonProperty("tx_type")
    private String txType;
    @JsonProperty("tx_status")
    private String txStatus;
    private Double amount;
    @JsonProperty("ref_id")
    private String refid;
    private String ctime;
    private String note;
    private String provider;
    private String currency;
    @JsonProperty("extra_info")
    private String extraInfo;
    @JsonProperty("source_address")
    private String sourceAddress;
    @JsonProperty("dest_address")
    private String destAddress;
}
