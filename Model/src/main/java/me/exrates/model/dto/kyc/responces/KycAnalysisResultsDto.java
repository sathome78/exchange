package me.exrates.model.dto.kyc.responces;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String[] listCapturedDocs;
    private KycAnalysisDataDto analysisData;
}
