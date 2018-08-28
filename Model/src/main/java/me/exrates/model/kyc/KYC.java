package me.exrates.model.kyc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class KYC {
    private int id;
    private int userId;
    private KycType kycType;
    private KycStatus kycStatus;
    private String companyName;
    private String regCountry;
    private String regNumber;
    private MultipartFile commercialRegistry;
    private String commercialRegistryPath;
    private MultipartFile companyCharter;
    private String companyCharterPath;
    private KycAddress address;
    private KycPerson person;
    private String admin;
}
