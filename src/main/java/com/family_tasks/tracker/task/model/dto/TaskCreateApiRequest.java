package com.family_tasks.tracker.task.model.dto;

import com.family_tasks.tracker.common.validation.annotation.ValidTaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    @Schema(description = "The task name.")
    @Size(max = TASK_NAME_MAX_LENGTH, message = TASK_NAME_TOO_LONG)
    @NotEmpty(message = TASK_NAME_NOT_SPECIFIED)
    private final String name;

    @Schema(description = "The task description.")
    @Size(max = TASK_DESCRIPTION_MAX_LENGTH, message = TASK_DESCRIPTION_TOO_LONG)
    @Size(min = TASK_DESCRIPTION_MIN_LENGTH, message = TASK_DESCRIPTION_TOO_SHORT)
    private final String description;

    @Schema(description = "The task priority. Affects ordering in the journal.", implementation = TaskPriority.class)
    @ValidTaskPriority
    @NotEmpty(message = TASK_PRIORITY_NULL)
    private final String priority;

    @Schema(description = "The user ID of the task creator.")
    @NotNull(message = TASK_REPORTER_NULL)
    private final Integer reporterId;

    @Schema(description = "The list of user IDs of the executors of the task.")
    @Builder.Default
    private final Set<Integer> executorIds = new HashSet<>();

    @Schema(description = "The flag indicates whether the task is public or visible only for the reporter and executors.")
    @NotNull(message = TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED)
    private final Boolean confidential;

    @Schema(description = "The number of reward points awarded when the task status changes to COMPLETED")
    @Positive(message = REWARDS_POINTS_POSITIVE)
    private Integer rewardsPoints;

    @Schema(description = "The task deadline indicates when the task is expected to be completed.")
    @FutureOrPresent(message = TASK_DEADLINE_DATE_NOT_PRESENT_OR_FUTURE)
    private final LocalDate deadline;
}