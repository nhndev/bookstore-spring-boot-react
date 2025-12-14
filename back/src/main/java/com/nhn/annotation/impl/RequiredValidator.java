package com.nhn.annotation.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import com.nhn.annotation.validation.Required;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredValidator implements ConstraintValidator<Required, Object> {
    @Override
    public boolean isValid(final Object s,
                           final ConstraintValidatorContext constraintValidatorContext) {
        if (s instanceof String) {
            return StringUtils.isNotBlank(s.toString());
        }
        return !ObjectUtils.isEmpty(s);
    }
}
