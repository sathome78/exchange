package me.exrates.model.kyc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KycInfo {
    private int id;
    private int userId;
    private int addressId;
    private int personId;
    private String admin;
    private KycType kycType;
    private KycStatus kycStatus;
}
