package me.exrates.model.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateApplicantDto {
    private String uid;
    private PersonKycDto inputData;
}
