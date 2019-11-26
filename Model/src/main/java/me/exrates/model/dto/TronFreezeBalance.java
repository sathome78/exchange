package me.exrates.model.dto;

public class TronFreezeBalance {

    private String owner_address;

    private Integer frozen_balance;

    private Integer frozen_duration;

    private String resource;

    private String receiver_address;

    public TronFreezeBalance(String owner_address, Integer frozen_balance, Integer frozen_duration, String resource,
                                String receiver_address) {
        this.owner_address = owner_address;
        this.frozen_balance = frozen_balance;
        this.frozen_duration = frozen_duration;
        this.resource = resource;
        this.receiver_address = receiver_address;
    }
}
