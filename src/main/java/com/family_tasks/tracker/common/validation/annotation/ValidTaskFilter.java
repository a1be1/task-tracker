package com.family_tasks.tracker.common.validation.annotation;

import com.family_tasks.tracker.common.validation.annotation.validator.ValidTaskFilterValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_FILTER_INVALID;

@Constraint(validatedBy = ValidTaskFilterValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaskFilter {
    String message() default TASK_FILTER_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}