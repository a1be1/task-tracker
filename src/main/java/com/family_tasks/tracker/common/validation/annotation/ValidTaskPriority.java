package com.family_tasks.tracker.common.validation.annotation;

import com.family_tasks.tracker.common.validation.annotation.validator.ValidTaskPriorityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_PRIORITY_INVALID;

@Constraint(
        validatedBy = ValidTaskPriorityValidator.class
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaskPriority {
    String message() default TASK_PRIORITY_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}