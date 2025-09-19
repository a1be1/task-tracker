package com.family_tasks.tracker.task.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TaskFilterRequest {
    private Integer userId;
    private String filter;
}