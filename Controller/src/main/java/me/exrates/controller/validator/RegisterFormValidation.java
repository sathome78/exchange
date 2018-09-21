package me.exrates.controller.validator;

import me.exrates.model.User;
import me.exrates.model.dto.ChangePasswordDto;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegisterFormValidation implements Validator {

    private static final String PASS_FIELD_NAME = "password";
    private static final String NICKNAME_FIELD_NAME = "nickname";
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String NICKNAME_VALIDATION_REG_EXP = "^[a-zA-Z][_A-Za-z0-9\\-_]{2,20}$";
    private static final String MOBILE_PATTERN = "[0-9]{12}";
    private static final String PASS_PATTERN = "^[a-zA-Z0-9][\\w!#$%&'*+/=?`{|}~^-]{7,19}$";
    private static final String NUMBER_SYMBOLS_PATTERN = "^(?=.*[0-9])(?=.*[!#$%&'*+/=?`{|}~^-])(?=\\S+$).{7,19}$";
    private static final String LETTERS_SYMBOLS_PATTERN = "^(?=.*[a-zA-Z])(?=.*[!#$%&'*+/=?`{|}~^-])(?=\\S+$).{7,19}$";
    private static final String LETTERS_NUMBERS_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=\\S+$).{7,19}$";
    private static final String LETTERS_NUMBERS_SYMBOLS_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=\\S+$)(?=.*[!#$%&'*+/=?`{|}~^-]).{7,19}$";
    //private static final String EMAIL_PATTERN = "^[\\w+_.-]+(?:\\.[\\w+_.-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final String EMAIL_START_PATTERN = "^[\\w+_.-]+(?:\\.[\\w+_.-]+)*$";


    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String ID_PATTERN = "[0-9]+";
    String STRING_PATTERN = "[a-zA-Z]+";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{8,20})";
    private static final String NICKNAME_PATTERN = "^\\D+[\\w\\d\\-_.]+";
    //    private static final Locale ru = new Locale("ru");
    private Locale ru = new Locale("en");


    @Autowired
    private UserService userService;

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
        String phoneIncorrect = messageSource.getMessage("validation.phoneincorrect", null, ru);

        validateEmail(errors, user.getEmail());

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(user.getPhone());
            if (!matcher.matches()) {
                errors.rejectValue("phone", "phone.incorrect",
                        phoneIncorrect);
            }
        }

        validatePassword(errors, user);

    }

    private void validatePassword(Errors errors, User user) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PASS_FIELD_NAME,
                "validation.passwordrequired");

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "validation.passwordmismatch");
        }

        if (StringUtils.isBlank(user.getPassword()) || !isValidPassword(user.getPassword())) {
            errors.rejectValue(PASS_FIELD_NAME, "validation.passwordincorrect");
        }
    }

    /**
     *
     * Password can consist of letters and numbers, letters and symbols, numbers and symbols and all of them
     *
     * @param password
     * @return true if password matches all statements
     */
    private boolean isValidPassword(String password) {
        return password.matches(NUMBER_SYMBOLS_PATTERN) || password.matches(LETTERS_NUMBERS_PATTERN) || password.matches(LETTERS_SYMBOLS_PATTERN) || password.matches(LETTERS_NUMBERS_SYMBOLS_PATTERN);
    }

    private void validateEmail(Errors errors, String email) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, EMAIL_FIELD_NAME,
                "validation.emailrequired");

        if (!email.substring(0, email.indexOf('@')).matches(EMAIL_START_PATTERN)) {
            errors.rejectValue(EMAIL_FIELD_NAME, "validation.emailrequirements");
        }

        if (!email.matches(EMAIL_PATTERN)) {
            errors.rejectValue(EMAIL_FIELD_NAME, "validation.emailincorrect");
        }

        if (!userService.ifEmailIsUnique(email)) {
            errors.rejectValue(EMAIL_FIELD_NAME, "validation.emailexists");
        }
    }


    private void validateNickname(Errors errors, String nickname) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NICKNAME_FIELD_NAME, "validation.nicknamerequired");

        if (!nickname.matches(NICKNAME_VALIDATION_REG_EXP)) {
            errors.rejectValue(NICKNAME_FIELD_NAME, "validation.nicknamerequirements");
        }

        if (!userService.ifNicknameIsUnique(nickname)) {
            errors.rejectValue(NICKNAME_FIELD_NAME, "validation.nicknameexists");
        }
    }

    public void validate(String nickname, String email, String password, Errors errors, Locale ru) {
        this.ru = ru;

        String nicknameRequired = messageSource.getMessage("validation.nicknamerequired", null, ru);
        String nicknameExceed = messageSource.getMessage("validation.nicknameexceed", null, ru);
        String nicknameExists = messageSource.getMessage("validation.nicknameexists", null, ru);
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, ru);
        String emailExists = messageSource.getMessage("validation.emailexists", null, ru);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, ru);
        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, ru);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, ru);

        if (nickname != null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nickname", "required.nickname",
                    nicknameRequired);

            if (!nickname.matches(NICKNAME_PATTERN)) {
                errors.rejectValue("nickname", "login.latinonly");
                errors.rejectValue("nickname", "login.symbonly");
                errors.rejectValue("nickname", "login.notdigit");
                return;
            }

            if (nickname.length() > 40) {
                errors.rejectValue("nickname", "nickname.exceed", nicknameExceed);
            }


            if (!userService.ifNicknameIsUnique(nickname)) {
                errors.rejectValue("nickname", "nickname.incorrect", nicknameExists);
            }
        }

        if (email != null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
                    "required.email", emailRequired);

            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                errors.rejectValue("email", "email.incorrect", emailIncorrect);
            }

            if (!userService.ifEmailIsUnique(email)) {
                errors.rejectValue("email", "email.exists", emailExists);
            }
        }

        if (password != null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                    "required.password", passwordRequired);

            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);
            if (!matcher.matches()) {
                errors.rejectValue("password", "password.incorrect", passwordIncorrect);
            }
        }
    }

    public void validateNickname(Object target, Errors errors, Locale locale) {
        User user = (User) target;
        String nicknameRequired = messageSource.getMessage("validation.nicknamerequired", null, locale);
        String nicknameExceed = messageSource.getMessage("validation.nicknameexceed", null, locale);
        String nicknameExists = messageSource.getMessage("validation.nicknameexists", null, locale);
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
    }

    public void validateEditUser(Object target, Errors errors, Locale ru) {
        User user = (User) target;
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, ru);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, ru);
        String phoneIncorrect = messageSource.getMessage("validation.phoneincorrect", null, ru);
        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, ru);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, ru);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, EMAIL_FIELD_NAME,
                "required.email", emailRequired);

        if (!(user.getEmail() != null && user.getEmail().isEmpty())) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(user.getEmail());
            if (!matcher.matches()) {
                errors.rejectValue(EMAIL_FIELD_NAME, "email.incorrect", emailIncorrect);
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
            pattern = Pattern.compile(PASS_PATTERN);
            matcher = pattern.matcher(user.getPassword());
            if (!matcher.matches()) {
                errors.rejectValue(PASS_FIELD_NAME, "password.incorrect", passwordIncorrect);
            }
        }
    }

    public void validateResetPassword(Object target, Errors errors, Locale ru) {
        User user = (User) target;

        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, ru);
        String passwordMismatch = messageSource.getMessage("validation.passwordmismatch", null, ru);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, ru);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PASS_FIELD_NAME,
                "required.password", passwordRequired);

