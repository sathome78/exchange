package me.exrates.model.dto.kyc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataKyc {

    private String birthDay;
    private String birthMonth;
    private String birthYear;
    private String[] firstNames;
    private String lastName;

    public static DataKyc of(IdentityDataRequest identityDataRequest) {
        DataKyc identityDataKyc = new DataKyc();
        identityDataKyc.setBirthDay(identityDataRequest.getBirthDay().toString());
        identityDataKyc.setBirthMonth(identityDataRequest.getBirthMonth().toString());
        identityDataKyc.setBirthYear(identityDataRequest.getBirthYear().toString());
        identityDataKyc.setLastName(identityDataRequest.getLastName());
        identityDataKyc.setFirstNames(new String[]{identityDataRequest.getFirstName()});
        return identityDataKyc;
    }
}
