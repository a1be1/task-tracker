package com.family_tasks.tracker.task.model.dto;

import com.family_tasks.tracker.common.validation.annotation.ValidTaskPriority;
import com.family_tasks.tracker.common.validation.annotation.ValidTaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TaskUpdateApiRequest {
    @ValidTaskStatus
    @NotEmpty(message = TASK_STATUS_NULL)
    private final String status;
    @Size(max = TASK_NAME_MAX_LENGTH, message = TASK_NAME_TOO_LONG)
    @NotEmpty(message = TASK_NAME_NOT_SPECIFIED)
    private final String name;
    @Size(max = TASK_DESCRIPTION_MAX_LENGTH, message = TASK_DESCRIPTION_TOO_LONG)
    @Size(min = TASK_DESCRIPTION_MIN_LENGTH, message = TASK_DESCRIPTION_TOO_SHORT)
    private final String description;
    @ValidTaskPriority
    @NotEmpty(message = TASK_PRIORITY_NULL)
    private final String priority;
    @Builder.Default
    private final Set<Integer> executorIds = Set.of();
    @NotNull(message = TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED)
    private final Boolean confidential;
    private final LocalDate deadline;
}