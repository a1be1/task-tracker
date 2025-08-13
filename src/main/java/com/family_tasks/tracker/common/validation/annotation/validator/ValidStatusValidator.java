package com.family_tasks.tracker.common.validation.annotation.validator;

import com.family_tasks.tracker.common.validation.annotation.ValidStatus;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidStatusValidator implements ConstraintValidator<ValidStatus, String> {

    private static final Set<String> VALID_STATUSES = Arrays.stream(TaskStatus.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return VALID_STATUSES.contains(string);
    }
}