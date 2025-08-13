package com.family_tasks.tracker.common.validation.annotation;

import com.family_tasks.tracker.common.validation.annotation.validator.ValidPriorityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_PRIORITY_INVALID;

@Constraint(
        validatedBy = ValidPriorityValidator.class
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPriority {
    String message() default TASK_PRIORITY_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}