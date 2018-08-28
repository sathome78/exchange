package me.exrates.controller.validator;

import me.exrates.model.kyc.KYC;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KycValidator {

    private static final String MOBILE_PATTERN = "[0-9]{11,12}";
    private static final String[] USER_FILE_TYPES = new String[] {"pdf", "png", "jpeg", "jpg"};

    @Autowired
    private MessageSource messageSource;

    public void validate(KYC kyc, Errors errors, Locale locale) {
        String phoneIncorrect = messageSource.getMessage("validation.phoneincorrect", null, locale);
        String fileIncorrectFormat = messageSource.getMessage("validation.documentIncorectFormat", null, locale);

        Pattern pattern = Pattern.compile(MOBILE_PATTERN);
        Matcher matcher = pattern.matcher(kyc.getPerson().getPhone());
        if (!matcher.matches()) {
            errors.rejectValue("person.phone", "phone.incorrect", phoneIncorrect);
        }

        if (kyc.getPerson().getConfirmDocument() != null && !kyc.getPerson().getConfirmDocument().isEmpty()) {
            String extension = FilenameUtils.getExtension(kyc.getPerson().getConfirmDocument().getOriginalFilename());
            if (Arrays.stream(USER_FILE_TYPES).noneMatch(type -> type.equals(extension))) {
                errors.rejectValue("person.confirmDocument", "confirmDocument.incorrect", fileIncorrectFormat);
            }
        }

        switch (kyc.getKycType()) {
            case LEGAL_ENTITY:
                if (kyc.getCompanyCharter() != null && !kyc.getCompanyCharter().isEmpty()) {
                    String extension = FilenameUtils.getExtension(kyc.getCompanyCharter().getOriginalFilename());
                    if (Arrays.stream(USER_FILE_TYPES).noneMatch(type -> type.equals(extension))) {
                        errors.rejectValue("companyCharter", "companyCharter.incorrect", fileIncorrectFormat);
                    }
                }
                if (kyc.getCommercialRegistry() != null && !kyc.getCommercialRegistry().isEmpty()) {
                    String extension = FilenameUtils.getExtension(kyc.getCommercialRegistry().getOriginalFilename());
                    if (Arrays.stream(USER_FILE_TYPES).noneMatch(type -> type.equals(extension))) {
                        errors.rejectValue("commercialRegistry", "commercialRegistry.incorrect", fileIncorrectFormat);
                    }
                }
        }
    }
}
