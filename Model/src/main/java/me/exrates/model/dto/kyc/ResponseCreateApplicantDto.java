package me.exrates.model.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCreateApplicantDto {
    private String uid;
    private String creationDate;
    private String lastUpdateDate;
    private String lastReportStatus;
    private String state;
    private String error;
}
