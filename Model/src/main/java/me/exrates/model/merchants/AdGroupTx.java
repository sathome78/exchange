package me.exrates.model.merchants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdGroupTx {
    private int id;
    private int refillRequestId;
    private int userId;
    private String tx;
    private String status;
    private Date time;
}
