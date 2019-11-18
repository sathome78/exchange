package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class UserLoginSessionShortDto {

    @JsonIgnore
    private final static String ACTVIE = "Active";

    private String modified;
    private String device;
    private String location;

    public UserLoginSessionShortDto(UserLoginSessionDto dto, String currentToken) {
        this.modified = currentToken.equals(dto.getToken()) ? ACTVIE : formatDate(dto.getModified());
        this.device = String.format("%s(%s)", dto.getUserAgent(), dto.getOs());
        this.location = String.format("%s/%s", dto.getIp(), dto.getCountry());
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh.mma dd-MM-yyyy");
        return dateTime.format(formatter);
    }
}
