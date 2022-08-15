package com.personnel_accounting.validation;

import com.personnel_accounting.entity.domain.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@PropertySource("classpath:regexp.properties")
@Component
public class ProfileValidator implements Validator {
    private final Environment env;

    @Autowired
    private MessageSource messageSource;

    public ProfileValidator(Environment env) {
        this.env = env;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Profile.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Profile profile = (Profile) target;
        if (!checkForRegexp(profile.getEmail(), env.getProperty("email.regexp")))
            errors.rejectValue("email", "email.not_matches",
                    messageSource.getMessage("profile.validator.email.regexp", null, LocaleContextHolder.getLocale()));
        if (!checkForRegexp(profile.getPhone(), env.getProperty("phone.regexp")))
            errors.rejectValue("phone", "phone.not_matches",
                    messageSource.getMessage("profile.validator.phone.regexp", null, LocaleContextHolder.getLocale()));
        if (checkSize(profile.getEducation(), 2048))
            errors.rejectValue("education", "education.size",
                    messageSource.getMessage("profile.validator.education.size", null, LocaleContextHolder.getLocale()));
        if (checkSize(profile.getAddress(), 256))
            errors.rejectValue("address", "address.size",
                    messageSource.getMessage("profile.validator.address.size", null, LocaleContextHolder.getLocale()));
        if (checkSize(profile.getPhone(), 60))
            errors.rejectValue("phone", "phone.size",
                    messageSource.getMessage("profile.validator.phone.size", null, LocaleContextHolder.getLocale()));
        if (checkSize(profile.getEmail(), 256))
            errors.rejectValue("email", "email.size",
                    messageSource.getMessage("profile.validator.email.size", null, LocaleContextHolder.getLocale()));
    }

    private boolean checkForRegexp(String input, String regexp) {
        return input.matches(regexp);
    }

    private boolean checkSize(String input, int max) {
        return (input.length() > max);
    }
}
