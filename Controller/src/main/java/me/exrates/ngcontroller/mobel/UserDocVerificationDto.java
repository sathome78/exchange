package me.exrates.ngcontroller.mobel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.exrates.ngcontroller.mobel.enums.VerificationDocumentType;

@Data
@Builder
@AllArgsConstructor(suppressConstructorProperties = true)
public class UserDocVerificationDto {

    private Integer userId;
    private VerificationDocumentType documentType;
    private String encoded;
}
