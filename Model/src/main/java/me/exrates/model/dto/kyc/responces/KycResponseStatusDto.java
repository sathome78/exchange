package me.exrates.model.dto.kyc.responces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycResponseStatusDto {
    private String status;
    private String uid;
}
