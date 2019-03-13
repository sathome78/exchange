package me.exrates.model.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@AllArgsConstructor
public class PersonKycDto {
    List<IdentityDataKyc> persons;
}
