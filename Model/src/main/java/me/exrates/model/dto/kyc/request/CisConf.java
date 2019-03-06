package me.exrates.model.dto.kyc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CisConf {
    private String realm;
    private String fileUid;
    private boolean fileLaunchCheck;
}
