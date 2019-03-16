package me.exrates.model.dto.kyc.responces;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class KycStatusResponseDto {

    private String status;
    private String errorMsg;
    private String[] missingOptionalDocs;
    private List<KycAnalysisResultsDto> analysisResults;
}
