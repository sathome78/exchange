package me.exrates.model.dto;

public class TronFreezeBalance {

    private String ownerAddress;

    private Integer frozenBalance;

    private Integer frozenDuration;

    private String resource;

    private String receiverAddress;

    public TronFreezeBalance(String ownerAddress, Integer frozenBalance, Integer frozenDuration, String resource,
                             String receiverAddress) {
        this.ownerAddress = ownerAddress;
        this.frozenBalance = frozenBalance;
        this.frozenDuration = frozenDuration;
        this.resource = resource;
        this.receiverAddress = receiverAddress;
    }
}
