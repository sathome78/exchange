package me.exrates.model.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.dto.kyc.request.InterfaceSetting;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@Setter
@Getter
@Data
@AllArgsConstructor
public class IdentityDataRequest {

    @NotNull
    private Integer birthDay;

    @NotNull
    private Integer birthMonth;

    @NotNull
    private Integer birthYear;

    @NotNull
    private String[] firstNames;

    @NotNull
    private String lastName;

    private DocTypeEnum typeDoc;

    @NotNull
    private String country;
}
