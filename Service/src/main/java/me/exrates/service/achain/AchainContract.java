package me.exrates.service.achain;

import lombok.Data;

/**
 * Created by Maks on 15.06.2018.
 */
@Data
public class AchainContract {

    private String contract;
    private String curencyName;
    private String merchantName;
    private String name;


    public AchainContract(String contract, String curencyName, String merchantName, String name) {
        this.contract = contract;
        this.curencyName = curencyName;
        this.merchantName = merchantName;
        this.name = name;
    }
}
