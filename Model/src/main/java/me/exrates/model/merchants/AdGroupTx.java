package me.exrates.model.merchants;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@Builder
public class AdGroupTx {
    private int id;
    private int refillRequestId;
    private String tx;
    private String status;
    private Date time;
}
