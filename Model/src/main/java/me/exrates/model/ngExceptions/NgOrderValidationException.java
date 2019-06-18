package me.exrates.model.ngExceptions;

import me.exrates.model.dto.OrderValidationDto;


public class NgOrderValidationException extends RuntimeException {

    private OrderValidationDto validationResults;

    public NgOrderValidationException(OrderValidationDto dto) {
        this.validationResults = dto;
    }

    public OrderValidationDto getValidationResults() {
        return validationResults;
    }
}
