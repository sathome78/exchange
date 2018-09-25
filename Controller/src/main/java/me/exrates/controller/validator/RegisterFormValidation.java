package me.exrates.controller.validator;

import me.exrates.model.User;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.UserService;
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

    @Autowired
    UserService userService;

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String ID_PATTERN = "[0-9]+";
    String STRING_PATTERN = "[a-zA-Z]+";
    String MOBILE_PATTERN = "[0-9]{12}";
    private static final String PASSWORD_PATTERN_LETTERS_AND_NUMBERS = "^(?=.*\\d)(?=.*[a-zA-Z])[\\w]{8,20}$";
    private static final String PASSWORD_PATTERN_LETTERS_AND_CHARACTERS = "^(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$";
    private static final String PASSWORD_PATTERN_LETTERS_AND_NUMBERS_AND_CHARACTERS = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$";

    private static final String FIELD_CONTAINS_SPACE = "\\s";

    private static final String NICKNAME_PATTERN = "^\\D+[\\w\\d\\-_.]+";
    private Locale locale = new Locale("en");

    @Autowired
    MessageSource messageSource;

    public boolean supports(Class<?> clazz){
        return false;
    }

    public void validate(Object target, Errors errors, Locale locale) {
        this.locale = locale;
        validate(target, errors);
    }

    public void validate(Object target, Errors errors) {
        User user = (User) target;
        String nicknameRequired = messageSource.getMessage("validation.nicknamerequired", null, locale);
        String nicknameExceed = messageSource.getMessage("validation.nicknameexceed", null, locale);
        String nicknameExists = messageSource.getMessage("validation.nicknameexists", null, locale);
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, locale);
        String emailExists = messageSource.getMessage("validation.emailexists", null, locale);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, locale);
        String passwordMismatch = messageSource.getMessage("validation.passwordmismatch", null, locale);
        String notReadRules = messageSource.getMessage("validation.notreadrules", null, locale);
        String phoneIncorrect = messageSource.getMessage("validation.phoneincorrect", null, locale);

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

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(user.getPhone());
            if (!matcher.matches()) {
                errors.rejectValue("phone", "phone.incorrect",
                        phoneIncorrect);
            }
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch", passwordMismatch);
        }

        validatePassword(user.getPassword(), errors);

    }

    public void validate(String nickname, String email, String password, Errors errors, Locale locale) {
        String nicknameRequired = messageSource.getMessage("validation.nicknamerequired", null, locale);
        String nicknameExceed = messageSource.getMessage("validation.nicknameexceed", null, locale);
        String nicknameExists = messageSource.getMessage("validation.nicknameexists", null, locale);
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, locale);
        String emailExists = messageSource.getMessage("validation.emailexists", null, locale);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, locale);

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

        validatePassword(password, errors);
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

    public void validateEditUser(Object target, Errors errors, Locale locale) {
        User user = (User) target;
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, locale);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, locale);
        String phoneIncorrect = messageSource.getMessage("validation.phoneincorrect", null, locale);
        String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, locale);
        String passwordIncorrect = messageSource.getMessage("validation.passwordincorrect", null, locale);

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

        validatePassword(user.getPassword(), errors);
    }

    public void validateEmail(User user, Errors errors, Locale locale) {
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, locale);
        String statusIncorrect = messageSource.getMessage("login.blocked", null, locale);

        int userId = userService.getIdByEmail(user.getEmail());
        if (userId != 0) {
            User findUser = userService.getUserById(userId);
            if (findUser.getStatus() == UserStatus.DELETED){
                errors.rejectValue("email", "email.incorrect", statusIncorrect);
            }
        } else {
            errors.rejectValue("email", "email.incorrect", emailIncorrect);
        }
    }

    /**
     * Validation method for password (change user password in user private cabinet)
     * @param user
     * @param errors
     * @param locale
     */
    public void validatePasswordInPrivateCabinet(User user, Errors errors, Locale locale) {
        String passwordMismatch = messageSource.getMessage("validation.passwordmismatch", null, locale);

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch", passwordMismatch);
        }

        validatePassword(user.getPassword(), errors);
    }

    /**
     * Validation method for general password validation (for create password in registration process,
     * for change password in user private cabinet, for reset password after recovery process)
     * @param password
     * @param errors
     */
    private void validatePassword(String password, Errors errors){
        String passwordRequired = messageSource.getMessage("validation.message.password.required", null, locale);
        String passwordIncorrect = messageSource.getMessage("validation.message.password.wrong", null, locale);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password", passwordRequired);

        if (password != null && !password.isEmpty()) {
            if ((!Pattern.matches(PASSWORD_PATTERN_LETTERS_AND_NUMBERS, password)
                    && !Pattern.matches(PASSWORD_PATTERN_LETTERS_AND_CHARACTERS, password)
                    && !Pattern.matches(PASSWORD_PATTERN_LETTERS_AND_NUMBERS_AND_CHARACTERS, password))
                    || Pattern.matches(FIELD_CONTAINS_SPACE, password)) {
                errors.rejectValue("password", "password.incorrect", passwordIncorrect);
            }
        }
    }
}