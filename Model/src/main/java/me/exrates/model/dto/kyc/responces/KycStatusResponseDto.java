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
public class KycStatusResponseDto {

    private String status;
    private String errorMsg;
    private List<?> missingOptionalDocs;
    private KycAnalysisResultsDto analysisResults;
}
