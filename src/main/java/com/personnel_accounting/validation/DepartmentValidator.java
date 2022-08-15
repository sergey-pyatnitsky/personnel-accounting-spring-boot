package com.personnel_accounting.validation;

import com.personnel_accounting.entity.domain.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class DepartmentValidator implements Validator {

    @Autowired
    private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return Department.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Department department = (Department) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required",
                messageSource.getMessage("department.validator.name.empty", null, LocaleContextHolder.getLocale()));
        if (!checkSize(department.getName()))
            errors.rejectValue("name", "name.size",
                    messageSource.getMessage("department.validator.name.size", null, LocaleContextHolder.getLocale()));
    }

    private boolean checkSize(String input) {
        return input.length() <= 256;
    }
}
