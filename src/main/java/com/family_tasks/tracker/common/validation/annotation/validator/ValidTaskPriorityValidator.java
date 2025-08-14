package com.family_tasks.tracker.common.validation.annotation.validator;

import com.family_tasks.tracker.common.validation.annotation.ValidTaskPriority;
import com.family_tasks.tracker.task.model.enums.Priority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidTaskPriorityValidator implements ConstraintValidator<ValidTaskPriority, String> {

    private static final Set<String> VALID_PRIORITIES = Arrays.stream(Priority.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return isEmpty(string) || VALID_PRIORITIES.contains(string);
    }

    private boolean isEmpty(String string) {
        return !StringUtils.hasText(string);
    }
}