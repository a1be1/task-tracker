package com.family_tasks.tracker.task.model.entity;

import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TaskEntity {
    private String taskId;
    private TaskStatus status;
    private String name;
    private String description;
    private Priority priority;
    private String reporterId;
    private String executorId;
    private boolean confidential;
    @Builder.Default
    private Set<String> sharedWith = Set.of();
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}