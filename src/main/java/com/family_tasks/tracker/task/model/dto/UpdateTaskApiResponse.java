package com.family_tasks.tracker.task.model.dto;

import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateTaskApiResponse {

    private final String taskId;
    private final String name;
    private final String description;
    private final TaskStatus status;
    private final Priority priority;
    private final String reporterId;
    private final String executorId;
    private final boolean confidential;
    private final Set<String> sharedWith;
    private final LocalDate deadline;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}