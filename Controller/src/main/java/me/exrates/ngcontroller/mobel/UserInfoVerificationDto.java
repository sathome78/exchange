package me.exrates.ngcontroller.mobel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.serializer.LocalDateDeserializer;
import me.exrates.model.serializer.LocalDateSerializer;
import me.exrates.ngcontroller.util.NgUtil;

import java.time.LocalDate;

@Data
@Builder
public class UserInfoVerificationDto {

    private Integer userId;
    private String firstName;
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = NgUtil.DATE_PATTERN)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate born;
    private String residentialAddress;
    private String postalCode;
    private String country;
    private String city;
}
