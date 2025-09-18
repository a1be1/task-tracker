package com.family_tasks.tracker.task.model.dto;

import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
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

    private final String taskId;
    private final String name;
    private final TaskStatus status;
    private final String description;
    private final TaskPriority priority;
    private final Integer reporterId;
    private final Set<Integer> executorIds;
    private final boolean confidential;
    private final LocalDate deadline;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}