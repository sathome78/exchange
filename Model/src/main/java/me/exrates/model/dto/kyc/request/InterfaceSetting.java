package me.exrates.model.dto.kyc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InterfaceSetting {
    private String confCode;
    private String language;
}
