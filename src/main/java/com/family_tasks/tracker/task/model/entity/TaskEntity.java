package com.family_tasks.tracker.task.model.entity;

import com.family_tasks.tracker.task.model.enums.Priority;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TaskEntity {
    private final String taskId;
    private final String name;
    private final String description;
    private final Priority priority;
    private final String reporterId;
    private final String executorId;
    private final boolean confidential;
    private final Set<String> sharedWith;
    private final LocalDateTime deadline;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
