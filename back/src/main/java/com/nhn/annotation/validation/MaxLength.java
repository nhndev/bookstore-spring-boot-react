package com.nhn.annotation.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.nhn.annotation.impl.MaxLengthValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {MaxLengthValidator.class})
public @interface MaxLength {
    String message() default "{}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long value();
}
