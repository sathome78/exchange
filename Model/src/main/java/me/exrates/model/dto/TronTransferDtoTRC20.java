package me.exrates.model.dto;

public class TronTransferDtoTRC20 {

    private String contract_address;

    private String function_selector;

    private String parameter;

    private String fee_limit;

    private Long call_value;

    private String owner_address;

    public TronTransferDtoTRC20(String parameter, Long call_value ,String owner_address) {
        this.contract_address = "41A614F803B6FD780986A42C78EC9C7F77E6DED13C";
        this.function_selector = "transfer(address,uint256)";
        this.parameter = parameter;
        this.fee_limit = "100000";
        this.call_value = call_value;
        this.owner_address = owner_address;
    }
}
