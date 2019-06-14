package me.exrates.controller.exception;

import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.ngExceptions.NgOrderValidationException;


public class OrdersValidationErrorInfo extends ErrorInfo {

    public OrderValidationDto validationResults;

    public OrdersValidationErrorInfo(CharSequence url, NgOrderValidationException ex) {
        super(url, ex);
        this.validationResults = ex.getValidationResults();
    }
}
