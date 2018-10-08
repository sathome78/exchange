package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.enums.VerificationDocumentType;

import java.time.LocalDate;

@Data
@Builder
public class UserVerificationDto {

    private Integer userId;
    private VerificationDocumentType documentType;
    private String firstName;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate born;
    private String residentialAddress;
    private String postalCode;
    private String country;
    private String city;
    private String filePath;

}
