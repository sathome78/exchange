package me.exrates.model.dto.kyc;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdentityDataKyc {
    private String birthDay;
    private String birthMonth;
    private String birthYear;
    private String[] firstNames;
    private String lastName;

    public static IdentityDataKyc of(IdentityDataRequest identityDataRequest) {
        IdentityDataKyc identityDataKyc = new IdentityDataKyc();
        identityDataKyc.setBirthDay(identityDataRequest.getBirthDay().toString());
        identityDataKyc.setBirthMonth(identityDataRequest.getBirthMonth().toString());
        identityDataKyc.setBirthYear(identityDataRequest.getBirthYear().toString());
        identityDataKyc.setLastName(identityDataRequest.getLastName());
        identityDataKyc.setFirstNames(identityDataRequest.getFirstNames());
        return identityDataKyc;
    }
}
