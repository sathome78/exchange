package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OpenApiPermission;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class OpenApiToken {
    private Long id;
    private Integer userId;
    private String userEmail;
    private String alias;
    private String publicKey;
    private String privateKey;
    private Boolean allowTrade = false;
    private Boolean allowWithdraw = false;
    private Boolean allowAcceptById = false;
    private LocalDateTime generationDate;


    public List<OpenApiPermission> getPermissions() {
        List<OpenApiPermission> permissions = new ArrayList<>();
        if (allowTrade) {
            permissions.add(OpenApiPermission.TRADE);
        }
        if (allowWithdraw) {
            permissions.add(OpenApiPermission.WITHDRAW);
        }
        if (allowAcceptById) {
            permissions.add(OpenApiPermission.ACCEPT_BY_ID);
        }
        return permissions;
    }
}
