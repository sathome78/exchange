package me.exrates.controller.exception;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorInfoDto {
    public String error;
    public String detail;

    public ErrorInfoDto(String error) {
        this.error = error;
    }
}