//        if (!user.getPassword().equals(user.getConfirmPassword())) {
//            errors.rejectValue("confirmPassword", "password.mismatch",
//                    passwordMismatch);
//        }

        if (!(user.getPassword() != null && user.getPassword().isEmpty())) {
            pattern = Pattern.compile(PASS_PATTERN);
            matcher = pattern.matcher(user.getPassword());
            if (!matcher.matches()) {
                errors.rejectValue(PASS_FIELD_NAME, "password.incorrect", passwordIncorrect);
            }
        }
    }

    public void validateChangePassword(ChangePasswordDto changePasswordDto, Errors errors, Locale locale) {
        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, locale);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, locale);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                "required.password", passwordRequired);
        if (!(StringUtils.isEmpty(changePasswordDto.getPassword()))) {
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(changePasswordDto.getConfirmPassword());
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
            pattern = Pattern.compile(PASS_PATTERN);
            matcher = pattern.matcher(user.getFinpassword());
            if (!matcher.matches()) {
                errors.rejectValue("finpassword", "password.incorrect", passwordIncorrect);
            }
        }
    }

    public void validateEmail(User user, Errors errors, Locale ru) {
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, ru);
        String statusIncorrect = messageSource.getMessage("login.blocked", null, ru);

        int userId = userService.getIdByEmail(user.getEmail());
        if (userId != 0) {
            User findUser = userService.getUserById(userId);
            if (findUser.getStatus() == UserStatus.DELETED) {
                errors.rejectValue("email", "email.incorrect", statusIncorrect);
            }
        } else {
            errors.rejectValue(EMAIL_FIELD_NAME, "email.incorrect", emailIncorrect);
        }
    }
}