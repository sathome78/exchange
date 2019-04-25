package me.exrates.model.dto.logging;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceLog {

    private String methodName;
    private String arguments;
    private String result;
    private String userEmail;
    private long processingTime;
    private String error;
}
