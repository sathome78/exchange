package me.exrates.model.userOperation;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserOperationAuthorityEntity {
    private int userId;
    private int userOperationId;
    private boolean enabled;
}
