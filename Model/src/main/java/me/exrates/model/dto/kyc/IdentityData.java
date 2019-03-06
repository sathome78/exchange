package me.exrates.model.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@Setter
@Getter
@Data
@AllArgsConstructor
public class IdentityData {

    @NotNull
    private String birthDay;

    @NotNull
    private String birthMonth;

    @NotNull
    private String birthYear;

    @NotNull
    private String[] firstNames;

    @NotNull
    private String lastName;

    private DocTypeEnum typeDoc;

    public static IdentityData valueOf(KycAttemptDto kycAttemptDto) {
        IdentityData data = new IdentityData();
        data.setBirthDay(String.valueOf(kycAttemptDto.getDateOfBirth().getDayOfMonth()));
        data.setBirthMonth(String.valueOf(kycAttemptDto.getDateOfBirth().getMonthValue()));
        data.setBirthYear(String.valueOf(kycAttemptDto.getDateOfBirth().getMonthValue()));
        List<String> names = Arrays.asList(kycAttemptDto.getFirstNames().split(" "));
        data.setFirstNames(names.toArray(new String[0]));
        data.setLastName(kycAttemptDto.getLastNames());
        return data;
    }

}
