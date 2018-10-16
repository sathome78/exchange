package me.exrates.ngcontroller.mobel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

//    @JsonCreator
//    public UserDocVerificationDto(@JsonProperty("userId") int userId,
//                                  @JsonProperty("documentType") String documentType,
//                                  @JsonProperty("encoded") String encoded) {
//        this.userId = userId;
//        this.documentType = VerificationDocumentType.of(documentType);
//        this.encoded = encoded;
//    }
}
