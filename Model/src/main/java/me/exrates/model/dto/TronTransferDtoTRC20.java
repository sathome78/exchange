package me.exrates.model.dto;

public class TronTransferDtoTRC20 {

    private String contract_address;

    private String function_selector;

    private String parameter;

    private String fee_limit;

    private Long call_value;

    private String owner_address;

    public TronTransferDtoTRC20(String contract_address, String function_selector, String parameter, String fee_limit,
                                Long call_value ,String owner_address) {
        this.contract_address = contract_address;
        this.function_selector = function_selector;
        this.parameter = parameter;
        this.fee_limit = fee_limit;
        this.call_value = call_value;
        this.owner_address = owner_address;
    }
}
