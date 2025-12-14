package com.nhn.annotation.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.nhn.annotation.validation.MaxLength;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxLengthValidator implements ConstraintValidator<MaxLength, Object> {
    private long maxLength;

    @Override
    public void initialize(final MaxLength maxLength) {
        this.maxLength = maxLength.value();
    }

    @Override
    public boolean isValid(final Object s,
                           final ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(s)) {
            return true;
        }
        return StringUtils.length(s.toString()) <= this.maxLength;
    }
}
