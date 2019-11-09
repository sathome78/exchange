package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoLocation {

    private String country;
    private String region;
    private String city;


    public static GeoLocation empty() {
        String undefined = "UNDEFINED";
        return GeoLocation.builder()
                .country(undefined)
                .region(undefined)
                .city(undefined)
                .build();
    }
}
