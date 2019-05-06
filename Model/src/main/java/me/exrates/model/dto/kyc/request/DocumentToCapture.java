package me.exrates.model.dto.kyc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentToCapture {
    private String code;
    private String label;
    private String description;
    private String[] docTypes;

}
