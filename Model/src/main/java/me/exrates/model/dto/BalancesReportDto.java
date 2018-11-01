package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class BalancesReportDto {

    private int id;

    @JsonProperty("file_name")
    private String fileName;

    private byte[] content;

    @JsonProperty("created_at")
    private LocalDate createdAt;

    public BalancesReportDto(String fileName,
                             byte[] content) {
        this.fileName = fileName;
        this.content = content;
    }
}
