package me.exrates.controller.validator;

import me.exrates.model.User;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegisterFormValidation implements Validator {

    @Autowired
    UserService userService;


    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String ID_PATTERN = "[0-9]+";
    String STRING_PATTERN = "[a-zA-Z]+";
    String MOBILE_PATTERN = "[0-9]{12}";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{8,20})";
    private static final String NICKNAME_PATTERN = "^\\D+[\\w\\d\\-_]+";
//    private static final Locale ru = new Locale("ru");
    private Locale ru = new Locale("en");

    @Autowired
    MessageSource messageSource;

    public boolean supports(Class<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public void validate(Object target, Errors errors, Locale ru) {
        this.ru = ru;
        validate(target, errors);
    }

    public void validate(Object target, Errors errors) {
        User user = (User) target;
        String nicknameRequired = messageSource.getMessage("validation.nicknamerequired", null, ru);
        String nicknameExceed = messageSource.getMessage("validation.nicknameexceed", null, ru);
        String nicknameExists = messageSource.getMessage("validation.nicknameexists", null, ru);
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, ru);
        String emailExists = messageSource.getMessage("validation.emailexists", null, ru);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, ru);
        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, ru);
        String passwordMismatch = messageSource.getMessage("validation.passwordmismatch", null, ru);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, ru);
        String notReadRules = messageSource.getMessage("validation.notreadrules", null, ru);
        String phoneIncorrect = messageSource.getMessage("validation.phoneincorrect", null, ru);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nickname", "required.nickname",
                nicknameRequired);

        if (!user.getNickname().matches(NICKNAME_PATTERN)) {
            errors.rejectValue("nickname", "login.latinonly");
            errors.rejectValue("nickname", "login.symbonly");
            errors.rejectValue("nickname", "login.notdigit");
            return;
        }

        if (user.getNickname().length() > 40) {
            errors.rejectValue("nickname", "nickname.exceed", nicknameExceed);
        }


        if (!userService.ifNicknameIsUnique(user.getNickname())) {
            errors.rejectValue("nickname", "nickname.incorrect", nicknameExists);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
                "required.email", emailRequired);

        if (!(user.getEmail() != null && user.getEmail().isEmpty())) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(user.getEmail());
            if (!matcher.matches()) {
                errors.rejectValue("email", "email.incorrect", emailIncorrect);
            }
        }
        if (!userService.ifEmailIsUnique(user.getEmail())) {
            errors.rejectValue("email", "email.exists", emailExists);
        }
//		  ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone",  
//		    "required.phone", "Phone is required.");  
//		  
        // phone number validation

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(user.getPhone());
            if (!matcher.matches()) {
                errors.rejectValue("phone", "phone.incorrect",
                        phoneIncorrect);
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                "required.password", passwordRequired);

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch",
                    passwordMismatch);
        }

        if (!(user.getPassword() != null && user.getPassword().isEmpty())) {
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(user.getPassword());
            if (!matcher.matches()) {
                errors.rejectValue("password", "password.incorrect", passwordIncorrect);
            }
        }

    }

    public void validateEditUser(Object target, Errors errors, Locale ru) {
        User user = (User) target;
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, ru);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, ru);
        String phoneIncorrect = messageSource.getMessage("validation.phoneincorrect", null, ru);
        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, ru);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, ru);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
                "required.email", emailRequired);

        if (!(user.getEmail() != null && user.getEmail().isEmpty())) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(user.getEmail());
            if (!matcher.matches()) {
                errors.rejectValue("email", "email.incorrect", emailIncorrect);
            }
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(user.getPhone());

            if (!matcher.matches()) {
                errors.rejectValue("phone", "phone.incorrect",
                        phoneIncorrect);
            }
        }

        if (!(user.getPassword() != null && user.getPassword().isEmpty())) {
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(user.getPassword());
            if (!matcher.matches()) {
                errors.rejectValue("password", "password.incorrect", passwordIncorrect);
            }
        }
    }

    public void validateResetPassword(Object target, Errors errors, Locale ru) {
        User user = (User) target;

        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, ru);
        String passwordMismatch = messageSource.getMessage("validation.passwordmismatch", null, ru);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, ru);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                "required.password", passwordRequired);

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch",
                    passwordMismatch);
        }

        if (!(user.getPassword() != null && user.getPassword().isEmpty())) {
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(user.getPassword());
            if (!matcher.matches()) {
                errors.rejectValue("password", "password.incorrect", passwordIncorrect);
            }
        }
    }

    public void validateResetFinPassword(Object target, Errors errors, Locale ru) {
        User user = (User) target;

        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, ru);
        String passwordMismatch = messageSource.getMessage("validation.passwordmismatch", null, ru);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, ru);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "finpassword",
                "required.password", passwordRequired);

        if (!user.getFinpassword().equals(user.getConfirmFinPassword())) {
            errors.rejectValue("confirmFinPassword", "password.mismatch",
                    passwordMismatch);
        }

        if (!(user.getFinpassword() != null && user.getFinpassword().isEmpty())) {
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(user.getFinpassword());
            if (!matcher.matches()) {
                errors.rejectValue("finpassword", "password.incorrect", passwordIncorrect);
            }
        }
    }

    public void validateEmail(User user, Errors errors, Locale ru) {
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, ru);

        if (userService.ifEmailIsUnique(user.getEmail())) {
            errors.rejectValue("email", "email.incorrect", emailIncorrect);
        }
    }
}