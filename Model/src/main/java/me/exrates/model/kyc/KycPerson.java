package me.exrates.model.kyc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.Gender;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class KycPerson {
    private String position;
    private String name;
    private String surname;
    private String middleName;
    private String phone;
    private String birthDate;
    private String nationality;
    private String idNumber;
    private Gender gender;
    private MultipartFile confirmDocument;
    private String confirmDocumentPath;
}
