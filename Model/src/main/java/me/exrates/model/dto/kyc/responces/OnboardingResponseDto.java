package me.exrates.model.dto.kyc.responces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponseDto {

    private String uid;
    private String url;
    private String expirationDate;
    private DispatchInfo dispatchInfo;
}
