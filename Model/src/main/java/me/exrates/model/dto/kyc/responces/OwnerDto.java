package me.exrates.model.dto.kyc.responces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class OwnerDto {
    @NotNull
    private String[] firstNames;
    @NotNull
    private String[] lastNames;
    private Map<String, Integer> birthDate;

    public Date getDateOfBirth() {
        int day = birthDate.getOrDefault("day", 1);
        int month = birthDate.getOrDefault("month", 1);
        int year = birthDate.getOrDefault("year", 1);
        LocalDate date = LocalDate.of(year, month, day);
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
