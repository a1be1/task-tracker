package com.family_tasks.tracker.task.model.dto;

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
public class CreateTaskApiRequest {
    private final String name;
    private final String description;
    private final String priority;
    private final String reporterId;
    private final String executorId;
    private final boolean confidential;
    private final Set<String> sharedWith;
    private final LocalDateTime deadline;
}
