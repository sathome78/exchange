package me.exrates.model.dto.kyc.responces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KycAnalysisDataDto {

    @NotNull
    private String docType;
    @NotNull
    private List<String> firstNames;
    @NotNull
    private List<String> lastNames;
    private Map<String, Integer> birthDate;

    public Date getDateOfBirth() {
        int day = birthDate.getOrDefault("day", 1);
        int month = birthDate.getOrDefault("month", 1);
        int year = birthDate.getOrDefault("year", 1);
        LocalDate date = LocalDate.of(year, month, day);
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
