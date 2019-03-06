package me.exrates.model.dto.kyc.responces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KycAnalysisResultsDto {

    private String code;
    private List<String> expectedDocTypes;
    private List<String> listCapturedDocs;
    private KycAnalysisDataDto analysisData;
}
