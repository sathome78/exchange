package me.exrates.model.form;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.userOperation.UserOperationAuthorityOption;

import java.util.List;

@Getter @Setter
public class UserOperationAuthorityOptionsForm {
    private List<UserOperationAuthorityOption> options;
    private Integer userId;
}
