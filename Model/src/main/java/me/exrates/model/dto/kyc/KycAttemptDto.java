package me.exrates.model.dto.kyc;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import me.exrates.model.serializer.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KycAttemptDto {

    @Singular
    private List<String> docTypes = new ArrayList<>();
    private String lastNames;
    private String firstNames;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfBirth;


}
