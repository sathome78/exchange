package me.exrates.model.kyc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KycAddress {
    private String country;
    private String city;
    private String street;
    private String zipCode;
}
