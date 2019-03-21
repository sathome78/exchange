package me.exrates.model.dto.kyc.responces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = "listCapturedDocs")
public class KycAnalysisResultsDto {

    private String code;
    private String[] expectedDocTypes;
    private String[] listCapturedDocs;
    private KycAnalysisDataDto analysisData;
}
