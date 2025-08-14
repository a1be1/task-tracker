package com.family_tasks.tracker.common.validation.annotation.validator;

import com.family_tasks.tracker.common.validation.annotation.ValidTaskStatus;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidTaskStatusValidator implements ConstraintValidator<ValidTaskStatus, String> {

    private static final Set<String> VALID_STATUSES = Arrays.stream(TaskStatus.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return isEmpty(string) || VALID_STATUSES.contains(string);
    }

    private boolean isEmpty(String string) {
        return !StringUtils.hasText(string);
    }
}