package com.family_tasks.tracker.task.model.dto;

import com.family_tasks.tracker.common.validation.annotation.ValidTaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TaskCreateApiRequest {
    @Size(max = TASK_NAME_MAX_LENGTH, message = TASK_NAME_TOO_LONG)
    @NotEmpty(message = TASK_NAME_NOT_SPECIFIED)
    private final String name;
    @Size(max = TASK_DESCRIPTION_MAX_LENGTH, message = TASK_DESCRIPTION_TOO_LONG)
    @Size(min = TASK_DESCRIPTION_MIN_LENGTH, message = TASK_DESCRIPTION_TOO_SHORT)
    private final String description;
    @ValidTaskPriority
    @NotEmpty(message = TASK_PRIORITY_NULL)
    private final String priority;
    @NotNull(message = TASK_REPORTER_NULL)
    private final Integer reporterId;
    @Builder.Default
    private final Set<Integer> executorIds = new HashSet<>();
    @NotNull(message = TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED)
    private final Boolean confidential;
    @Future(message = TASK_DEADLINE_DATE_NOT_FUTURE)
    private final LocalDate deadline;
}