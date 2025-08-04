package com.family_tasks.tracker.task.model.entity;

import com.family_tasks.tracker.task.model.enums.Priority;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.catalina.User;

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
    private final User reporter;
    private final User executor;
    private final Boolean isPrivate;
    private final Set<User> sharedWith;
    private final LocalDateTime deadline;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
