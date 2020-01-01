package me.exrates.model.userOperation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author Vlad Dziubak
 * Date: 30.07.2018
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class UserOperationAuthorityOption {
    private UserOperationAuthority userOperationAuthority;
    private Boolean enabled;

    private String userOperationAuthorityLocalized;

    public void localize(MessageSource messageSource, Locale locale) {
        userOperationAuthorityLocalized = userOperationAuthority.toString(messageSource, locale);
    }

    @Override
    public String toString() {
        return "UserOperationAuthorityOption{" +
                ", userOperationAuthority=" + userOperationAuthority +
                ", enabled=" + enabled +
                '}';
    }
}
