package me.exrates.model.dto.kyc.responces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class KycAnalysisDataDto {

    @NotNull
    private String docType;

    private OwnerDto owner;
}
