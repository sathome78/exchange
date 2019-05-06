package me.exrates.model.ngModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class RefillPendingRequestDto {

    private Integer requestId;
    private String date;
    private String currency;
    private double amount;
    private double commission;
    private String system;
    private String status;
    private String operation;

}