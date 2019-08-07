package me.exrates.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ErrorReportDto {

    private String userEmail;
    @NotNull
    @Size(max = 200)
    private String url;
    @NotNull
    @Size(max = 20)
    private String method;
    @NotNull
    private int respStatus;
    @NotNull
    @Size(max = 1000)
    private String responseBody;
}
