package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class InterkassaActionUrlDto {

    private String actionURL;
    private String method;
    private Map<String, Object> parameters;
}