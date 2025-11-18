package com.family_tasks.tracker.task.model.dto;

import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TaskApiResponse {

    @Schema(description = "The task ID.")
    private final String taskId;
    @Schema(description = "The task name.")
    private final String name;
    @Schema(description = "The task status.", implementation = TaskStatus.class)
    private final TaskStatus status;
    @Schema(description = "The task description.")
    private final String description;
    @Schema(description = "The task priority. Affects ordering in the journal.", implementation = TaskPriority.class)
    private final TaskPriority priority;
    @Schema(description = "The user ID of the task creator.")
    private final Integer reporterId;
    @Schema(description = "The list of user IDs of the executors of the task.")
    private final Set<Integer> executorIds;
    @Schema(description = "The flag indicates whether the task is public or visible only for the reporter and executors.")
    private final boolean confidential;
    @Schema(description = "The number of reward points awarded when the task status changes to COMPLETED.")
    private final Integer rewardsPoints;
    @Schema(description = "The task deadline indicates when the task is expected to be completed.")
    private final LocalDate deadline;
    @Schema(description = "The datetime of the task creation.")
    private final LocalDateTime createdAt;
    @Schema(description = "The datetime of the last task update.")
    private final LocalDateTime updatedAt;
}