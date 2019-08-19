package me.exrates.model.dto.merchants.adgroup.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseListTxDto {
    private String total;
    private List<Transaction> transactions;
    private String errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Transaction {
        @JsonProperty("_id")
        private String id;
        @JsonProperty("tx")
        private List<Tx> tx;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Tx {
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

}
