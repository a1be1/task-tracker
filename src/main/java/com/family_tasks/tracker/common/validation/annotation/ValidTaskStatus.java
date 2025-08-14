package com.family_tasks.tracker.common.validation.annotation;

import com.family_tasks.tracker.common.validation.annotation.validator.ValidTaskStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_STATUS_INVALID;

@Constraint(
        validatedBy = ValidTaskStatusValidator.class
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaskStatus {
    String message() default TASK_STATUS_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}