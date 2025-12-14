package com.nhn.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface SysAuthorize {
    String role() default "";

    String[] permissions() default {};
}
