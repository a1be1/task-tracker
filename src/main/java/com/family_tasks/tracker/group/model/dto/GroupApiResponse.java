package com.family_tasks.tracker.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class GroupApiResponse {
    @Schema(description = "The group ID")
    private final Integer groupId;
    @Schema(description = "The owner ID")
    private final Integer ownerId;
}