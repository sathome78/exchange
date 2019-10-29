package me.exrates.model.dto;

public class TronTransferDtoTRC20 {

    private String ownerAddress;

    private String toAddress;

    private Long amount;

    public TronTransferDtoTRC20(String toAddress, String ownerAddress, Long amount) {
        this.ownerAddress = ownerAddress;
        this.toAddress = toAddress;
        this.amount = amount;
    }
}
