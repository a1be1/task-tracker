package com.family_tasks.tracker.task.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;


@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TaskGetApiRequest {
    @NotNull(message = USER_NOT_SPECIFIED)
    private final Integer userId;
}