package com.family_tasks.tracker.common.validation.annotation.validator;

import com.family_tasks.tracker.common.validation.annotation.ValidPriority;
import com.family_tasks.tracker.task.model.enums.Priority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidPriorityValidator implements ConstraintValidator<ValidPriority, String> {

    private static final Set<String> VALID_PRIORITIES = Arrays.stream(Priority.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return VALID_PRIORITIES.contains(string);
    }
}