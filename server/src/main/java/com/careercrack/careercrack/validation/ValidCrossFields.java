package com.careercrack.careercrack.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put on a request DTO that implements CrossFieldValidated. Each broken rule is reported against the field it
 * belongs to (for example "salaryMax"), so it appears in fieldErrors and shows under that input like any other error.
 * A plain @AssertTrue would be reported as "salaryRangeValid", which no input has.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CrossFieldValidator.class)
public @interface ValidCrossFields {
    String message() default "Invalid request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
