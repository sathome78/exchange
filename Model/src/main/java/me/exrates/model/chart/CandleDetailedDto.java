package me.exrates.model.chart;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.serializer.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleDetailedDto {

    private String pairName;
    private String resolution;
    private CandleDto candleDto;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastDealTime;
}