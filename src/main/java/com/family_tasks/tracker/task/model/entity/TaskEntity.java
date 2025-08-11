package com.family_tasks.tracker.task.model.entity;

import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import lombok.*;

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
    private Set<String> sharedWith;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}